package com.arunditti.android.todolist.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.arunditti.android.todolist.R;
import com.arunditti.android.todolist.ui.activities.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class TodoListWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.todo_list_widget_provider);

        CharSequence widgetText = context.getString(R.string.app_name);
        views.setTextViewText(R.id.tv_widget_title, widgetText);

        //Create an intent to launch MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        //Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_layout);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        for(int i = 0; i < appWidgetIds.length; i ++) {
        Intent intent = new Intent(context, TodoListWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.todo_list_widget_provider);
        rv.setRemoteAdapter( R.id.widget_list_view, intent);

        appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.widget_layout);
    }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        ComponentName todoListWidget = new ComponentName(context.getApplicationContext(), TodoListWidgetProvider.class);
        int[] appWidgetids = appWidgetManager.getAppWidgetIds(todoListWidget);
        onUpdate(context, appWidgetManager, appWidgetids);
    }

}

