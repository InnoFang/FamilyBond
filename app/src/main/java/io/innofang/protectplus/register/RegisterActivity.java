package io.innofang.protectplus.register;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.User;
import io.innofang.base.utils.common.CircularAnimUtils;
import io.innofang.protectplus.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/12 20:44
 * Description:
 */


public class RegisterActivity extends BaseActivity implements RegisterContract.View, View.OnClickListener {

    EditText mRegisterUsernameEditText;
    EditText mRegisterPasswordEditText;
    EditText mRegisterRepeatPasswordEditText;
    Button mNextButton;
    CardView mRegisterCardView;
    FloatingActionButton mSwitchFab;
    ProgressBar mRegisterProgressBar;
    AppCompatRadioButton mChildrenClientRadioButton;
    AppCompatRadioButton mParentsClientRadioButton;

    private RegisterContract.Presenter mPresenter;


    void initView() {
        mRegisterUsernameEditText = (EditText) findViewById(R.id.register_username_edit_text);
        mRegisterPasswordEditText = (EditText) findViewById(R.id.register_password_edit_text);
        mRegisterRepeatPasswordEditText = (EditText) findViewById(R.id.register_repeat_password_edit_text);
        mNextButton = (Button) findViewById(R.id.next_button);
        mRegisterCardView = (CardView) findViewById(R.id.register_card_view);
        mSwitchFab = (FloatingActionButton) findViewById(R.id.switch_fab);
        mRegisterProgressBar = (ProgressBar) findViewById(R.id.register_progress_bar);
        mChildrenClientRadioButton = (AppCompatRadioButton) findViewById(R.id.children_client_radio_button);
        mParentsClientRadioButton = (AppCompatRadioButton) findViewById(R.id.parents_client_radio_button);

        mNextButton.setOnClickListener(this);
        mSwitchFab.setOnClickListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        mPresenter = new RegisterPresenter(this, this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPresenter.showEnterAnimation(mRegisterCardView, mSwitchFab);
        }
    }

    @Override
    public void onBackPressed() {
        mPresenter.animateRevealClose(mRegisterCardView, mSwitchFab);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_button:
                mPresenter.register(
                        mRegisterUsernameEditText.getText().toString().trim(),
                        mRegisterPasswordEditText.getText().toString().trim(),
                        mRegisterRepeatPasswordEditText.getText().toString().trim(),
                        mChildrenClientRadioButton.isChecked() ? User.CHILDREN : User.PARENTS
                );
                toast(R.string.register_success);
                break;
            case R.id.switch_fab:
                mPresenter.animateRevealClose(mRegisterCardView, mSwitchFab);
                break;
        }
    }

    @Override
    public void showInfo(String text) {
        Snackbar.make(mRegisterCardView, text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void beforeRegister() {
        mRegisterProgressBar.setVisibility(View.VISIBLE);
        CircularAnimUtils.hide(mNextButton);
    }

    @Override
    public void registerSuccessful() {
        toast(R.string.register_success);
        finish();
    }

    @Override
    public void registerFailed(String text) {
        showInfo(text);
        mRegisterProgressBar.setVisibility(View.GONE);
        CircularAnimUtils.show(mNextButton);
    }

    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        mPresenter = presenter;
    }

}
