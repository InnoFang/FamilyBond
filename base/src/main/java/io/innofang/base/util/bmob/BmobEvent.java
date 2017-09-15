package io.innofang.base.util.bmob;

import cn.bmob.v3.exception.BmobException;
import io.innofang.base.bean.User;

/**
 * Author: Inno Fang
 * Time: 2017/9/13 16:35
 * Description:
 */


public class BmobEvent {

     public interface onLoginListener {
        boolean beforeLogin();

        void loginSuccessful(User user);

        void loginFailed(BmobException e);
    }

    public interface onRegisterListener {
        boolean beforeRegister();

        void registerSuccessful(User user);

        void registerFailed(BmobException e);
    }

}