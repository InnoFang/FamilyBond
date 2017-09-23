package io.innofang.children.reminder.voice_reminder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.innofang.base.base.BasePresenter;
import io.innofang.base.base.BaseView;

/**
 * Author: Inno Fang
 * Time: 2017/9/23 16:07
 * Description:
 */


public class VoiceReminderContract {

    interface View extends BaseView<Presenter> {
        void showInfo(String text);

        TextView getVoiceTipsTextView();

        FloatingActionButton getVoiceRecordFab();

        RelativeLayout getVoiceLayout();

        ImageView getRecordImageView();

        void setVoiceTipsText(@StringRes int id);

        Drawable[] getVoiceAnimDrawable();

        Toast showVoiceShortToast();

        Context getContext();

        void setContact(String contact);
    }

    interface Presenter extends BasePresenter {
        void chooseContact();

        void initRecordManager();
    }

}
