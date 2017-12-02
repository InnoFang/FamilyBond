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

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.event.MessageEvent;
import io.innofang.base.utils.common.L;
import io.innofang.parents.R;
import io.innofang.xfyun.XFYunUtil;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 09:13
 * Description:
 */


public class ReminderFragment extends Fragment implements View.OnClickListener {

    TextView mMessageTextView;
    private String mMessage;

    public static ReminderFragment newInstance() {
        return new ReminderFragment();
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
        return inflater.inflate(R.layout.fragment_reminder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMessageTextView = (TextView) view.findViewById(R.id.message_text_view);
        mMessageTextView.setOnClickListener(this);
        mMessageTextView.setText(mMessage);
    }

    /**
     * 注册消息接收时间
     *
     * @param event
     */
    @Subscribe
    public void onHandleMessageEvent(MessageEvent event) {
        L.i("Reminder onHandleMessageEvent: is called");
        handleMessage();
    }

    private void handleMessage() {
        L.i("Reminder handle message");
        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        BmobIMMessage message = list.get(0).getMessages().get(0);
        if (null != list) {
            if (message.getMsgType().equals(BmobIMMessageType.TEXT.getType()) || message.getMsgType().equals("agree")) {
                mMessageTextView.setText(message.getContent());
                XFYunUtil.build(getContext()).speak("您的家人为您发来了消息提醒，注意查看");
                Toast.makeText(getActivity(), list.get(0).getConversationTitle() + "发来消息提醒", Toast.LENGTH_LONG).show();
            } else if (message.getMsgType().equals(BmobIMMessageType.VOICE.getType())) {
                //使用buildFromDB方法转化成指定类型的消息
                BmobIMAudioMessage audio = BmobIMAudioMessage.buildFromDB(true, message);
                XFYunUtil.build(getContext()).speak("您的家人为您发来了语音提醒，注意查看");
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

    public void onClick(View view) {
        XFYunUtil.build(getContext()).speak(mMessageTextView.getText().toString());
    }
}
