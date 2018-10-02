package com.arunditti.android.todolist.ui.adapter;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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
import com.firebase.ui.auth.data.model.Resource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.google.android.gms.flags.impl.SharedPreferencesFactory.getSharedPreferences;

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
        void onItemClickListener(int taskId);
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

       // mDb = AppDatabase.getInstance(getApplicationContext());
        //Determine the values of the wanted data
        final TaskEntry taskEntry = mTaskEntries.get(position);
        final String title = taskEntry.getTitle();
        final String description = taskEntry.getDescription();
        final String category = taskEntry.getCategory();
        final int priority = taskEntry.getPriority();

        final String updatedAt = dateFormat.format(taskEntry.getUpdatedAt());
        final String dueDate = dateFormat.format(taskEntry.getDueDate());

        //Set values
        holder.titleView.setText("Title: " + title);
        holder.descriptionView.setText("Description: " + description);
        holder.categoryView.setText("Category: " + category);

        holder.updatedAtView.setText("Updated At: " + updatedAt);
        holder.dueDateView.setText("Due Date: " + dueDate);


        if (dueDate.compareTo(updatedAt) < 0) {
            holder.dueDateView.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        }



//        if(!dueDate.isEmpty()) {
//            holder.dueDateView.setText("Due Date: " + dueDate);
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
        holder.checkBoxView.setChecked(isCompleted);

        ///////////////////////

//        holder.checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(buttonView.isChecked()) {
//                    saveCheckBoxStatus(true);
//                } else {
//                    saveCheckBoxStatus(false);
//                }
//            }
//        });

        ///////////////////////////

        holder.shareTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Check the attached task");
                intent.putExtra(Intent.EXTRA_TEXT, "Title:  " + title +
                        "\nDescription: " + description +
                "\nDue Date: " + dueDate);
                intent.setType("text/plain");
                mContext.startActivity(Intent.createChooser(intent, "Send To"));
            }
        });

    }


//    private void saveCheckBoxStatus(boolean isCompleted) {
//        SharedPreferences sharedPreferences = mContext.getSharedPreferences("CheckBox", MODE_PRIVATE);
//        SharedPreferences.Editor mEditor = sharedPreferences.edit();
//        mEditor.putBoolean("item", isCompleted);
//        mEditor.apply();
//    }

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
        @BindView(R.id.cv_list) CardView cardView;
        @BindView(R.id.tv_taskTitle) TextView titleView;
        @BindView(R.id.tv_taskDescription) TextView descriptionView;
        @BindView(R.id.tv_category) TextView categoryView;
        @BindView(R.id.tv_taskUpdatedAt) TextView updatedAtView;
        @BindView(R.id.tv_todo_due_date) TextView dueDateView;
        @BindView(R.id.tv_priority) TextView priorityView;
        @BindView(R.id.completed) AppCompatCheckBox checkBoxView;
        @BindView(R.id.share_task) ImageButton shareTask;

        public TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
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