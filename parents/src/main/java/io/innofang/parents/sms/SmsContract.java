package io.innofang.parents.sms;

import android.content.Context;

import io.innofang.base.base.BasePresenter;
import io.innofang.base.base.BaseView;
import io.innofang.base.bean.SMS;

/**
 * Author: Inno Fang
 * Time: 2017/10/12 15:49
 * Description:
 */


public class SmsContract {

    interface View extends BaseView<Presenter> {
        Context getContext();
        void showInfo(String text);
    }

    interface Presenter extends BasePresenter {
        void sendToChildren(SMS sms);
    }
}
