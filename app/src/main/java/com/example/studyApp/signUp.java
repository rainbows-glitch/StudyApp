package com.example.studyApp;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class signUp extends Fragment {
//    declare
    private View glass;
    private ConstraintLayout loginLayout;
    private TextView emailLabel, passwordLabel, oAuthLabel, register1, register2;
    private EditText emailInput, passwordInput;
    private ImageView googleIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

//        initialise
        glass = view.findViewById(R.id.glass);
        loginLayout = view.findViewById(R.id.loginLayout);
        emailLabel = view.findViewById(R.id.emailLabel);
        emailInput = view.findViewById(R.id.emailInput);
        passwordLabel = view.findViewById(R.id.passwordLabel);
        passwordInput = view.findViewById(R.id.passwordInput);
        oAuthLabel = view.findViewById(R.id.oAuthLabel);
        googleIcon = view.findViewById(R.id.googleIcon);
        register1 = view.findViewById(R.id.registerP1);
        register2 = view.findViewById(R.id.registerP2);

        ((MainActivity)getActivity()).hideNavigation();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

//        set glass height to be 85% of window height
        float vh = displayMetrics.heightPixels/100;
        glass.getLayoutParams().height = (int) vh*85;

        ConstraintSet mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(loginLayout);

//        centre glass relative to window (not fragment)
        glass.post(()->{
            int verticalSpace = displayMetrics.heightPixels - glass.getHeight();
            mConstraintSet.setMargin(glass.getId(), ConstraintSet.BOTTOM, verticalSpace/2);
            mConstraintSet.applyTo(loginLayout);
        });

//      so that when user clicks label, they are focused onto its corresponding editText
        emailLabel.setOnClickListener(view1 -> emailInput.requestFocus());
        passwordLabel.setOnClickListener(view1 -> passwordInput.requestFocus());

//        centre register button/textView
        glass.post(()-> register1.post(()-> register2.post(()->{
            int horizontalSpace = glass.getWidth() - (register1.getWidth() + register2.getWidth());
            mConstraintSet.setMargin(register1.getId(), ConstraintSet.END, (horizontalSpace/2) + register2.getWidth());
            mConstraintSet.setMargin(register1.getId(), ConstraintSet.START, horizontalSpace/2);
            mConstraintSet.applyTo(loginLayout);
        })));

//        centre Google OAuth horizontally
        glass.post(()-> oAuthLabel.post(()-> googleIcon.post(()->{
            int horizontalSpace = glass.getWidth() - (oAuthLabel.getWidth() + googleIcon.getWidth());
            mConstraintSet.setMargin(oAuthLabel.getId(), ConstraintSet.END, horizontalSpace/2);
            mConstraintSet.setMargin(oAuthLabel.getId(), ConstraintSet.START, (horizontalSpace/2) + googleIcon.getWidth());
            mConstraintSet.applyTo(loginLayout);
        })));

//        switch to login fragment
        register1.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.signUp2Login));
        register2.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.signUp2Login));

        return view;
    }
}