package com.example.studyApp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class home extends Fragment {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ((MainActivity)requireActivity()).defaultBackgroundSettings();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

//        show navigation since we know users might enter home from login/signup
        ((MainActivity)requireActivity()).showNavigation();

//        check if user is signed in
        if(user == null){
            NavHostFragment.findNavController(this).navigate(R.id.home2login);
        }else{
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            String greeting;
            if(hour >= 0 && hour < 12){
                greeting = "Good Morning";
            }else if(hour >=12 && hour <18){
                greeting = "Good Afternoon";
            }else{
                greeting = "Good Evening";
            }
            TextView welcomeHeader = view.findViewById(R.id.welcomeHeader), welcomeName = view.findViewById(R.id.welcomeName);
            welcomeHeader.setText(greeting);

            String name = user.getDisplayName();
            for(int i = 0; i < name.length(); i++){
                if (Character.isWhitespace(name.charAt(i))){
                    name = Character.toUpperCase(name.charAt(0)) + name.substring(1, i);
                    break;
                }
            }
            welcomeName.setText(name);

//          retrieve data from cloud firestore TODO: could be more efficient. Consider queries
            FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("userInformation")
                    .document("classes").get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()){
                            final Map<String, Object> classes = documentSnapshot.getData();

                            FirebaseFirestore.getInstance().collection("users")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("userInformation")
                                    .document("timetable").get()
                                    .addOnSuccessListener(documentSnapshot1 -> {

                                        if (documentSnapshot1.exists()){
                                            Map<String, Object> timetable = documentSnapshot1.getData();
                                            Log.d("TIMETABLE", timetable.toString());
                                            sliderAdapter parentAdapter = new sliderAdapter(getContext(), classes, timetable);
                                            RecyclerView rv = view.findViewById(R.id.horizontalRecycle);
                                            rv.setAdapter(parentAdapter);

                                        }else{NavHostFragment.findNavController(this).navigate(R.id.home2UserInfo);}
                                    });
                        }else{NavHostFragment.findNavController(this).navigate(R.id.home2UserInfo);}
                    })
                    .addOnFailureListener(e -> Log.d("FIREBASE_FIRESTORE/HOME", e.getMessage()));

            RecyclerView rv = view.findViewById(R.id.horizontalRecycle);
            LinearLayoutManager parentLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(parentLayout);
            AtomicInteger adapterPosition = new AtomicInteger();

            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(rv);

            int today = LocalDateTime.now().getDayOfWeek().getValue() -1;
            if(today > 4){today = 0;}
            TextView dayName = view.findViewById(R.id.day);
            adapterPosition.set(10 + today);
            rv.scrollToPosition(adapterPosition.get());
            updateDayName(adapterPosition.get(), dayName);

//            make buttons functional
            view.findViewById(R.id.backArrow).setOnClickListener(view1 -> {
                if(parentLayout.findFirstVisibleItemPosition() >0){
                    int pos = parentLayout.findFirstVisibleItemPosition() -1;
                    adapterPosition.set(pos);
                    rv.smoothScrollToPosition(pos);
                    updateDayName(pos, dayName);
                    view.findViewById(R.id.frontArrow).setVisibility(View.VISIBLE);
                    if (parentLayout.findFirstVisibleItemPosition()-1 == 0){
                        view.findViewById(R.id.backArrow).setVisibility(View.GONE);
                    }
                }else{
                    rv.smoothScrollToPosition(0);
                    view.findViewById(R.id.backArrow).setVisibility(View.GONE);
                }
            });
            view.findViewById(R.id.frontArrow).setOnClickListener(view1 -> {
                if (parentLayout.findLastVisibleItemPosition() < 24) {
                    int pos = parentLayout.findLastVisibleItemPosition() + 1;
                    adapterPosition.set(pos);
                    rv.smoothScrollToPosition(pos);
                    updateDayName(pos, dayName);
                    view.findViewById(R.id.backArrow).setVisibility(View.VISIBLE);
                    if (parentLayout.findLastVisibleItemPosition() + 1 == 24) {
                        view.findViewById(R.id.frontArrow).setVisibility(View.GONE);
                    }
                } else {
                    rv.smoothScrollToPosition(24);
                    view.findViewById(R.id.frontArrow).setVisibility(View.GONE);
                }
            });

            rv.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    Log.d("VelocityX", Integer.toString(velocityX));
                    String state;
                    if (velocityX <= 0){
                        state = "left";
                    }else{
                        state = "right";
                    }

                    if(Math.abs(velocityX) >= 10000 && Math.abs(velocityX) < 20000){
                        new Handler().postDelayed(()-> onSwipe(parentLayout, dayName, view, state),2000);
                    }else{
                        new Handler().postDelayed(()-> onSwipe(parentLayout, dayName, view, state),3500);
                    }
                    return false;
                }
            });

        }
        return view;
    }

    private void updateDayName(int position, TextView textView){
        String dayName;
        dayName = DayOfWeek.of(position%5 +1).toString();
        dayName = dayName.charAt(0) + dayName.substring(1).toLowerCase();
        textView.setText(dayName);
    }

    private void onSwipe(LinearLayoutManager layout, TextView textView, View view, String state){
        int pos =0;
        if(Objects.equals(state, "left")){
            pos = layout.findFirstVisibleItemPosition();
        }else if (Objects.equals(state, "right")){
            pos = layout.findLastVisibleItemPosition();
        }
        updateDayName(pos, textView);
        view.findViewById(R.id.frontArrow).setVisibility(View.VISIBLE);
        if (pos == 0){
            view.findViewById(R.id.backArrow).setVisibility(View.GONE);
        }else if (layout.findLastVisibleItemPosition() == 24) {
            view.findViewById(R.id.frontArrow).setVisibility(View.GONE);
        }
    }
}