package com.enconiya.hospitalapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.enconiya.hospitalapp.Datasets.UserDataset;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;


public class SignUpFragment extends Fragment {


    private TextInputEditText fullname,email,pass,mobile;
    private Button loginbtn;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private TextInputLayout fullay, maillay, passlay, mobilelay;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =inflater.inflate(R.layout.fragment_sign_up, container, false);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        //Edittexts declarations
        fullname = root.findViewById(R.id.fullname_signup);
        email = root.findViewById(R.id.email_signup);
        pass = root.findViewById(R.id.password_signup);
        mobile = root.findViewById(R.id.mobile_signup);
        loginbtn = root.findViewById(R.id.create_acc_signup);

        //Edittext Layouts
        fullay = root.findViewById(R.id.namelay_signup);
        maillay = root.findViewById(R.id.maillay_signup);
        passlay = root.findViewById(R.id.passlay_signup);
        mobilelay = root.findViewById(R.id.mobilelay_signup);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the string data from the edittext
                String name = fullname.getText().toString();
                String mail = email.getText().toString();
                String password = pass.getText().toString();
                String phone = mobile.getText().toString();
                if (name.isEmpty()){
                    fullay.setErrorEnabled(true);
                    fullay.setError("দয়া করে পুরো নাম লিখুন");
                }
                if (mail.isEmpty()){
                    maillay.setErrorEnabled(true);
                    maillay.setError("দয়া করে মেইল এ্যাডরেসটি লিখুন");
                }
                if (password.isEmpty()){
                    passlay.setErrorEnabled(true);
                    passlay.setError("দয়া করে পাসওয়ার্ড লিখুন");
                }
                if (phone.isEmpty()){
                    mobilelay.setErrorEnabled(true);
                    mobilelay.setError("দয়া করে ফোন নম্বর লিখুন");
                }
                else {
                    auth.createUserWithEmailAndPassword(mail,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            reference.setValue(new UserDataset(name,phone,mail,new Date().toLocaleString())).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
                                    dialog.setTitle("অভিনন্দন!");
                                    dialog.setMessage("আপনার একাউন্ট টি সফলভাবে খোলা হয়েছে");
                                    dialog.setButton("মূল স্ক্রিনে যান", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(getActivity(),MainActivity.class));
                                        }
                                    });
                                    dialog.show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(view,e.getMessage(),BaseTransientBottomBar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(view,e.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        return root;
    }
}