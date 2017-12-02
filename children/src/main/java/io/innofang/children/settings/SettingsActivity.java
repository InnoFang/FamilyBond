package io.innofang.children.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;

import cn.bmob.v3.BmobUser;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.User;
import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/16 21:16
 * Description:
 */


public class SettingsActivity extends BaseActivity implements SettingsContract.View, View.OnClickListener {

    CardView mAddContact;
    private SettingsContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAddContact = (CardView) findViewById(R.id.add_contact);
        mAddContact.setOnClickListener(this);

        mPresenter = new SettingsPresenter(this);
    }

    public void onClick(View view) {
        mPresenter.showPopup(this, BmobUser.getCurrentUser(User.class));
    }

    @Override
    public void setPresenter(SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showInfo(String text) {
        Snackbar.make(mAddContact, text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            showProgressDialog();
        } else {
            closeProgressDialog();
        }
    }


    private ProgressDialog progressDialog;

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
