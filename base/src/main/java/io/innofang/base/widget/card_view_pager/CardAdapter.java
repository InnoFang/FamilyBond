package io.innofang.base.widget.card_view_pager;

import android.support.v7.widget.CardView;

/**
 * Author: Inno Fang
 * Time: 2017/9/10 09:58
 * Description:
 */

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}