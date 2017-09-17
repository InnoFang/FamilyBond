package io.innofang.base.bean

import cn.bmob.v3.BmobUser
import io.innofang.base.bean.User.Companion.CHILDREN

/**
 * Author: Inno Fang
 * Time: 2017/9/10 14:48
 * Description:
 */


data class User(var client: String = CHILDREN,
                var contact: MutableList<User> = ArrayList<User>()) : BmobUser(){
    companion object {
        @JvmField
        val PARENTS: String = "PARENTS"
        @JvmField
        val CHILDREN: String = "CHILDREN"
    }
}

