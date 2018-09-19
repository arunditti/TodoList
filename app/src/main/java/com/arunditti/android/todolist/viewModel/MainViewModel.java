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

import java.util.List;

/**
 * Created by arunditti on 8/29/18.
 */

public class MainViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    private TaskRepository mTaskRepository;

    public MainViewModel(Application application) {
        super(application);

        mTaskRepository = TaskRepository.getsInstance(application);

    }

    // Create a getter for the tasks variable
    public LiveData<List<TaskEntry>> getTaskByDueDate() {
        return mTaskRepository.loadAllTasksByDueDate();
    }

    public LiveData<List<TaskEntry>> getTasksByPriority() {
        return mTaskRepository.loadAllTaskByPriority();
    }

    public LiveData<List<TaskEntry>> getTaskByCategory(String category) {
         return mTaskRepository.loadTaskByCategory(category);
    }

    public LiveData<Integer> isTaskCompleted(boolean taskCompleted) {
        return mTaskRepository.isTaskCompleted(taskCompleted);
    }

}