package com.example.mac.recipez.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mac.recipez.R;

/**
 * Created by mac on 5/29/17.
 */

public class Settings extends android.support.v4.app.Fragment {
    public Settings() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings, viewGroup, false);
        return rootView;
    }
}