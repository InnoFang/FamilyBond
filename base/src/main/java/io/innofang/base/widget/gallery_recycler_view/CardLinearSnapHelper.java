package io.innofang.base.widget.gallery_recycler_view;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Author: Inno Fang
 * Time: 2017/9/9 20:47
 * Description:
 */


public class CardLinearSnapHelper extends LinearSnapHelper {

    public boolean NotNeedToScroll = false;


    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        if (NotNeedToScroll) {
            return new int[]{0, 0};
        } else {
            return super.calculateDistanceToFinalSnap(layoutManager, targetView);
        }
    }
}
