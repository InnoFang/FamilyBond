package io.innofang.children.medically_exam_report;

import android.support.v4.app.Fragment;

import io.innofang.base.base.FragmentContainerActivity;
import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/13 16:34
 * Description:
 */


public class MedicallyExamReportActivity extends FragmentContainerActivity {
    @Override
    protected Fragment createFragment() {
        return MedicallyExamReportFragment.newInstance();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }
}
