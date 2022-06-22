package com.example.studyApp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class home extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

//        show navigation since we know users might enter home from login/signup
        ((MainActivity)requireActivity()).showNavigation();

//        check if user is signed in
        if(user == null){
            NavHostFragment.findNavController(this).navigate(R.id.home2login);
        }

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting;
        if(hour >= 1 && hour < 12){
            greeting = "Good Morning";
        }else if(hour >=11 && hour <18){
            greeting = "Good Afternoon";
        }else{
            greeting = "Good Evening";
        }
        TextView welcomeHeader = view.findViewById(R.id.welcomeHeader), welcomeName = view.findViewById(R.id.welcomeName);
        welcomeHeader.setText(greeting);

        String name = user.getDisplayName().trim();
        for(int i = 0; i < name.length(); i++){
            if (Character.isWhitespace(name.charAt(i))){
                name = Character.toUpperCase(name.charAt(0)) + name.substring(1, i);
                welcomeName.setText(name);
                break;
            }
        }

        return view;
    }
}