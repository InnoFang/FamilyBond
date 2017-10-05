package io.innofang.medically.heat_beat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import io.innofang.medically.R;
import io.innofang.medically.utils.event.MedicallyEvent;
import io.innofang.medically.data_display.DataDisplayActivity;
import io.innofang.medically.utils.ImageProcessing;
import io.innofang.medically.widget.CountDownProgress;

/**
 * Author: Inno Fang
 * Time: 2017/10/1 17:27
 * Description:
 */


@SuppressWarnings("deprecation")
public class HeartBeatOldFragment extends Fragment {

    private static final String TAG = "HeartBeatOldFragment";

    private static final int THRESHOLD = 200;


    public static HeartBeatOldFragment newInstance() {
        return new HeartBeatOldFragment();
    }

    /* 定时任务，绘制曲线 */
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;

    /* 平均像素值 */
    private int averagePixel;
    private int flag = 1;
    private Handler mHandler;
    private String mTitle = "pulse";
    private XYSeries mSeries;
    private XYMultipleSeriesDataset mDataSet;
    private GraphicalView mChart;
    private XYMultipleSeriesRenderer mRenderer;
    private int addX;
    private int addY;
    int[] mXAxisValue = new int[300];
    int[] mYAxisValue = new int[300];

    /* 模拟脉搏波波形 */
    int[] pulse = new int[]{
            9, 10, 11, 12, 13,
            14, 13, 12, 11, 10,
            9, 8, 7, 6, 7,
            8, 9, 10, 11, 10,
            10};
    /* 脉搏波波形数组下标 */
    private int pulseIndex;

    private final AtomicBoolean processing = new AtomicBoolean(false);
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private TextView mBpsTextView;
    private TextView mAveragePixelValueTextView;
    private TextView pulseNumberTextView;
    private TextView mTipsTextView;
    private PowerManager.WakeLock wakeLock;
    private int averageIndex = 0;
    private final int averageArraySize = 4;
    private final int[] averageArray = new int[averageArraySize];
    private int beatsIndex = 0;
    private final int beatsArraySize = 3;
    private final int[] beatsArray = new int[beatsArraySize];
    private double beats = 0;
    private long startTime = 0;

    private enum TYPE {
        GREEN, RED
    }

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_heart_beat_old, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //这里获得main界面上的布局，下面会把图表画在这个布局里面
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.linearLayout1);

        //这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
        mSeries = new XYSeries(mTitle);

        //创建一个数据集的实例，这个数据集将被用来创建图表
        mDataSet = new XYMultipleSeriesDataset();

        //将点集添加到这个数据集中
        mDataSet.addSeries(mSeries);

        //以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
        int color = Color.GREEN;
        PointStyle style = PointStyle.CIRCLE;
        mRenderer = buildRenderer(color, style, true);

        //设置好图表的样式
        setChartSettings(mRenderer, "X", "Y", 0, 300, 4, 16, Color.WHITE, Color.WHITE);

        //生成图表
        mChart = ChartFactory.getLineChartView(getActivity(), mDataSet, mRenderer);

        //将图表添加到布局中去
        layout.addView(mChart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


		/*	       thread = new Thread(){
               public void arrayList(int u) {
	    		   ArrayList arrayList = new ArrayList();
	    		   arrayList.add(HardwareControler.readADC());
	   		}
	       };*/
        //这里的Handler实例将配合下面的Timer实例，完成定时更新图表的功能
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 刷新图表
                updateChart();
                super.handleMessage(msg);
            }
        };
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        };
        mTimer.schedule(mTimerTask, 1, 20);          //曲线

        mTipsTextView = (TextView) view.findViewById(R.id.tips_text_view);
        mSurfaceView = (SurfaceView) view.findViewById(R.id.preview);
        mBpsTextView = (TextView) view.findViewById(R.id.bps_text_view);
        mAveragePixelValueTextView = (TextView) view.findViewById(R.id.average_pixel_value_text_view);
        pulseNumberTextView = (TextView) view.findViewById(R.id.pulse_number_text_view);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(surfaceCallback);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");

        final CountDownProgress countDownProgress = (CountDownProgress) view.findViewById(R.id.countdown_progress);
        countDownProgress.setCountdownTime(20 * 1000);
        countDownProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTipsTextView.setVisibility(View.INVISIBLE);
                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(params);
                mCamera.startPreview(); // 开始亮灯

                countDownProgress.startCountDownTime(new CountDownProgress.OnCountdownFinishListener() {
                    @Override
                    public void countdownFinished() {
//                        Toast.makeText(getContext(), "倒计时结束了--->该UI处理界面逻辑了", Toast.LENGTH_LONG).show();
                        MedicallyEvent event = new MedicallyEvent();
                        event.bps = mBpsTextView.getText().toString();
                        EventBus.getDefault().postSticky(event);
                        startActivity(new Intent(getActivity(), DataDisplayActivity.class));
                    }
                });
            }
        });
    }

    //	曲线
    @Override
    public void onDestroy() {
        //当结束程序时关掉Timer
        mTimer.cancel();
        super.onDestroy();
    }


    protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        //设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.RED);
