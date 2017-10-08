package io.innofang.base.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Author: Inno Fang
 * Time: 2017/10/9 21:41
 * Description:
 */


public class SMSModel extends BmobObject {

    private BmobFile file;

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile name) {
        this.file = name;
    }
}
