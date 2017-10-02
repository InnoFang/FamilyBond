package io.innofang.medically.data_display;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.innofang.medically.R;
import io.innofang.medically.dao.MedicallyEvent;

/**
 * Author: Inno Fang
 * Time: 2017/10/2 20:50
 * Description:
 */


public class DataDisplayFragment extends Fragment {

    private TextView mBpsTextView;

    public static DataDisplayFragment newInstance() {

        Bundle args = new Bundle();
        DataDisplayFragment fragment = new DataDisplayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(sticky = true)
    public void onHandleMedicallyEvent(MedicallyEvent event) {
        Toast.makeText(getActivity(), event.bps, Toast.LENGTH_SHORT).show();
        mBpsTextView.setText(String.format("BPS: %s", event.bps));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_display, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBpsTextView = (TextView) view.findViewById(R.id.bps_text_view);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
