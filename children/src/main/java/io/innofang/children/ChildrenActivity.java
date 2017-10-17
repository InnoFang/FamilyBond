package io.innofang.children;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.ViewStub;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.User;
import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.bean.greendao.BpmDao;
import io.innofang.base.bean.greendao.DaoSession;
import io.innofang.base.configure.GreenDaoConfig;
import io.innofang.base.widget.card_view_pager.ShadowTransformer;
import io.innofang.children.map.MapActivity;
import io.innofang.children.medically_exam_report.MedicallyExamReportActivity;
import io.innofang.children.reminder.ReminderActivity;
import io.innofang.children.settings.SettingsActivity;
import io.innofang.children.sms_intercept.SMSInterceptionActivity;

@Route(path = "/children/1")
public class ChildrenActivity extends BaseActivity {


    @BindView(R2.id.card_view_pager)
    ViewPager mCardViewPager;
    @BindView(R2.id.bpm_text_view)
    TextView mBpmTextView;
    @BindView(R2.id.tips_text_view)
    TextView mTipsTextView;
    @BindView(R2.id.view_stub)
    ViewStub mViewStub;
    @BindView(R2.id.time_text_view)
    TextView mTimeTextView;
    @BindView(R2.id.description_text_view)
    TextView mDescriptionTextView;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    private DaoSession mDaoSession;
    private BpmDao mBpmDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);
        ButterKnife.bind(this);

        // start handle message service
        Intent startIntent = new Intent(this, HandleMessageService.class);
        startService(startIntent);

        mDaoSession = GreenDaoConfig.getInstance().getDaoSession();
        mBpmDao = mDaoSession.getBpmDao();

        init();
        showAddContactTip();

    }

    @Override
    public void onResume() {
        super.onResume();
        // 展示主屏信息
        showMainInfo();
    }

    private void showMainInfo() {
        List<Bpm> list = mBpmDao.queryBuilder().orderAsc(BpmDao.Properties.Id).build().list();

        if (!list.isEmpty()) {
            Bpm bpm = list.get(list.size() - 1);
            mBpmTextView.setText(bpm.getBpm());
            mTimeTextView.setText(getString(R.string.bpm_time, bpm.getTime()));
            mDescriptionTextView.setText(bpm.getDescription());
            mTipsTextView.setText(R.string.remind_parents_tips);
            if (list.size() >= 2) {
                // 最后一次测量
                int lastOne = Integer.parseInt(list.get(list.size() - 1).getBpm());
                // 倒数第二次测量
                int lastTwo = Integer.parseInt(list.get(list.size() - 2).getBpm());
                String text = "";
                if (lastOne > lastTwo) {
                    int increase = lastOne - lastTwo;
                    text = getString(R.string.bpm_increase_tips, increase + "bpm");
                } else if (lastOne < lastTwo) {
                    int decrease = lastTwo - lastOne;
                    text = getString(R.string.bpm_decrease_tips, decrease + "bpm");
                } else {
                    text = getString(R.string.bpm_no_change_tips);
                }
                mTipsTextView.setText(text);
            }
        } else {
            mBpmTextView.setText("00");
            mTipsTextView.setText(R.string.remind_parents_tips);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop handle message service
        Intent stopIntent = new Intent(this, HandleMessageService.class);
        stopService(stopIntent);
    }

    private void init() {
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.sms_interception, R.drawable.sms_interception));
        mCardAdapter.addCardItem(new CardItem(R.string.message_reminder, R.drawable.voice_reminder));
        mCardAdapter.addCardItem(new CardItem(R.string.medically_exam_report, R.drawable.medically_exam));
        mCardAdapter.addCardItem(new CardItem(R.string.location, R.drawable.map));
        mCardAdapter.addCardItem(new CardItem(R.string.common_settings, R.drawable.settings));

        mCardShadowTransformer = new ShadowTransformer(mCardViewPager, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);
        mCardViewPager.setAdapter(mCardAdapter);
        mCardViewPager.setPageTransformer(false, mCardShadowTransformer);
        mCardViewPager.setOffscreenPageLimit(3);
        mCardViewPager.setCurrentItem(2);
        mCardAdapter.setOnItemClickListener(new CardPagerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(ChildrenActivity.this, SMSInterceptionActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(ChildrenActivity.this, ReminderActivity.class));
                        break;
                    case 2:
                        if (!mBpmDao.queryBuilder().build().list().isEmpty()) {
                            startActivity(new Intent(ChildrenActivity.this, MedicallyExamReportActivity.class));
                        } else {
                            toast("还没有数据，通知你的家人进行测量吧");
                        }

                        break;
                    case 3:
                        startActivity(new Intent(ChildrenActivity.this, MapActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(ChildrenActivity.this, SettingsActivity.class));
                        break;
                }
            }
        });
    }

    private void showAddContactTip() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user.getContact().isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.add_contact)
                    .setMessage(R.string.tip_of_add_contact)
                    .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(ChildrenActivity.this, SettingsActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }
    }
}
