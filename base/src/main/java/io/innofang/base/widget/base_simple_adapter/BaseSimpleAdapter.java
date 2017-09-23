package io.innofang.base.widget.base_simple_adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Author: Inno Fang
 * Time: 2017/9/9 21:11
 * Description:
 */


public abstract class BaseSimpleAdapter<T> extends RecyclerView.Adapter<BaseSimpleViewHolder> {


    private Context mContext;
    private List<T> mList;
    @LayoutRes
    private int mLayoutResId;

    public BaseSimpleAdapter(Context context, @LayoutRes int id, List<T> list) {
        mContext = context;
        mLayoutResId = id;
        mList = list;
    }


    public List<T> getList() {
        return mList;
    }

    public void setList(List<T> list) {
        mList = list;
    }

    public void addItem(T t) {
        mList.add(t);
        notifyItemInserted(mList.size() - 1);
    }

    public void delItem(T t) {
        int position = mList.indexOf(t);
        if (-1 != position) {
            mList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public BaseSimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BaseSimpleViewHolder.getViewHolder(mContext, parent, mLayoutResId);
    }

    @Override
    public void onBindViewHolder(BaseSimpleViewHolder holder, int position) {
        bindViewHolder(holder, getList().get(position), position);
    }

    @Override
    public int getItemCount() {
        return getList() == null ? 0 : getList().size();
    }

    protected abstract void bindViewHolder(BaseSimpleViewHolder viewHolder, T t, int position);
}