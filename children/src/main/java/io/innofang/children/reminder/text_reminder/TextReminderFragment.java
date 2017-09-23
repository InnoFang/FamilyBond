package io.innofang.children.reminder.text_reminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.innofang.base.base.BaseFragment;
import io.innofang.children.R;
import io.innofang.children.R2;

/**
 * Author: Inno Fang
 * Time: 2017/9/23 11:01
 * Description:
 */


public class TextReminderFragment extends BaseFragment implements TextReminderContract.View {

    @BindView(R2.id.reminder_content_edit_text)
    EditText mReminderContentEditText;
    @BindView(R2.id.reminder_settings_list_view)
    ListView mReminderSettingsListView;
    Unbinder unbinder;
    @BindView(R2.id.send_reminder_fab)
    FloatingActionButton mSendReminderFab;

    private TextReminderContract.Presenter mPresenter;
    private SimpleAdapter mAdapter;
    private String mTime, mDate;
    private int mRepeatMode;
    private Map<String, String> mReminderTime, mReminderDate, mReminderRepeat;
    private Calendar mAlertTime;
    private List<Map<String, String>> mapList;

    private static final String NONE = "不重复";
    private static final String HOURLY = "每小时";
    private static final String DAILY = "每天";
    private static final String WEEKLY = "每周";
    private static final String MONTHLY = "每月";
    private static final String YEARLY = "每年";

    private static final String[] REPEAT_MODES =
            new String[]{NONE, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY};

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm aa", Locale.CHINA);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy", Locale.CHINA);

    public static TextReminderFragment newInstance() {

        Bundle args = new Bundle();

        TextReminderFragment fragment = new TextReminderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new TextReminderPresenter(this);
        mapList = new ArrayList<>();
        mReminderTime = new HashMap<>();
        mReminderDate = new HashMap<>();
        mReminderRepeat = new HashMap<>();

        mRepeatMode = 0;

        mAlertTime = Calendar.getInstance();

        Calendar current = Calendar.getInstance();
        mTime = TIME_FORMAT.format(current.getTime());
        mDate = DATE_FORMAT.format(current.getTime());
        mAlertTime.setTimeInMillis(current.getTimeInMillis());

        mReminderTime.put("mTitle", "时间");
        mReminderTime.put("subtext", mTime);
        mReminderDate.put("mTitle", "日期");
        mReminderDate.put("subtext", mDate);
        mReminderRepeat.put("mTitle", "重复");
        mReminderRepeat.put("subtext", REPEAT_MODES[mRepeatMode]);


        mapList.add(mReminderTime);
        mapList.add(mReminderDate);
        mapList.add(mReminderRepeat);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_reminder, container, false);
        unbinder = ButterKnife.bind(this, view);

        mAdapter = new SimpleAdapter(getContext(), mapList, android.R.layout.simple_list_item_2,
                new String[]{"mTitle", "subtext"}, new int[]{android.R.id.text1, android.R.id.text2});
        mReminderSettingsListView.setAdapter(mAdapter);

        mReminderSettingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //if first item in list (the mTime), then show timePickerDialog
                if (i == 0) {
                    TimePickerDialog timePicker = timePicker();
                    timePicker.show();
                    //if second item in list (the mDate), then show datePicker dialog
                } else if (i == 1) {
                    DatePickerDialog datePicker = datePicker();
                    datePicker.show();
                } else {
                    repeatDialog().show();
                }

            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    // mTime picker
    private TimePickerDialog timePicker() {
        return new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        mAlertTime.set(Calendar.HOUR_OF_DAY, hour);
                        mAlertTime.set(Calendar.MINUTE, minute);
                        mAlertTime.set(Calendar.SECOND, 0);
                        mTime = TIME_FORMAT.format(mAlertTime.getTime());
                        mReminderTime.put("subtext", mTime);
                        mAdapter.notifyDataSetChanged();
                    }
                }, mAlertTime.get(Calendar.HOUR_OF_DAY), mAlertTime.get(Calendar.MINUTE), false);
    }

    // mDate picker
    private DatePickerDialog datePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        mAlertTime.set(Calendar.YEAR, year);
                        mAlertTime.set(Calendar.MONTH, month);
                        mAlertTime.set(Calendar.DAY_OF_MONTH, day);
                        mDate = DATE_FORMAT.format(mAlertTime.getTime());
                        mReminderDate.put("subtext", mDate);
                        mAdapter.notifyDataSetChanged();
                    }
                }, mAlertTime.get(Calendar.YEAR), mAlertTime.get(Calendar.MONTH),
                mAlertTime.get(Calendar.DAY_OF_MONTH));
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePicker;
    }


    // set repeat mode from None, Hour, Daily, Monthly, Yearly
    private AlertDialog repeatDialog() {
        final int prevRepeat = mRepeatMode;
        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.repeat)
                .setSingleChoiceItems(REPEAT_MODES, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        mRepeatMode = i;
                    }
                })
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // set label to selected repeat mode
                        mReminderRepeat.put("subtext", REPEAT_MODES[mRepeatMode]);
                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        mRepeatMode = prevRepeat;
                    }
                })
                .create();
    }

    @OnClick(R2.id.send_reminder_fab)
    public void onViewClicked() {
        mPresenter.chooseContact();
    }

    @Override
    public void setPresenter(TextReminderContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public FloatingActionButton getSendReminderButton() {
        return mSendReminderFab;
    }

    @Override
    public void showInfo(String text) {
        Snackbar.make(mSendReminderFab, text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public String getReminderText() {
        return mReminderContentEditText.getText().toString();
    }
}
