package com.chemicalwedding.artemis;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chemicalwedding.artemis.database.LensAdapter;

import java.util.List;

public class LensAdaptersAdapter extends RecyclerView.Adapter<LensAdaptersAdapter.LensAdapterViewHolder> {

    private List<LensAdapter> lensAdapters;
    private SelectedLensAdapterCallback callback;
    private Context context;

    public static class LensAdapterViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public LensAdapterViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public LensAdaptersAdapter(List<LensAdapter> lensAdapters, SelectedLensAdapterCallback callback, Context context) {
        this.lensAdapters = lensAdapters;
        this.callback = callback;
        this.context = context;
    }

    @Override
    public LensAdaptersAdapter.LensAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.circle_text_view, parent, false);


        LensAdapterViewHolder vh = new LensAdapterViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(LensAdapterViewHolder holder, int position) {
        holder.textView.setText("x" + lensAdapters.get(position).getMagnificationFactor());

        if(lensAdapters.get(position).isCustomAdapter()) {
            ((GradientDrawable) holder.textView.getBackground()).setStroke(2, context.getColor(R.color.gray));
            holder.textView.setTextColor(context.getColor(R.color.gray));
        } else {
            ((GradientDrawable) holder.textView.getBackground()).setStroke(2, context.getColor(R.color.orangeArtemisText));
            holder.textView.setTextColor(context.getColor(R.color.orangeArtemisText));
        }

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.selectedLensAdapter(lensAdapters.get(holder.getAdapterPosition()));
            }
        });

        holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                callback.deleteLensAdapter(lensAdapters.get(holder.getAdapterPosition()));
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return lensAdapters.size();
    }

    public interface SelectedLensAdapterCallback {
        void selectedLensAdapter(LensAdapter adapter);
        void deleteLensAdapter(LensAdapter adapter);
    }
}
