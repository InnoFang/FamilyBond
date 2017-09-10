package io.innofang.base.widget.gallery_recycler_view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Author: Inno Fang
 * Time: 2017/9/9 21:01
 * Description:
 */


public class GalleryRecyclerView extends RecyclerView {

    /* 减速因子 */
    private static final float FLINT_SCALE_DOWN_FACTOR = 0.5f;
    /* 最大顺时滑动速度 */
    private static final int FLING_MAX_VELOCITY = 8000;

    public GalleryRecyclerView(Context context) {
        super(context);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityX = solveVelocity(velocityX);
        velocityY = solveVelocity(velocityY);
        return super.fling(velocityX, velocityY);
    }

    private int solveVelocity(int velocity) {
        if (velocity > 0) {
            return Math.min(velocity, FLING_MAX_VELOCITY);
        } else {
            return Math.max(velocity, -FLING_MAX_VELOCITY);
        }
    }
}
