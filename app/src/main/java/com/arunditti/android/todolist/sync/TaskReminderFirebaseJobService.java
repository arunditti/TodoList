package com.arunditti.android.todolist.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.arunditti.android.todolist.utils.NotificationUtils;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by arunditti on 9/24/18.
 */

public class TaskReminderFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {

        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                Context context = TaskReminderFirebaseJobService.this;
                NotificationUtils.remindUser(context);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {

                jobFinished(job, false);
            }
        };

        mBackgroundTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}