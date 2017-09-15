package io.innofang.parents.medically_exam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.innofang.parents.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 09:14
 * Description:
 */


public class MedicallyExamFragment extends Fragment{

    public static MedicallyExamFragment newInstance() {
        return Holder.sInstance;
    }

    private static class Holder {
        private static final MedicallyExamFragment sInstance = new MedicallyExamFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medically_exam, container, false);
        return view;
    }
}
