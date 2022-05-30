package com.example.studyApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    protected ImageView foreWave;
    protected Guideline alphaGuide;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        foreWave = findViewById(R.id.foreWave);
        alphaGuide = findViewById(R.id.alphaGuide);
        foreWave.post(() -> {
            alphaGuide.setGuidelineEnd(foreWave.getHeight()/7);
            Log.d("alphawave","guide adjusted");
        });
    }
}