package io.innofang.parents.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import cn.bmob.newim.core.BmobDownloadManager;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.FileDownloadListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import io.innofang.parents.R;
import io.innofang.parents.R2;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 09:13
 * Description:
 */


public class HomeFragment extends Fragment {

    @BindView(R2.id.message_text_view)
    TextView mMessageTextView;
    Unbinder unbinder;

    public static HomeFragment newInstance() {
        return Holder.sInstance;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private static class Holder {
        private static final HomeFragment sInstance = new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    /**
     * 注册消息接收时间
     *
     * @param event
     */
    @Subscribe
    public void onHandleMessageEvent(MessageEvent event) {
        Log.i("tag", "handle message");
        if (null != event && null != event.getMessage()) {
            BmobIMMessage msg = event.getMessage();
            mMessageTextView.setText(msg.getContent());

            BmobIMAudioMessage message = BmobIMAudioMessage.buildFromDB(false, msg);
            String currentUid = BmobUser.getCurrentUser().getObjectId();
            boolean isExists = BmobDownloadManager.isAudioExist(currentUid, message);
            if (!isExists) {
                BmobDownloadManager downloadManager = new BmobDownloadManager(getContext(), msg, new FileDownloadListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void done(BmobException e) {

                    }
                });
                downloadManager.execute(message.getContent());
            }
        }
    }

    /**
     * 注册消息接收时间
     *
     * @param event
     */
    @Subscribe
    public void onHandleMessageEvent(OfflineMessageEvent event) {
        handleMessage();
    }


    private void handleMessage() {
        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        if (null != list) {
            BmobIMMessage message = list.get(0).getMessages().get(0);
            if (message.getMsgType().equals(BmobIMMessageType.TEXT.getType()) || message.getMsgType().equals("agree")) {
                mMessageTextView.setText(message.getContent());
                Toast.makeText(getActivity(), list.get(0).getConversationTitle() + "发来消息提醒", Toast.LENGTH_LONG).show();
            } else if (message.getMsgType().equals(BmobIMMessageType.VOICE.getType())) {

                Toast.makeText(getActivity(), list.get(0).getConversationTitle() + "发来语音提醒", Toast.LENGTH_LONG).show();
            }
        }
    }

}
