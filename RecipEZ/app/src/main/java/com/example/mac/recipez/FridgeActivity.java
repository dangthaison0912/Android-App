package com.example.mac.recipez;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mac.recipez.fragment.Fridge;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static com.example.mac.recipez.fragment.Fridge.updateFridge;

/**
 * Created by mac on 5/31/17.
 */

public class FridgeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private String username;

    Button submitItem;
    Button datePicker;


    EditText foodNameInput;
    TextView dateView;
    String foodNameString;
    String cookedDateString;

    ArrayList<HashMap<String, String>> fridgeItems;

    public static final String RECIPE_NAME = "Recipe_Name";
    public static final String EXPIRE_DATE = "Expire_Date";
    public static final String USERNAME = "Username";

    /* Initialise button and editTexts
    * record text from editTexts when button is clicked and end the activity*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = getIntent().getStringExtra("Username");
        Log.e("FridgeActivity", username);

        getSupportActionBar().setTitle("Insert Food Into Fridge");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.fridge_activity);
        fridgeItems = Fridge.getFridgeItems();

        submitItem = (Button) findViewById(R.id.fridge_submit_item);
        datePicker = (Button) findViewById(R.id.date_picker);
        foodNameInput = (EditText) findViewById(R.id.food_name_input);
        dateView = (TextView) findViewById(R.id.show_date);

        submitItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foodNameString = foodNameInput.getText().toString();
                if (foodNameString.isEmpty()) {
                    foodNameInput.setError("\"Food\" cannot be empty.");
                    Toast.makeText(getApplicationContext(), "Please fill in the necessary fields.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Item added into fridge.", Toast.LENGTH_SHORT).show();
                    addItemToList(foodNameString, cookedDateString);

//                    Fridge fridgeFragment = (Fridge) getSupportFragmentManager().findFragmentByTag("FridgeTag");
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    ft.detach(fridgeFragment);
//                    ft.attach(fridgeFragment);
//                    ft.commit();

                    startFoodNotification();
                    finish();
                }
            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate(view);
            }
        });
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(v);
            }
        });

    }

    /* Up navigation to finish the activity */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    /* Add the expired date to the list based on the cooked input date
    * Normally food can last up to 4 days before it goes bad */
    private void addItemToList(String name, String cookedDate) {
//        HashMap<String, String> item = new HashMap<>();
//        item.put(RECIPE_NAME, name);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date expired = new Date();
        try {
            Date cooked = format.parse(cookedDate);
            long date = cooked.getTime() + 4*24*60*60*1000;
            expired = new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
//
//        item.put(EXPIRE_DATE, format.format(expired));
//        fridgeItems.add(item);

        JSONObject fridgeJSON = new JSONObject();
        try {
            fridgeJSON.put(RECIPE_NAME, name);
            fridgeJSON.put(EXPIRE_DATE, format.format(expired));
            fridgeJSON.put(USERNAME, username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addIntoServer(fridgeJSON);

    }

    private void addIntoServer(final JSONObject bodyJSON) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://www.doc.ic.ac.uk/project/2016/271/g1627110/web/api/addFridge.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response:", response);
                        updateFridge();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response:", error.toString());
                    }
                }
        ){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return bodyJSON.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(postRequest);

    }

    private void pickDate(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "date");
    }   

    private void setDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        TextView showDate = (TextView) findViewById(R.id.show_date);
        cookedDateString = dateFormat.format(calendar.getTime());
        showDate.setText(cookedDateString);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        setDate(cal);
    }

    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            return new DatePickerDialog(getActivity(),
                    (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
    }

    private void startFoodNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(FridgeActivity.this, FridgeFoodNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+5000, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+1000, 60000, pendingIntent);
        Log.i("ALARM", "Alarm activated");
    }
}
