package com.example.mac.recipez.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mac.recipez.R;
import com.example.mac.recipez.model.ItemSlideMenu;

import java.util.List;

/**
 * Created by mac on 5/29/17.
 */

public class SlidingMenuAdapter extends BaseAdapter {

    private Context context;
    private List<ItemSlideMenu> itemList;

    public SlidingMenuAdapter(Context context, List<ItemSlideMenu> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.item_sliding_menu, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.item_image);
        TextView textView = (TextView) v.findViewById(R.id.item_title);

        ItemSlideMenu item = itemList.get(i);
        imageView.setImageResource(item.getImgId());
        textView.setText(item.getTitle());
        return v;
    }
}
