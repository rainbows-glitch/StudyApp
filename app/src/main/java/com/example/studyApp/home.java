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
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.DayOfWeek;
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

//        show navigation since we know users might enter home from login/signup
        ((MainActivity)requireActivity()).showNavigation();

//        check if user is signed in
        if(user == null){
            NavHostFragment.findNavController(this).navigate(R.id.home2login);
        }else{
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            String greeting;
            if(hour >= 1 && hour < 12){
                greeting = "Good Morning";
            }else if(hour >=11 && hour <18){
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
            rv.setAdapter(parentAdapter);
            LinearLayoutManager parentLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(parentLayout);
            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(rv);
            rv.scrollToPosition(13);

//            make buttons functional
            view.findViewById(R.id.backArrow).setOnClickListener(view1 -> {
                if(parentLayout.findFirstVisibleItemPosition() >0){
                    rv.smoothScrollToPosition(parentLayout.findFirstVisibleItemPosition() -1);
                    updateDayName(parentLayout, view.findViewById(R.id.day));
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
                if(parentLayout.findLastVisibleItemPosition()< 24){
                    rv.smoothScrollToPosition(parentLayout.findLastVisibleItemPosition() +1);
                    updateDayName(parentLayout, view.findViewById(R.id.day));
                    view.findViewById(R.id.backArrow).setVisibility(View.VISIBLE);
                    if (parentLayout.findLastVisibleItemPosition()+1 == 24){
                        view.findViewById(R.id.frontArrow).setVisibility(View.GONE);
                    }
                }else{
                    rv.smoothScrollToPosition(24);
                    view.findViewById(R.id.frontArrow).setVisibility(View.GONE);
                }
            });
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateDayName(LinearLayoutManager layout, TextView textView){
        int pos;
        String dayName;
        pos = layout.findFirstVisibleItemPosition()%5 +1;
        dayName = DayOfWeek.of(pos).toString();
        dayName = dayName.charAt(0) + dayName.substring(1).toLowerCase();
        textView.setText(dayName);
    }
}