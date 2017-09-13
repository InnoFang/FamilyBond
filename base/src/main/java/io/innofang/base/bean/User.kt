package io.innofang.base.bean

import cn.bmob.v3.BmobUser

/**
 * Author: Inno Fang
 * Time: 2017/9/10 14:48
 * Description:
 */


data class User(var client: Client = Client.CHILDREN) : BmobUser()

enum class Client { CHILDREN, PARENTS }