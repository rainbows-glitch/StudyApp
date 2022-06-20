package com.example.studyApp;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class forgot_pw extends Fragment {

    private View glass;
    private ConstraintLayout forgotPWLayout;
    private ImageView backArrow;
    private TextView backArrowLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_pw, container, false);

        glass = view.findViewById(R.id.glass);
        forgotPWLayout = view.findViewById(R.id.forgotPWLayout);
        backArrow = view.findViewById(R.id.returnToLogin);
        backArrowLabel = view.findViewById(R.id.backLabel);
        EditText email = view.findViewById(R.id.emailInput);

        ((MainActivity) getActivity()).hideNavigation();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

//        set glass height to be 85% of window height
        float vh = displayMetrics.heightPixels / 100;
        glass.getLayoutParams().height = (int) vh * 85;

        ConstraintSet mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(forgotPWLayout);

//        center glass relative to window (not fragment)
        glass.post(() -> {
            int verticalSpace = displayMetrics.heightPixels - glass.getHeight();
            mConstraintSet.setMargin(glass.getId(), ConstraintSet.BOTTOM, verticalSpace / 2);
            mConstraintSet.applyTo(forgotPWLayout);
        });

        //center return
        glass.post(() -> backArrowLabel.post(() -> backArrow.post(() -> {
            int horizontalSpace = glass.getWidth() - (backArrowLabel.getWidth() + backArrow.getWidth());
            mConstraintSet.setMargin(backArrowLabel.getId(), ConstraintSet.END, horizontalSpace / 2);
            mConstraintSet.applyTo(forgotPWLayout);
        })));

        backArrowLabel.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.forgotPW2Login));
        backArrow.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.forgotPW2Login));

        view.findViewById(R.id.forgotLabel).setOnClickListener(view13 -> {
           email.requestFocus();
        });

        //reset password (via email)
        view.findViewById(R.id.continueButton).setOnClickListener(view12 -> {
            resetPW(email);
        });

        //react to enter button
        email.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == 100 || i == EditorInfo.IME_NULL){
                resetPW(email);
                return true;
            } else {return false;}
        });

        return view;
    }

    public void resetPW(EditText email) {
        String emailValue = email.getText().toString().trim();
        if (!emailValue.trim().isEmpty()) {
            if (!emailValue.contains("@")) {
                emailValue += "@students.mrgs.school.nz";
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailValue)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "An email to reset your password has been sent to this Inbox", Toast.LENGTH_LONG).show();
                            Log.d("ForgotPW", "Password Reset sent.");
                            NavHostFragment.findNavController(this).navigate(R.id.forgotPW2Login);
                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("ForgotPassword",task.getException().getMessage());
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Please enter your Email", Toast.LENGTH_LONG).show();
            Log.d("ForgotPW", "Email Input Empty");
        }
    }
}