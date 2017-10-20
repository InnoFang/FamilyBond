package io.innofang.parents;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import io.innofang.base.bean.SMSModel;
import io.innofang.base.bean.User;
import io.innofang.base.bean.bmob.LocationMessage;
import io.innofang.base.bean.bmob.ShareMapMessage;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.utils.common.L;
import io.innofang.base.utils.common.RequestPermissions;
import io.innofang.sms_intercept.SMSModelUtil;

/**
 * Author: Inno Fang
 * Time: 2017/10/14 20:19
 * Description:
 */


public class HandleMessageService extends Service {


    private List<BmobIMConversation> mIMConversations = new ArrayList<>();
    private BmobIMConversation mConversationManager;

    private GeocodeSearch geocodeSearch;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    double locationType = amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    double latitude = amapLocation.getLatitude();//获取纬度
                    double longitude = amapLocation.getLongitude();//获取纬度
                    L.i("location", "locationType:" + locationType + ",latitude:" + latitude + ",longitude:" + longitude);
                    LatLng latLng = new LatLng(latitude, longitude);
                    getAddressByLatlng(latLng);
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    L.i("location", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };


    private void getAddressByLatlng(final LatLng latLng) {
        L.i("location  getAddressByLatlng is called");
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
                String formatAddress = regeocodeAddress.getFormatAddress();
                L.i("location 查询经纬度对应详细地址：\n" + formatAddress);

                User user = BmobUser.getCurrentUser(User.class);
                String username = user.getContact().get(0).getUsername();
                checkConversations(username, formatAddress, latLng);

            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);
    }

    private void sendToChildrenLocation(final String address, final LatLng latLng) {
        LocationMessage message = new LocationMessage();
        message.setContent("location");
        Map<String, Object> map = new HashMap<>();
        map.put("id", BmobUser.getCurrentUser(User.class).getUsername());
        map.put("address", address);
        map.put("latitude", latLng.latitude);
        map.put("longitude", latLng.longitude);
        message.setExtraMap(map);

        L.i("send location");

        mConversationManager.sendMessage(message, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if (e != null) {
                    L.i("send location failed " + e.getMessage());
                } else {
                    L.i("send location successfully");
                    L.i("address: " + address);
                    L.i("latitude: " + latLng.latitude);
                    L.i("longitude: " + latLng.longitude);
                }
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.i("location", "Handle Message Service onCreate: is called");
        checkConnect();

        geocodeSearch = new GeocodeSearch(this);
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);


        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        /**
         * 获取一次定位
         */
        //该方法默认为false，true表示只定位一次
        mLocationOption.setOnceLocation(true);

        RequestPermissions.requestRuntimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new RequestPermissions.OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                L.i("download dir is " + SMSModelUtil.isModelFilesExit());
                if (!SMSModelUtil.isModelFilesExit()) {
                    BmobQuery<SMSModel> query = new BmobQuery<>();
                    query.findObjects(new FindListener<SMSModel>() {
                        @Override
                        public void done(List<SMSModel> list, BmobException e) {
                            for (SMSModel smsModel : list) {
                                download(smsModel);
                            }
                        }
                    });
                }
            }

            @Override
            public void onDenied(List<String> deniedPermission) {

            }
        });


        EventBus.getDefault().register(this);
    }

    private void checkConversations(String username, final String address, final LatLng latlng) {
        if (null != mIMConversations && !mIMConversations.isEmpty()) {

            for (BmobIMConversation conversationEntrance : mIMConversations) {
                if (conversationEntrance.getConversationTitle().equals(username)) {
                    mConversationManager = BmobIMConversation.obtain(
                            BmobIMClient.getInstance(), conversationEntrance);
                    sendToChildrenLocation(address, latlng);
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
                            sendToChildrenLocation(address, latlng);
                        }

                        @Override
                        public void connectFailed(String error) {
                            L.i("location", error);
                        }
                    });
                }

                @Override
                public void queryFailed(BmobException e) {
                    L.i("location", e.getMessage());
                }
            });

        }
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkConnect() {

        BmobUtil.connect(BmobUser.getCurrentUser(User.class), new BmobEvent.onConnectListener() {
            @Override
            public void connectSuccessful(User user) {
                //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                EventBus.getDefault().post(new BmobIMMessage());
                //会话： 更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                BmobIM.getInstance().
                        updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                user.getUsername(), null));


                List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
                if (null != list) {
                    mIMConversations.addAll(list);
                }
            }

            @Override
            public void connectFailed(String error) {
                Toast.makeText(HandleMessageService.this, error, Toast.LENGTH_SHORT).show();
            }
        });
        // 连接： 监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                Toast.makeText(HandleMessageService.this, status.getMsg(), Toast.LENGTH_SHORT).show();
                L.i(BmobIM.getInstance().getCurrentStatus().getMsg());
            }
        });

    }

    /**
     * 注册消息接收时间
     *
     * @param event
     */

    public void onHandleMessageEvent(MessageEvent event) {
        L.i("location", "onHandleMessageEvent: is called");
        handleMessage(event);
    }

    @Subscribe
    public void onHandleShareMapMessageEvent(String status) {
        if (status.equals(ShareMapMessage.OPEN)) {
            //启动定位
            mLocationClient.startLocation();
        } else {
            //关闭定位
            mLocationClient.stopLocation();
        }
        L.i(status);
    }


    private void handleMessage(MessageEvent event) {
        L.i("location", "handle message");
        BmobIMMessage message = event.getMessage();
        if (message.getMsgType().equals(ShareMapMessage.MAP)) {
            Toast.makeText(this, message.getContent(), Toast.LENGTH_SHORT).show();
            if (message.getContent().equals(ShareMapMessage.OPEN)) {
                //启动定位
                mLocationClient.startLocation();
            } else {
                mLocationClient.stopLocation();
            }
            L.i(event.getConversation().getConversationTitle() + "发来地图共享提示");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
