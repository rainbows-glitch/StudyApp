package com.example.studyApp;

import androidx.annotation.NonNull;
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

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
//    declare variables
    private ConstraintLayout toolBarLayout;
    private ConstraintSet mConstraintSet;
    private TextView header;
    private Toolbar toolBar;
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;
    private ImageView foreWave;
    private Guideline alphaGuide;

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
        NavigationView drawer = findViewById(R.id.drawer);

//        tells system to use toolbar as app bar
        setSupportActionBar(toolBar);

//        defines all top-level pages (added the menu icon too)
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.close_nav, R.id.nav_home, R.id.nav_study, R.id.nav_chat).setOpenableLayout(drawerLayout).build();
//        sets up appBar for use with navController using appBar config defined above
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
//        syncs the navigationView with the navController (the navigation drawer)
        NavigationUI.setupWithNavController(drawer, mNavController);

//        called when item from navSlider selected
        drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() != R.id.close_nav){
                    NavigationUI.onNavDestinationSelected(item, mNavController);
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });

//        centre header
        mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(toolBarLayout);
        toolBar.post(()-> header.post(()->{
            int whiteSpace = toolBar.getWidth() - header.getWidth();
            mConstraintSet.setMargin(header.getId(), ConstraintSet.END, whiteSpace/2);
            mConstraintSet.applyTo(toolBarLayout);
        }));

//        adjust alphaWave
        foreWave = findViewById(R.id.foreWave);
        alphaGuide = findViewById(R.id.alphaGuide);
        foreWave.post(() -> alphaGuide.setGuidelineEnd(foreWave.getHeight()/7));
    }

//    navigate from deep-level to top-level page
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}