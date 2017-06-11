package com.example.mac.recipez.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mac.recipez.R;
import com.example.mac.recipez.RecipePage;

import java.util.List;



/**
 * Created by julia on 05-Jun-17.
 */

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerAdapter.ViewHolder>{

    private final List<String> listCategories;
    private final RecyclerClickListener listener;

    public RecipeRecyclerAdapter(List<String> listItem, RecyclerClickListener listener){
        this.listCategories = listItem;
        this.listener = listener;

    }

    public interface RecyclerClickListener {
        void onItemClick(String category);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public Button buttonCategories;
        private Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            context = itemView.getContext();
            buttonCategories = (Button) itemView.findViewById(R.id.buttonCategories);
        }

        public void bind(final String category, final RecyclerClickListener listener) {
            buttonCategories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(category);
                }
            });
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_categories, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(listCategories.get(position), listener);
        holder.buttonCategories.setText(listCategories.get(position));

    }

    @Override
    public int getItemCount() {
        return listCategories.size();
    }
}
