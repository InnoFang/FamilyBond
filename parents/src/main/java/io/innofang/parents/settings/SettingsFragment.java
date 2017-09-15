package io.innofang.parents.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.innofang.parents.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 09:14
 * Description:
 */


public class SettingsFragment extends Fragment{

    public static SettingsFragment newInstance() {
        return Holder.sInstance;
    }

    private static class Holder {
        private static final SettingsFragment sInstance = new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }
}
