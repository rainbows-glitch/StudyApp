package com.example.studyApp;

import androidx.annotation.Nullable;
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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.internal.IStatusCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
//    declare variables
    private ConstraintLayout toolBarLayout;
    private ConstraintSet mConstraintSet;
    private TextView header, hiddenHeader;
    private Toolbar toolBar;
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;
    private ImageView foreWave;
    private Guideline alphaGuide;

    private GoogleSignInClient client;
    private GoogleSignInOptions options;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initialize variables
        toolBarLayout = (ConstraintLayout) findViewById(R.id.toolBarConstraint);
        toolBar = findViewById(R.id.toolbar);
        header = findViewById(R.id.header);
        hiddenHeader = findViewById(R.id.hiddenTitle);
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
        drawer.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() != R.id.close_nav) {
                NavigationUI.onNavDestinationSelected(item, mNavController);
            }
            drawerLayout.closeDrawers();
            return false;
        });

//        centre header
        mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(toolBarLayout);
        toolBar.post(() -> header.post(() -> {
            int whiteSpace = toolBar.getWidth() - header.getWidth();
            mConstraintSet.setMargin(header.getId(), ConstraintSet.END, whiteSpace / 2);
            mConstraintSet.applyTo(toolBarLayout);
        }));

//        adjust alphaWave
        foreWave = findViewById(R.id.foreWave);
        alphaGuide = findViewById(R.id.alphaGuide);
        foreWave.post(() -> alphaGuide.setGuidelineEnd(foreWave.getHeight() / 7));

//        Google Sign In
        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("332292739647-j5dvfu77t9ecudtj6r8kkk35rhbo0lqb.apps.googleusercontent.com")
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(this, options);


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
        }
    }

    public void googleSignInClicked(){
        Intent intent = client.getSignInIntent();
        startActivityForResult(intent, 10001);
    }

    public void googleSignOutPressed(){client.revokeAccess();}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10001){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account;
            try {
                account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(task1 ->{
                            if(task.isSuccessful()){
                                startActivity(new Intent(this, MainActivity.class));
                                Log.d("login","Logged In");
                            }else{
                                Log.d("login", task.getException().getMessage());
                            }
                        });
            } catch (ApiException err) {
                err.printStackTrace();
            }
        }
    }
}