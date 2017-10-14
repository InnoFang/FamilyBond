package io.innofang.children.medically_exam_report;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.widget.base_adapter.BaseAdapter;
import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/4 17:52
 * Description:
 */


public class MedicallyExamAdapter extends BaseAdapter<MedicallyExamReportViewHolder, Bpm> {

    public MedicallyExamAdapter(Context context, List<Bpm> list) {
        super(context, list);
    }

    @Override
    public int getItemLayoutResId() {
        return R.layout.item_medically_exam_report;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType, View view) {
        return new MedicallyExamReportViewHolder(view);
    }
}
