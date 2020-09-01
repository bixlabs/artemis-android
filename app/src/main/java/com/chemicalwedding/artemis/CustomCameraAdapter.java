package com.chemicalwedding.artemis;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chemicalwedding.artemis.database.CustomCamera;

import java.util.ArrayList;

public class CustomCameraAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CustomCamera> options;
    private ListView listView;

    public CustomCameraAdapter(Context mContext, ArrayList<CustomCamera> options, ListView listView) {
        this.mContext = mContext;
        this.options = options;
        this.listView = listView;
    }


    @Override
    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.text_list_item, parent, false);
        }

        CustomCamera option = options.get(position);

        TextView textView = listItem.findViewById(R.id.text);
        textView.setText(option.getName());

        return listItem;
    }

    public int getCount(){
        return this.options.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public CustomCamera getItem(int position) {
        return this.options.get(position);
    }
}
