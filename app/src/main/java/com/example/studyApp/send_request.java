package com.example.studyApp;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class send_request extends Fragment {
    String state;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_request, container, false);

        ConstraintLayout requestLayout = view.findViewById(R.id.requestLayout);
        View glass = view.findViewById(R.id.glass);

        send_requestArgs args = send_requestArgs.fromBundle(getArguments());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float vh = displayMetrics.heightPixels/100f;
        glass.getLayoutParams().height = (int) vh*85;

        ConstraintSet mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(requestLayout);
//        centre glass relative to window (not fragment)
        glass.post(()->{
            int verticalSpace = displayMetrics.heightPixels - glass.getHeight();
            mConstraintSet.setMargin(glass.getId(), ConstraintSet.BOTTOM, verticalSpace/2);
            mConstraintSet.applyTo(requestLayout);
        });

        TextView requestLabel = view.findViewById(R.id.requestLabel);
        if (args.getState().equals("counsellor")){
            requestLabel.setText("Request a Counsellor");
        } else if (args.getState().equals("mentor")){
            requestLabel.setText("Request an Academic Mentor");
        }

        view.findViewById(R.id.requestButton).setOnClickListener(view1 -> {
            EditText idInput = view.findViewById(R.id.idInput);
            EditText lvlInput = view.findViewById(R.id.levelInput);
            EditText helpInput = view.findViewById(R.id.helpInput);
            if (!idInput.getText().toString().isEmpty() && !lvlInput.getText().toString().isEmpty() && !helpInput.getText().toString().isEmpty()){
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"kumardashik@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Requesting a academic mentor");
                i.putExtra(Intent.EXTRA_TEXT,
                        "Hello. I would like to request an Academic mentor please \n\n" +
                                "Details:\n" +
                                "\nName: " +FirebaseAuth.getInstance().getCurrentUser().getDisplayName() +
                                "\nStudent ID " + idInput.getText().toString().trim() +
                                "\nSubject: " + helpInput.getText().toString().trim() +
                                "\nLevel: " + lvlInput.getText().toString().trim()
                                +"\n\n Thank you"
                                +"\n StudyApp Team"
                        );//TODO change name to real
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(requireContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}