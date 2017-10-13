package io.innofang.base.base;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import io.innofang.base.utils.common.RequestPermissions;

/**
 * Author: Inno Fang
 * Time: 2017/9/23 11:02
 * Description:
 */


public abstract class BaseFragment extends Fragment {


    protected final static String NULL = "";
    private Toast toast;

    public void toast(@StringRes int id) {
        toast(getString(id));
    }

    public void toast(final Object obj) {
        try {
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (toast == null)
                        toast = Toast.makeText(getActivity(), NULL, Toast.LENGTH_SHORT);
                    toast.setText(obj.toString());
                    toast.show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对运行时权限请求结果进行处理
     *
     * @param requestCode  请求码
     * @param permissions  申请的运行时权限
     * @param grantResults 运行时权限申请结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        RequestPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}