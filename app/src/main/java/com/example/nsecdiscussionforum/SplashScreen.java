package com.example.nsecdiscussionforum;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    ImageView img;
    TextView name,name1;
    long ani_time = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        img = findViewById(R.id.logo_splash);
        name1 = findViewById(R.id.name1);
        name = findViewById(R.id.name);

        ObjectAnimator ay = ObjectAnimator.ofFloat(img,"y", 210f);
        ObjectAnimator nx = ObjectAnimator.ofFloat(name,"x",250f);
        ObjectAnimator nx1 = ObjectAnimator.ofFloat(name1,"x",200f);
        ay.setDuration(ani_time);
        nx.setDuration(ani_time);
        nx1.setDuration(ani_time);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ay,nx,nx1);
        animatorSet.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user != null)
                {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        },4000);
    }
}