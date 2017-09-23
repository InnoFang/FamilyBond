package io.innofang.children.reminder.text_reminder;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;

import io.innofang.base.base.BasePresenter;
import io.innofang.base.base.BaseView;

/**
 * Author: Inno Fang
 * Time: 2017/9/23 15:11
 * Description:
 */


public class TextReminderContract {

    interface View extends BaseView<Presenter> {
        FloatingActionButton getSendReminderButton();
        Context getContext();
        void showInfo(String text);
        String getReminderText();
    }

    interface Presenter extends BasePresenter {
        void chooseContact();
        void sendReminder();
    }
}
