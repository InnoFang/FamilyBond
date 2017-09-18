package io.innofang.base.util.bmob;

import android.text.TextUtils;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import io.innofang.base.bean.User;

/**
 * Author: Inno Fang
 * Time: 2017/9/12 21:21
 * Description:
 */


public class BmobUtil {

    public static void login(String username, String password, final BmobEvent.onLoginListener listener) {
        if (listener.beforeLogin()) {
            BmobUser.loginByAccount(username, password, new LogInListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (null == e) {
                        listener.loginSuccessful(user);
                    } else {
                        listener.loginFailed(e);
                    }
                }
            });
        }
    }

    public static void register(User user, final BmobEvent.onRegisterListener listener) {
        if (listener.beforeRegister()) {
            user.signUp(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (null == e) {
                        listener.registerSuccessful(user);
                    } else {
                        listener.registerFailed(e);
                    }
                }
            });
        }
    }

    public static void update(User user, final BmobEvent.onUpdateListener listener) {
        if (listener.beforeUpdate()) {
            user.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (null == e) {
                        listener.updateSuccessful();
                    } else {
                        listener.updateFailed(e);
                    }
                }
            });
        }
    }

    public static void query(String username, final BmobEvent.onQueryListener listener) {
        if (listener.beforeQuery()) {
            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("username", username);
            query.setLimit(1);
            query.findObjects(new FindListener<User>() {
                @Override
                public void done(List<User> list, BmobException e) {
                    if (null == e) {
                        listener.querySuccessful(list);
                    } else {
                        listener.queryFailed(e);
                    }
                }
            });
        }
    }

    public static void connect(final User user, final BmobEvent.onConnectListener listener) {
        User curr = BmobUser.getCurrentUser(User.class);
        if (!TextUtils.isEmpty(curr.getObjectId())) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (null == e) {
                        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
                            listener.connectFailed("haven't connect server");
                            return;
                        }
                        listener.connectSuccessful(user);
                    } else {
                        listener.connectFailed(e.getMessage());
                    }
                }
            });
        }
    }

}
