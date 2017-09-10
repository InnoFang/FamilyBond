package io.innofang.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import io.innofang.test.card.CardActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.amap_button:
                startActivity(new Intent(this, AMapActivity.class));
                break;
            case R.id.gallery_button:
                startActivity(new Intent(this, GalleryActivity.class));
                break;
            case R.id.card_button:
                startActivity(new Intent(this, CardActivity.class));
                break;
        }
    }
}
