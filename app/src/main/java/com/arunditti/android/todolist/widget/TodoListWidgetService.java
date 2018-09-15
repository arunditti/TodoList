package com.arunditti.android.todolist.widget;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.RemoteViewsService;

/**
 * Created by arunditti on 9/12/18.
 */

public class TodoListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TodoListRemoteViewsFactory(this.getApplicationContext());
    }
}
