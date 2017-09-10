package io.innofang.test.IM;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import io.innofang.base.bean.User;
import io.innofang.test.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/10 15:46
 * Description:
 */


public class IMActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mMessageTextView;
    private EditText mEditText;
    private Button mSendButton;

    BmobIMConversation mConversationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im);


        EventBus.getDefault().register(this);

        mMessageTextView = (TextView) findViewById(R.id.message_text_view);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mSendButton = (Button) findViewById(R.id.send_text_button);
        mSendButton.setOnClickListener(this);

        BmobIMConversation conversationEntrance = (BmobIMConversation) getIntent().getSerializableExtra("c");
        mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
//        mConversationManager = (BmobIMConversation) getIntent().getSerializableExtra("c");
    }

    @Override
    public void onClick(View v) {

        User user = BmobUser.getCurrentUser(User.class);
        if (!TextUtils.isEmpty(user.getObjectId())) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (null == e) {
                        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
                            Toast.makeText(IMActivity.this, "Haven't connect the IM server", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sendMessage();
                    } else {
                        Toast.makeText(IMActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void sendMessage() {
        final String text = mEditText.getText().toString();
        if (TextUtils.isEmpty(text.trim())) {
            Toast.makeText(this, "please input content", Toast.LENGTH_SHORT).show();
            return;
        }

        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            Toast.makeText(IMActivity.this, "Haven't connect the IM server", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setContent(text);
    /*    Map<String, Object> map = new HashMap<>();
        map.put("level", "1");
        msg.setExtraMap(map);*/
        mConversationManager.sendMessage(msg, new MessageSendListener() {

            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if (e != null) {
                    Toast.makeText(IMActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }


    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        if (null != event && null != event.getMessage()) {
            mMessageTextView.setText(event.getMessage().getContent());
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        BmobIM.getInstance().disConnect();
        super.onDestroy();
    }
}
