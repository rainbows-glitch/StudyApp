package com.example.studyApp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class study extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);

        TextView text = view.findViewById(R.id.textView3);
        View tint = view.findViewById(R.id.tint2);
        View slider = view.findViewById(R.id.slider2);

        text.setOnClickListener(view13 -> {
            tint.setVisibility(View.VISIBLE);
            slider.setVisibility(View.VISIBLE);
        });

        tint.setOnClickListener(view12 -> {
            tint.setVisibility(View.GONE);
            slider.setVisibility(View.GONE);
        });

        slider.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.home_globalAction));

        return view;
    }
}