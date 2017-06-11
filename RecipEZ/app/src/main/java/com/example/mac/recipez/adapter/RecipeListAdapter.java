package com.example.mac.recipez.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mac.recipez.R;
import com.example.mac.recipez.model.Recipe;

import java.util.List;

/**
 * Created by Yvonne Kong on 31/5/2017.
 */

public class RecipeListAdapter extends BaseAdapter {

    private Activity activity;
    private List<Recipe> itemList;

    public RecipeListAdapter(Activity activity, List<Recipe> itemList) {
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class CurrentView {
        ImageView recipeImage;
        TextView recipeName;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        CurrentView currentView;
        if (view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.recipe_layout, null);
            currentView = new CurrentView();
            currentView.recipeImage = (ImageView) view.findViewById(R.id.recipe_image);
            currentView.recipeName = (TextView) view.findViewById(R.id.recipe_name);
            view.setTag(currentView);
        } else {
            currentView = (CurrentView) view.getTag();
        }
        Recipe recipe = itemList.get(position);
        currentView.recipeImage.setImageResource(recipe.getImage());
        currentView.recipeName.setText(recipe.getName());

        return view;
    }
}
