package io.innofang.parents;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import io.innofang.base.base.BaseActivity;
import io.innofang.base.bean.SMSModel;
import io.innofang.base.bean.User;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.utils.common.BottomNavigationViewHelper;
import io.innofang.base.utils.common.L;
import io.innofang.base.utils.common.RequestPermissions;
import io.innofang.parents.medically_exam.MedicallyExamFragment;
import io.innofang.parents.reminder.ReminderFragment;
import io.innofang.parents.sms.SmsFragment;
import io.innofang.sms_intercept.SMSModelUtil;
import io.innofang.xfyun.XFYunUtil;

@Route(path = "/parents/1")
public class ParentsActivity extends BaseActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R2.id.view_pager)
    ViewPager mViewPager;
    @BindView(R2.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    private MenuItem mMenuItem;

//    private LocationManager mLocationManager;
//    private String provider;
//

    private int[] stringIds = {
            R.string.sms,
            R.string.medically_exam,
            R.string.reminder
    };

    private int[] drawableIds = {
            R.drawable.ic_sms,
            R.drawable.ic_medically_exam,
            R.drawable.ic_reminder,
    };
//    private GeocodeSearch geocodeSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (null != mMenuItem) {
                    mMenuItem.setChecked(false);
                } else {
                    mBottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                mMenuItem = mBottomNavigationView.getMenu().getItem(position);
                mMenuItem.setChecked(true);
                tips(stringIds[position], drawableIds[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        List<Fragment> list = new ArrayList<>();
        list.add(SmsFragment.newInstance());
        list.add(MedicallyExamFragment.newInstance());
        list.add(ReminderFragment.newInstance());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), list);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(1);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);


        if (BmobUser.getCurrentUser(User.class).getContact().isEmpty()) {
            startActivity(new Intent(this, AddContactActivity.class));
        }
//        checkConnect();

        RequestPermissions.requestRuntimePermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, new RequestPermissions.OnRequestPermissionsListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> deniedPermission) {

            }
        });

        Intent intent = new Intent(this, HandleMessageService.class);
        startService(intent);

        XFYunUtil.build(this).setSpeed("20").speak("欢迎使用家宝，祝您使用愉快");



//        initAMapLocation();
//        geocodeSearch = new GeocodeSearch(this);
//        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        List<String> providerList = mLocationManager.getProviders(true);
//        if (providerList.contains(LocationManager.GPS_PROVIDER))
//            provider = LocationManager.GPS_PROVIDER;
//        else if (providerList.contains(LocationManager.NETWORK_PROVIDER))
//            provider = LocationManager.NETWORK_PROVIDER;
//        else {
//            Toast.makeText(this, "No location provider to use", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        RequestPermissions.requestRuntimePermission(new String[]{
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION}, new RequestPermissions.OnRequestPermissionsListener() {
//            @Override
//            public void onGranted() {
////                locationTest2();
//                if (ActivityCompat.checkSelfPermission(ParentsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ParentsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                    return;
//                }
//                Log.i("location", "called");
//                Location location = mLocationManager.getLastKnownLocation(provider);
//                if (null != location) {
//                    showLocation(location);
//                }
//                mLocationManager.requestLocationUpdates(provider, 5000, 1, new LocationListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        showLocation(location);
//                    }
//
//                    @Override
//                    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                    }
//
//                    @Override
//                    public void onProviderEnabled(String provider) {
//
//                    }
//
//                    @Override
//                    public void onProviderDisabled(String provider) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onDenied(List<String> deniedPermission) {
//
//            }
//        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, HandleMessageService.class);
        stopService(intent);
    }

    //    private void showLocation(Location location) {
//        String position = "latitude:" + location.getLatitude() + " longitude:" + location.getLongitude();
//        Log.i("location", position);
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//        getAddressByLatlng(latLng);
//    }
//
//    private void getAddressByLatlng(LatLng latLng) {
//        Log.i("location", "getAddressByLatlng is called");
//        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
//        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
//        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
//        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
//            @Override
//            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
//                RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
//                String formatAddress = regeocodeAddress.getFormatAddress();
//                Log.i("location", "查询经纬度对应详细地址：\n" + formatAddress.substring(9));
//            }
//
//            @Override
//            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
//
//            }
//        });
//        //异步查询
//        geocodeSearch.getFromLocationAsyn(query);
//    }

