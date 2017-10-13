package io.innofang.children.settings;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import io.innofang.base.bean.User;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.widget.custom_popup_window.CustomPopupWindow;
import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/16 21:35
 * Description:
 */


public class SettingsPresenter implements SettingsContract.Presenter {

    private SettingsContract.View mView;

    public SettingsPresenter(SettingsContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {

    }

    @Override
    public void showPopup(final Context context, final User user) {
        final CustomPopupWindow popupWindow = new CustomPopupWindow.Builder(context)
                .setContentView(R.layout.layout_add_contact)
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setFocus(true) // 设置是否可以获取焦点
                .setOutsideCancel(true)  // 设置点击外部取消
                .setElevation(5)
                .setAnimStyle(R.style.PopupWindowAnimStyle)
                .build()
                .showAtLocation(R.layout.activity_settings, Gravity.CENTER, 0, 0);

        final ListView listView = (ListView) popupWindow.findView(R.id.select_item_list_view);

        final List<String> usernames = new ArrayList<>();
        final List<User> currentContactList = new ArrayList<>();
        currentContactList.addAll(user.getContact());
        for (User contact : currentContactList) {
            usernames.add(contact.getUsername());
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_list_item_1, usernames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mView.showInfo(usernames.get(position));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    mView.showInfo(context.getString(R.string.delete_of, usernames.get(position)));
                    usernames.remove(position);
                    currentContactList.remove(position);
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (!currentContactList.equals(user.getContact())) {
                    updateUser(user, currentContactList);
                }
            }
        });

        final EditText editText = (EditText) popupWindow.findView(R.id.popup_edit_text);
        popupWindow.setOnClickListener(R.id.ok_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = editText.getText().toString();
                if (!TextUtils.isEmpty(username)) {
                    BmobUtil.query(username, new BmobEvent.onQueryListener() {
                        @Override
                        public boolean beforeQuery() {
                            mView.showLoading(true);
                            return true;
                        }

                        @Override
                        public void querySuccessful(List<User> list) {
                            editText.setText("");
                            User user = list.get(0);
                            if (!user.getClient().equals(User.PARENTS)) {
                                mView.showInfo("不是父母端，不能添加");
                            } else {
                                usernames.add(user.getUsername());
                                currentContactList.add(user);
                                adapter.notifyDataSetChanged();

                            }
                            mView.showLoading(false);
                            popupWindow.dismiss();
                        }

                        @Override
                        public void queryFailed(BmobException e) {
                            mView.showInfo(e.toString());
                            mView.showLoading(false);
                        }
                    });
                } else {
                    mView.showInfo(context.getString(R.string.cannot_be_empty));
                }

            }
        });

    }

    private void updateUser(User user, List<User> currentContactList) {
        user.setContact(currentContactList);
        BmobUtil.update(user, new BmobEvent.onUpdateListener() {
            @Override
            public boolean beforeUpdate() {
                mView.showLoading(true);
                return true;
            }

            @Override
            public void updateSuccessful() {
                mView.showInfo("更新数据成功");
                mView.showLoading(false);
            }

            @Override
            public void updateFailed(BmobException e) {
                mView.showInfo(e.toString());
                mView.showLoading(false);
            }
        });
    }

}
