package io.innofang.children.voice_reminder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.innofang.base.base.BaseActivity;
import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/16 15:04
 * Description:
 */


public class VoiceReminderActivity extends BaseActivity {

    @BindView(R.id.contact_chooser_button)
    Button mContactChooserButton;
    @BindView(R.id.type_message_edit_text)
    EditText mTypeMessageEditText;
    @BindView(R.id.speak_fab)
    FloatingActionButton mSpeakFab;
    @BindView(R.id.time_text_view)
    TextView mTimeTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_reminder);
        ButterKnife.bind(this);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.CHINA);//设置日期格式
        mTimeTextView.setText(df.format(new Date()));
    }

    @OnClick({R.id.contact_chooser_button, R.id.speak_fab, R.id.time_text_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.contact_chooser_button:
                break;
            case R.id.speak_fab:
                break;
            case R.id.time_text_view:
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(),getString(R.string.reminder_time));
                break;
        }
    }

}
