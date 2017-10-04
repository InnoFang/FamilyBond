package io.innofang.base.widget.base_adapter;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Author: Inno Fang
 * Time: 2017/10/4 17:48
 * Description:
 */



public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    private View mItemView;

    public BaseViewHolder(View itemView) {
        super(itemView);
        mItemView = itemView;
    }

    public abstract void bindHolder(T model);

    public View find(@IdRes int id) {
        return mItemView.findViewById(id);
    }

}