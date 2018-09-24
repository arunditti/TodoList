package com.arunditti.android.todolist.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.widget.Spinner;

import java.util.List;

/**
 * Created by arunditti on 9/4/18.
 */

public class TaskRepository {

    private static final Object LOCK = new Object();
    private final TaskDao mTaskDao;
    private LiveData<List<TaskEntry>> taskEntries;
    private static TaskRepository sInstance;

    public TaskRepository(Application application) {
        AppDatabase mDb = AppDatabase.getInstance(application.getApplicationContext());
        mTaskDao = mDb.taskDao();
        taskEntries = mTaskDao.loadAllTasksByDueDate();
    }

    public synchronized static TaskRepository getsInstance(Application application) {
        if(sInstance == null) {
            synchronized (LOCK) {
                sInstance = new TaskRepository(application);
            }
        }
        return sInstance;
    }

    public LiveData<List<TaskEntry>> loadAllTaskByPriority() {
        return mTaskDao.loadAllTasksByPriority();
    }

    public LiveData<List<TaskEntry>> loadAllTasksByDueDate() {
        return mTaskDao.loadAllTasksByDueDate();
    }

    public LiveData<List<TaskEntry>> loadTaskByCategory(String category) {
        return mTaskDao.loadTaskByCategory(category);
    }

    public LiveData<TaskEntry> loadTaskById(int id) {
        return mTaskDao.loadTaskById(id);
    }

    public LiveData<Integer> isTaskCompleted(boolean taskCompleted) {
        return mTaskDao.isTaskCompleted(taskCompleted);
    }

    public LiveData<List<TaskEntry>> loadCompletedTask() {
        return mTaskDao.loadCompletedTask();
    }
}
