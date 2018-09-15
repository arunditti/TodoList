package com.arunditti.android.todolist.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by arunditti on 8/28/18.
 */

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task ORDER BY priority")
    LiveData<List<TaskEntry>> loadAllTasksByPriority();

    @Query("SELECT * FROM task ORDER BY dueDate")
    LiveData<List<TaskEntry>> loadAllTasksByDueDate();

    @Query("SELECT * FROM task ORDER BY category")
    LiveData<List<TaskEntry>> loadAllTasksByCategory();

    @Query("SELECT * FROM task WHERE id = :id")
    LiveData<TaskEntry> loadTaskById(int id);

    @Query("SELECT * FROM task ORDER BY dueDate")
    List<TaskEntry> loadAllTasksForWidget();


    @Query("UPDATE task SET completed = :completed WHERE id = :id")
    void updateCompleted(String id, boolean completed);

    @Query("DELETE FROM task WHERE completed = 1")
    int deleteCompletedTasks();

    @Query("DELETE FROM Task WHERE id = :id")
    int deleteTaskById(int id);

    @Insert
    void insertTask(TaskEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(TaskEntry taskEntry);

    @Delete
    void deleteTask(TaskEntry taskEntry);

    @Query("DELETE FROM task")
    void deleteAllTask();

}
