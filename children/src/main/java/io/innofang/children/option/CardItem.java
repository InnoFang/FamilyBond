package io.innofang.children.option;

/**
 * Author: Inno Fang
 * Time: 2017/9/10 10:20
 * Description:
 */


public class CardItem {


    private int mImageRes;
    private int mTitleRes;

    public CardItem(int title, int text) {
        mTitleRes = title;
        mImageRes = text;
    }

    public int getImageRes() {
        return mImageRes;
    }

    public int getTitle() {
        return mTitleRes;
    }
}