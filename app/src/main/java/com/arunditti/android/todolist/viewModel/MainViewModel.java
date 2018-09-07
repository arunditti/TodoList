package com.arunditti.android.todolist.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.arunditti.android.todolist.database.AppDatabase;
import com.arunditti.android.todolist.database.TaskEntry;

import java.util.List;

/**
 * Created by arunditti on 8/29/18.
 */

public class MainViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    //private TaskRepository mTaskRepository;
    // Add a tasks member variable for a list of TaskEntry objects wrapped in a LiveData
    private LiveData<List<TaskEntry>> tasks;

    public MainViewModel(Application application) {
        super(application);
        // In the constructor use the loadAllTasks of the taskDao to initialize the tasks variable
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(LOG_TAG, "Actively retrieving the tasks from the DataBase");
        tasks = database.taskDao().loadAllTasksByPriority();

//        mTaskRepository = TaskRepository.getsInstance(application);
//        tasks = mTaskRepository.loadAllTaskByPriority();

    }

    // Create a getter for the tasks variable
    public LiveData<List<TaskEntry>> getTasks() {
        return tasks;
    }

    public LiveData<List<TaskEntry>> getCompletedTasks() {
        return tasks;
    }
}