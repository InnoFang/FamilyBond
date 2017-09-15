package io.innofang.parents.communication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.innofang.parents.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 09:13
 * Description:
 */


public class CommunicationFragment extends Fragment {

    public static CommunicationFragment newInstance() {
        return Holder.sInstance;
    }

    private static class Holder {
        private static final CommunicationFragment sInstance = new CommunicationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communication, container, false);
        return view;
    }
}
