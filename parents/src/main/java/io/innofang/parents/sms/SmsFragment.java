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

import io.innofang.base.bean.greendao.SMS;
import io.innofang.base.utils.common.RequestPermissions;
import io.innofang.parents.R;
import io.innofang.sms_intercept.SMSEvent;
import io.innofang.xfyun.XFYunUtil;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 09:25
 * Description:
 */


public class SmsFragment extends Fragment implements SmsContract.View {


    private TextView mSmsTextView;
    private String mSms;
    private SmsContract.Presenter mPresenter;

    public static SmsFragment newInstance() {
        return new SmsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new SmsPresenter(this);
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
        mSmsTextView.setText(mSms);
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
        mSms = event.sms;
        mSmsTextView.setText(event.sms);
        showInfo("拦截到可疑短信");
        XFYunUtil.build(getContext()).speak("为您拦截到可疑短信");
        SMS sms = new SMS();
        sms.setTime(event.time);
        sms.setAddress(event.address);
        sms.setContent(event.sms);
        sms.setProbability(event.probability);

        mPresenter.sendToChildren(sms);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("sms", mSms);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (null != savedInstanceState) {
            mSms = savedInstanceState.getString("sms");
        }
    }

    @Override
    public void setPresenter(SmsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showInfo(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }
}
