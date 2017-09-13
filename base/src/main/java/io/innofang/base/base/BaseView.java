package io.innofang.base.base;

/**
 * Author: Inno Fang
 * Time: 2017/9/13 16:55
 * Description:
 */


public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);
}
