package com.turkoid.practiceonejohn;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.turkoid.practiceonejohn.beans.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by turkoid on 8/11/2016.
 */
public class UserListAdapter extends ArrayAdapter<User> {
    public static final String TAG = "turkoid";
    private Context context;
    public UserListAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);
        this.context = context;
    }

    private void saveUser(final User user) {
        if (user != null && !user.getName().trim().isEmpty()) {
            StringRequest request = new StringRequest(Request.Method.PUT, "http://api.turkoid.com/users/" + user.getId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG,"user saved for " + user.getId());
                    Log.d(TAG,"user save response: " + response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Log.d(TAG,"user save params now");
                    Map<String, String> params = new HashMap<>();
                    params.put("name", user.getName());
                    params.put("status", user.isState() ? "1" : "0");
                    return params;
                }
            };
            RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item, null);
        }

        final User user = getItem(position);

        if (user != null) {

            TextView name = (TextView) v.findViewById(R.id.userName);
            final CheckBox state = (CheckBox) v.findViewById(R.id.userState);

            if (name != null) {
                name.setText(user.getName());
            }

            if (state != null) {
                state.setChecked(user.isState());
                state.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.setState(state.isChecked());
                        saveUser(user);
                    }
                });
            }
        }

        return v;
    }
}
