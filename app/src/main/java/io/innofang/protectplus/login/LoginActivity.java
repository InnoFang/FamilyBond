package io.innofang.protectplus.login;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;

import cn.bmob.v3.BmobUser;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.User;
import io.innofang.base.utils.common.CircularAnimUtils;
import io.innofang.protectplus.R;

public class LoginActivity extends BaseActivity implements LoginContract.View, View.OnClickListener {

    EditText mLoginUsernameEditText;
    EditText mLoginPasswordEditText;
    Button mLoginButton;
    TextView mForgetPasswordTextView;
    CardView mLoginCardView;
    FloatingActionButton mSwitchFab;
    ProgressBar mLoginInProgressBar;


    private LoginContract.Presenter mPresenter;

    void initView() {
        mLoginUsernameEditText = (EditText) findViewById(R.id.login_username_edit_text);
        mLoginPasswordEditText = (EditText) findViewById(R.id.login_password_edit_text);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mForgetPasswordTextView = (TextView) findViewById(R.id.forget_password_text_view);
        mLoginCardView = (CardView) findViewById(R.id.login_card_view);
        mSwitchFab = (FloatingActionButton) findViewById(R.id.switch_fab);
        mLoginInProgressBar = (ProgressBar) findViewById(R.id.login_in_progress_bar);

        mLoginButton.setOnClickListener(this);
        mForgetPasswordTextView.setOnClickListener(this);
        mSwitchFab.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        mPresenter = new LoginPresenter(this, this);

        User user;
        if ((user = BmobUser.getCurrentUser(User.class)) != null) {
            if (user.getClient().equals(User.CHILDREN)) {
                ARouter.getInstance().build("/children/1").navigation();
            } else {
                ARouter.getInstance().build("/parents/1").navigation();
            }
            finish();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                mPresenter.login(
                        mLoginUsernameEditText.getText().toString().trim(),
                        mLoginPasswordEditText.getText().toString().trim()
                );
                break;
            case R.id.forget_password_text_view:
                toast(R.string.forgot_password);
                break;
            case R.id.switch_fab:
                mPresenter.switchToRegister(mSwitchFab);
                break;
        }
    }

    @Override
    public void showInfo(String text) {
        Snackbar.make(mLoginCardView, text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void beforeLogin() {
        mLoginInProgressBar.setVisibility(View.VISIBLE);
        CircularAnimUtils.hide(mLoginButton);
    }

    @Override
    public void loginSuccessful() {
        toast(R.string.login_successful);
    }

    @Override
    public void loginFailed(String text) {
        showInfo(text);
        mLoginInProgressBar.setVisibility(View.GONE);
        CircularAnimUtils.show(mLoginButton);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

}
