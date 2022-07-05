package com.example.studyApp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class study extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        ((MainActivity)requireActivity()).onStudyStart();

        view.findViewById(R.id.gradientM1).post(() -> Log.d("STUDY", "GMesh1 loaded"));
        view.findViewById(R.id.gradientM1).post(() -> Log.d("STUDY", "GMesh2 loaded"));

        return view;
    }
}