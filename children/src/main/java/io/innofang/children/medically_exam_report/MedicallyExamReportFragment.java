package io.innofang.children.medically_exam_report;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

import io.innofang.base.bean.greendao.Bpm;
import io.innofang.base.bean.greendao.BpmDao;
import io.innofang.base.bean.greendao.DaoSession;
import io.innofang.base.configure.GreenDaoConfig;
import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/10/13 16:35
 * Description:
 */


public class MedicallyExamReportFragment extends Fragment {

    private LineChart mChart;

    private List<Bpm> data = new ArrayList<>();
    private RecyclerView mBpmRecyclerView;
    private BpmDao mBpmDao;

    public static MedicallyExamReportFragment newInstance() {
        return new MedicallyExamReportFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medically_exam_report, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mChart = (LineChart) view.findViewById(R.id.line_chart);
        mBpmRecyclerView = (RecyclerView) view.findViewById(R.id.data_display_recycler_view);

        initChart();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true); /*让RecyclerView的元素倒序显示*/
        layoutManager.setStackFromEnd(true);/*初始元素不默认从底部开始显示*/
        mBpmRecyclerView.setLayoutManager(layoutManager);
        mBpmRecyclerView.setAdapter(new MedicallyExamAdapter(getActivity(), data));
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

}
