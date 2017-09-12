package io.innofang.protectplus.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;

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


public class RegisterActivity extends BaseActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
    }

    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                mRegisterCardView.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(mRegisterCardView, mRegisterCardView.getWidth() / 2, 0, mSwitchFab.getWidth() / 2, mRegisterCardView.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mRegisterCardView.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(mRegisterCardView, mRegisterCardView.getWidth() / 2, 0, mRegisterCardView.getHeight(), mSwitchFab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterCardView.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                mSwitchFab.setImageResource(R.drawable.ic_login);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    @OnClick({R.id.next_button, R.id.switch_fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next_button:
                animateRevealClose();
                toast(R.string.register_success);
                break;
            case R.id.switch_fab:
                animateRevealClose();
                break;
        }
    }
}
