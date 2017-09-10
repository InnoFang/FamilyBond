package io.innofang.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.innofang.base.bean.User;
import io.innofang.base.widget.custom_popup_window.CustomPopupWindow;
import io.innofang.test.IM.IMActivity;
import io.innofang.test.card.CardActivity;

public class MainActivity extends AppCompatActivity {

    private CustomPopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.amap_button:
                startActivity(new Intent(this, AMapActivity.class));
                break;
            case R.id.gallery_button:
                startActivity(new Intent(this, GalleryActivity.class));
                break;
            case R.id.card_button:
                startActivity(new Intent(this, CardActivity.class));
                break;
            case R.id.im_button:

                popupWindow = new CustomPopupWindow.Builder(this)
                        .setContentView(R.layout.layout_login)
                        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setFocus(true) // 设置是否可以获取焦点
//                                .setOutsideCancel(true)  // 设置点击外部取消
                        .setElevation(5)
                        .build()
                        .showAtLocation(R.layout.activity_main, Gravity.CENTER, 0, 0);

                popupWindow.findView(R.id.login).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username =
                                ((EditText) popupWindow.findView(R.id.username_edit_text)).getText().toString();
                        String password =
                                ((EditText) popupWindow.findView(R.id.password_edit_text)).getText().toString();

                        final String to = ((EditText) popupWindow.findView(R.id.send_to_edit_text)).getText().toString();
                        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(to)) {
                            User user = new User();
                            user.setUsername(username);
                            user.setPassword(password);
                            user.login(new SaveListener<User>() {
                                @Override
                                public void done(User user, BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(MainActivity.this, "Login success, searching user " + to, Toast.LENGTH_SHORT).show();
                                        queryUser(to);
                                    } else {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                });

                break;
        }
    }

    private void queryUser(final String to) {
        BmobQuery<User> query = new BmobQuery<>();
        //去掉当前用户
        try {
            BmobUser user = BmobUser.getCurrentUser();
            query.addWhereNotEqualTo("username", user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        query.addWhereContains("username", to);
        query.setLimit(1);
        query.order("-createdAt");
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        connect(list.get(0));
                    } else {
                        Toast.makeText(MainActivity.this, to + " isn't exits.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void connect(final User user) {
        Toast.makeText(this, "connecting", Toast.LENGTH_SHORT).show();
        User curr = BmobUser.getCurrentUser(User.class);
        if (!TextUtils.isEmpty(curr.getObjectId())) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        createConversion(user);
                    } else {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void createConversion(User user) {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            Toast.makeText(this, "haven't connect server", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i("tag", "TO: " + user.getUsername());

        BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), null);
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
//        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);

        popupWindow.dismiss();

        Intent intent = new Intent(MainActivity.this, IMActivity.class);
        intent.putExtra("c", conversationEntrance);
        startActivity(intent);
        finish();
    }
}
