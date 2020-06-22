package com.chemicalwedding.artemis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chemicalwedding.artemis.database.Lens;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StringOptionArrayAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> options;
    private ListView listView;

    public StringOptionArrayAdapter(Context mContext, List<String> options, ListView listView) {
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

        String option = options.get(position);

        TextView textView = listItem.findViewById(R.id.text);
        textView.setText(option);

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
    public String getItem(int position) {
        return this.options.get(position);
    }
}
