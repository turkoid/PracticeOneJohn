package com.turkoid.practiceonejohn;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by turkoid on 8/11/2016.
 */
public class RequestQueueSingleton {
    private static RequestQueueSingleton instance;
    private RequestQueue queue;
    private static Context context;

    private RequestQueueSingleton(Context context) {
        this.context = context;
    }

    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new RequestQueueSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return queue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

}
