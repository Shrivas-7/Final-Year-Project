package com.example.nsecdiscussionforum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNav);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new profile_fragment()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener onNav = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selected = null; 
            switch (item.getItemId())
            {
                case R.id.profile_bottom:
                    selected = new profile_fragment();
                    break;
                case R.id.home_bottom:
                    selected = new home_fragment();
                    break;
                case R.id.df_bottom:
                    selected = new df_fragment();
                    break;
                case R.id.search_bottom:
                    selected = new search_fragment();
                    break;
                case R.id.notification_bottom:
                    selected = new notification_fragment();
                    break;


            }

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selected).commit();
            return true;
        }
    };




}