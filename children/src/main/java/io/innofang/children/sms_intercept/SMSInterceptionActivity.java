package io.innofang.children.sms_intercept;

import android.support.v4.app.Fragment;

import io.innofang.base.base.FragmentContainerActivity;
import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/13 16:09
 * Description:
 */


public class SMSInterceptionActivity extends FragmentContainerActivity {

    @Override
    protected Fragment createFragment() {
        return SMSInterceptionFragment.newInstance();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }
}
