package com.chemicalwedding.artemis;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chemicalwedding.artemis.model.Frameline;

import java.util.List;

public class FramelinesAdapter extends BaseAdapter {

    private Context context;
    private List<Frameline> options;
    private FramelinesCallback callback;

    public FramelinesAdapter(Context context, List<Frameline> options, FramelinesCallback framelinesCallback) {
        this.context = context;
        this.options = options;
        this.callback = framelinesCallback;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.framelines_list_item, parent, false);
        }

        Frameline option = options.get(position);
        CheckBox checkFrameline = (CheckBox) listItem.findViewById(R.id.applyFramelineCheckbox);
        checkFrameline.setChecked(option.isApplied());

        TextView textView = listItem.findViewById(R.id.framelineText);
        textView.setText(option.getTitle());

        TextView editButton = (TextView) listItem.findViewById(R.id.editFramelineButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.editFrameline(option);
            }
        });

        checkFrameline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    callback.applyFrameline(options.get(position));
                } else {
                    callback.removeFrameline(options.get(position));
                }
            }
        });

        ImageView deleteButton = (ImageView) listItem.findViewById(R.id.deleteFramelineButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.deleteFrameline(option);
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
    public Frameline getItem(int position) {
        return this.options.get(position);
    }

    interface FramelinesCallback {
        void applyFrameline(Frameline frameline);
        void removeFrameline(Frameline frameline);
        void deleteFrameline(Frameline frameline);
        void editFrameline(Frameline frameline);
    }
}
