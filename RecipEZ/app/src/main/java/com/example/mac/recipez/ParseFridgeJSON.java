package com.example.mac.recipez;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dexterfung on 8/6/2017.
 */

public class ParseFridgeJSON {

    ArrayList<HashMap<String, String>> fridgeItems;

    public static final String RECIPE_NAME = "Recipe_Name";
    public static final String EXPIRE_DATE = "Expire_Date";


    private static String[] food;
    private static String[] dates;

    private JSONArray listOfFood = null;

    private String json;

    public ParseFridgeJSON(String json) {
        this.json = json;
    }

    public void parseJSON() {
        JSONObject jsonObject=null;
        try {
            jsonObject = new JSONObject(json);
            listOfFood = jsonObject.getJSONArray("result");

            food = new String[listOfFood.length()];
            dates = new String[listOfFood.length()];

            for (int i=0; i<listOfFood.length(); i++) {
                JSONObject jsonObject1 = listOfFood.getJSONObject(i);
                food[i] = jsonObject1.getString("Food");
                dates[i] = jsonObject1.getString("Date");
            }

        } catch (JSONException e) {
            e.printStackTrace();;
        }
    }

    public ArrayList<HashMap<String, String>> getArrayOfMap() {

        fridgeItems = new ArrayList<HashMap<String, String>>();

        for (int i=0; i<food.length; i++) {
            HashMap<String, String> foodEntry = new HashMap<>();
            foodEntry.put(RECIPE_NAME, food[i]);
            foodEntry.put(EXPIRE_DATE, dates[i]);

            fridgeItems.add(foodEntry);
        }

        return fridgeItems;
    }

    public String[] getFood() {
        return food;
    }

    public String[] getDates() {
        return dates;
    }

}
