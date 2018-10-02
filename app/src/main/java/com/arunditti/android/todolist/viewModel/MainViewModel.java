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

//public class MainViewModel extends AndroidViewModel {
//
//    private static final String LOG_TAG = MainViewModel.class.getSimpleName();
//
//    private TaskRepository mTaskRepository;
//
//    public MainViewModel(Application application) {
//        super(application);
//
//        mTaskRepository = TaskRepository.getsInstance(application);
//
//    }
//
//    // Create a getter for the tasks variable
//    public LiveData<List<TaskEntry>> getTaskByDueDate() {
//        return mTaskRepository.loadAllTasksByDueDate();
//    }
//
//    public LiveData<List<TaskEntry>> getTasksByPriority() {
//        return mTaskRepository.loadAllTaskByPriority();
//    }
//
//    public LiveData<List<TaskEntry>> getTaskByCategory(String category) {
//         return mTaskRepository.loadTaskByCategory(category);
//    }
//
//    public LiveData<Integer> isTaskCompleted(boolean taskCompleted) {
//        return mTaskRepository.isTaskCompleted(taskCompleted);
//    }
//
//    public LiveData<List<TaskEntry>> getTaskCompleted() {
//        return mTaskRepository.loadCompletedTask();
//    }
//
//}

public class MainViewModel extends AndroidViewModel {

    private static final String LOG_TAG = MainViewModel.class.getSimpleName();

    private TaskRepository mTaskRepository;
    String category;

    //Add a tasks member variable for a list of TaskEntry objects wrapped in a LiveData
    private LiveData<List<TaskEntry>> tasks1;
    private LiveData<List<TaskEntry>> tasks2;
    private LiveData<List<TaskEntry>> tasks3;
    private LiveData<List<TaskEntry>> tasks4;

    AppDatabase database = AppDatabase.getInstance(this.getApplication());
    public MainViewModel(Application application) {
        super(application);
                mTaskRepository = TaskRepository.getsInstance(application);
        // In the constructor use the loadAllTasks of the taskDao to initialize the tasks variable
        //AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(LOG_TAG, "Actively retrieving the tasks from the DataBase");
//        tasks1 = database.taskDao().loadAllTasksByPriority();
//        tasks2 = database.taskDao().loadAllTasksByDueDate();
//        tasks3 = database.taskDao().loadCompletedTask();
//        tasks4 =database.taskDao().loadTaskByCategory(category);


        tasks1 = mTaskRepository.loadAllTaskByPriority();
        tasks2 = mTaskRepository.loadAllTasksByDueDate();
        tasks3 = mTaskRepository.loadCompletedTask();
        tasks4 = mTaskRepository.loadTaskByCategory(category);

//        mTaskRepository = TaskRepository.getsInstance(application);
//        tasks = mTaskRepository.loadAllTasks();
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
        return database.taskDao().loadTaskByCategory(category);
    }

}