package com.example.studyApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    protected ConstraintLayout constraintLayout;
    protected ImageView foreWave, alphaWave;
    protected Guideline alphaGuide;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.constraint_layout);
        foreWave = findViewById(R.id.foreWave);
        alphaWave = findViewById(R.id.alphaWave);
        alphaGuide = findViewById(R.id.alphaGuide);

        foreWave.post(() -> {
            alphaGuide.setGuidelineEnd(foreWave.getHeight()/7);
        });
    }
}