package io.innofang.protectplus.login;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;

import io.innofang.base.base.BasePresenter;
import io.innofang.base.base.BaseView;

/**
 * Author: Inno Fang
 * Time: 2017/9/13 17:18
 * Description:
 */


public class LoginContract {

    interface View extends BaseView<Presenter>{

    }

    interface Presenter extends BasePresenter {
        void login(String username, String password);

        void switchToRegister(Activity activity, FloatingActionButton fab);
    }

}
