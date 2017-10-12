package io.innofang.parents.sms;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import io.innofang.base.bean.SMS;
import io.innofang.base.bean.User;
import io.innofang.base.bean.bmob.SMSMessage;
import io.innofang.base.util.bmob.BmobEvent;
import io.innofang.base.util.bmob.BmobUtil;
import io.innofang.base.util.common.L;
import io.innofang.parents.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/12 15:52
 * Description:
 */


public class SmsPresenter implements SmsContract.Presenter {

    private SmsContract.View mView;
    private Context mContext;

    private List<BmobIMConversation> mIMConversations = new ArrayList<>();
    private BmobIMConversation mConversationManager;

    public SmsPresenter(SmsContract.View view) {
        mView = view;
        mContext = view.getContext();
        mView.setPresenter(this);

        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        if (null != list) {
            mIMConversations.addAll(list);
        }
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void sendToChildren(SMS sms) {
        User user = BmobUser.getCurrentUser(User.class);
        String username = user.getContact().get(0).getUsername();
        checkConversations(username);
        send(sms);
    }

    private void send(SMS sms) {
        SMSMessage message = new SMSMessage();
        message.setContent(mContext.getString(R.string.send_to_children_sms_tips));
        Map<String, Object> map = new HashMap<>();
        map.put("time", sms.getTime());
        map.put("address", sms.getAddress());
        map.put("content", sms.getContent());
        map.put("probability", sms.getProbability());
        message.setExtraMap(map);
        mConversationManager.sendMessage(message, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if (e != null) {
                    mView.showInfo("send to children error " + e.getMessage());
                } else {
                    L.i("send to children successfully.");
                }
            }
        });
    }

    private void checkConversations(String username) {
        if (null != mIMConversations && !mIMConversations.isEmpty()) {

            for (BmobIMConversation conversationEntrance : mIMConversations) {
                if (conversationEntrance.getConversationTitle().equals(username)) {
                    mConversationManager = BmobIMConversation.obtain(
                            BmobIMClient.getInstance(), conversationEntrance);
                }
            }
        } else {
            BmobUtil.query(username, new BmobEvent.onQueryListener() {
                @Override
                public boolean beforeQuery() {
                    return true;
                }

                @Override
                public void querySuccessful(final List<User> list) {
                    BmobUtil.connect(list.get(0), new BmobEvent.onConnectListener() {
                        @Override
                        public void connectSuccessful(User user) {
                            BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), null);
                            BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
                            mIMConversations.add(conversationEntrance);
                            mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
                        }

                        @Override
                        public void connectFailed(String error) {
                            mView.showInfo(error);
                        }
                    });
                }

                @Override
                public void queryFailed(BmobException e) {
                    mView.showInfo(e.getMessage());
                }
            });

        }
    }

}
