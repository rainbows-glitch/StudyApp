package com.example.studyApp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
            Log.d("Home","Checking if signed in");
//            getActivity().getSupportFragmentManager().popBackStack(); //to keep back button functional and avoid infinite loop
        }

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("332292739647-j5dvfu77t9ecudtj6r8kkk35rhbo0lqb.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(requireContext(), options);

        Button mButton = view.findViewById(R.id.button);
        mButton.setOnClickListener(view1 -> {
            mAuth.signOut();
            client.revokeAccess();
            NavHostFragment.findNavController(this).navigate(R.id.home2login);
        });

        return view;
    }
}