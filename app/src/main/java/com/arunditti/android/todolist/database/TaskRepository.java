package com.arunditti.android.todolist.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by arunditti on 9/4/18.
 */

//public class TaskRepository {
//
//    private static final Object LOCK = new Object();
//    private final TaskDao mTaskDao;
//    private static TaskRepository sInstance;
//
//    public TaskRepository(Application application) {
//        AppDatabase mDb = AppDatabase.getInstance(application.getApplicationContext());
//        mTaskDao = mDb.taskDao();
//    }
//
//    public synchronized static TaskRepository getsInstance(Application application) {
//        if(sInstance == null) {
//            synchronized (LOCK) {
//                sInstance = new TaskRepository(application);
//            }
//        }
//        return sInstance;
//    }
//
//    public LiveData<List<TaskEntry>> loadAllTaskByPriority() {
//        return mTaskDao.loadAllTasksByPriority();
//    }
//
//    public LiveData<List<TaskEntry>> loadAllTasksByDueDate() {
//        return mTaskDao.loadAllTasksByDueDate();
//    }
//
//    public LiveData<List<TaskEntry>> loadAllTasksByCategory() {
//        return mTaskDao.loadAllTasksByCategory();
//    }
//
//
//    public LiveData<TaskEntry> loadTaskById(int id) {
//        return mTaskDao.loadTaskById(id);
//    }
//
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
//
//
//    public void deleteTask(TaskEntry id) {
//        new DeleteTaskAsyncTask(mTaskDao).execute(id);
//    }
//
//    private static class DeleteTaskAsyncTask extends AsyncTask<TaskEntry, Void, Void> {
//        private TaskDao taskDao;
//
//        public DeleteTaskAsyncTask(TaskDao mTaskDao) {
//            this.taskDao = mTaskDao;
//        }
//
////        @Override
////        protected Void doInBackground(TaskEntry... taskEntries) {
////            taskDao.deleteTask(taskEntries[0]);
////            return null;
////        }
//
//
//        @Override
//        protected Void doInBackground(TaskEntry... ints) {
//            taskDao.deleteTask(ints[0]);
//            return null;
//        }
//    }
//
//
//}
