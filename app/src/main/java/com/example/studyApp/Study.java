package com.example.studyApp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Study extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        ((MainActivity)requireActivity()).onStudyStart();

        view.findViewById(R.id.gradientM1).setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.study2Counselor);
        });
        view.findViewById(R.id.gradientM2).setOnClickListener(view1 -> {
            NavHostFragment.findNavController(this).navigate(R.id.study2Request);
        });

        FirebaseFirestore.getInstance().collection("other").document("homeworkCentres").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        HashMap<String, Object> data = (HashMap<String, Object>) documentSnapshot.getData();

                        RecyclerView rv = view.findViewById(R.id.studyRV);
                        rv.setAdapter(new HomeworkCentreAdapter(requireContext(), data));
                        LinearLayoutManager parentLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        rv.setLayoutManager(parentLayout);

                    }else{ //error handling
                        Toast.makeText(requireContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();}
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
                    Log.d("STUDY/FIREBASE", e.getMessage());
                });

        return view;
    }
}