package io.innofang.parents;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.SMSModel;
import io.innofang.base.bean.User;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.utils.common.BottomNavigationViewHelper;
import io.innofang.base.utils.common.L;
import io.innofang.base.utils.common.RequestPermissions;
import io.innofang.parents.medically_exam.MedicallyExamFragment;
import io.innofang.parents.reminder.ReminderFragment;
import io.innofang.parents.sms.SmsFragment;
import io.innofang.sms_intercept.SMSModelUtil;
import io.innofang.xfyun.XFYunUtil;

@Route(path = "/parents/1")
public class ParentsActivity extends BaseActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R2.id.view_pager)
    ViewPager mViewPager;
    @BindView(R2.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    private MenuItem mMenuItem;

    private int[] stringIds = {
            R.string.sms,
            R.string.medically_exam,
            R.string.reminder
    };

    private int[] drawableIds = {
            R.drawable.ic_sms,
            R.drawable.ic_medically_exam,
            R.drawable.ic_reminder,
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
        list.add(MedicallyExamFragment.newInstance());
        list.add(ReminderFragment.newInstance());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), list);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(1);


        if (BmobUser.getCurrentUser(User.class).getContact().isEmpty()) {
            startActivity(new Intent(this, AddContactActivity.class));
        }
        checkConnect();
    }

    private void checkConnect() {

        BmobUtil.connect(BmobUser.getCurrentUser(User.class), new BmobEvent.onConnectListener() {
            @Override
            public void connectSuccessful(User user) {
                //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                EventBus.getDefault().post(new BmobIMMessage());
                //会话：更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                BmobIM.getInstance().
                        updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                user.getUsername(), null));
            }

            @Override
            public void connectFailed(String error) {
                toast(error);
            }
        });
        //连接：监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                toast(status.getMsg());
                L.i(BmobIM.getInstance().getCurrentStatus().getMsg());
            }
        });


        XFYunUtil.build(this).setSpeed("20").speak("欢迎使用家宝，祝您使用愉快");

        RequestPermissions.requestRuntimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new RequestPermissions.OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                if (!SMSModelUtil.isModelFilesExit()) {
                    BmobQuery<SMSModel> query = new BmobQuery<>();
                    query.findObjects(new FindListener<SMSModel>() {
                        @Override
                        public void done(List<SMSModel> list, BmobException e) {
                            for (SMSModel smsModel : list) {
                                download(smsModel);
                            }
                        }
                    });
                }
            }

            @Override
            public void onDenied(List<String> deniedPermission) {

            }
        });
    }

    private void download(SMSModel smsModel) {
        File dir = new File(SMSModelUtil.DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String fileName = SMSModelUtil.getFileName(smsModel.getFile().getFilename());
        if ("".equals(fileName))
            return;

        File saveFile = new File(SMSModelUtil.DIRECTORY, fileName);
        smsModel.getFile().download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (null == e) {
                    L.i("download", "success: " + fileName);
                } else {
                    L.i("download", "failed: " + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

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
            case R2.id.item_reminder:
                mViewPager.setCurrentItem(2);
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

    private void tips(@StringRes int strId, @DrawableRes int imgId) {
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
}
