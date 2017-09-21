package io.innofang.parents.home;

import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import io.innofang.base.base.BasePresenter;
import io.innofang.base.base.BaseView;

/**
 * Author: Inno Fang
 * Time: 2017/9/21 20:43
 * Description:
 */


public class HomeContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {

        List<BmobIMConversation> getConversation();

    }

}
