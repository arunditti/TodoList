package com.arunditti.android.todolist.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.arunditti.android.todolist.database.AppDatabase;

/**
 * Created by arunditti on 8/29/18.
 */

public class AddTaskViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    //Add two member variables. One for the database and one for the taskId
    private final AppDatabase mDb;
    private final int mTaskId;


    // Initialize the member variables in the constructor with the parameters received
    public AddTaskViewModelFactory(AppDatabase database, int taskId) {
        this.mDb = database;
        this.mTaskId = taskId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new AddTaskViewModel(mDb, mTaskId);
    }
}
