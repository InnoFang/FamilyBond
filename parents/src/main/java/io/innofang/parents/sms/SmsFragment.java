package io.innofang.parents.sms;

import android.Manifest;
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

import java.util.List;

import io.innofang.base.util.common.RequestPermissions;
import io.innofang.parents.R;
import io.innofang.sms_intercept.SMSEvent;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 09:25
 * Description:
 */


public class SmsFragment extends Fragment {


    private TextView mSmsTextView;

    public static SmsFragment newInstance() {
        return new SmsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sms, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSmsTextView = (TextView) view.findViewById(R.id.sms_text_view);
        RequestPermissions.requestRuntimePermission(getActivity(), new String[]{Manifest.permission.RECEIVE_SMS}, new RequestPermissions.OnRequestPermissionsListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> deniedPermission) {

            }
        });
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe
    public void onHandleSmsEvent(SMSEvent event) {
        mSmsTextView.setText(event.sms);
        Toast.makeText(getActivity(), "拦截到可疑短信", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("sms", mSmsTextView.getText().toString());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (null != savedInstanceState) {
            mSmsTextView.setText(savedInstanceState.getString("sms"));
        }
    }
}
