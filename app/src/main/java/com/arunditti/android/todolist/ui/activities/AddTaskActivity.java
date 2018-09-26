package com.arunditti.android.todolist.ui.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.arunditti.android.todolist.R;
import com.arunditti.android.todolist.database.AppDatabase;
import com.arunditti.android.todolist.database.TaskEntry;
import com.arunditti.android.todolist.ui.fragments.DatePickerFragment;
import com.arunditti.android.todolist.utils.AppExecutors;
import com.arunditti.android.todolist.viewModel.AddTaskViewModel;
import com.arunditti.android.todolist.viewModel.AddTaskViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String LOG_TAG = AddTaskActivity.class.getSimpleName();

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constants for priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;

    // Constant for date format
    private static final String DATE_FORMAT = "MM/dd/yyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    private boolean isCompleted;

    private Date mDate;
    private Calendar mCalender;
    private Date mDueDate;
    private int mTaskId = DEFAULT_TASK_ID;

    Spinner spinner;
    private String[] categories = {
            "Groceries",
            "Shopping",
            "School",
            "Assignments"
    };

    public ArrayList<String> spinnerList = new ArrayList<>(Arrays.asList(categories));

    // Create AppDatabase member variable for the Database
    // Member variable for the Database
    private AppDatabase mDb;

    @BindView(R.id.appbar_activity_add_task) AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_activity_add_task) Toolbar toolbar;
    @BindView(R.id.editTextTaskTitle) EditText mEditTextTitle;
    @BindView(R.id.editTextTaskDescription) EditText mEditTextDescription;
    @BindView(R.id.spinner) Spinner mSpinnerCategory;
    @BindView(R.id.radioGroup) RadioGroup mRadioGroup;
    @BindView(R.id.completed) AppCompatCheckBox mCheckbox;
    @BindView(R.id.tv_due_date) TextView mTextViewDueDate;
    @BindView(R.id.button_date) Button mDateDialog;
//    @BindView(R.id.button_save) Button mButton;

    private boolean mTaskHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mTaskHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        ButterKnife.bind(this);


        mEditTextTitle.setOnTouchListener(mTouchListener);
        mEditTextDescription.setOnTouchListener(mTouchListener);
        mRadioGroup.setOnTouchListener(mTouchListener);
        mSpinnerCategory.setOnTouchListener(mTouchListener);
        mCheckbox.setOnTouchListener(mTouchListener);
        mDateDialog.setOnTouchListener(mTouchListener);
        mTextViewDueDate.setOnTouchListener(mTouchListener);


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_activity_add_task);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.tv_edit_task_title));

        spinner = findViewById(R.id.spinner);

        // Category spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        initViews();

        // Initialize member variable for the data base
        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            //mButton.setText(R.string.update_button);
            getSupportActionBar().setTitle(getString(R.string.tv_update_task_title));

            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                //  Assign the value of EXTRA_TASK_ID in the intent to mTaskId
                // Use DEFAULT_TASK_ID as the default
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);

                // Declare a AddTaskViewModelFactory using mDb and mTaskId
                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mDb, mTaskId);
                // Declare a AddTaskViewModel variable and initialize it by calling ViewModelProviders.of
                // for that use the factory created above AddTaskViewModel
                final AddTaskViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(AddTaskViewModel.class);

                // Observe the LiveData object in the ViewModel. Use it also when removing the observer
                viewModel.getTask().observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(@Nullable TaskEntry taskEntry) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(taskEntry);
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {

        //mEditTextTitle = findViewById(R.id.editTextTaskTitle);
       // mEditTextDescription = findViewById(R.id.editTextTaskDescription);
        mSpinnerCategory = findViewById(R.id.spinner);
        //mRadioGroup = findViewById(R.id.radioGroup);
        //mCheckbox = findViewById(R.id.completed);
        mTextViewDueDate = findViewById(R.id.tv_due_date);

        mCalender = Calendar.getInstance();

        //mDateDialog = findViewById(R.id.button_date);
        mDateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDatePickerDialog.show();
                showDatePickerDialog();
            }
        });
//
//        //mButton = findViewById(R.id.button_save);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onSaveButtonClicked();
//            }
//        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {
        // return if the task is null
        if (task == null) {
            return;
        }

        // use the variable task to populate the UI
        mEditTextTitle.setText(task.getTitle());
        mEditTextDescription.setText(task.getDescription());
        mSpinnerCategory.setSelection(spinnerList.indexOf(task.getCategory()));
        setPriorityInViews(task.getPriority());

        isCompleted = task.getCompleted();
//        if(isCompleted == TaskEntry.TASK_COMPLETED) {
//            isCompleted = TaskEntry.TASK_COMPLETED;
//        } else {
//            isCompleted = TaskEntry.TASK_NOT_COMPLETED;
//        }
        mCheckbox.setChecked(isCompleted);

        final String dueDate = dateFormat.format(task.getDueDate());
        if(dueDate.isEmpty()) {
            mTextViewDueDate.setText("");
        } else {
            mTextViewDueDate.setText(dueDate);
        }

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {

        String title = mEditTextTitle.getText().toString();
        // Create a description variable and assign to it the value in the edit text
        String description = mEditTextDescription.getText().toString();

        String category = spinner.getSelectedItem().toString();
        //Create a priority variable and assign the value returned by getPriorityFromViews()
        int priority = getPriorityFromViews();

        // Create a date variable and assign to it the current Date
        mDate = new Date();

        //mDatePickerDialog.updateDate(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);

        mDueDate = mCalender.getTime();
       // Log.d(LOG_TAG, "************* due date is: " + mDueDate);

        final boolean completed;
        if(mCheckbox.isChecked()) {
            completed = TaskEntry.TASK_COMPLETED;
        } else {
            completed = TaskEntry.TASK_NOT_COMPLETED;
        }
        mCheckbox.setChecked(completed);

        //Make taskEntry final so it is visible inside the run method
        final TaskEntry task = new TaskEntry(title, description, category, priority, completed, mDate, mDueDate);
        //  Get the diskIO Executor from the instance of AppExecutors and
        // call the diskIO execute method with a new Runnable and implement its run method
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // insert the task only if mTaskId matches DEFAULT_TASK_ID
                // Otherwise update it
                // call finish in any case
                if (mTaskId == DEFAULT_TASK_ID) {
                    // insert new task
                    mDb.taskDao().insertTask(task);
                } else {
                    //update task
                    task.setId(mTaskId);
                    Log.d(LOG_TAG, "**********************************position is " + task);
                    mDb.taskDao().updateTask(task);
                }
                finish();
            }
        });
    }

    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }

    /**
     * setPriority is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mCalender.set(Calendar.YEAR, year);
        mCalender.set(Calendar.MONTH, month);
        mCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mDueDate = mCalender.getTime();

//Date one dat before due date
        //mCalender.add(Calendar.DAY_OF_YEAR, -3);
    }

    private void showDatePickerDialog(){
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu_editor, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save the task to database
                onSaveButtonClicked();
                //Exit Activity
                finish();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the task hasn't changed, continue with navigating up to parent activity
                if (!mTaskHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddTaskActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddTaskActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the task hasn't changed, continue with handling back button press
        if (!mTaskHasChanged) {
            super.onBackPressed();
            return;
        }


        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog and continue editing the task.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}