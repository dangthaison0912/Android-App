package com.example.mac.recipez.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mac.recipez.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.text.SimpleDateFormat;

/**
 * Created by mac on 5/31/17.
 */

public class FridgeListAdapter extends BaseAdapter {

    public static final String RECIPE_NAME = "Recipe_Name";
    public static final String EXPIRE_DATE = "Expire_Date";
    public ArrayList<HashMap<String,String>> list;
    Activity activity;

    public FridgeListAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        TextView recipeName;
        TextView expireDate;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if(view == null) {
            view = inflater.inflate(R.layout.rows, null);
            holder = new ViewHolder();
            holder.recipeName = (TextView) view.findViewById(R.id.recipe_name);
            holder.expireDate = (TextView) view.findViewById(R.id.expire_date);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //get mapping table from the list
        HashMap<String, String> map = list.get(position);
        //set recipe name
        holder.recipeName.setText(map.get(RECIPE_NAME));
        //set expire date
        String expireDate = map.get(EXPIRE_DATE);
        holder.expireDate.setText(expireDate);
        //set date color based on day left
        setExpireDateColour(holder.expireDate, expireDate);
        return view;
    }

    /* Calculate the time difference between current date and the expire date
    * Set colour of text as follow:
    * Black for expired
    * Red for 1-2 days left
    * Green for > 2 days
    * */
    private void setExpireDateColour(TextView expire_date, String expireDate) {
        String currentDateTime = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date current = format.parse(currentDateTime);
            Date expire = format.parse(expireDate);

            long diff = expire.getTime() - current.getTime();
            Log.i("DateTime", "Diff = " + diff);
            long diffDays = diff / (24*60*60*1000);
            int color;
            if (diffDays < 0) {
                color = Color.argb(255, 255, 0, 0);
            } else if (diffDays < 3) {
                color = Color.argb(255, 204, 204, 18);
            } else {
                color = Color.argb(255, 0, 255, 0);
            }
            expire_date.setTextColor(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