//		r.setPointStyle(null);
//		r.setFillPoints(fill);
        r.setLineWidth(1);
        renderer.addSeriesRenderer(r);
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String xTitle, String yTitle,
                                    double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        //有关对图表的渲染可参看api文档
        renderer.setChartTitle(mTitle);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.GREEN);
        renderer.setXLabels(20);
        renderer.setYLabels(10);
        renderer.setXTitle("Time");
        renderer.setYTitle("mmHg");
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setPointSize((float) 3);
        renderer.setShowLegend(false);
    }

    private void updateChart() {
        if (flag == 1)
            addY = 10;
        else {
            flag = 1;
            /* 阈值：200，平均像素值大于该值时，视为手指在摄像头上 */
            if (averagePixel < THRESHOLD) {
                if (pulse[20] > 1) {
                    Toast.makeText(getActivity(), "请用您的指尖盖住摄像头镜头！", Toast.LENGTH_SHORT).show();
                    pulse[20] = 0;
                }
                pulse[20]++;
                return;
            } else
                pulse[20] = 10;
            pulseIndex = 0;
        }
        if (pulseIndex < 20) {
            addY = pulse[pulseIndex];
            pulseIndex++;
        }

        //移除数据集中旧的点集
        mDataSet.removeSeries(mSeries);

        //判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
        int length = mSeries.getItemCount();
        int bz = 0;
        //		addX = length;
        if (length > 300) {
            length = 300;
            bz = 1;
        }
        addX = length;
        //将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果
        for (int i = 0; i < length; i++) {
            mXAxisValue[i] = (int) mSeries.getX(i) - bz;
            mYAxisValue[i] = (int) mSeries.getY(i);
        }

        //点集先清空，为了做成新的点集而准备
        mSeries.clear();
        mDataSet.addSeries(mSeries);
        //将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
        //这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点

        mSeries.add(addX, addY);
        for (int k = 0; k < length; k++) {
            mSeries.add(mXAxisValue[k], mYAxisValue[k]);
        }

        //在数据集中添加新的点集
        //		mDataSet.addSeries(mSeries);

        //视图更新，没有这一步，曲线不会呈现动态
        //如果在非UI主线程中，需要调用postInvalidate()，具体参考api
        mChart.invalidate();
    }


    //曲线
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        wakeLock.acquire();
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null)
                throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null)
                throw new NullPointerException();
            if (!processing.compareAndSet(false, true))
                return;
            int width = size.width;
            int height = size.height;
            //图像处理
            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), width, height);
            averagePixel = imgAvg;
            mAveragePixelValueTextView.setText(String.format("平均像素值：%s", String.valueOf(imgAvg)));
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            Log.i(TAG, "onPreviewFrame: imgAvg = " + imgAvg + " rollingAverage = " + rollingAverage);
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    flag = 0;
                    pulseNumberTextView.setText(String.format("脉冲数：%s", String.valueOf(beats)));
                    Log.i(TAG, "BEAT!! beats=" + beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            /* 防止越界 */
            if (averageIndex == averageArraySize)
                averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                //				image.postInvalidate();
            }

            //获取系统结束时间（ms）
            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 2) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180 || imgAvg < 200) {
                    //获取系统开始时间（ms）
                    startTime = System.currentTimeMillis();
                    //beats心跳总数
                    beats = 0;
                    processing.set(false);
                    return;
                }
                Log.e(TAG, "totalTimeInSecs=" + totalTimeInSecs + " beats=" + beats);
                if (beatsIndex == beatsArraySize)
                    beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;
                int beatsArraySum = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArraySum += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArraySum / beatsArrayCnt);
                mBpsTextView.setText(String.valueOf(beatsAvg));
//                mBpsTextView.setText("小毛同志的心率是" + String.valueOf(beatsAvg) + "  zhi:" + String.valueOf(beatsArray.length)
//                        + "    " + String.valueOf(beatsIndex) + "    " + String.valueOf(beatsArraySum) + "    " + String.valueOf(beatsArrayCnt));
                //获取系统时间（ms）
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };

    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.setPreviewCallback(previewCallback);
            } catch (IOException e) {
                Log.e(TAG, "Exception in setPreviewDisplay() ", e);
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    };

    /**
     * 将预览 Surface 的宽高与相机参数中的宽高做比较，最终得出最小预览大小
     *
     * @param width      Surface 的宽（在 Surface 大小或格式改变后获得）
     * @param height     Surface 的高
     * @param parameters 相机参数，包含宽、高等信息
     * @return 最小预览宽高
     */
    private static Camera.Size getSmallestPreviewSize(int width, int height,
                                                      Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea)
                        result = size;
                }
            }
        }
        return result;
    }

}
