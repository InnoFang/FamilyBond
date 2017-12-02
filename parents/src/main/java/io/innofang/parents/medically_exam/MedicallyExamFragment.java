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

import com.alibaba.android.arouter.launcher.ARouter;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.bean.greendao.BpmDao;
import io.innofang.base.bean.greendao.DaoSession;
import io.innofang.base.configure.GreenDaoConfig;
import io.innofang.base.utils.common.RequestPermissions;
import io.innofang.parents.R;
import io.innofang.xfyun.XFYunUtil;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 09:14
 * Description:
 */


public class MedicallyExamFragment extends Fragment implements View.OnClickListener {

    TextView mBpmTextView;
    TextView mTipsTextView;
    CardView mCardView;
    TextView mBpmLabel;
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

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.card_view || id == R.id.start_button) {
            RequestPermissions.requestRuntimePermission(new String[]{
                    Manifest.permission.CAMERA
            }, new RequestPermissions.OnRequestPermissionsListener() {
                @Override
                public void onGranted() {
                    ARouter.getInstance().build("/heart_beat/1").navigation();
                }

                @Override
                public void onDenied(List<String> deniedPermission) {

                }
            });
        } else if (id == R.id.bpm_text_view) {
            XFYunUtil.build(getContext()).speak(mBpmTextView.getText().toString() + " bpm");
        } else if (id == R.id.tips_text_view) {
            XFYunUtil.build(getContext()).speak(mTipsTextView.getText().toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medically_exam, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBpmTextView = (TextView) view.findViewById(R.id.bpm_text_view);
        mTipsTextView = (TextView) view.findViewById(R.id.tips_text_view);
        mCardView = (CardView) view.findViewById(R.id.card_view);
        mBpmLabel = (TextView) view.findViewById(R.id.bpm_label);
        mStartButton = (Button) view.findViewById(R.id.start_button);

        mBpmTextView.setOnClickListener(this);
        mTipsTextView.setOnClickListener(this);
        mCardView.setOnClickListener(this);
        mStartButton.setOnClickListener(this);


        mDaoSession = GreenDaoConfig.getInstance().getDaoSession();
        mBpmDao = mDaoSession.getBpmDao();
        updateInfo();
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
            if (list.size() >= 2) {
                // 最后一次测量
                int lastOne = Integer.parseInt(list.get(list.size() - 1).getBpm());
                // 倒数第二次测量
                int lastTwo = Integer.parseInt(list.get(list.size() - 2).getBpm());
                String text = "";
                if (lastOne > lastTwo) {
                    int increase = lastOne - lastTwo;
                    text = getString(R.string.bpm_increase_tips, increase + " bpm");
                } else if (lastOne < lastTwo) {
                    int decrease = lastTwo - lastOne;
                    text = getString(R.string.bpm_decrease_tips, decrease + " bpm");
                } else {
                    text = getString(R.string.bpm_no_change_tips);
                }
                mTipsTextView.setText(text);
            }
        } else {
            mBpmLabel.setVisibility(View.INVISIBLE);
            mTipsTextView.setText("还没有测试记录，点击卡片进行测试");
        }
    }
}
