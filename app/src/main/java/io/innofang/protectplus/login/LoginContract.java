package io.innofang.protectplus.login;

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
        void showInfo(String text);
        void beforeLogin();
        void loginSuccessful();
        void loginFailed(String text);
    }

    interface Presenter extends BasePresenter {
        void login(String username, String password);

        void switchToRegister(FloatingActionButton fab);
    }

}
