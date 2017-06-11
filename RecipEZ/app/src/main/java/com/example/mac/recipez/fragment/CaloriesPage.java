package com.example.mac.recipez.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mac.recipez.R;

/**
 * Created by Yvonne Kong on 31/5/2017.
 */

public class CaloriesPage  extends android.support.v4.app.Fragment {

    private ProgressBar progressBar;
    private TextView calories;
    private int progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.calories_page, viewGroup, false);

        // Initialise progress bar to increase by 25% with each click
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progress = 0;
        progressBar.setProgress(0);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // HARDCODED! INCREASE PROGRESS BY 25% each click.
                progress += 25;
                setNewProgress(progress);
            }
        });

        // Initialise the textView for calories
        calories = (TextView) rootView.findViewById(R.id.textView);
        return rootView;
    }

    // For setting the new progress
    private void setNewProgress(int newProgress) {
        if (progress > 100) {
            return;
        }
        progressBar.setProgress(newProgress);
        calories.setText("Calories: " + (newProgress*17500/100) + "/17500");
    }

}
