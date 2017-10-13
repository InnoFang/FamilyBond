package io.innofang.children.medically_exam;

import android.support.v4.app.Fragment;

import io.innofang.base.base.FragmentContainerActivity;
import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/13 16:34
 * Description:
 */


public class MedicallyExamActivity extends FragmentContainerActivity {
    @Override
    protected Fragment createFragment() {
        return MedicallyExamFragment.newInstance();
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
