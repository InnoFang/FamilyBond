package io.innofang.children.reminder;

import android.Manifest;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.util.common.RequestPermissions;
import io.innofang.children.R;
import io.innofang.children.R2;

/**
 * Author: Inno Fang
 * Time: 2017/9/16 15:04
 * Description:
 */


public class ReminderActivity extends BaseActivity implements VoiceReminderContract.View {

    @BindView(R2.id.contact_chooser_button)
    Button mContactChooserButton;
    @BindView(R2.id.type_message_edit_text)
    EditText mTypeMessageEditText;
    @BindView(R2.id.speak_fab)
    FloatingActionButton mSpeakFab;
    @BindView(R2.id.time_text_view)
    TextView mTimeTextView;
    @BindView(R2.id.record_image_view)
    ImageView mRecordImageView;
    @BindView(R2.id.voice_tips_text_view)
    TextView mVoiceTipsTextView;
    @BindView(R2.id.record_layout)
    RelativeLayout mRecordLayout;
    @BindView(R2.id.voice_length_text_view)
    TextView mVoiceLengthTextView;
    @BindView(R2.id.progress_load)
    ProgressBar mProgressLoad;
    @BindView(R2.id.voice_record_layout)
    RelativeLayout mVoiceRecordLayout;
    @BindView(R2.id.send_button)
    Button mSendButton;

    private VoiceReminderContract.Presenter mPresenter;
    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_reminder);
        ButterKnife.bind(this);

        RequestPermissions.requestRuntimePermission(new String[]{
                Manifest.permission.RECORD_AUDIO
        }, new RequestPermissions.OnRequestPermissionsListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                toast("取消权限将无法发送语音提醒");
            }
        });

        mPresenter = new VoiceReminderPresenter(this);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.CHINA);//设置日期格式
        mTimeTextView.setText(df.format(new Date()));


    }

    @OnClick({R2.id.contact_chooser_button, R2.id.speak_fab, R2.id.time_text_view, R2.id.voice_record_layout, R2.id.send_button})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.contact_chooser_button) {
            mPresenter.chooseContact(this, mContactChooserButton);
        } else if (id == R.id.speak_fab) {
            if (mContactChooserButton.getText().toString().isEmpty()) {
                showInfo(getString(R.string.have_not_choose_a_contact));
            } else {
                mSpeakFab.setOnTouchListener(
                        ((VoiceReminderPresenter) mPresenter).new VoiceTouchListener());
                 /* initialize record manager */
                mPresenter.initRecordManager(this);
            }
        } else if (id == R.id.time_text_view) {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getSupportFragmentManager(), getString(R.string.reminder_time));
        } else if (id == R.id.voice_record_layout) {
            mPresenter.playRecordVoice();
        } else if (id == R.id.send_button) {
            if (mContactChooserButton.getText().toString().isEmpty()) {
                showInfo(getString(R.string.have_not_choose_a_contact));
            } else {
                mPresenter.sendReminder();
            }
        }
    }

    @Override
    public void setPresenter(VoiceReminderContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showInfo(String text) {
        Snackbar.make(mSpeakFab, text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setVoiceTipsText(@StringRes int id) {
        mVoiceTipsTextView.setText(id);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Drawable[] getVoiceAnimDrawable() {
        return new Drawable[]{
                getResources().getDrawable(R.mipmap.chat_icon_voice2),
                getResources().getDrawable(R.mipmap.chat_icon_voice3),
                getResources().getDrawable(R.mipmap.chat_icon_voice4),
                getResources().getDrawable(R.mipmap.chat_icon_voice5),
                getResources().getDrawable(R.mipmap.chat_icon_voice6)};
    }

    @Override
    public TextView getVoiceTipsTextView() {
        return mVoiceTipsTextView;
    }

    @Override
    public FloatingActionButton getSpeakFab() {
        return mSpeakFab;
    }

    @Override
    public ImageView getRecordImageView() {
        return mRecordImageView;
    }

    @Override
    public RelativeLayout getVoiceLayout() {
        return mRecordLayout;
    }

    @Override
    public String getContact() {
        return mContactChooserButton.getText().toString();
    }

    @Override
    public String getReminderText() {
        return mTypeMessageEditText.getText().toString();
    }

    @Override
    public String getReminderTime() {
        return mTimeTextView.getText().toString();
    }

    /**
     * 显示录音时间过短的Toast
     */
    @Override
    public Toast showVoiceShortToast() {
        if (toast == null) {
            toast = new Toast(this);
        }
        View view = LayoutInflater.from(this).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }

}
