package com.example.studyApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Guideline;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
//    declare variables
    private ImageView foreWave;
    private Guideline alphaGuide;
    private Toolbar toolBar;
    private DrawerLayout mDrawerLayout;
    private NavHostFragment navHost;
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initialize variables
        toolBar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navhost);
        mNavController = navHost.getNavController();

        setSupportActionBar(toolBar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_study, R.id.nav_chat).setOpenableLayout(mDrawerLayout).build();
        NavigationUI.setupWithNavController(toolBar, mNavController, mDrawerLayout);
        NavigationUI.setupWithNavController((NavigationView) findViewById(R.id.drawer), mNavController);

        foreWave = findViewById(R.id.foreWave);
        alphaGuide = findViewById(R.id.alphaGuide);
        foreWave.post(() -> alphaGuide.setGuidelineEnd(foreWave.getHeight()/7));
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}