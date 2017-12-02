package io.innofang.parents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.User;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.utils.common.L;

/**
 * Author: Inno Fang
 * Time: 2017/10/12 16:03
 * Description:
 */


public class AddContactActivity extends BaseActivity implements View.OnClickListener {

    EditText mInputContactEditText;
    Button mAddContactButton;
    private User mUser;

    void initView() {

        mInputContactEditText = (EditText) findViewById(R.id.input_contact_edit_text);
        mAddContactButton = (Button) findViewById(R.id.add_contact_button);

        mAddContactButton.setOnClickListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        mUser = BmobUser.getCurrentUser(User.class);

        initView();
    }

    public void onClick(View view) {
        final String contact = mInputContactEditText.getText().toString();
        if (!TextUtils.isEmpty(contact)) {
            toast("正在查询，请稍后");
            BmobUtil.query(contact, new BmobEvent.onQueryListener() {
                @Override
                public boolean beforeQuery() {
                    return true;
                }

                @Override
                public void querySuccessful(final List<User> list) {
                    mInputContactEditText.setText("");
                    User contact = list.get(0);
                    if (!contact.getClient().equals(User.CHILDREN)) {
                        toast("不是子女端，不能添加");
                    } else {

                        List<User> contactList = mUser.getContact();
                        contactList.add(list.get(0));
                        toast("正在添加，请稍后");
                        BmobUtil.update(mUser, new BmobEvent.onUpdateListener() {
                            @Override
                            public boolean beforeUpdate() {
                                return true;
                            }

                            @Override
                            public void updateSuccessful() {
                                toast("添加成功");
                                finish();
                            }

                            @Override
                            public void updateFailed(BmobException e) {
                                toast("添加失败" + e.getMessage());
                                L.i(e.getMessage());
                            }
                        });
                    }

                }

                @Override
                public void queryFailed(BmobException e) {
                    toast("查询失败" + e.getMessage());
                }
            });
        } else {
            toast("联系人不能为空！");
        }
    }
}
