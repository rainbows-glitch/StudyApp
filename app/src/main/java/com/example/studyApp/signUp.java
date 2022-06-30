package com.example.studyApp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.method.PasswordTransformationMethod;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class signUp extends Fragment {
//    declare
    private View glass;
    private ConstraintLayout loginLayout;
    private TextView oAuthLabel;
    private TextView register1;
    private TextView register2;
    private EditText emailInput, passwordInput, nameInput;
    private ImageView googleIcon;

    private FirebaseAuth mAuth;
    private GoogleSignInClient client;
    private boolean passwordToggle = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

//        initialise
        glass = view.findViewById(R.id.glass);
        loginLayout = view.findViewById(R.id.loginLayout);
        TextView emailLabel = view.findViewById(R.id.emailLabel);
        emailInput = view.findViewById(R.id.emailInput);
        nameInput = view.findViewById(R.id.nameInput);
        TextView passwordLabel = view.findViewById(R.id.passwordLabel);
        passwordInput = view.findViewById(R.id.passwordInput);
        oAuthLabel = view.findViewById(R.id.oAuthLabel);
        googleIcon = view.findViewById(R.id.googleIcon);
        register1 = view.findViewById(R.id.registerP1);
        register2 = view.findViewById(R.id.registerP2);

        mAuth = FirebaseAuth.getInstance();

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

//        sign up with email(/id) and password
        view.findViewById(R.id.continueButton).setOnClickListener(view13 -> {
            if(isOnline()){
                signUpUsingEmail();
            }else{
                Toast.makeText(getContext(), "You seem to be offline. Please connect to the Internet", Toast.LENGTH_LONG).show();
            }
        });

        //        show/hide password
        view.findViewById(R.id.eyeIcon).setOnClickListener(view14 -> {
            if(passwordToggle){
                passwordInput.setTransformationMethod(null);
            }else{
                passwordInput.setTransformationMethod(new PasswordTransformationMethod());
            }
            passwordToggle = !passwordToggle;
        });

        passwordInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == 100 || i == EditorInfo.IME_NULL){
                if(isOnline()){
                    signUpUsingEmail();
                }else{
                    Toast.makeText(getContext(), "You seem to be offline. Please connect to the Internet", Toast.LENGTH_LONG).show();
                }
                return true;
            } else {return false;}
        });

//        sign in/up with google
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("332292739647-j5dvfu77t9ecudtj6r8kkk35rhbo0lqb.apps.googleusercontent.com")
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(requireContext(), options);

        view.findViewById(R.id.OAuthBG).setOnClickListener(view12 -> googleSignInClicked());

        return view;
    }

    public void signUpClicked(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SignUp", "createUserWithEmail:success");
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        //TODO: make sure to replace "studyApp" with actual name when done
                                        Toast.makeText(getContext(), "A verification email has been sent to your Inbox. Welcome to StudyApp", Toast.LENGTH_LONG).show();
                                        Log.d("Email Verification:Success", "Email sent.");
                                    }else{Log.d("Email Verification:Failed",task.getException().toString());}
                                });
                        UserProfileChangeRequest updateProfile = new UserProfileChangeRequest.Builder().setDisplayName(nameInput.getText().toString().trim()).build();
                        mAuth.getCurrentUser().updateProfile(updateProfile)
                                .addOnCompleteListener(requireActivity(), task1 -> {
                                    if (task1.isSuccessful()){
                                        NavHostFragment.findNavController(this).navigate(R.id.signUp2Home);
                                    }
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                    }
                });
    }

    public void googleSignInClicked(){
        if(isOnline()){
            Intent intent = client.getSignInIntent();
            startActivityForResult(intent, 10001);
        }else{
            Toast.makeText(getContext(), "You seem to be offline. Please connect to the Internet", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account;
            try {
                account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(task1 -> {
                            if (task.isSuccessful()) {
                                NavHostFragment.findNavController(this).navigate(R.id.signUp2Home);
                                Log.d("login", "Logged In");
                            } else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                Log.d("login", task.getException().getMessage());
                            }
                        });
            } catch (ApiException err) {
                err.printStackTrace();
            }
        }
    }

    public void signUpUsingEmail(){
        boolean emailProvided;
//          Email validation
        String emailValue = emailInput.getText().toString();
        if(!emailValue.trim().isEmpty()){
            if (emailValue.contains("@")) { //assume valid email was provided
                emailProvided = true;
            } else if (emailValue.length() >= 5) {
                try { //assuming user inputs ID instead of email
                    for (int i = 0; i < emailValue.length(); i++) {
                        Integer.parseInt(Character.toString(emailValue.charAt(i))); //try raise NumberFormatException to check if input is numbers (ensure its a ID)
                    }
                    emailValue +="@students.mrgs.school.nz";
                    emailProvided = true;
                } catch (NumberFormatException err) {
                    Toast.makeText(getContext(), "Invalid Email Field. Please try again", Toast.LENGTH_LONG).show();
                    Log.d("signUp", "Email input not valid");
                    return;
                }
            } else {
                Toast.makeText(getContext(), "Invalid Email Field. Please try again", Toast.LENGTH_LONG).show();
                Log.d("signUp", "Invalid Email Input");
                return;
            }
        }else{
            Toast.makeText(getContext(), "Please enter your Email and password", Toast.LENGTH_LONG).show();
            return;
        }

//        check if name field is not ex
        boolean nameProvided = false;
        if (nameInput.getText().toString().trim().length() > 0){
            nameProvided = true;
        }else{
            Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_LONG).show();
        }

//        Password validation
        if(emailProvided && nameProvided) {
            String passwordValue = passwordInput.getText().toString();
            if (passwordValue.length() >= 8) {
                int conditionsMet = 0;
                boolean upperCasePresent = false, lowerCasePresent = false, numberPresent = false, specialCharacterPresent = false;
                Pattern specialCharacters = Pattern.compile("[^A-Za-z0-9]");
                for (int i = 0; i < passwordValue.length(); i++) {
                    if (Character.isUpperCase(passwordValue.charAt(i)) && !upperCasePresent) {
                        upperCasePresent = true;
                    } else if (Character.isLowerCase(passwordValue.charAt(i)) && !lowerCasePresent) {
                        lowerCasePresent = true;
                    } else if (Character.isDigit(passwordValue.charAt(i)) && !numberPresent) {
                        conditionsMet++;
                        numberPresent = true;
                    } else if (specialCharacters.matcher(Character.toString(passwordValue.charAt(i))).matches() && !specialCharacterPresent) {
                        conditionsMet++;
                        specialCharacterPresent = true;
                    }

                    if(upperCasePresent && lowerCasePresent){
                        conditionsMet++;
                    }
                }
                if (conditionsMet >= 2) {  //if a minimum of 2 (out of 3) conditions are met
                    Log.d("PW", "Password is Safe");
                    signUpClicked(emailValue, passwordValue);
                } else {
                    Toast.makeText(getContext(), "This password is insecure. Your password must contain either numbers and special characters. Your password must also contain uppercase and lowercase letters.", Toast.LENGTH_LONG).show();
                    Log.d("PW", "Insecure password");
                }
            } else {
                Toast.makeText(getContext(), "This password is too short. Please try again with a longer password", Toast.LENGTH_LONG).show();
                Log.d("PW", "password too short");
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}