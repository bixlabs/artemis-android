package com.chemicalwedding.artemis.model;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chemicalwedding.artemis.R;

import java.util.List;

public class FramelineRatesAdapter extends RecyclerView.Adapter<FramelineRatesAdapter.FramelineRateViewHolder> {

    private List<FramelineRate> framelineRateList;
    private SelectedFramelineRateCallback callback;
    private Context context;

    public FramelineRatesAdapter(List<FramelineRate> framelineRateList, SelectedFramelineRateCallback callback, Context context) {
        this.framelineRateList = framelineRateList;
        this.callback = callback;
        this.context = context;
    }

    @Override
    public FramelineRatesAdapter.FramelineRateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.frameline_rate, parent, false);


        FramelineRateViewHolder framelineRateViewHolder = new FramelineRateViewHolder(v);
        return framelineRateViewHolder;
    }

    @Override
    public void onBindViewHolder(FramelineRateViewHolder holder, int position ) {
        holder.textView.setText(String.format("%.2f", framelineRateList.get(position).getRate()));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.selectedFramelineRate(framelineRateList.get(holder.getAdapterPosition()));
            }
        });

        holder.textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                callback.deleteFramelineRate(framelineRateList.get(holder.getAdapterPosition()));
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return framelineRateList.size();
    }

    public static class FramelineRateViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public FramelineRateViewHolder(TextView textView) {
            super(textView);
            this.textView = textView;
        }
    }

    public interface SelectedFramelineRateCallback {
        void selectedFramelineRate(FramelineRate framelineRate);
        void deleteFramelineRate(FramelineRate framelineRate);
    }
}
