package io.innofang.children.reminder.voice_reminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.innofang.base.base.BaseFragment;
import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/23 11:00
 * Description:
 */


public class VoiceReminderFragment extends BaseFragment implements VoiceReminderContract.View, View.OnClickListener {


    ListView mVoiceReminderSettingsListView;
    FloatingActionButton mVoiceRecordFab;
    ImageView mRecordImageView;
    TextView mVoiceTipsTextView;
    RelativeLayout mRecordLayout;

    private VoiceReminderContract.Presenter mPresenter;
    private SimpleAdapter mAdapter;
    private String mTime, mDate, mContact;
    private int mRepeatMode;
    private Map<String, String> mReminderTime, mReminderDate, mReminderRepeat, mReminderContact;
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

    public static VoiceReminderFragment newInstance() {

        Bundle args = new Bundle();

        VoiceReminderFragment fragment = new VoiceReminderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapList = new ArrayList<>();
        mReminderTime = new HashMap<>();
        mReminderDate = new HashMap<>();
        mReminderRepeat = new HashMap<>();
        mReminderContact = new HashMap<>();

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
        mReminderContact.put("mTitle", "联系人");
        mReminderContact.put("subtext", mContact);


        mapList.add(mReminderTime);
        mapList.add(mReminderDate);
        mapList.add(mReminderRepeat);
        mapList.add(mReminderContact);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voice_reminder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVoiceReminderSettingsListView = (ListView) view.findViewById(R.id.voice_reminder_settings_list_view);
        mVoiceRecordFab = (FloatingActionButton) view.findViewById(R.id.voice_record_fab);
        mRecordImageView = (ImageView) view.findViewById(R.id.record_image_view);
        mVoiceTipsTextView = (TextView) view.findViewById(R.id.voice_tips_text_view);
        mRecordLayout = (RelativeLayout) view.findViewById(R.id.record_layout);


        mVoiceRecordFab.setOnClickListener(this);

        mPresenter = new VoiceReminderPresenter(this);

        mAdapter = new SimpleAdapter(getContext(), mapList, android.R.layout.simple_list_item_2,
                new String[]{"mTitle", "subtext"}, new int[]{android.R.id.text1, android.R.id.text2});
        mVoiceReminderSettingsListView.setAdapter(mAdapter);

        mVoiceReminderSettingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                } else if (i == 2) {
                    repeatDialog().show();
                } else {
                    mPresenter.chooseContact();
                }

            }
        });
    }

    public void onClick(View view) {
        if (TextUtils.isEmpty(mContact)) {
            showInfo(getString(R.string.have_not_choose_a_contact));
        } else {
            mVoiceRecordFab.setOnTouchListener(
                    ((VoiceReminderPresenter) mPresenter).new VoiceTouchListener());
                 /* initialize record manager */
            mPresenter.initRecordManager();
        }
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

    @Override
    public void setPresenter(VoiceReminderContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showInfo(String text) {
        Snackbar.make(mVoiceRecordFab, text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public TextView getVoiceTipsTextView() {
        return mVoiceTipsTextView;
    }

    @Override
    public FloatingActionButton getVoiceRecordFab() {
        return mVoiceRecordFab;
    }

    @Override
    public RelativeLayout getVoiceLayout() {
        return mRecordLayout;
    }

    @Override
    public ImageView getRecordImageView() {
        return mRecordImageView;
    }

    @Override
    public void setVoiceTipsText(@StringRes int id) {
        mVoiceTipsTextView.setText(id);
    }

    @Override
    public void setContact(String contact) {
        mContact = contact;
        mReminderContact.put("subtext", contact);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Drawable[] getVoiceAnimDrawable() {
        return new Drawable[]{
                getResources().getDrawable(R.mipmap.chat_icon_voice2),
                getResources().getDrawable(R.mipmap.chat_icon_voice3),
                getResources().getDrawable(R.mipmap.chat_icon_voice4),
                getResources().getDrawable(R.mipmap.chat_icon_voice5),
                getResources().getDrawable(R.mipmap.chat_icon_voice6)};
    }


    private Toast toast;

    /**
     * 显示录音时间过短的Toast
     */
    @Override
    public Toast showVoiceShortToast() {
        if (toast == null) {
            toast = new Toast(getActivity());
        }
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.include_chat_voice_short, null);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        return toast;
    }
}
