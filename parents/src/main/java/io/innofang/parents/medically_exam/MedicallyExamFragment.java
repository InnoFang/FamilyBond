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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.innofang.base.util.common.RequestPermissions;
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

    public static MedicallyExamFragment newInstance() {
        return new MedicallyExamFragment();
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
                    ARouter.getInstance().build("/heart_beat/1").navigation();
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


        return view;
    }
}
