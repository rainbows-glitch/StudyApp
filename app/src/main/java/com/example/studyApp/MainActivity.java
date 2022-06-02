package com.example.studyApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
//    declare variables
    private ConstraintLayout toolBarLayout;
    private ConstraintSet mConstraintSet;
    private TextView header;
    private ImageView foreWave;
    private Guideline alphaGuide;
    private Toolbar toolBar;
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initialize variables
        toolBarLayout = (ConstraintLayout) findViewById(R.id.toolBarConstraint);
        toolBar = findViewById(R.id.toolbar);
        header = findViewById(R.id.header);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navhost);
        assert navHost != null;
        mNavController = navHost.getNavController();

        setSupportActionBar(toolBar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_study, R.id.nav_chat).setOpenableLayout(drawerLayout).build();
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController((NavigationView) findViewById(R.id.drawer), mNavController);

        mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(toolBarLayout);
        toolBar.post(()-> header.post(()->{
            int whiteSpace = toolBar.getWidth() - header.getWidth();
            mConstraintSet.setMargin(header.getId(), ConstraintSet.END, whiteSpace/2);
            mConstraintSet.applyTo(toolBarLayout);
        }));


        foreWave = findViewById(R.id.foreWave);
        alphaGuide = findViewById(R.id.alphaGuide);
        foreWave.post(() -> alphaGuide.setGuidelineEnd(foreWave.getHeight()/7));
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}