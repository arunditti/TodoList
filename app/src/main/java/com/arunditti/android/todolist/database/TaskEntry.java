package com.arunditti.android.todolist.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;

import java.util.Date;

/**
 * Created by arunditti on 8/28/18.
 */

@Entity(tableName = "task")
public class TaskEntry {

    public final static boolean TASK_COMPLETED = true;
    public final static boolean TASK_NOT_COMPLETED = false;

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @Nullable
    @ColumnInfo(name = "description")
    private String description;

    @Nullable
    @ColumnInfo(name = "category")
    private String category;

    @Nullable
    @ColumnInfo(name = "priority")
    private int priority;

    @Nullable
    @ColumnInfo(name = "Completed")
    private boolean completed;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @ColumnInfo(name = "DueDate")
    private Date dueDate;

    @Ignore
    public TaskEntry(String title, String description, int priority, Date updatedAt, Date dueDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.completed = completed;
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
    }

    @Ignore
    public TaskEntry(boolean completed) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.completed = completed;
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
    }


    @Ignore
    public TaskEntry(String title, String description, int priority, boolean completed) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.completed = completed;
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
    }

    @Ignore
    public TaskEntry(String title, String description, int priority, boolean completed, Date updatedAt, Date dueDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.completed = completed;
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
    }

    // Use the Ignore annotation so Room knows that it has to use the other constructor instead
    @Ignore
    public TaskEntry(String title, String description, String category, int priority, boolean completed, Date updatedAt, Date dueDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.completed = completed;
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
    }

    public TaskEntry(int id, String title, String description, String category, int priority, boolean completed, Date updatedAt, Date dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.completed = completed;
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean getActive() {
        return !completed;
    }

    public void setActive(boolean completed) {
        this.completed = !completed;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
