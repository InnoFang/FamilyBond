package io.innofang.parents.reminder;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;

/**
 * Author: Inno Fang
 * Time: 2017/9/21 20:47
 * Description:
 */


public class ReminderPresenter implements ReminderContract.Presenter {

    private ReminderContract.View mView;

    public ReminderPresenter(ReminderContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public List<BmobIMConversation> getConversation() {

        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();

        return null;
    }
}
