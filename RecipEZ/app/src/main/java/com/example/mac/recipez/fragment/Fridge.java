package com.example.mac.recipez.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mac.recipez.FridgeActivity;
import com.example.mac.recipez.MainActivity;
import com.example.mac.recipez.ParseFridgeJSON;
import com.example.mac.recipez.R;
import com.example.mac.recipez.adapter.FridgeListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mac on 5/29/17.
 */

public class Fridge extends Fragment {
    FloatingActionButton fridge_add_item;
    private static ArrayList<HashMap<String, String>> fridgeItems;
    public static final String RECIPE_NAME = "Recipe_Name";
    public static final String EXPIRE_DATE = "Expire_Date";
    public MainActivity mainActivity;

    private ListView listView;

    private String username;

    private static FridgeListAdapter adapter;
    public Fridge() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        Log.e("FRIDGE", "Open");

        View rootView = inflater.inflate(R.layout.fridge, viewGroup, false);

        mainActivity = (MainActivity) getActivity();
        username = mainActivity.getUsername();

        fridge_add_item = (FloatingActionButton) rootView.findViewById(R.id.fridge_add_item);
        fridge_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FridgeActivity.class);
                intent.putExtra("Username", username);
                startActivity(intent);
            }
        });

        fridgeItems = new ArrayList<>();

        listView = (ListView) rootView.findViewById(R.id.fridge_list);
        listView.setEmptyView(rootView.findViewById(R.id.empty_list));

        Log.e("Fridge", username);
        requestFridge();

        return rootView;
    }

    public void requestFridge() {

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://www.doc.ic.ac.uk/project/2016/271/g1627110/web/api/readFridge.php";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response:", response);
                        showJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Fridge", "Checkpoint2error");
                        Log.d("Error.Response:", error.toString());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Username", username);
                Log.d("Get param", username);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };


        requestQueue.add(postRequest);
//        requestQueue.start();

    }

    private void showJSON(String json) {
        Log.e("Fridge", "Checkpoint2");
        ParseFridgeJSON pj = new ParseFridgeJSON(json);
        pj.parseJSON();
        fridgeItems = pj.getArrayOfMap();
        Log.i("Fridge", fridgeItems.toString());

        adapter = new FridgeListAdapter(getActivity(), fridgeItems);
        Log.i("Fridge", "Created adapter.");
        listView.setAdapter(adapter);
        Log.i("Fridge", "Set adapter.");
        updateFridge();
    }



    public static ArrayList<HashMap<String, String>> getFridgeItems() {
        return fridgeItems;
    }

    public static void updateFridge() {
        Log.e("Fridge", "Update Fridge");
        adapter.notifyDataSetChanged();
    }
}