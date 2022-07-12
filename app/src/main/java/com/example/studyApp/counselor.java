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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class counselor extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_counselor, container, false);
        ((MainActivity)requireActivity()).showNavigation();

        //initialize
        ConstraintLayout counselorLayout = view.findViewById(R.id.counselorLayout);
        View glass = view.findViewById(R.id.glass);
        EditText idInput = view.findViewById(R.id.idInput);
        EditText yrLvlInput = view.findViewById(R.id.yrLvlInput);
        EditText counselorInput = view.findViewById(R.id.counselorInput);
        EditText briefInput = view.findViewById(R.id.briefInput);

        //set glass to be 85% of the window (width-wise)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float vh = displayMetrics.heightPixels/100f;
        glass.getLayoutParams().height = (int) vh*85;

        //centre glass relative to window (not fragment)
        ConstraintSet mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(counselorLayout);
        glass.post(()->{
            int verticalSpace = displayMetrics.heightPixels - glass.getHeight();
            mConstraintSet.setMargin(glass.getId(), ConstraintSet.BOTTOM, verticalSpace/2);
            mConstraintSet.applyTo(counselorLayout);
        });

        //increasing target area by requesting focus when labels (& subtexts) are clicked.
        view.findViewById(R.id.idLabel).setOnClickListener(view2 -> idInput.requestFocus());
        view.findViewById(R.id.yrLvlLabel).setOnClickListener(view2 -> yrLvlInput.requestFocus());
        view.findViewById(R.id.counselorLabel).setOnClickListener(view2 -> counselorInput.requestFocus());
        view.findViewById(R.id.counselorSubtext).setOnClickListener(view2 -> counselorInput.requestFocus());
        view.findViewById(R.id.briefLabel).setOnClickListener(view2 -> briefInput.requestFocus());

        //enter key allows user ot navigate to next field
        idInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == 100 || i == EditorInfo.IME_NULL){
                yrLvlInput.requestFocus();
                return true;
            } else {return false;}
        });
        yrLvlInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == 100 || i == EditorInfo.IME_NULL){
                counselorInput.requestFocus();
                return true;
            } else {return false;}
        });
        counselorInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == 100 || i == EditorInfo.IME_NULL){
                briefInput.requestFocus();
                return true;
            } else {return false;}
        });

        view.findViewById(R.id.requestButton).setOnClickListener(view1 -> { //once button is clicked

            if (!idInput.getText().toString().trim().isEmpty() && !yrLvlInput.getText().toString().trim().isEmpty() && !briefInput.getText().toString().trim().isEmpty()){
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"kumardashik@gmail.com"});//TODO: change email to MRGS counselling
                i.putExtra(Intent.EXTRA_SUBJECT, "Requesting a Counsellor");
                if(counselorInput.getText().toString().trim().isEmpty()){ //if user's first time visiting MRGS counselling
                    i.putExtra(Intent.EXTRA_TEXT,
                            "Hello. I would like to request a Counselor \n\n" +
                                    "Details:\n" +
                                    "\nName: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() +
                                    "\nStudent ID " + idInput.getText().toString().trim() +
                                    "\nYear Level: " + yrLvlInput.getText().toString().trim() +
                                    "\nBrief Description: " + briefInput.getText().toString().trim()
                                    +"\n\n Thank you"
                                    +"\n StudyApp Team" //TODO change name to real
                    );
                }else{
                    i.putExtra(Intent.EXTRA_TEXT,
                            "Hello. I would like to request a Counselor \n\n" +
                                    "Details:\n" +
                                    "\nName: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() +
                                    "\nStudent ID " + idInput.getText().toString().trim() +
                                    "\nYear Level: " + yrLvlInput.getText().toString().trim() +
                                    "\nBrief Description: " + briefInput.getText().toString().trim()
                                    +"\n\n I have made an appointment before. Last time I talked to" + counselorInput.getText().toString().trim()
                                    +".\n\n Thank you"
                                    +"\n StudyApp Team" //TODO change name to real
                    );
                }
                try {
                    startActivity(Intent.createChooser(i, "Send Request via..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(requireContext(), "There are no email clients installed on your device. Please download an Email client such as Gmail.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}