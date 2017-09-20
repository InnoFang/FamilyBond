package io.innofang.children.voice_reminder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.innofang.base.base.BasePresenter;
import io.innofang.base.base.BaseView;

/**
 * Author: Inno Fang
 * Time: 2017/9/16 17:57
 * Description:
 */


public class VoiceReminderContract {

    interface View extends BaseView<Presenter>{
        void showInfo(String text);
        TextView getVoiceTipsTextView();
        FloatingActionButton getSpeakFab();
        ImageView getRecordImageView();
        RelativeLayout getVoiceLayout();
        void setVoiceTipsText(@StringRes int id);
        Drawable[] getVoiceAnimDrawable();
        Toast showVoiceShortToast();
        String getContact();
        String getReminderText();
    }

    interface Presenter extends BasePresenter {
        void chooseContact(Context context, Button button);
        void initRecordManager(Context context);
        void sendReminder();
        void playRecordVoice();
    }
}
