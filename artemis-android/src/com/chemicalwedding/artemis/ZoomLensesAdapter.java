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

import com.chemicalwedding.artemis.database.ZoomLens;

import java.util.ArrayList;

public class ZoomLensesAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ZoomLens> options;
    private ListView listView;

    public ZoomLensesAdapter(Context mContext, ArrayList<ZoomLens> options, ListView listView) {
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

        ZoomLens option = options.get(position);

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
    public ZoomLens getItem(int position) {
        return this.options.get(position);
    }
}
