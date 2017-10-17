package io.innofang.medically.data_display;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import io.innofang.base.bean.User;
import io.innofang.base.bean.bmob.BpmMessage;
import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.bean.greendao.BpmDao;
import io.innofang.base.bean.greendao.DaoSession;
import io.innofang.base.configure.GreenDaoConfig;
import io.innofang.base.utils.bmob.BmobEvent;
import io.innofang.base.utils.bmob.BmobUtil;
import io.innofang.base.utils.common.L;
import io.innofang.medically.R;
import io.innofang.medically.utils.event.MedicallyEvent;

/**
 * Author: Inno Fang
 * Time: 2017/10/2 20:50
 * Description:
 */


public class DataDisplayFragment extends Fragment {

    private TextView mBpsTextView;
    private LineChart mChart;

    private List<Bpm> data = new ArrayList<>();
    private RecyclerView mDataDisplayRecyclerView;
    private BpmDao mBpmDao;

    private List<BmobIMConversation> mIMConversations = new ArrayList<>();
    private BmobIMConversation mConversationManager;

    public static DataDisplayFragment newInstance() {

        Bundle args = new Bundle();
        DataDisplayFragment fragment = new DataDisplayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe(sticky = true)
    public void onHandleMedicallyEvent(MedicallyEvent event) {
        Bpm bpm = new Bpm();
        bpm.setBpm(event.bpm);
        int value = Integer.valueOf(event.bpm);
        if (value >= 60 && value <= 90)
            bpm.setDescription("继续保持");
        else if (value < 60)
            bpm.setDescription("勤加锻炼");
        else if (value > 90)
            bpm.setDescription("注意休息");
        SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
        bpm.setTime(mFormat.format(new Date()));
        data.add(bpm);
        L.i("onHandleMedicallyEvent: " + String.format("%sbpm ", event.bpm));
        mBpsTextView.setText(/*String.format("%s bpm ", */event.bpm);

        mBpmDao.insert(bpm);

        // send bpm to children
        User user = BmobUser.getCurrentUser(User.class);
        String username = user.getContact().get(0).getUsername();
        L.i("send " + username);
        checkConversations(username, bpm);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        if (null != list) {
            mIMConversations.addAll(list);
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_display, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBpsTextView = (TextView) view.findViewById(R.id.bpm_text_view);
        mChart = (LineChart) view.findViewById(R.id.line_chart);
        mDataDisplayRecyclerView = (RecyclerView) view.findViewById(R.id.data_display_recycler_view);

        initChart();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true); /*让RecyclerView的元素倒序显示*/
        layoutManager.setStackFromEnd(true);/*初始元素不默认从底部开始显示*/
        mDataDisplayRecyclerView.setLayoutManager(layoutManager);
        mDataDisplayRecyclerView.setAdapter(new DataDisplayAdapter(getActivity(), data));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initChart() {
        // init chart
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        mChart.setBackground(getActivity().getDrawable(R.drawable.bg_chart));
        mChart.setViewPortOffsets(80f, 0f, 80f, 0f);
        setData();
        mChart.invalidate();
        mChart.getLegend().setEnabled(false);

        // init xAxis
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setDrawGridLines(true);
        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(false);
        xAxis.setGranularityEnabled(true);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yAxis.setTextColor(ColorTemplate.getHoloBlue());
        yAxis.setDrawGridLines(true);
        yAxis.setGranularityEnabled(true);
        yAxis.setAxisMinimum(50f);
        yAxis.setAxisMaximum(130f);
        yAxis.setYOffset(-9f);
        yAxis.setXOffset(-20f);
        yAxis.setTextColor(Color.rgb(255, 192, 56));
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);

        mChart.getAxisRight().setEnabled(false);
    }

    private void setData() {

        DaoSession daoSession = GreenDaoConfig.getInstance().getDaoSession();
        mBpmDao = daoSession.getBpmDao();
        Query<Bpm> bpmQuery = mBpmDao.queryBuilder().orderAsc(BpmDao.Properties.Id).build();
        data.addAll(bpmQuery.list());

        // 拿到最新测量数据
        EventBus.getDefault().register(this);

        int start = 0;
        if (data.size() > 5)
            start = data.size() - 5;
        else
            start = 0;

        List<Entry> values = new ArrayList<>();
        for (int i = start; i < data.size(); i++) {
            values.add(new Entry(i - start, Float.parseFloat(data.get(i).getBpm())));
        }
        // create a dataset and give it a type
        LineDataSet set = new LineDataSet(values, "DataSet 1");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.WHITE);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setValueTextColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2.5f);
        set.setDrawCircles(true);
        set.setCircleRadius(3);
        set.setDrawValues(true);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));

        // create a data object with the datasets
        LineData data = new LineData(set);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }

    private void send(Bpm bpm) {
        BpmMessage message = new BpmMessage();
        message.setContent(getString(R.string.send_to_children_bpm_tips));
        Map<String, Object> map = new HashMap<>();

        map.put("mBpm", bpm.getBpm());
        map.put("mTime", bpm.getTime());
        map.put("mDescription", bpm.getDescription());
        message.setExtraMap(map);
        mConversationManager.sendMessage(message, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if (e == null) {
                    L.i("send bpm to children successfully.");
                } else {
                    showInfo("send bpm to children error " + e.getMessage());
                }
            }
        });
    }

    private void checkConversations(String username, final Bpm bpm) {
        if (null != mIMConversations && !mIMConversations.isEmpty()) {

            for (BmobIMConversation conversationEntrance : mIMConversations) {
                if (conversationEntrance.getConversationTitle().equals(username)) {
                    mConversationManager = BmobIMConversation.obtain(
                            BmobIMClient.getInstance(), conversationEntrance);
                    send(bpm);
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
                            send(bpm);
                        }

                        @Override
                        public void connectFailed(String error) {
                            showInfo(error);
                        }
                    });
                }

                @Override
                public void queryFailed(BmobException e) {
                    showInfo(e.getMessage());
                }
            });

        }
    }

    private void showInfo(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}
