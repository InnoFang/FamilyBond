package io.innofang.protectplus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.innofang.protectplus.login.LoginActivity;

/**
 * Author: Inno Fang
 * Time: 2017/10/3 20:31
 * Description:
 */


public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DISPLAY_TIME = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_TIME);

    }
}
