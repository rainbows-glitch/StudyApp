package com.example.studyApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class sliderAdapter extends RecyclerView.Adapter<sliderAdapter.sliderViewHolder>{ //parent adapter
    Context mContext;
    Map<String,Object> mClasses;
    Map<String,Object> mTimetable;

    sliderAdapter(Context context, Map<String, Object> classes, Map<String,Object> timetable){
        mContext = context;
        mClasses = classes;
        mTimetable = timetable;
    }

    @NonNull
    @Override
    public sliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View columnView = inflater.inflate(R.layout.slider_view, parent, false);
        return new sliderViewHolder(columnView);
    }

    @Override
    public void onBindViewHolder(@NonNull sliderViewHolder holder, int position) {

        holder.classesRV.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        holder.classesRV.setAdapter(new classesAdapter(mContext, mClasses, mTimetable, position%5+1));
    }

    @Override
    public int getItemCount() {
        return 25;
    } //5 weeks

    public static class sliderViewHolder extends RecyclerView.ViewHolder{
        public RecyclerView classesRV;

        public sliderViewHolder(@NonNull View itemView) {
            super(itemView);
            classesRV = itemView.findViewById(R.id.classesRV);
        }
    }
}

class classesAdapter extends RecyclerView.Adapter<classesAdapter.classesViewHolder>{ //child adapter
    Context mContext;
    Map<String,Object> mClasses;
    Map<String,Object> mTimetable;
    int mParentPos;
    Map<Integer, String[]> times = new HashMap<>();

    classesAdapter(Context context, Map<String, Object> classes, Map<String,Object> timetable, int parentPos){
        mContext = context;
        mClasses = classes;
        mTimetable = timetable;
        mParentPos = parentPos;

        times.put(1, new String[]{"9:00am - 9:55am", "9:55am - 10:50am", "11:40am - 12:35pm", "12:35pm - 1:30pm", "2:10pm - 3:05pm"});
        times.put(2, new String[]{"9:00am - 9:55am", "9:55am - 10:50am", "11:40am - 12:35pm", "12:35pm - 1:30pm", "2:10pm - 3:05pm"});
        times.put(3, new String[]{"9:20am - 10:15am", "10:15am- 11:10am", "11:35am - 12:30pm", "12:30pm - 1:25pm", "2:05pm - 3:00pm"});
        times.put(4, new String[]{"9:00am - 9:55am", "9:55am - 10:50am", "11:40am - 12:35pm", "12:35pm - 1:30pm", "2:10pm - 3:05pm"});
        times.put(5, new String[]{"9:00am - 9:55am", "9:55am - 10:50am", "11:40am - 12:35pm", "12:35pm - 1:30pm", "2:10pm - 3:05pm"});
    }

    @NonNull
    @Override
    public classesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View classesView = inflater.inflate(R.layout.classes_view, parent, false);
        return new classesViewHolder(classesView);
    }

    @Override
    public void onBindViewHolder(@NonNull classesViewHolder holder, int position) {
        holder.tab.post(() -> holder.subject.post(() -> holder.room.post(() -> {
            ViewGroup.MarginLayoutParams timeLayoutParams = (ViewGroup.MarginLayoutParams) holder.time.getLayoutParams();
            int timeMargin = timeLayoutParams.leftMargin;
            int maxWidth = holder.tab.getWidth() - holder.room.getWidth() - timeMargin;
            holder.subject.setMaxWidth(maxWidth); //TODO: TEST

        })));

        //set data
        ArrayList cardData = (ArrayList) mTimetable.get(DayOfWeek.of(mParentPos).toString());
        Object classKey = cardData.get(position);

        //cloud firestore data
        Map<String, String> classInformation = (HashMap) mClasses.get(classKey);
        holder.subject.setText(classInformation.get("subject"));
        holder.teacher.setText(classInformation.get("teacher"));
        holder.room.setText(classInformation.get("room"));

        //time
        String[] currentDayTimes = times.get(mParentPos);
        holder.time.setText(currentDayTimes[position]);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class classesViewHolder extends RecyclerView.ViewHolder{
        public ConstraintLayout cardLayout;
        public ImageView tab;
        public TextView subject, teacher, room, time;

        public classesViewHolder(@NonNull View itemView) {
            super(itemView);
            cardLayout = itemView.findViewById(R.id.cardLayout);
            tab = itemView.findViewById(R.id.tab);
            subject = itemView.findViewById(R.id.subject);
            teacher= itemView.findViewById(R.id.teacher);
            room = itemView.findViewById(R.id.room);
            time = itemView.findViewById(R.id.classTime);
        }
    }
}