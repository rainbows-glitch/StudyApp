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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
//    declare variables
    private ConstraintLayout toolBarLayout;
    private ConstraintSet mConstraintSet;
    private TextView header, hiddenHeader;
    private Toolbar toolBar;
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;
    private ImageView foreWave, purpleBlob, blueBlob;
    private Guideline alphaGuide;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initialize variables
        toolBarLayout = findViewById(R.id.toolBarConstraint);
        toolBar = findViewById(R.id.toolbar);
        header = findViewById(R.id.header);
        hiddenHeader = findViewById(R.id.hiddenTitle);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navhost);
        assert navHost != null;
        mNavController = navHost.getNavController();
        NavigationView drawer = findViewById(R.id.drawer);
        purpleBlob = findViewById(R.id.purpleBlob);
        blueBlob = findViewById(R.id.blueBlob);

//        tells system to use toolbar as app bar
        setSupportActionBar(toolBar);

//        defines all top-level pages (added the menu icon too)
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.close_nav, R.id.nav_home, R.id.nav_study, R.id.nav_chat).setOpenableLayout(drawerLayout).build();
//        sets up appBar for use with navController using appBar config defined above
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
//        syncs the navigationView with the navController (the navigation drawer)
        NavigationUI.setupWithNavController(drawer, mNavController);

//        called when item from navSlider selected
        drawer.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() != R.id.close_nav) {
                NavigationUI.onNavDestinationSelected(item, mNavController);
            }
            drawerLayout.closeDrawers();
            return false;
        });

//        adjust alphaWave
        foreWave = findViewById(R.id.foreWave);
        alphaGuide = findViewById(R.id.alphaGuide);
        foreWave.post(() -> alphaGuide.setGuidelineEnd(foreWave.getHeight() / 7));
    }

//    navigate from deep-level to top-level page
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

//    methods to be called from fragments
    public void hideNavigation(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            hiddenHeader.setVisibility(View.VISIBLE);
            Log.d("ToolBar", "Hidden via hideNavigation()");
        }
    }
    public void showNavigation(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            hiddenHeader.setVisibility(View.GONE);
            //        centre header
            mConstraintSet = new ConstraintSet();
            mConstraintSet.clone(toolBarLayout);
            toolBar.post(() -> header.post(() -> {
                int whiteSpace = toolBar.getWidth() - header.getWidth();
                mConstraintSet.setMargin(header.getId(), ConstraintSet.END, whiteSpace / 2);
                mConstraintSet.applyTo(toolBarLayout);
            }));
            Log.d("ToolBar", "shown via showNavigation()");
        }
    }

//   Adjust blobs
    public void defaultBackgroundSettings(){
        purpleBlob.setAlpha(1f);
        blueBlob.setAlpha(0.68f);

    }
    public void onStudyStart(){
        purpleBlob.setAlpha(0.4f);
        blueBlob.setAlpha(0.4f);
    }

}