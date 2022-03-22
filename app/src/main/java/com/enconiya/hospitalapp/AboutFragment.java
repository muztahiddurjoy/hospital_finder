package com.enconiya.hospitalapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class AboutFragment extends Fragment {


    Button logout;
    TextView attr;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =inflater.inflate(R.layout.fragment_about, container, false);
        logout = root.findViewById(R.id.logout);
        attr = root.findViewById(R.id.text_attr);
        attr.setText(Html.fromHtml("<a href=\"https://www.flaticon.com/free-icons/email\" title=\"email icons\">Email icons created by Tomas Knop - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/password\" title=\"password icons\">Password icons created by Freepik - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/user\" title=\"user icons\">User icons created by Freepik - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/phone\" title=\"phone icons\">Phone icons created by Gregor Cresnar - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/hospital\" title=\"hospital icons\">Hospital icons created by Freepik - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/pharmacy\" title=\"pharmacy icons\">Pharmacy icons created by nawicon - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/about\" title=\"about icons\">About icons created by Tempo_doloe - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/hospital\" title=\"hospital icons\">Hospital icons created by mavadee - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/human-body\" title=\"human-body icons\">Human-body icons created by Soremba - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/doctor\" title=\"doctor icons\">Doctor icons created by DinosoftLabs - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/hospital\" title=\"hospital icons\">Hospital icons created by Freepik - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/road\" title=\"road icons\">Road icons created by Those Icons - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/hourglass\" title=\"hourglass icons\">Hourglass icons created by prettycons - Flaticon</a>\n" +
                "<a href=\"https://www.flaticon.com/free-icons/hand\" title=\"hand icons\">Hand icons created by surang - Flaticon</a>" +
                "<a href=\"https://www.flaticon.com/free-icons/hospital\" title=\"hospital icons\">Hospital icons created by Freepik - Flaticon</a>"));
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(),AuthActivity.class));
            }
        });
        return root;
    }
}