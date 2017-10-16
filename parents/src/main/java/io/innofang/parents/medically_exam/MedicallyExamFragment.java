package io.innofang.parents.medically_exam;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.bean.greendao.BpmDao;
import io.innofang.base.bean.greendao.DaoSession;
import io.innofang.base.configure.GreenDaoConfig;
import io.innofang.base.utils.common.RequestPermissions;
import io.innofang.parents.R;
import io.innofang.parents.R2;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 09:14
 * Description:
 */


public class MedicallyExamFragment extends Fragment {

    @BindView(R2.id.bpm_text_view)
    TextView mBpmTextView;
    @BindView(R2.id.tips_text_view)
    TextView mTipsTextView;
    @BindView(R2.id.card_view)
    CardView mCardView;
    Unbinder unbinder;
    @BindView(R2.id.bpm_label)
    TextView mBpmLabel;
    @BindView(R2.id.start_button)
    Button mStartButton;

    private DaoSession mDaoSession;
    private BpmDao mBpmDao;

    public static MedicallyExamFragment newInstance() {
        return new MedicallyExamFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDaoSession = GreenDaoConfig.getInstance().getDaoSession();
        mBpmDao = mDaoSession.getBpmDao();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R2.id.bpm_text_view, R2.id.tips_text_view, R2.id.card_view, R2.id.start_button})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.card_view || id == R.id.start_button) {
            RequestPermissions.requestRuntimePermission(new String[]{
                    Manifest.permission.CAMERA
            }, new RequestPermissions.OnRequestPermissionsListener() {
                @Override
                public void onGranted() {
                    if (!mBpmDao.queryBuilder().build().list().isEmpty()) {
                        ARouter.getInstance().build("/heart_beat/1").navigation();
                    } else {
                        Toast.makeText(getContext(), "还没有数据，通知你的家人进行测量吧", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onDenied(List<String> deniedPermission) {

                }
            });
        } else {
            // FIXME： 语音提示
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medically_exam, container, false);
        unbinder = ButterKnife.bind(this, view);

        mDaoSession = GreenDaoConfig.getInstance().getDaoSession();
        mBpmDao = mDaoSession.getBpmDao();
        updateInfo();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
    }

    private void updateInfo() {
        Query<Bpm> bpmQuery = mBpmDao.queryBuilder().orderAsc(BpmDao.Properties.Id).build();
        List<Bpm> list = bpmQuery.list();
        if (!list.isEmpty()) {
            mBpmLabel.setVisibility(View.VISIBLE);
            mBpmTextView.setText(list.get(list.size() - 1).getBpm());
            if (list.size() > 2) {
                // 最后一次测量
                int lastOne = Integer.parseInt(list.get(list.size() - 1).getBpm());
                // 倒数第二次测量
                int lastTwo = Integer.parseInt(list.get(list.size() - 2).getBpm());
                String text = "";
                if (lastOne > lastTwo) {
                    double increase = (lastOne - lastTwo) / lastTwo * 1.0;
                    text = getString(R.string.bpm_increase_tips, ((int) increase * 100) + "%");
//                    text = String.format("心率同比增长%d%，注意适当休息，保持良好心情。", ((int) increase * 100));
                } else if (lastOne < lastTwo) {
                    double increase = (lastTwo - lastOne) / lastTwo * 1.0;
                    text = getString(R.string.bpm_decrease_tips, ((int) increase * 100) + "%");
//                    text = String.format("心率同比降低%d%，可以外出保持活力，保持良好心情。", ((int) increase * 100));
                } else {
                    text = getString(R.string.bpm_no_change_tips);
//                    text = "最近心率变化不大，可以适当休息或外出活动，保持良好心情";
                }
                mTipsTextView.setText(text);
            }
        } else {
            mBpmLabel.setVisibility(View.INVISIBLE);
            mTipsTextView.setText("还没有测试记录，点击卡片进行测试");
        }
    }
}
