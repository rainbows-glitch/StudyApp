package com.example.studyApp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class home extends Fragment {
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ((MainActivity)getActivity()).showNavigation();
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null){
            NavHostFragment.findNavController(this).navigate(R.id.home2login);
            getActivity().getSupportFragmentManager().popBackStack(); //to keep back button functional and avoid infinite loop
        }

        Button mButton = view.findViewById(R.id.button);
        mButton.setOnClickListener(view1 -> {
            mAuth.signOut();
            ((MainActivity) getActivity()).googleSignOutPressed();
            NavHostFragment.findNavController(getParentFragment()).navigate(R.id.home2login);
            getActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }
}