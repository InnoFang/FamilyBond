package io.innofang.medically.data_display;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.widget.base_adapter.BaseAdapter;
import io.innofang.medically.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/4 17:52
 * Description:
 */


public class DataDisplayAdapter extends BaseAdapter<DataDisplayViewHolder, Bpm> {

    public DataDisplayAdapter(Context context, List<Bpm> list) {
        super(context, list);
    }

    @Override
    public int getItemLayoutResId() {
        return R.layout.item_data_display;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType, View view) {
        return new DataDisplayViewHolder(view);
    }
}
