package com.arunditti.android.todolist.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Spinner;

import com.arunditti.android.todolist.utils.AppExecutors;
import com.google.android.gms.tasks.Task;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by arunditti on 9/4/18.
 */

public class TaskRepository {

    private static final Object LOCK = new Object();
    private final TaskDao mTaskDao;
    private LiveData<List<TaskEntry>> taskEntries;
    private static TaskRepository sInstance;

    private AppExecutors mAppExecutors;

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

//    public LiveData<Integer> isTaskCompleted(boolean taskCompleted) {
//        return mTaskDao.isTaskCompleted(taskCompleted);
//    }

    public LiveData<List<TaskEntry>> loadCompletedTask() {
        return mTaskDao.loadCompletedTask();
    }




    public LiveData<Integer> isTaskCompleted(long taskId) {
        return mTaskDao.isTaskCompleted(taskId);
    }




//    public void completeTask(@NonNull TaskEntry taskEntry) {
//
//        Runnable completeRunnable = new Runnable() {
//            @Override
//            public void run() {
//                mTaskDao.updateCompleted(taskEntry.getId(), true);
//            }
//        };
//
//        mAppExecutors.diskIO().execute(completeRunnable);
//
//        //taskEntries.put(taskEntry.getId(), completedTask);
//    }


    //    public void loadTasksByCategory(String  category) {
//        new loadTasksByCategoryAsyncTask(mTaskDao).execute(category);
//    }
//
//    private static class loadTasksByCategoryAsyncTask extends AsyncTask<String , Void, List<TaskEntry>> {
//
//        private TaskDao mTaskDao;
//
//        loadTasksByCategoryAsyncTask(TaskDao dao) {
//            this.mTaskDao = dao;
//        }
//        @Override
//        protected List<TaskEntry> doInBackground(String ... params) {
//            mTaskDao.loadTasksByCategory(params[0]);
//            return null;
//        }
//    }

//    public void insertTask(TaskEntry taskEntry) {
//        new insertTaskAsyncTask(mTaskDao).execute(taskEntry);
//    }
//
//    private static class insertTaskAsyncTask extends AsyncTask<TaskEntry, Void, Void> {
//
//        private TaskDao mTaskDao;
//
//        insertTaskAsyncTask(TaskDao dao) {
//            this.mTaskDao = dao;
//        }
//        @Override
//        protected Void doInBackground(TaskEntry... taskEntries) {
//            mTaskDao.insertTask(taskEntries[0]);
//            return null;
//        }
//    }

//    public void updateTask(TaskEntry taskEntry) {
//        new updateTaskAsyncTask(mTaskDao).execute(taskEntry);
//    }
//
//    private static class updateTaskAsyncTask extends AsyncTask<TaskEntry, Void, Void> {
//
//        private TaskDao mTaskDao;
//
//        updateTaskAsyncTask(TaskDao dao) {
//            this.mTaskDao = dao;
//        }
//        @Override
//        protected Void doInBackground(TaskEntry... taskEntries) {
//            mTaskDao.insertTask(taskEntries[0]);
//            return null;
//        }
//    }

//    public void deleteTask(TaskEntry taskEntry) {
//        new DeleteTaskAsyncTask(mTaskDao).execute(taskEntry);
//    }
//
//    private static class DeleteTaskAsyncTask extends AsyncTask<TaskEntry, Void, Void> {
//        private TaskDao taskDao;
//
//        public DeleteTaskAsyncTask(TaskDao mTaskDao) {
//            this.taskDao = mTaskDao;
//        }
//
//        @Override
//        protected Void doInBackground(TaskEntry... taskEntries) {
//            taskDao.deleteTask(taskEntries[0]);
//            return null;
//        }
//    }

//    public void deleteAllTask() {
//        new DeleteTaskAsyncTask(mTaskDao).execute();
//    }
//
//    private static class DeleteAllTaskAsyncTask extends AsyncTask<Void, Void, Void> {
//        private TaskDao taskDao;
//
//        public DeleteAllTaskAsyncTask(TaskDao mTaskDao) {
//            this.taskDao = mTaskDao;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            taskDao.deleteAllTask();
//            return null;
//        }
//    }
}
