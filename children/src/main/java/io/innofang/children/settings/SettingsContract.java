package io.innofang.children.settings;

import android.content.Context;

import io.innofang.base.base.BasePresenter;
import io.innofang.base.base.BaseView;
import io.innofang.base.bean.User;

/**
 * Author: Inno Fang
 * Time: 2017/9/16 21:35
 * Description:
 */


public class SettingsContract {

    interface View extends BaseView<Presenter> {
        void showInfo(String text);

        void showLoading(boolean show);
    }

    interface Presenter extends BasePresenter {
        void showPopup(Context context, User user);
    }

}
