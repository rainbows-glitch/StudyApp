package com.example.studyApp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends Fragment {
//    declare
    private View glass;
    private ConstraintLayout loginLayout;
    private TextView oAuthLabel;
    private TextView register1;
    private TextView register2;
    private EditText emailInput, passwordInput;
    private ImageView googleIcon;

    private FirebaseAuth mAuth;
    private GoogleSignInClient client;
    private boolean passwordToggle = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();  //to keep back button functional and avoid infinite loop
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ((MainActivity)requireActivity()).defaultBackgroundSettings();

//        initialise
        glass = view.findViewById(R.id.glass);
        loginLayout = view.findViewById(R.id.loginLayout);
        TextView emailLabel = view.findViewById(R.id.emailLabel);
        emailInput = view.findViewById(R.id.emailInput);
        TextView passwordLabel = view.findViewById(R.id.passwordLabel);
        passwordInput = view.findViewById(R.id.passwordInput);
        oAuthLabel = view.findViewById(R.id.oAuthLabel);
        googleIcon = view.findViewById(R.id.googleIcon);
        register1 = view.findViewById(R.id.registerP1);
        register2 = view.findViewById(R.id.registerP2);

        mAuth = FirebaseAuth.getInstance();

       ((MainActivity)requireActivity()).hideNavigation();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

//        set glass height to be 85% of window height
        float vh = displayMetrics.heightPixels/100f;
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

//        switch to SignUp fragment
        register1.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.login2SignUp));
        register2.setOnClickListener(view1 -> NavHostFragment.findNavController(this).navigate(R.id.login2SignUp));

//        switch to forgotPW fragment
        view.findViewById(R.id.forgotPW).setOnClickListener(view14 -> NavHostFragment.findNavController(this).navigate(R.id.login2ForgotPW));

        //enter button
        emailInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == 100 || i == EditorInfo.IME_NULL){
                passwordInput.requestFocus();
                return true;
            } else {return false;}
        });
        passwordInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == 100 || i == EditorInfo.IME_NULL){
                if(isOnline()){
                    signInUsingPW(view.findViewById(R.id.emailInput), view.findViewById(R.id.passwordInput));
                }else{
                    Toast.makeText(getContext(), "You seem to be offline. Please connect to the Internet", Toast.LENGTH_LONG).show();
                }
                return true;
            } else {return false;}
        });

//        Sign user in using email and password
        view.findViewById(R.id.continueButton).setOnClickListener(view13 -> {
            if(isOnline()){
                signInUsingPW(view.findViewById(R.id.emailInput), view.findViewById(R.id.passwordInput));
            }else{
                Toast.makeText(getContext(), "You seem to be offline. Please connect to the Internet", Toast.LENGTH_LONG).show();
            }
        });

//        show/hide password
        view.findViewById(R.id.eyeIcon).setOnClickListener(view15 -> {
            if(passwordToggle){
                passwordInput.setTransformationMethod(null);
            }else{
                passwordInput.setTransformationMethod(new PasswordTransformationMethod());
            }
            passwordToggle = !passwordToggle;
        });

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("332292739647-j5dvfu77t9ecudtj6r8kkk35rhbo0lqb.apps.googleusercontent.com")
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(requireContext(), options);

        view.findViewById(R.id.OAuthBG).setOnClickListener(view12 -> googleSignInClicked());

        return view;
    }

    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseFirestore.getInstance().collection("users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("userInformation")
                                .document("classes")
                                .get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()){
                                        NavHostFragment.findNavController(this).navigate(R.id.globalNavHome);
                                    }else{ //new account created
                                        NavHostFragment.findNavController(this).navigate(R.id.login2UserInfo);
                                    }
                                });
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.w("SignUp", "UserSignInWithEmail:failure", task.getException());
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
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .collection("userInformation")
                                        .document("classes")
                                        .get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()){
                                        NavHostFragment.findNavController(this).navigate(R.id.globalNavHome);
                                    }else{ //new account created
                                        NavHostFragment.findNavController(this).navigate(R.id.login2UserInfo);
                                    }
                                });
                                Log.d("Login", "Logged In");
                            } else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                Log.d("Login", task.getException().getMessage());
                            }
                        });
            } catch (ApiException err) {
                err.printStackTrace();
            }
        }
    }

    public void signInUsingPW(EditText emailInput, EditText passwordInput){
        String emailValue = emailInput.getText().toString();
        String passwordValue = passwordInput.getText().toString();
        if(!emailValue.trim().isEmpty() && !passwordValue.trim().isEmpty()){
            if(!emailValue.contains("@")){
                emailValue += "@students.mrgs.school.nz";
            }
            signIn(emailValue, passwordValue);
        }else{
            Toast.makeText(getContext(), "Please enter your Email and Password", Toast.LENGTH_LONG).show();
            Log.d("LOGIN","Empty Field");
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}