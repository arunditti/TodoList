package com.arunditti.android.todolist.ui.adapter;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arunditti.android.todolist.R;
import com.arunditti.android.todolist.database.AppDatabase;
import com.arunditti.android.todolist.database.TaskEntry;
import com.arunditti.android.todolist.ui.activities.AddTaskActivity;
import com.arunditti.android.todolist.ui.activities.MainActivity;
import com.arunditti.android.todolist.utils.AppExecutors;
import com.arunditti.android.todolist.viewModel.AddTaskViewModelFactory;
import com.arunditti.android.todolist.viewModel.MainViewModel;
import com.firebase.ui.auth.AuthUI;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * Created by arunditti on 8/28/18.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private static final String LOG_TAG = AddTaskActivity.class.getSimpleName();

    // Constant for date format
    private static final String DATE_FORMAT = "MM/dd/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<TaskEntry> mTaskEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private boolean isCompleted;
    private static final int DEFAULT_TASK_ID = -1;
    private int mTaskId = DEFAULT_TASK_ID;
    private AppDatabase mDb;


    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    //Constructor for the TaskAdapter that initializes the context
    public TaskAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final TaskAdapter.TaskViewHolder holder, int position) {

        mDb = AppDatabase.getInstance(getApplicationContext());
        //Determine the values of the wanted data
        final TaskEntry taskEntry = mTaskEntries.get(position);
        final String title = taskEntry.getTitle();
        final String description = taskEntry.getDescription();
        final String category = taskEntry.getCategory();
        final int priority = taskEntry.getPriority();

        final String updatedAt = dateFormat.format(taskEntry.getUpdatedAt());
        final String dueDate = dateFormat.format(taskEntry.getDueDate());

        //Set values
        holder.titleView.setText(title);
        holder.descriptionView.setText(description);
        holder.categoryView.setText(category);

        holder.updatedAtView.setText(updatedAt);
        holder.dueDateView.setText(dueDate);

//        if(!dueDate.isEmpty()) {
//            holder.dueDateView.setText(dueDate);
//        } else {
//            holder.dueDateView.setText(" ");
//        }

        //Programmatically set the text and color for the priority TextView
        String priorityString = "" + priority;
        holder.priorityView.setText(priorityString);
        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
        //Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);

        isCompleted = taskEntry.getCompleted();
        Log.d(LOG_TAG, "************** iscompleted = " + isCompleted);
//        holder.checkBoxView.setChecked(isCompleted);

        MainViewModel viewModel = ViewModelProviders.of((FragmentActivity) mContext).get(MainViewModel.class);

//        position = holder.getAdapterPosition();
//        // Observe the LiveData object in the ViewModel
//        viewModel.getCompletedTasks().observe((LifecycleOwner) mContext, new Observer<List<TaskEntry>>() {
//            @Override
//            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
//                Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");
//                if(isCompleted = taskEntry.getCompleted()) {
//                    holder.checkBoxView.setChecked(isCompleted);
//                }
//            }
//        });


       // MainViewModel viewModel = ViewModelProviders.of((FragmentActivity) mContext).get(MainViewModel.class);

        position = holder.getAdapterPosition();

         //Observe the LiveData object in the ViewModel
        viewModel.isTaskCompleted(isCompleted).observe((LifecycleOwner) mContext, isChecked -> {
            if(holder.checkBoxView.isChecked()) {
                holder.checkBoxView.setChecked(true);
            } else {
               holder.checkBoxView.setChecked(false);
            }
        });

    }

    /*
  Helper method for selecting the correct priority circle color.
  P1 = red, P2 = orange, P3 = yellow
  */
    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch (priority) {
            case 1:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default:
                break;
        }
        return priorityColor;
    }

    @Override
    public int getItemCount() {
        if (mTaskEntries == null) {
            return 0;
        }
        return mTaskEntries.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Class variables for the task
        TextView titleView;
        TextView descriptionView;
        TextView categoryView;
        TextView priorityView;
        AppCompatCheckBox checkBoxView;
        TextView updatedAtView;
        TextView dueDateView;

        public TaskViewHolder(View itemView) {
            super(itemView);

            titleView = itemView.findViewById(R.id.tv_taskTitle);
            descriptionView = itemView.findViewById(R.id.tv_taskDescription);
            categoryView = itemView.findViewById(R.id.tv_category);
            updatedAtView = itemView.findViewById(R.id.tv_taskUpdatedAt);
            dueDateView = itemView.findViewById(R.id.tv_todo_due_date);
            priorityView = itemView.findViewById(R.id.tv_priority);
            checkBoxView = itemView.findViewById(R.id.completed);
            itemView.setOnClickListener(this);
            //checkBoxView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int elementId = mTaskEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }

    // Add a getTasks method that returns mTaskEntries
    public List<TaskEntry> getTasks() {
        return mTaskEntries;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTasks(List<TaskEntry> taskEntries) {
        mTaskEntries = taskEntries;
        notifyDataSetChanged();
    }
}
