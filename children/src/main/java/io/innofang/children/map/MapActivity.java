package io.innofang.children.map;

import android.Manifest;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;

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
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.User;
import io.innofang.base.bean.bmob.ShareMapMessage;
import io.innofang.base.utils.amap.SensorEventHelper;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.utils.common.L;
import io.innofang.base.utils.common.RequestPermissions;
import io.innofang.children.R;
import io.innofang.children.R2;
import io.innofang.children.reminder.ReminderActivity;
import io.innofang.children.settings.SettingsActivity;

/**
 * Author: Inno Fang
 * Time: 2017/9/15 14:38
 * Description:
 */

public class MapActivity extends BaseActivity implements AMapLocationListener, LocationSource {

    @BindView(R2.id.map)
    MapView mMapView;
    @BindView(R2.id.locbtn)
    AppCompatImageButton mLocbtn;
    @BindView(R2.id.action_locate)
    com.getbase.floatingactionbutton.FloatingActionButton mActionLocate;
    @BindView(R2.id.action_settings)
    com.getbase.floatingactionbutton.FloatingActionButton mActionSettings;
    @BindView(R2.id.action_reminder)
    com.getbase.floatingactionbutton.FloatingActionButton mActionReminder;
    @BindView(R2.id.floating_actions_menu)
    FloatingActionsMenu mFloatingActionsMenu;

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

    private List<BmobIMConversation> mIMConversations = new ArrayList<>();
    private BmobIMConversation mConversationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        // open share map
        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        if (null != list) {
            mIMConversations.addAll(list);
        }
        User user = BmobUser.getCurrentUser(User.class);
        String username = user.getContact().get(0).getUsername();
        checkConversations(username);

        //初始化定位参数
        initLocation();
        //初始化请求权限，存储权限
        mMapView.onCreate(savedInstanceState);
        init();

        checkLocationPermission();

    }
    private void checkConversations(String username) {
        if (null != mIMConversations && !mIMConversations.isEmpty()) {

            for (BmobIMConversation conversationEntrance : mIMConversations) {
                if (conversationEntrance.getConversationTitle().equals(username)) {
                    mConversationManager = BmobIMConversation.obtain(
                            BmobIMClient.getInstance(), conversationEntrance);
                    sendToOpenSharedMap();
                }
            }
        } else {
            BmobUtil.query(username, new BmobEvent.onQueryListener() {
                @Override
                public boolean beforeQuery() {
                    return true;
                }

                @Override
                public void querySuccessful(final List<User> list) {
                    BmobUtil.connect(list.get(0), new BmobEvent.onConnectListener() {
                        @Override
                        public void connectSuccessful(User user) {
                            BmobIMUserInfo info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), null);
                            BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
                            mIMConversations.add(conversationEntrance);
                            mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
                            sendToOpenSharedMap();
                        }

                        @Override
                        public void connectFailed(String error) {
                            toast(error);
                        }
                    });
                }

                @Override
                public void queryFailed(BmobException e) {
                    toast(e.getMessage());
                }
            });

        }
    }


    private void sendToOpenSharedMap() {
        ShareMapMessage message = new ShareMapMessage();
        message.setContent(ShareMapMessage.OPEN);
        mConversationManager.sendMessage(message, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if (null == e) {
                    toast("开始地图共享");
                } else {
                    toast(e.getMessage());
                    L.i("location", e.getMessage());
                }
            }
        });
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

    @OnClick(R2.id.locbtn)
    public void onLocBtnClick() {
        checkLocationPermission();
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

        if (mLocMarker != null) {
            mLocMarker.destroy();
        }
        destroyLocation();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        sendToCloseShareMap();
        super.onDestroy();
    }

    private void sendToCloseShareMap() {
        ShareMapMessage message = new ShareMapMessage();
        message.setContent(ShareMapMessage.CLOSE);
        mConversationManager.sendMessage(message, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if (null == e) {
                    toast("结束地图共享");
                } else {
                    toast(e.getMessage());
                    L.i("location", e.getMessage());
                }
            }
        });
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

    @OnClick({R2.id.action_locate, R2.id.action_settings, R2.id.action_reminder})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.action_locate) {
            toast("locate");
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_reminder) {
            startActivity(new Intent(this, ReminderActivity.class));
        }
        mFloatingActionsMenu.toggle();
    }
}
