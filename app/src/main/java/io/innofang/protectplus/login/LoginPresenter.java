package io.innofang.protectplus.login;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;

import cn.bmob.v3.exception.BmobException;
import io.innofang.base.bean.User;
import io.innofang.base.util.bmob.BmobEvent;
import io.innofang.base.util.bmob.BmobUtil;
import io.innofang.protectplus.R;
import io.innofang.protectplus.register.RegisterActivity;

/**
 * Author: Inno Fang
 * Time: 2017/9/13 17:19
 * Description:
 */


public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;
    private Activity mActivity;

    public LoginPresenter(LoginContract.View view, Activity activity) {
        mView = view;
        mActivity = activity;
        mView.setPresenter(this);
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void login(final String username, final String password) {
        BmobUtil.login(username, password, new BmobEvent.onLoginListener() {
            @Override
            public boolean beforeLogin() {
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    mView.beforeLogin();
                    return true;
                }
                mView.showInfo(mActivity.getString(R.string.username_or_password_cannot_be_empty));
                return false;
            }

            @Override
            public void loginSuccessful(User user) {
                mView.loginSuccessful();
                if (user.getClient() == User.CHILDREN) {
                    ARouter.getInstance().build("/children/1").navigation();
                    mView.showInfo("Children");
                } else {
                    ARouter.getInstance().build("/parents/1").navigation();
                    mView.showInfo("Parents");
                }
            }

            @Override
            public void loginFailed(BmobException e) {
                mView.loginFailed(e.getMessage());
            }
        });
    }

    @Override
    public void switchToRegister(FloatingActionButton fab) {
        mActivity.getWindow().setExitTransition(null);
        mActivity.getWindow().setEnterTransition(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(mActivity, fab, fab.getTransitionName());
            mActivity.startActivity(new Intent(mActivity, RegisterActivity.class), options.toBundle());
        } else {
            mActivity.startActivity(new Intent(mActivity, RegisterActivity.class));
        }
    }
}
