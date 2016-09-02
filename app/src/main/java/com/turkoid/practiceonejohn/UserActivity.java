package com.turkoid.practiceonejohn;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.turkoid.practiceonejohn.beans.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    private User user = null;
    public static final String TAG = "turkoid";
    private Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        int userId;
        String userOperation = "";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                userId = -1;
                userOperation = "";
            } else {
                userId = extras.getInt("userId");
                userOperation = extras.getString("USER_OPERATION");
            }
        } else {
            userId = (int) savedInstanceState.getSerializable("userId");
            userOperation = (String) savedInstanceState.getSerializable("USER_OPERATION");
        }
        switch (userOperation) {
            case "new":
                actionBar.setTitle("Add new user");
                setUser(new User(-1, "", false));
                break;
            case "edit":
                actionBar.setTitle("Edit user");
                if (userId >= 0) {
                    getUser(userId);
                }
                break;
        }
        Log.d(TAG,"opening user page for id=" + userId);
    }

    private void updateUserObject(User user) {
        if (user != null) {
            TextView name = (TextView) findViewById(R.id.userName);
            CheckBox state = (CheckBox) findViewById(R.id.userStatus);

            if (name != null) {
                user.setName(name.getText().toString());
            }

            if (state != null) {
                user.setState(state.isChecked());
            }
        }
    }

    private void deleteUser(final User user) {
        if (user != null) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Delete this user?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String url = "http://api.turkoid.com/users/" + user.getId();
                            int method = Request.Method.DELETE;

                            StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG,"user deleted: " + user.getId());
                                    Log.d(TAG,"user save response: " + response.toString());
                                    finish();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });

                            RequestQueueSingleton.getInstance(UserActivity.this).addToRequestQueue(request);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
            dialog.show();

        }
    }

    private void saveUser(final User user) {
        if (user != null && !user.getName().trim().isEmpty()) {
            String url = "http://api.turkoid.com/users";
            int method = Request.Method.POST;
            if (user.getId() >= 0) {
                url += "/" + user.getId();
                method = Request.Method.PUT;
            }
            StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (user.getId() == -1) {
                        Log.d(TAG,"new user created");
                    } else {
                        Log.d(TAG,"user saved for " + user.getId());
                    }
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
            RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(request);
        }
    }

    private void setUser(User user) {
        this.user = user;
        if (user != null) {
            TextView name = (TextView) findViewById(R.id.userName);
            CheckBox state = (CheckBox) findViewById(R.id.userStatus);

            if (name != null) {
                name.setText(user.getName());
            }

            if (state != null) {
                state.setChecked(user.isState());
            }

            if (menu == null) {
                Log.d(TAG,"should not be null");
            }
            Log.d(TAG,"user id for editing/new=" + user.getId());
            if (menu != null  && user.getId() >= 0) {
                Log.d(TAG,"editing user");
                MenuItem item = menu.findItem(R.id.action_saveuser);
                if (item == null) {
                    Log.d(TAG,"save button is null");
                }
                item = menu.findItem(R.id.action_deleteuser);
                if (item == null) {
                    Log.d(TAG,"delete button is null");
                } else {
                    item.setVisible(true);
                }
            }
        }
    }

    public void getUser(int id) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "http://api.turkoid.com/users/" + id, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response.toString());
                if (response.length() > 0) {
                    try {
                        JSONObject userJSON = response.getJSONObject(0);
                        int id = Integer.parseInt(userJSON.getString(MainActivity.TAG_ID));
                        String name = userJSON.getString(MainActivity.TAG_NAME);
                        boolean state = userJSON.getString(MainActivity.TAG_STATUS).equals("1") ? true : false;
                        setUser(new User(id, name, state));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueueSingleton.getInstance(this.getApplicationContext()).addToRequestQueue(request);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG,"creating options menu for User");
        getMenuInflater().inflate(R.menu.menu_user, menu);
        this.menu = menu;
        if (user != null && user.getId() >= 0) {
            menu.findItem(R.id.action_deleteuser).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_saveuser:
                updateUserObject(user);
                saveUser(user);
                finish();
                return true;
            case R.id.action_deleteuser:
                Log.d(TAG,"here");
                deleteUser(user);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
