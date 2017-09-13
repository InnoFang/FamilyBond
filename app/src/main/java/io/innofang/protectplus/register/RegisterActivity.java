package io.innofang.protectplus.register;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.innofang.base.base.BaseActivity;
import io.innofang.protectplus.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/12 20:44
 * Description:
 */


public class RegisterActivity extends BaseActivity implements RegisterContract.View {

    @BindView(R.id.register_username_edit_text)
    EditText mRegisterUsernameEditText;
    @BindView(R.id.register_password_edit_text)
    EditText mRegisterPasswordEditText;
    @BindView(R.id.register_repeat_password_edit_text)
    EditText mRegisterRepeatPasswordEditText;
    @BindView(R.id.next_button)
    Button mNextButton;
    @BindView(R.id.register_card_view)
    CardView mRegisterCardView;
    @BindView(R.id.switch_fab)
    FloatingActionButton mSwitchFab;
    @BindView(R.id.register_progress_bar)
    ProgressBar mRegisterProgressBar;

    private RegisterContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mPresenter = new RegisterPresenter(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPresenter.showEnterAnimation(this, mRegisterCardView, mSwitchFab);
        }
    }

    @Override
    public void onBackPressed() {
        mPresenter.animateRevealClose(this, mRegisterCardView, mSwitchFab);
    }

    @OnClick({R.id.next_button, R.id.switch_fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next_button:
                mPresenter.animateRevealClose(this, mRegisterCardView, mSwitchFab);
                toast(R.string.register_success);
                break;
            case R.id.switch_fab:
                mPresenter.animateRevealClose(this, mRegisterCardView, mSwitchFab);
                break;
        }
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
