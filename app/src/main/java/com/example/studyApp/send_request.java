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

public class send_request extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_request, container, false);
        ((MainActivity)requireActivity()).showNavigation();

        //initialize
        ConstraintLayout requestLayout = view.findViewById(R.id.requestLayout);
        View glass = view.findViewById(R.id.glass);
        EditText idInput = view.findViewById(R.id.idInput);
        EditText lvlInput = view.findViewById(R.id.levelInput);
        EditText helpInput = view.findViewById(R.id.helpInput);

        //set glass to be 85% of the window (width-wise)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float vh = displayMetrics.heightPixels/100f;
        glass.getLayoutParams().height = (int) vh*85;

        //centre glass relative to window (not fragment)
        ConstraintSet mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(requestLayout);
        glass.post(()->{
            int verticalSpace = displayMetrics.heightPixels - glass.getHeight();
            mConstraintSet.setMargin(glass.getId(), ConstraintSet.BOTTOM, verticalSpace/2);
            mConstraintSet.applyTo(requestLayout);
        });

        //increasing target area by requesting focus when labels are clicked too.
        view.findViewById(R.id.idLabel).setOnClickListener(view2 -> idInput.requestFocus());
        view.findViewById(R.id.helpLabel).setOnClickListener(view2 -> helpInput.requestFocus());
        view.findViewById(R.id.levelLabel).setOnClickListener(view2 -> lvlInput.requestFocus());

        //enter key allows user ot navigate to next field
        idInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == 100 || i == EditorInfo.IME_NULL){
                helpInput.requestFocus();
                return true;
            } else {return false;}
        });
        helpInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == 100 || i == EditorInfo.IME_NULL){
                lvlInput.requestFocus();
                return true;
            } else {return false;}
        });


        view.findViewById(R.id.requestButton).setOnClickListener(view1 -> { //once button is clicked
            if (!idInput.getText().toString().trim().isEmpty() && !lvlInput.getText().toString().trim().isEmpty() && !helpInput.getText().toString().trim().isEmpty()){
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"kumardashik@gmail.com"});//TODO: change email to msBurns
                i.putExtra(Intent.EXTRA_SUBJECT, "Requesting a academic mentor");
                i.putExtra(Intent.EXTRA_TEXT,
                        "Hello. I would like to request an Academic mentor.\n\n" +
                                "Details:\n" +
                                "\nName: " +FirebaseAuth.getInstance().getCurrentUser().getDisplayName() +
                                "\nStudent ID " + idInput.getText().toString().trim() +
                                "\nSubject: " + helpInput.getText().toString().trim() +
                                "\nLevel: " + lvlInput.getText().toString().trim()
                                +"\n\n Thank you"
                                +"\n StudyApp Team" //TODO change name to real
                );
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