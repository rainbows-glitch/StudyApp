package com.example.studyApp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class home extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView text = view.findViewById(R.id.textView);
        View slider = view.findViewById(R.id.slider);
        View tint = view.findViewById(R.id.tint);

        text.setOnClickListener(view12 -> {
            slider.setVisibility(View.VISIBLE);
            tint.setVisibility(View.VISIBLE);
        });

        tint.setOnClickListener(view13 -> {
            slider.setVisibility(View.GONE);
            tint.setVisibility(View.GONE);
        });

        slider.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.study_globalAction));

        return view;
    }
}