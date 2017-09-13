package io.innofang.protectplus.register;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;

import io.innofang.base.base.BasePresenter;
import io.innofang.base.base.BaseView;
import io.innofang.base.bean.User;

/**
 * Author: Inno Fang
 * Time: 2017/9/13 16:56
 * Description:
 */


public class RegisterContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {
        void showEnterAnimation(Activity activity, CardView cardView, FloatingActionButton fab);

        void animateRevealShow(CardView cardView, FloatingActionButton fab);

        void animateRevealClose(Activity activity, CardView cardView, FloatingActionButton fab);

        void register(User user);
    }
}
