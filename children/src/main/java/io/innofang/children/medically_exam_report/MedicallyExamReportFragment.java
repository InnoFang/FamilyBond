package io.innofang.children.medically_exam_report;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/13 16:35
 * Description:
 */


public class MedicallyExamReportFragment extends Fragment {

    public static MedicallyExamReportFragment newInstance() {
        return new MedicallyExamReportFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medically_exam_report, container, false);
    }
}
