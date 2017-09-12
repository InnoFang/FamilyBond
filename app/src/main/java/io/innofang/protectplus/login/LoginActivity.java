package io.innofang.protectplus.login;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.util.CircularAnimUtils;
import io.innofang.protectplus.R;

public class LoginActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.login_button, R.id.forget_password_text_view, R.id.switch_fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                toast(R.string.login_success);
                mLoginInProgressBar.setVisibility(View.VISIBLE);
                CircularAnimUtils.hide(mLoginButton);
                break;
            case R.id.forget_password_text_view:
                break;
            case R.id.switch_fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, mSwitchFab, mSwitchFab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mLoginInProgressBar.getVisibility() == View.VISIBLE) {
            mLoginInProgressBar.setVisibility(View.GONE);
            CircularAnimUtils.show(mLoginButton);
        } else {
            super.onBackPressed();
        }
    }
}
