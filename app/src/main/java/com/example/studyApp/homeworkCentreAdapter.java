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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class homeworkCentreAdapter extends RecyclerView.Adapter<homeworkCentreAdapter.homeworkViewHolder>{
    Context mContext;
    HashMap<String, Object> hwData;

    homeworkCentreAdapter(Context context, HashMap<String, Object> data){
        mContext = context;
        hwData = data;
    }

    @NonNull
    @Override
    public homeworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View card = inflater.inflate(R.layout.homework_centre_view, parent, false);
        return new homeworkViewHolder(card);
    }

    @Override
    public void onBindViewHolder(@NonNull homeworkViewHolder holder, int position) {
        Object[] keySet = hwData.keySet().toArray();
        Map<String, String> currentData = (Map<String, String>) hwData.get(keySet[position]);
        AtomicBoolean status = new AtomicBoolean(false);

        ConstraintSet cSet = new ConstraintSet();
        cSet.clone(holder.mLayout);

        holder.card.post(() -> { //set arrow height to be 30% of card height and set arrow margin
            holder.arrow.getLayoutParams().height = (int) (holder.card.getHeight() *0.3);

            holder.description.setText(currentData.get("description"));
            holder.time.setText(currentData.get("time"));
            holder.location.setText(currentData.get("location"));
            cSet.setMargin(holder.arrow.getId(), ConstraintSet.TOP, (int)(holder.card.getHeight()*0.35));
            cSet.applyTo(holder.mLayout);
        });

        holder.label.setText(currentData.get("subject"));


        holder.card.setOnClickListener(view -> {
                if(status.get()){
                    holder.arrow.setRotationX(0f);
                    holder.expandedLayout.setVisibility(View.GONE);
                }else{
                    holder.arrow.setRotationX(180.0f);
                    holder.expandedLayout.setVisibility(View.VISIBLE);
                }
                status.set(!status.get());
        });
    }

    @Override
    public int getItemCount() {
        return hwData.size();
    }

    static class  homeworkViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout mLayout, expandedLayout;
        TextView label, description, time, location;
        ShapeableImageView card;
        ImageView arrow;
        public homeworkViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.hwLayout);
            label = itemView.findViewById(R.id.cardLabel);
            card = itemView.findViewById(R.id.cardBG);
            expandedLayout = itemView.findViewById(R.id.expandedLayout);
            arrow = itemView.findViewById(R.id.downArrow);
            description = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.when);
            location = itemView.findViewById(R.id.location);
        }
    }
}