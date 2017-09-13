package io.innofang.base.util.bmob;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
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

}
