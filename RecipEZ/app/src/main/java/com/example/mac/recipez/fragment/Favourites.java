package com.example.mac.recipez.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mac.recipez.R;
import com.example.mac.recipez.RecipePage;
import com.example.mac.recipez.adapter.RecipeListAdapter;
import com.example.mac.recipez.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yvonne Kong on 8/6/2017.
 */

public class Favourites extends android.support.v4.app.Fragment {

    private ListView listView;
    private List<Recipe> recipeList;
    private RecipeListAdapter recipeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourites_page, viewGroup, false);
        listView = (ListView) view.findViewById(R.id.list_view);

        recipeList = new ArrayList<>();

        createRecipeListView();
        return view;
    }

    private void createRecipeListView() {
        recipeList.add(new Recipe(R.drawable.chicken_salad, "Chicken Salad"));
        recipeList.add(new Recipe(R.drawable.fried_rice, "Pork Fried Rice"));
        recipeList.add(new Recipe(R.drawable.pasta, "Noodles"));
        recipeAdapter = new RecipeListAdapter(getActivity(), recipeList);
        listView.setAdapter(recipeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe recipe = (Recipe) recipeAdapter.getItem(position);
                // TODO: Get recipe from database
                Intent intent = new Intent(view.getContext(), RecipePage.class);
                startActivity(intent);
            }
        });
    }
}
