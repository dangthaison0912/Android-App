package com.example.mac.recipez.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mac.recipez.R;
import com.example.mac.recipez.RecipePage;
import com.example.mac.recipez.UserRecipe;
import com.example.mac.recipez.adapter.RecipeListAdapter;
import com.example.mac.recipez.adapter.RecipeRecyclerAdapter;
import com.example.mac.recipez.model.Recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 5/29/17.
 */

public class HomePage extends android.support.v4.app.Fragment {

    private ListView listView;
    private List<Recipe> recipeList;
    private List<String> category_list;
    private FloatingActionButton floatBtn;
    private RecipeListAdapter recipeAdapter;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_page, viewGroup, false);
        listView = (ListView) view.findViewById(R.id.list_view);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        category_list = new ArrayList<>();
        recipeList = new ArrayList<>();
        floatBtn = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        createCategoryRecyclerList();
        createRecipeListView();
        toUserRecipePage();
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

    private void createCategoryRecyclerList() {
        category_list.add("All");
        category_list.add("Chicken");
        category_list.add("Pork");
        category_list.add("Beef");
        category_list.add("Lamb");
        category_list.add("Fried Rice");
        category_list.add("Vegetarian");

        adapter = new RecipeRecyclerAdapter(category_list, new RecipeRecyclerAdapter.RecyclerClickListener() {
            @Override
            public void onItemClick(String category) {
                if (category.equals("All")) {
                    // Remains on the home page
                    return;
                }
                // TODO: For now, the rest of the buttons go to the default blank recipe page
                Intent intent = new Intent(getContext(), RecipePage.class);
                getContext().startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

    }

    private void toUserRecipePage() {
        // Floating button - Add new user recipe
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserRecipe.class);
                startActivity(intent);
            }
        });
    }
}
