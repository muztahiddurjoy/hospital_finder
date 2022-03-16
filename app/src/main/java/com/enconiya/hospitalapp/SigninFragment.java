package com.enconiya.hospitalapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.enconiya.hospitalapp.Datasets.UserDataset;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;


public class SigninFragment extends Fragment {
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton gbtn;
    private Button loginbtn;
    private TextInputEditText email,pass;
    private DatabaseReference reference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_signin, container, false);
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        loginbtn =  root.findViewById(R.id.login_btn);
        email = root.findViewById(R.id.email_login);
        pass = root.findViewById(R.id.password_login);
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailT = email.getText().toString().trim();
                String passT = pass.getText().toString();
                if (emailT.isEmpty()){
                    Snackbar.make(view,"দয়া করে ইমেইল এ্যাডরেস লিখুন", BaseTransientBottomBar.LENGTH_LONG).show();
                }
                if(passT.isEmpty()){
                    Snackbar.make(view,"দয়া করে পাসওয়ার্ড লিখুন", BaseTransientBottomBar.LENGTH_LONG).show();
                }
                else {
                    mAuth.signInWithEmailAndPassword(emailT,passT).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startActivity(new Intent(getActivity(),MainActivity.class));
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

        gbtn = root.findViewById(R.id.google_button);
        gbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signwg();
            }
        });
        return root;
    }

    private void signwg() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                reference.child(account.getId()).setValue(new UserDataset(account.getDisplayName(),null,account.getEmail(),new Date().toLocaleString()));
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }

                    private void updateUI(FirebaseUser user) {
                        if (user!=null){
                            Toast.makeText(getActivity(), "লগইন সম্পন্ন হয়েছে", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(),MainActivity.class));
                        }
                        else{
                            Toast.makeText(getActivity(), "দুঃখিত", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}