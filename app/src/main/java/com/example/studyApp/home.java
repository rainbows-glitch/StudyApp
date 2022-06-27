package com.example.studyApp;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Calendar;

public class home extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

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


            RecyclerView rv = view.findViewById(R.id.horizontalRecycle);
            sliderAdapter parentAdapter = new sliderAdapter(getContext());
            LinearLayoutManager parentLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rv.setAdapter(parentAdapter);
            rv.setLayoutManager(parentLayout);

            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(rv);

            int today = LocalDateTime.now().getDayOfWeek().getValue() -1;
            TextView dayName = view.findViewById(R.id.day);
            rv.scrollToPosition(10 + today);
            updateDayName(10 + today, dayName);

//            make buttons functional
            view.findViewById(R.id.backArrow).setOnClickListener(view1 -> {
                if(parentLayout.findFirstVisibleItemPosition() >0){
                    int pos = parentLayout.findFirstVisibleItemPosition() -1;
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
                    float dist = snapHelper.calculateScrollDistance(velocityX, velocityY)[0];
                    float holderWidth = displayMetrics.widthPixels;
                    int n = Math.round(dist/holderWidth);
                    if(velocityX >0){
                        int pos = parentLayout.findLastVisibleItemPosition() +n; //we don't need to plus or minus 1 to pos as swiping as already changed pos by 1
                        updateDayName(pos, dayName);
                        return false;
                    }else if(velocityX <0){
                        int pos = parentLayout.findFirstVisibleItemPosition() +n;
                        updateDayName(pos, dayName);
                        return false;
                    }else {return false;}
                }
            });
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDayName(int position, TextView textView){
        String dayName;
        dayName = DayOfWeek.of(position%5 +1).toString();
        dayName = dayName.charAt(0) + dayName.substring(1).toLowerCase();
        textView.setText(dayName);
    }
}