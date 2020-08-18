package com.chemicalwedding.artemis.model;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chemicalwedding.artemis.R;

import java.util.List;

public class ExtenderAdapter extends BaseAdapter  {

    private Context context;
    private List<Extender> options;
    private ExtenderCallback callback;

    public ExtenderAdapter(Context context, List<Extender> options, ExtenderCallback callback) {
        this.context = context;
        this.options = options;
        this.callback = callback;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.extender_list_item, parent, false);
        }

        Extender extender = options.get(position);

        TextView extenderTitle = listItem.findViewById(R.id.text);
        extenderTitle.setText(extender.toString());

        RelativeLayout container = listItem.findViewById(R.id.extenderContainer);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.applyExtender(extender);
            }
        });

        return listItem;
    }

    public int getCount() {
        return this.options.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Extender getItem(int position) {
        return this.options.get(position);
    }

    public interface ExtenderCallback {
        void applyExtender(Extender extender);
    }
}
