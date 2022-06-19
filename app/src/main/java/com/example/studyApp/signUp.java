package com.example.studyApp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class signUp extends Fragment {
//    declare
    private View glass;
    private ConstraintLayout loginLayout;
    private TextView emailLabel, passwordLabel, oAuthLabel, register1, register2;
    private EditText emailInput, passwordInput;
    private ImageView googleIcon;

    private FirebaseAuth mAuth;
    private GoogleSignInClient client;
    private GoogleSignInOptions options;

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
            signUpUsingEmail();
        });

        passwordInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == 100 || i == EditorInfo.IME_NULL){
                signUpUsingEmail();
                return true;
            } else {return false;}
        });

//        sign in/up with google
        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("332292739647-j5dvfu77t9ecudtj6r8kkk35rhbo0lqb.apps.googleusercontent.com")
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(requireContext(), options);

        view.findViewById(R.id.OAuthBG).setOnClickListener(view12 -> googleSignInClicked());

        return view;
    }

    public void signUp(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SignUp", "createUserWithEmail:success");
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d("Email Verification", "Email sent."); //TODO replace with snackBar
                                    }
                                });
                        NavHostFragment.findNavController(this).navigate(R.id.signUp2Home);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                    }
                });
    }

    public void googleSignInClicked(){
        Intent intent = client.getSignInIntent();
        startActivityForResult(intent, 10001);
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
                                Log.d("login", task.getException().getMessage());
                            }
                        });
            } catch (ApiException err) {
                err.printStackTrace();
            }
        }
    }

    public void signUpUsingEmail(){
        boolean emailProvided = false, isPasswordValueSafe = false;

//          Email validation
        String emailValue = emailInput.getText().toString();
        if (emailValue.contains("@") && !emailValue.trim().isEmpty()) { //assume valid email was provided
            emailProvided = true;
        } else if (emailValue.length() >= 5) {
            try { //assuming user inputs ID instead of email
                for (int i = 0; i < emailValue.length(); i++) {
                    Integer.parseInt(Character.toString(emailValue.charAt(i))); //try to raise NumberFormatException to check if input is numbers (ensure its a ID)
                }
                emailValue +="@students.mrgs.school.nz";
                emailProvided = true;
                Log.d("signUp", emailValue);

            } catch (NumberFormatException err) {
                Log.d("signUp", "Email input not valid. Error:" + err.getMessage());
            }
        } else {
            Log.d("signUp", "Invalid Email Input"); //TODO: add snackBars?
        }

//          Password validation
        String passwordValue = passwordInput.getText().toString();
        if(passwordValue.length() >= 10){
            boolean upperCasePresent = false, lowerCasePresent = false, numberPresent = false;
            for(int i = 0; i < passwordValue.length(); i++){
                if(Character.isUpperCase(passwordValue.charAt(i))){
                    upperCasePresent = true;
                }else if(Character.isLowerCase(passwordValue.charAt(i))){
                    lowerCasePresent = true;
                }else if(!Character.isAlphabetic(passwordValue.charAt(i))){
                    numberPresent = true;
                }
            }
            if (upperCasePresent && lowerCasePresent || lowerCasePresent && numberPresent || numberPresent && upperCasePresent){
                isPasswordValueSafe = true;
                Log.d("PW", "Password is Safe");

//              TODO: Again. SnackBars need to be added
            }else{
                Log.d("PW", "Password needs to include both upper and lower case characters. Numbers need to be present too");
            }
        }else{
            Log.d("PW", "password too short");
        }

//          If both password and email are valid(/safe)
        if(emailProvided && isPasswordValueSafe){
            signUp(emailValue, passwordValue);
        }
    }

}