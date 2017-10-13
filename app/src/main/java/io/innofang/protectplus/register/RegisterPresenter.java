package io.innofang.protectplus.register;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;

import cn.bmob.v3.exception.BmobException;
import io.innofang.base.bean.User;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.protectplus.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/13 17:01
 * Description:
 */


public class RegisterPresenter implements RegisterContract.Presenter {

    private RegisterContract.View mView;
    private Activity mActivity;

    public RegisterPresenter(RegisterContract.View view, Activity activity) {
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
    public void register(final String username, final String password, final String repeatPassword, String client) {
        final User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setClient(client);
        BmobUtil.register(user, new BmobEvent.onRegisterListener() {
            @Override
            public boolean beforeRegister() {
                if (!TextUtils.isEmpty(username)
                        && !TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(repeatPassword)) {
                    if (password.equals(repeatPassword)) {
                        mView.beforeRegister();
                        return true;
                    }
                    mView.showInfo(mActivity.getString(R.string.username_or_password_cannot_be_empty));
                    return false;
                }
                mView.showInfo(mActivity.getString(R.string.password_mismatch));
                return false;
            }

            @Override
            public void registerSuccessful(User user) {
                mView.registerSuccessful();
            }

            @Override
            public void registerFailed(BmobException e) {
                mView.registerFailed(e.getMessage());
            }
        });
    }

    @Override
    public void showEnterAnimation(final CardView cardView, final FloatingActionButton fab) {
        Transition transition = TransitionInflater.from(mActivity).inflateTransition(R.transition.fabtransition);
        mActivity.getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cardView.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow(cardView, fab);
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

    @Override
    public void animateRevealShow(final CardView cardView, FloatingActionButton fab) {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(
                cardView, cardView.getWidth() / 2, 0,
                fab.getWidth() / 2, cardView.getHeight());
        mAnimator.setDuration(300);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cardView.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void animateRevealClose(final CardView cardView, final FloatingActionButton fab) {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(
                cardView, cardView.getWidth() / 2, 0,
                cardView.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(300);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cardView.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.ic_login);
                mActivity.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
}
