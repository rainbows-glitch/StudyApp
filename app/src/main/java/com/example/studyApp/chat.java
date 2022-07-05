package com.example.studyApp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class chat extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ((MainActivity)requireActivity()).defaultBackgroundSettings();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("332292739647-j5dvfu77t9ecudtj6r8kkk35rhbo0lqb.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(requireContext(), options);

        view.findViewById(R.id.button).setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            client.revokeAccess();
            NavHostFragment.findNavController(this).navigate(R.id.chat2Login); //TODO: remember to delete chat2Login action
        });

        return view;
    }
}