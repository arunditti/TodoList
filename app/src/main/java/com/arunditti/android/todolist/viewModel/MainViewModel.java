package com.arunditti.android.todolist.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;
import android.view.MenuItem;

import com.arunditti.android.todolist.R;
import com.arunditti.android.todolist.database.AppDatabase;
import com.arunditti.android.todolist.database.TaskEntry;
import com.arunditti.android.todolist.database.TaskRepository;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * Created by arunditti on 8/29/18.
 */


public class MainViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    private TaskRepository mTaskRepository;
    String category;

    //Add a tasks member variable for a list of TaskEntry objects wrapped in a LiveData
    private LiveData<List<TaskEntry>> tasks1;
    private LiveData<List<TaskEntry>> tasks2;
    private LiveData<List<TaskEntry>> tasks3;

    public MainViewModel(Application application) {
        super(application);
        mTaskRepository = TaskRepository.getsInstance(application);

        Log.d(LOG_TAG, "Actively retrieving the tasks from the DataBase");
        tasks1 = mTaskRepository.loadAllTaskByPriority();
        tasks2 = mTaskRepository.loadAllTasksByDueDate();
        tasks3 = mTaskRepository.loadCompletedTask();
    }

    // Create a getter for the tasks variable
    public LiveData<List<TaskEntry>> getTasksByPriority() {
        return tasks1;
    }

        public LiveData<List<TaskEntry>> getTaskByDueDate() {
        return tasks2;
    }

    public LiveData<List<TaskEntry>> getTaskCompleted() {
        return tasks3;
    }

    public LiveData<List<TaskEntry>> getTaskByCategory(String category) {
         //return tasks4;
        return mTaskRepository.loadTaskByCategory(category);
    }
//
//    public void completeTask(TaskEntry taskEntry, boolean completed) {
//            mTaskRepository.isTaskCompleted(completed);
//    }

//    public LiveData<Integer> isTaskCompleted(long taskId) {
//        return mTaskRepository.isTaskCompleted(taskId);
//    }
//
//    public void insertTask(TaskEntry taskEntry) {
//        mTaskRepository.insertTask(taskEntry);
//    }
//
//    public void updateTask(TaskEntry taskEntry) {
//        mTaskRepository.updateTask(taskEntry);
//    }

    public void deleteTask(TaskEntry taskEntry) {
        mTaskRepository.deleteTask(taskEntry);
    }

    public void deleteAllTasks() {
        mTaskRepository.deleteAllTask();
    }

}