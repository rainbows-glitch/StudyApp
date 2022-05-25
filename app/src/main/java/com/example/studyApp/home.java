//package com.example.studyApp;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.navigation.Navigation;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//public class home extends Fragment {
//
//    protected View tester, tint, navSlider;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        tester = view.findViewById(R.id.testing2);
//        tint = view.findViewById(R.id.tint);
//        navSlider = view.findViewById(R.id.navSlider);
//
//        tester.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tint.setVisibility(View.VISIBLE);
//                navSlider.setVisibility(View.VISIBLE);
//            }
//        });
//
//        navSlider.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view).navigate(R.id.chatAction_GLOBAL);
//            }
//        });
//
//        tint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tint.setVisibility(View.GONE);
//                navSlider.setVisibility(View.GONE);
//            }
//        });
//
//        return view;
//    }
//
//
//
//}