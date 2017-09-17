package io.innofang.protectplus.login;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.User;
import io.innofang.base.util.common.CircularAnimUtils;
import io.innofang.protectplus.R;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    @BindView(R.id.login_username_edit_text)
    EditText mLoginUsernameEditText;
    @BindView(R.id.login_password_edit_text)
    EditText mLoginPasswordEditText;
    @BindView(R.id.login_button)
    Button mLoginButton;
    @BindView(R.id.forget_password_text_view)
    TextView mForgetPasswordTextView;
    @BindView(R.id.login_card_view)
    CardView mLoginCardView;
    @BindView(R.id.switch_fab)
    FloatingActionButton mSwitchFab;
    @BindView(R.id.login_in_progress_bar)
    ProgressBar mLoginInProgressBar;


    private LoginContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mPresenter = new LoginPresenter(this, this);

        User user = null;
        if ((user = BmobUser.getCurrentUser(User.class)) != null) {
            if (user.getClient().equals(User.CHILDREN)) {
                ARouter.getInstance().build("/children/1").navigation();
            } else {
                ARouter.getInstance().build("/parents/1").navigation();
            }
            finish();
        }
    }

    @OnClick({R.id.login_button, R.id.forget_password_text_view, R.id.switch_fab})
    public void onViewClicked(View view) {
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
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