//    public AMapLocationClientOption mLocationOption = null;
//
//    //声明AMapLocationClient类对象
//    public AMapLocationClient mLocationClient = null;
//    //声明定位回调监听器
//    public AMapLocationListener mLocationListener = new AMapLocationListener() {
//
//        @Override
//        public void onLocationChanged(AMapLocation amapLocation) {
//            if (amapLocation != null) {
//                if (amapLocation.getErrorCode() == 0) {
//                    //可在其中解析amapLocation获取相应内容。
//                    double locationType = amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                    double latitude = amapLocation.getLatitude();//获取纬度
//                    double longitude = amapLocation.getLongitude();//获取经度
//                    Log.i("location", "locationType:"+locationType+",latitude:"+latitude+"longitude:"+longitude);
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    getAddressByLatlng(latLng);
//                }else {
//                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
//                    Log.i("location","location Error, ErrCode:"
//                            + amapLocation.getErrorCode() + ", errInfo:"
//                            + amapLocation.getErrorInfo());
//                }
//            }
//        }
//    };
//
//    public void initAMapLocation() {
//        //初始化定位
//        mLocationClient = new AMapLocationClient(getApplicationContext());
//        //设置定位回调监听
//        mLocationClient.setLocationListener(mLocationListener);
//
//        //初始化AMapLocationClientOption对象
//        mLocationOption = new AMapLocationClientOption();
//        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
//
//
//        //给定位客户端对象设置定位参数
//        mLocationClient.setLocationOption(mLocationOption);
//
//        //该方法默认为false，true表示只定位一次
//        mLocationOption.setOnceLocation(true);
//        geocodeSearch = new GeocodeSearch(this);
//    }
//    private void locationTest2() {
//        //启动定位
//        mLocationClient.startLocation();
//    }


//    private GeocodeSearch geocodeSearch;

//    private void locationTest() {
//        geocodeSearch = new GeocodeSearch(this);
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        List<String> providerList = locationManager.getProviders(true);
//        String provider = "";
//        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
//            provider = LocationManager.GPS_PROVIDER;
//        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
//            provider = LocationManager.NETWORK_PROVIDER;
//        } else {
//            toast("No location provider to use");
//            Log.i("location", "No location provider to use");
//            return;
//        }
//        Log.i("location", "locationTest is called");
//        Location location = locationManager.getLastKnownLocation(provider);
//        if (null != location) {
//            showLocation(location);
//        }
//        locationManager.requestLocationUpdates(provider, 5000, 1, new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                showLocation(location);
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        });
//    }
//
//    private void showLocation(Location location) {
//        Log.i("location", "latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude());
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        getAddressByLatlng(latLng);
//    }
//
//    private void getAddressByLatlng(LatLng latLng) {
//        Log.i("location", "getAddressByLatlng is called");
//        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
//        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
//        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
//        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
//            @Override
//            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
//                RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
//                String formatAddress = regeocodeAddress.getFormatAddress();
//                Log.i("location", "查询经纬度对应详细地址：\n" + formatAddress.substring(9));
//            }
//
//            @Override
//            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
//
//            }
//        });
//        //异步查询
//        geocodeSearch.getFromLocationAsyn(query);
//    }


    private void checkConnect() {

        BmobUtil.connect(BmobUser.getCurrentUser(User.class), new BmobEvent.onConnectListener() {
            @Override
            public void connectSuccessful(User user) {
                //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                EventBus.getDefault().post(new BmobIMMessage());
                //会话：更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                BmobIM.getInstance().
                        updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                user.getUsername(), null));
            }

            @Override
            public void connectFailed(String error) {
                toast(error);
            }
        });
        //连接：监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                toast(status.getMsg());
                L.i(BmobIM.getInstance().getCurrentStatus().getMsg());
            }
        });


       }

    private void download(SMSModel smsModel) {
        File dir = new File(SMSModelUtil.DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String fileName = SMSModelUtil.getFileName(smsModel.getFile().getFilename());
        if ("".equals(fileName))
            return;

        File saveFile = new File(SMSModelUtil.DIRECTORY, fileName);
        smsModel.getFile().download(saveFile, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (null == e) {
                    L.i("download", "success: " + fileName);
                } else {
                    L.i("download", "failed: " + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_sms) {
            mViewPager.setCurrentItem(0);
        } else  if (id == R.id.item_medically_exam) {
            mViewPager.setCurrentItem(1);
        } else  if (id == R.id.item_reminder) {
            mViewPager.setCurrentItem(2);
        }
        return true;
    }




    Toast mToast;
    View mToastView;
    TextView mToastTextView;
    ImageView mToastImageView;

    private void tips(@StringRes int strId, @DrawableRes int imgId) {
        if (null == mToast) {
            mToast = new Toast(this);
            mToastView = LayoutInflater.from(this).inflate(R.layout.layout_toast, null);
            mToast.setView(mToastView);
            mToastTextView = (TextView) mToastView.findViewById(R.id.toast_text_view);
            mToastImageView = (ImageView) mToastView.findViewById(R.id.toast_image_view);
        }
        mToastTextView.setText(strId);
        mToastImageView.setImageResource(imgId);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

}
