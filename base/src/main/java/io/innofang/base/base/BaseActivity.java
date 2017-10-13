package io.innofang.base.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import io.innofang.base.utils.common.ActivityCollector;
import io.innofang.base.utils.common.RequestPermissions;

/**
 * Author: Inno Fang
 * Time: 2017/9/12 20:14
 * Description:
 */


public class BaseActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 1;

//    private Unbinder unbinder = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

//    @Override
//    public void setContentView(@LayoutRes int layoutResID) {
//        super.setContentView(layoutResID);
//        unbinder = ButterKnife.bind(this);
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    protected void onStop() {
//        EventBus.getDefault().unregister(this);
//        super.onStop();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (null != unbinder) {
//            unbinder.unbind();
//        }
        ActivityCollector.removeActivty(this);
    }

    protected final static String NULL = "";
    private Toast toast;

    public void toast(@StringRes int id) {
        toast(getString(id));
    }

    public void toast(final Object obj) {
        try {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (toast == null)
                        toast = Toast.makeText(BaseActivity.this, NULL, Toast.LENGTH_SHORT);
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
