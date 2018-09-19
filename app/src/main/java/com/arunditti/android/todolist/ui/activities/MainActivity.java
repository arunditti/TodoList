package com.arunditti.android.todolist.ui.activities;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.arunditti.android.todolist.R;
import com.arunditti.android.todolist.database.AppDatabase;
import com.arunditti.android.todolist.database.TaskEntry;
import com.arunditti.android.todolist.database.TaskRepository;
import com.arunditti.android.todolist.ui.adapter.TaskAdapter;
import com.arunditti.android.todolist.utils.AppExecutors;
import com.arunditti.android.todolist.viewModel.MainViewModel;
import com.arunditti.android.todolist.widget.TodoListWidgetProvider;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener, AdapterView.OnItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;

   // private TaskRepository mTaskRepository;
    private AppDatabase mDb;

    MainViewModel viewModel;
    private TaskRepository mTaskRepository;

    View emptyView;
    private int mTaskIndex;

    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

//    @BindView(R.id.spinner_main_activity)
    private Spinner mSpinnerMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(MainActivity.this, "You're now signed in. Welcome to your Todo List.", Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mSpinnerMain = findViewById(R.id.spinner_main_activity);
        // Initialize member variable for the data base
        mDb = AppDatabase.getInstance(getApplicationContext());
        setupSpinner();
        mSpinnerMain.setSelection(0);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.rv_tasks_list);
        emptyView = findViewById(R.id.empty_view);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                // Get the diskIO Executor from the instance of AppExecutors and
                // call the diskIO execute method with a new Runnable and implement its run method
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // get the position from the viewHolder parameter
                        int position = viewHolder.getAdapterPosition();
                        List<TaskEntry> tasks = mAdapter.getTasks();
                        // Call deleteTask in the taskDao with the task at that position
                        mDb.taskDao().deleteTask(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        mDb = AppDatabase.getInstance(getApplicationContext());
        //Call retrieveTAsks
        setupViewModelByDate();


        Intent widgetIntent = new Intent(MainActivity.this, TodoListWidgetProvider.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        getApplicationContext().sendBroadcast(widgetIntent);

        Toast.makeText(this, "Widget is added", Toast.LENGTH_SHORT).show();
        setupTaskSharedPreferences();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {

                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void setupSpinner() {

                String[] categories = {
                        "All",
                "Groceries",
                "Shopping",
                "School",
                "Assignments"
        };

        ArrayList<String> spinnerList = new ArrayList<>(Arrays.asList(categories));

        // Category spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMain.setAdapter(adapter);

        //Spinner click listener
        mSpinnerMain.setSelection(mTaskIndex, true);
        mSpinnerMain.setOnItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(LOG_TAG, "onStart: Preferences were updated");
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(startSettingsActivity);
                break;

            case R.id.task_by_due_date:
                setupViewModelByDate();
                break;

            case R.id.task_by_priority:
                setupViewModelByPriority();
                break;

            case R.id.task_completed:
                //viewModel.setFiltering(TasksFilterType.TASK_COMPLETED);

                break;

            case R.id.sign_out_menu:
                //Sign out
                AuthUI.getInstance().signOut(this);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    private void setupViewModelByDate() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // Observe the LiveData object in the ViewModel

        viewModel.getTaskByDueDate().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
                setEmptyView(taskEntries);
            }
        });
    }

    private void setupViewModelByPriority() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // Observe the LiveData object in the ViewModel

        viewModel.getTasksByPriority().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
                setEmptyView(taskEntries);
            }
        });
    }

    public void setEmptyView(List<TaskEntry> taskEntries) {
        if (taskEntries.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity with itemId as extra in the intent for the key AddTaskActivity.EXTRA_TASK_ID
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0) {
                viewModel.getTaskByDueDate().observe(this, new Observer<List<TaskEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                        Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");
                        mAdapter.setTasks(taskEntries);
                        setEmptyView(taskEntries);
                    }
                });
        } else {
            String string = parent.getItemAtPosition(position).toString();

            viewModel.getTaskByCategory(string).observe(this, new Observer<List<TaskEntry>>() {
                @Override
                public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                    Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");
                    mAdapter.setTasks(taskEntries);
                    setEmptyView(taskEntries);
                }
            });
        }
// Showing selected spinner item
        mTaskIndex = position;
        Toast.makeText(parent.getContext(), "Selected: " + mTaskIndex, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setupTaskSharedPreferences() {
        String sortBy;
        SharedPreferences sharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);

        String keyForTask = getString(R.string.pref_sort_by_key);
        String defaultTask = getString(R.string.pref_sort_by_default_value);
        sortBy = sharedPreferences.getString(keyForTask, defaultTask);

        if (sortBy.equals(getString(R.string.pref_sort_by_due_date_value))) {
            setupViewModelByDate();
        } else if (sortBy.equals(getString(R.string.pref_sort_by_priority_value))) {
            setupViewModelByPriority();

        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "Preferences are updated");
        PREFERENCES_HAVE_BEEN_UPDATED = true;
        setupTaskSharedPreferences();
    }
}
