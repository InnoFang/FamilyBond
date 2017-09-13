package io.innofang.protectplus.login;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;

import io.innofang.protectplus.register.RegisterActivity;

/**
 * Author: Inno Fang
 * Time: 2017/9/13 17:19
 * Description:
 */


public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;

    public LoginPresenter(LoginContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void login(String username, String password) {

    }

    @Override
    public void switchToRegister(Activity activity, FloatingActionButton fab) {
        activity.getWindow().setExitTransition(null);
        activity.getWindow().setEnterTransition(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(activity, fab, fab.getTransitionName());
            activity.startActivity(new Intent(activity, RegisterActivity.class), options.toBundle());
        } else {
            activity.startActivity(new Intent(activity, RegisterActivity.class));
        }
    }
}
