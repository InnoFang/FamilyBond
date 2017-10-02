package io.innofang.medically.data_display;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import io.innofang.medically.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/2 20:49
 * Description:
 */


public class DataDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DataDisplayFragment.newInstance())
                    .commit();
        }
    }
}
