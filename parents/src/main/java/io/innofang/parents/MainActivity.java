package io.innofang.parents;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.User;
import io.innofang.base.util.bmob.BmobEvent;
import io.innofang.base.util.bmob.BmobUtil;
import io.innofang.base.util.common.BottomNavigationViewHelper;
import io.innofang.medically.heat_beat.HeartBeatOldFragment;
import io.innofang.parents.communication.CommunicationFragment;
import io.innofang.parents.home.HomeFragment;
import io.innofang.parents.settings.SettingsFragment;
import io.innofang.parents.sms.SmsFragment;

@Route(path = "/parents/1")
public class MainActivity extends BaseActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R2.id.view_pager)
    ViewPager mViewPager;
    @BindView(R2.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    private MenuItem mMenuItem;

    private int[] stringIds = {
            R.string.sms,
            R.string.medically_exam,
            R.string.home,
            R.string.communication,
            R.string.settings
    };

    private int[] drawableIds = {
            R.drawable.ic_sms,
            R.drawable.ic_medically_exam,
            R.drawable.ic_home,
            R.drawable.ic_communication,
            R.drawable.ic_settings
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (null != mMenuItem) {
                    mMenuItem.setChecked(false);
                } else {
                    mBottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                mMenuItem = mBottomNavigationView.getMenu().getItem(position);
                mMenuItem.setChecked(true);
                tips(stringIds[position], drawableIds[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        List<Fragment> list = new ArrayList<>();
        list.add(SmsFragment.newInstance());
        list.add(HeartBeatOldFragment.newInstance());
        list.add(HomeFragment.newInstance());
        list.add(CommunicationFragment.newInstance());
        list.add(SettingsFragment.newInstance());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), list);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(2);

        checkConnect();
    }

    private void checkConnect() {

        BmobUtil.connect(BmobUser.getCurrentUser(User.class), new BmobEvent.onConnectListener() {
            @Override
            public void connectSuccessful(User user) {
                //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                EventBus.getDefault().post(new BmobIMMessage());
                //TODO 会话：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                BmobIM.getInstance().
                        updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                user.getUsername(), null));
            }

            @Override
            public void connectFailed(String error) {
                toast(error);
            }
        });
        //TODO 连接：3.3、监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                toast(status.getMsg());
                Log.i("tag", BmobIM.getInstance().getCurrentStatus().getMsg());
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R2.id.item_sms:
                mViewPager.setCurrentItem(0);
                break;
            case R2.id.item_medically_exam:
                mViewPager.setCurrentItem(1);
                break;
            case R2.id.item_home:
                mViewPager.setCurrentItem(2);
                break;
            case R2.id.item_communication:
                mViewPager.setCurrentItem(3);
                break;
            case R2.id.item_settings:
                mViewPager.setCurrentItem(4);
                break;
            default:
                break;
        }
        return false;
    }

    Toast mToast;
    View mToastView;
    TextView mToastTextView;
    ImageView mToastImageView;

    private void showToast(@StringRes int strId, @DrawableRes int imgId) {
        if (null == mToast) {
            mToast = new Toast(this);
            mToastView = LayoutInflater.from(this).inflate(R.layout.layout_toast, null);
            mToast.setView(mToastView);
            mToastTextView = (TextView) mToastView.findViewById(R.id.toast_text_view);
            mToastImageView = (ImageView) mToastView.findViewById(R.id.toast_image_view);
        }
        mToastTextView.setText(strId);
        mToastImageView.setImageResource(imgId);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }


    private void tips(@StringRes int strId, @DrawableRes int imgId) {
        if (null == mToast) {
            mToast = Toast.makeText(this, strId, Toast.LENGTH_SHORT);
            mToastImageView = new ImageView(this);
            ((LinearLayout)mToast.getView()).addView(mToastImageView, 0);
        }
        mToast.setText(strId);
        mToastImageView.setImageResource(imgId);
        mToast.show();
    }
}
