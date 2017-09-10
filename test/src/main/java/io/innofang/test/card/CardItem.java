package io.innofang.test.card;

/**
 * Author: Inno Fang
 * Time: 2017/9/10 10:20
 * Description:
 */


public class CardItem {


    private int mTextResource;
    private int mTitleResource;

    public CardItem(int title, int text) {
        mTitleResource = title;
        mTextResource = text;
    }

    public int getText() {
        return mTextResource;
    }

    public int getTitle() {
        return mTitleResource;
    }
}