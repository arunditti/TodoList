package com.arunditti.android.todolist.ui.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.arunditti.android.todolist.ui.activities.AddTaskActivity;

import java.util.Calendar;

/**
 * Created by arunditti on 9/4/18.
 */

public class DatePickerFragment extends DialogFragment {

    public static final String LOG_TAG = DatePickerDialog.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int mon = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), (AddTaskActivity)getActivity(), year, mon, day);
    }

}
