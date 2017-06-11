package com.example.mac.recipez.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mac.recipez.R;

/**
 * Created by Yvonne Kong on 30/5/2017.
 */

public class Search extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_page, viewGroup, false);
        return rootView;
    }
}
