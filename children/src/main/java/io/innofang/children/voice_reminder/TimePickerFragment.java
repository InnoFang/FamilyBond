package io.innofang.children.voice_reminder;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import io.innofang.children.R;

/**
 * Author: Inno Fang
 * Time: 2017/9/16 18:17
 * Description:
 */


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView tv = (TextView) getActivity().findViewById(R.id.time_text_view);
        tv.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
    }
}
