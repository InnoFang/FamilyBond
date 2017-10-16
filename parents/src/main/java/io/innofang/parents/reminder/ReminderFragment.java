package io.innofang.parents.reminder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import io.innofang.base.utils.common.L;
import io.innofang.parents.R;
import io.innofang.parents.R2;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 09:13
 * Description:
 */


public class ReminderFragment extends Fragment {

    @BindView(R2.id.message_text_view)
    TextView mMessageTextView;
    Unbinder unbinder;
    private String mMessage;

    public static ReminderFragment newInstance() {
        return new ReminderFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        unbinder = ButterKnife.bind(this, view);
        mMessageTextView.setText(mMessage);
        return view;
    }

    /**
     * 注册消息接收时间
     *
     * @param event
     */
    @Subscribe
    public void onHandleMessageEvent(MessageEvent event) {
        L.i("onHandleMessageEvent: is called");
        handleMessage();
    }

    /**
     * 注册消息接收时间
     *
     * @param event
     */
    @Subscribe
    public void onHandleMessageEvent(OfflineMessageEvent event) {
        L.i("onHandleMessageEvent: is called");
        handleMessage();
    }


    private void handleMessage() {
        L.i("handle message");
        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        BmobIMMessage message = list.get(0).getMessages().get(0);
        if (null != list) {
            if (message.getMsgType().equals(BmobIMMessageType.TEXT.getType()) || message.getMsgType().equals("agree")) {
                mMessageTextView.setText(message.getContent());
                Toast.makeText(getActivity(), list.get(0).getConversationTitle() + "发来消息提醒", Toast.LENGTH_LONG).show();
            } else if (message.getMsgType().equals(BmobIMMessageType.VOICE.getType())) {
                //使用buildFromDB方法转化成指定类型的消息
                BmobIMAudioMessage audio = BmobIMAudioMessage.buildFromDB(true, message);
                Toast.makeText(getActivity(), list.get(0).getConversationTitle() + "发来语音提醒", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("message", mMessage);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (null != savedInstanceState) {
            mMessage = savedInstanceState.getString("message");
        }
    }
}
