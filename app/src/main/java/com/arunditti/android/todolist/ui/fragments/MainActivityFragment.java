package com.arunditti.android.todolist.ui.fragments;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arunditti.android.todolist.R;
import com.arunditti.android.todolist.database.AppDatabase;
import com.arunditti.android.todolist.database.TaskEntry;
import com.arunditti.android.todolist.database.TaskRepository;
import com.arunditti.android.todolist.sync.ReminderUtilities;
import com.arunditti.android.todolist.ui.activities.AddTaskActivity;
import com.arunditti.android.todolist.ui.activities.MainActivity;
import com.arunditti.android.todolist.ui.activities.SettingsActivity;
import com.arunditti.android.todolist.ui.adapter.TaskAdapter;
import com.arunditti.android.todolist.utils.AppExecutors;
import com.arunditti.android.todolist.viewModel.MainViewModel;
import com.arunditti.android.todolist.widget.TodoListWidgetProvider;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * Created by arunditti on 9/19/18.
 */

public class MainActivityFragment extends Fragment implements TaskAdapter.ItemClickListener,
        AdapterView.OnItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private static final String RECYCLER_VIEW_STATE = "recycler_view_state";

    private static Parcelable mRecyclerViewState;

    private static final String TASK_KEY = "task_key";


    private TaskAdapter mAdapter;
    private Toast mToast;
    private AppDatabase mDb;
    MainViewModel viewModel;
    private TaskRepository mTaskRepository;
    private int mTaskIndex;
    private String title;
    private String description;
    //Interface that triggers a callback in the host activity
    onTaskClickListener mCallBack;

    ArrayList<TaskEntry> mTask = new ArrayList<TaskEntry>();


    @BindView(R.id.spinner_main_activity) Spinner mSpinnerMain;
    @BindView(R.id.rv_tasks_list) RecyclerView mRecyclerView;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.adView) AdView mAdView;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            viewModel.getTaskByDueDate().observe(this, new Observer<List<TaskEntry>>() {
                @Override
                public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                    Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");
                    mAdapter.setTasks(taskEntries);
                    setEmptyView(taskEntries);
                }
            });

        } else {
            String category = parent.getItemAtPosition(position).toString();
            setupViewModelByCategory(category);
//
//            viewModel.getTaskByCategory(category).observe(this, new Observer<List<TaskEntry>>() {
//                @Override
//                public void onChanged(@Nullable List<TaskEntry> taskEntries) {
//                    Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");
//                    mAdapter.setTasks(taskEntries);
//                    setEmptyView(taskEntries);
//                }
//            });

        }
// Showing selected spinner item
        mTaskIndex = position;
        Toast.makeText(parent.getContext(), "Selected: " + mTaskIndex, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Interface that triggers a callback in the host activity
    public interface onTaskClickListener {
        void onTaskSelected(int taskId);
    }

    @Override
    public void onItemClickListener(int taskId) {
        mCallBack.onTaskSelected(taskId);
    }

    //Override onAttach to make sure that the conteiner activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //This makes sure that the host activity has implemented the callback interface. If not, it throws an exception
        try {
            mCallBack = (onTaskClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnImageClickListener");
        }
    }

    //Mandatory empty constructor
    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        //Inflate MainActivityFragment layout
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        // get test ads on a physical device.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        // Initialize member variable for the data base
        mDb = AppDatabase.getInstance(getContext());
        setupSpinner();
        mSpinnerMain.setSelection(0);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);
//
//        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), VERTICAL);
//        mRecyclerView.addItemDecoration(decoration);

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
//                AppExecutors.getInstance().diskIO().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        // get the position from the viewHolder parameter
//                        int position = viewHolder.getAdapterPosition();
//                        List<TaskEntry> tasks = mAdapter.getTasks();
//                        // Call deleteTask in the taskDao with the task at that position
//                        mDb.taskDao().deleteTask(tasks.get(position));
//                    }
//                });
//            }
//        }).attachToRecyclerView(mRecyclerView);

                // get the position from the viewHolder parameter
                int position = viewHolder.getAdapterPosition();
                List<TaskEntry> tasks = mAdapter.getTasks();
                viewModel.deleteTask(tasks.get(position));
            }
            }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = rootView.findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(getActivity(), AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        setupViewModelByDate();

        Intent widgetIntent = new Intent(getActivity(), TodoListWidgetProvider.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        getContext().sendBroadcast(widgetIntent);

        Toast.makeText(getActivity(), "Widget is added", Toast.LENGTH_SHORT).show();

        //Register MainActivity as an OnPreferenceChangedListener
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);
        setupTaskSharedPreferences();

        //Schedule the reminder job
        ReminderUtilities.scheduleReminder(getActivity());

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent startSettingsActivity = new Intent(getActivity(), SettingsActivity.class);
                startActivity(startSettingsActivity);
                break;

            case R.id.completed_task:
                setupViewModelByTaskCompleted();
                //viewModel.getTaskCompleted();


                break;

            case R.id.delete_completed_task:

                DialogInterface.OnClickListener discardButtonClickListener1 =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        // get the position from the viewHolder parameter
                                        List<TaskEntry> tasks = mAdapter.getTasks();
                                        mDb.taskDao().deleteCompletedTasks();
                                    }
                                });
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener1);
                break;

            case R.id.delete_all_task:

//                DialogInterface.OnClickListener discardButtonClickListener =
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                AppExecutors.getInstance().diskIO().execute(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        // get the position from the viewHolder parameter
//                                        List<TaskEntry> tasks = mAdapter.getTasks();
//                                        // Call deleteTask in the taskDao with the task at that position
//                                        mDb.taskDao().deleteAllTask();
//                                    }
//                                });
//                            }
//                        };
//                showUnsavedChangesDialog(discardButtonClickListener);

                viewModel.deleteAllTasks();
                break;

            case R.id.sign_out_menu:
                //Sign out
                AuthUI.getInstance().signOut(getActivity());
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.warning_delete_all);
        builder.setPositiveButton(R.string.yes_delete_all_tasks, discardButtonClickListener);
        builder.setNegativeButton(R.string.discard_delete_all, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog and continue editing the task.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void setupViewModelByDate() {

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

        viewModel.getTasksByPriority().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
                setEmptyView(taskEntries);
            }
        });
    }

    private void setupViewModelByTaskCompleted() {

        viewModel.getTaskCompleted().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                Log.d(LOG_TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
                setEmptyView(taskEntries);
            }
        });
    }

    private void setupViewModelByCategory(String category) {

        viewModel.getTaskByCategory(category).observe(this, new Observer<List<TaskEntry>>() {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMain.setAdapter(adapter);

        //Spinner click listener
        mSpinnerMain.setSelection(mTaskIndex, true);
        mSpinnerMain.setOnItemSelectedListener(this);
    }


    public void setupTaskSharedPreferences() {
        String sortBy;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

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

    @Override
    public void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(LOG_TAG, "onStart: Preferences were updated");
            PREFERENCES_HAVE_BEEN_UPDATED = false;
            setupTaskSharedPreferences();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupTaskSharedPreferences();

    }
    @Override
    public void onPause() {
        super.onPause();
        setupTaskSharedPreferences();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}