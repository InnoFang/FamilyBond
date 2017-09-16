package io.innofang.children;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.util.amap.SensorEventHelper;
import io.innofang.base.util.common.RequestPermissions;
import io.innofang.children.option.OptionActivity;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 14:38
 * Description:
 */

@Route(path = "/children/1")
public class MapActivity extends BaseActivity implements AMapLocationListener, LocationSource {

    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.locbtn)
    AppCompatImageButton mLocbtn;
    @BindView(R.id.option_fab)
    FloatingActionButton mOptionFab;

    private AMap mAMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private Marker mLocMarker;
    private OnLocationChangedListener mListener;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private boolean mFirstFix = false;
    private SensorEventHelper mSensorHelper;
    private Circle mCircle;
    public static final String LOCATION_MARKER_FLAG = "你的位置";

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
        init();

    }

    private void init() {
        if (null == mAMap) {
            mAMap = mMapView.getMap();
            setUpMap();
        }
        mSensorHelper = new SensorEventHelper(this);
        if (null != mSensorHelper) {
            mSensorHelper.registerSensorListener();
        }
    }

    private void setUpMap() {
        mAMap.setLocationSource(this);// 设置定位监听
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
//        mAMap.setMyLocationType(AMap.MAP_TYPE_NORMAL);
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
        /*if (aMapLocation != null
                && aMapLocation.getErrorCode() == 0) {
            double longitude = aMapLocation.getLongitude();
            double latitude = aMapLocation.getLatitude();
            LatLng location = new LatLng(latitude, longitude);
            changeLocation(location);
        } else {
            String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
            Log.e("AmapErr", errText);
            Toast.makeText(MapActivity.this, errText, Toast.LENGTH_LONG).show();
        }*/
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {

                LatLng location = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, aMapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(aMapLocation.getAccuracy());
                    mLocMarker.setPosition(location);
                    mAMap.moveCamera(CameraUpdateFactory.changeLatLng(location));
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = mAMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_locked)));
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = mAMap.addMarker(options);
        mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }

    @OnClick({R.id.locbtn, R.id.option_fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.locbtn:
                checkLocationPermission();
                break;
            case R.id.option_fab:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(this, mOptionFab, mOptionFab.getTransitionName());
                    startActivity(new Intent(this, OptionActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, OptionActivity.class));
                }
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
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        if (mMapView != null) {
            mMapView.onPause();
        }
        deactivate();
        mFirstFix = false;
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
        if (mLocMarker != null) {
            mLocMarker.destroy();
        }
        destroyLocation();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }
}
