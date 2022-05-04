package com.example.messingaround;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    public ConstraintLayout constraintLayout;
    public ImageView foreWave, alphaWave;
    public Guideline alphaGuide;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.constraint_layout);
        foreWave = (ImageView) findViewById(R.id.foreWave);
        alphaWave = (ImageView) findViewById(R.id.alphaWave);
        alphaGuide = (Guideline) findViewById(R.id.alphaGuide);

        foreWave.post(() -> {
            alphaGuide.setGuidelineEnd(foreWave.getHeight()/7);
        });
    }
}