package io.innofang.base.utils.bmob;

import java.util.List;

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

    public interface onUpdateListener {
        boolean beforeUpdate();

        void updateSuccessful();

        void updateFailed(BmobException e);
    }

    public interface onQueryListener {
        boolean beforeQuery();

        void querySuccessful(List<User> list);

        void queryFailed(BmobException e);
    }

    public interface onConnectListener {
        void connectSuccessful(User user);

        void connectFailed(String error);
    }
}
