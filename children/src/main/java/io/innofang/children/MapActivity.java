package io.innofang.children;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.util.common.RequestPermissions;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 14:38
 * Description:
 */


public class MapActivity extends BaseActivity implements AMapLocationListener/*, HintDialogFragment.DialogFragmentCallback*/ {

    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.locbtn)
    Button mLocbtn;

    private AMap mAMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private Marker locMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        //初始化定位参数
        initLocation();
        //初始化请求权限，存储权限
        checkLocationPermission();

        mMapView.onCreate(savedInstanceState);
        if (null == mAMap)
            mAMap = mMapView.getMap();
    }

    private void checkLocationPermission() {
        RequestPermissions.requestRuntimePermission(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new RequestPermissions.OnRequestPermissionsListener() {
                    @Override
                    public void onGranted() {
                        startLocation();
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        toast(deniedPermission.toString() + "被拒绝");
                    }
                });
    }

    //定位
    private void initLocation() {
        //初始化client
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        // 设置定位监听
        mLocationClient.setLocationListener(this);
        //定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置为单次定位
        mLocationOption.setOnceLocation(true);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null
                && aMapLocation.getErrorCode() == 0) {
            double longitude = aMapLocation.getLongitude();
            double latitude = aMapLocation.getLatitude();
            LatLng location = new LatLng(latitude, longitude);
            changeLocation(location);
        } else {
            String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            Log.e("AmapErr", errText);
            Toast.makeText(MapActivity.this, errText, Toast.LENGTH_LONG).show();
        }
    }

    private void changeLocation(LatLng location) {
        if (locMarker == null) {
            locMarker = mAMap.addMarker(new MarkerOptions().position(location));
        } else {
            locMarker.setPosition(location);
        }
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

    }

    @OnClick(R.id.locbtn)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.locbtn:
                checkLocationPermission();
                break;
        }
    }

    private void startLocation() {
        /*
        此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        在定位结束后，在合适的生命周期调用onDestroy()方法
        在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        启动定位
         */
        // 启动定位
        mLocationClient.startLocation();
    }

    /**
     * 销毁定位
     */
    private void destroyLocation() {
        if (null != mLocationClient) {
            mLocationClient.stopLocation();
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationClient = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }
}
