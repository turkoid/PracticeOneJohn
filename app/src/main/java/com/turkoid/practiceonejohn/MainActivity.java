package com.turkoid.practiceonejohn;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.turkoid.practiceonejohn.beans.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG_ID = "Id";
    public static final String TAG_NAME = "Name";
    public static final String TAG_STATUS = "Status";
    public static final String TAG = "turkoid";
    ArrayList<User> users;

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("userId", -1);
                intent.putExtra("USER_OPERATION", "new");
                startActivityForResult(intent, 0);
            }
        });
        list = (ListView) findViewById(R.id.listUsers);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) list.getItemAtPosition(i);
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("userId", user.getId());
                intent.putExtra("USER_OPERATION", "edit");
                startActivityForResult(intent, 0);

            }
        });
        //getUsers();
        Log.d(TAG, "creating main activity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUsers();
        Log.d(TAG,"resuming main activity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Snackbar.make(findViewById(android.R.id.content), "LOL, there are no settings.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getUsers() {
        users = new ArrayList<>();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://api.turkoid.com/users", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject userJSON = response.getJSONObject(i);
                        int id = Integer.parseInt(userJSON.getString(TAG_ID));
                        String name = userJSON.getString(TAG_NAME);
                        boolean state = userJSON.getString(TAG_STATUS).equals("1") ? true : false;

                        User user = new User(id, name, state);
                        users.add(user);
                    }
                    ListAdapter adapter = new UserListAdapter(MainActivity.this, R.layout.list_item, users);
                    list.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(request);
    }
}
