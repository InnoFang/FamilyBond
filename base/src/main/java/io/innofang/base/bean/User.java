package io.innofang.base.bean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Author: Inno Fang
 * Time: 2017/12/2 11:18
 * Description:
 */


public class User extends BmobUser{

    public static final String PARENTS = "PARENTS";
    public static final String CHILDREN = "CHILDREN";

    private String client = CHILDREN;
    private List<User> contact = new ArrayList<>();

    public User() {
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public List<User> getContact() {
        return contact;
    }

    public void setContact(List<User> contact) {
        this.contact = contact;
    }
}
