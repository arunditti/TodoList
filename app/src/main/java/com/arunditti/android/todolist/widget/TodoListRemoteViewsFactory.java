package com.arunditti.android.todolist.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.arunditti.android.todolist.R;
import com.arunditti.android.todolist.database.AppDatabase;
import com.arunditti.android.todolist.database.TaskEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by arunditti on 9/12/18.
 */

public class TodoListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = TodoListRemoteViewsFactory.class.getSimpleName();

    private Context mContext;
    private AppDatabase database;
    private List<TaskEntry> mTasks;
    private static final String DATE_FORMAT = "MM/dd/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());


    public TodoListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
        //Initialize the database
        database = AppDatabase.getInstance(mContext);
    }

    @Override
    public void onDataSetChanged() {
        mTasks = database.taskDao().loadAllTasksForWidget();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(mTasks != null) {
            return mTasks.size();
        } else
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        TaskEntry taskEntry = mTasks.get(position);

        String title = taskEntry.getTitle();
        String date = dateFormat.format(taskEntry.getDueDate());

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.item_todo_list_widget);

        remoteViews.setTextViewText(R.id.tv_widget_title, title);
        remoteViews.setTextViewText(R.id.tv_widget_due_date, date);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
