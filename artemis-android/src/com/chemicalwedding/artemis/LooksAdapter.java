package com.chemicalwedding.artemis;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chemicalwedding.artemis.database.Look;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_AQUA;
import static android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_BLACKBOARD;
import static android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_MONO;
import static android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_NEGATIVE;
import static android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_OFF;
import static android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_POSTERIZE;
import static android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_SEPIA;
import static android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_SOLARIZE;
import static android.hardware.camera2.CameraMetadata.CONTROL_EFFECT_MODE_WHITEBOARD;

interface LooksTrashClickListener {
    void onTrashClick(Integer position);
}

public class LooksAdapter extends RecyclerView.Adapter<LooksAdapter.LooksViewHolder> {

    public ArrayList<Look> availableEffects;

    public LooksAdapter(ArrayList<Look> availableEffects) {
        this.availableEffects = availableEffects;
    }

    public Boolean canDeleteLooks = false;
    public RecyclerItemClickListener mOnRecyclerItemListener;
    public LooksTrashClickListener mLooksTrashClickListener;

    public void setRecyclerItemListener(RecyclerItemClickListener listener) {
        mOnRecyclerItemListener = listener;
    }

    public void setOnLooksTrashClickListener(LooksTrashClickListener listener) {
        mLooksTrashClickListener = listener;
    }

    @Override
    public LooksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.looks_list_row, parent, false);

        return new LooksViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LooksViewHolder holder, final int position) {
        if (canDeleteLooks && position != 0) {
            holder.trash.setVisibility(View.VISIBLE);
        } else {
            holder.trash.setVisibility(View.INVISIBLE);
        }

        holder.trash.setOnClickListener(view -> mLooksTrashClickListener.onTrashClick(position));

        holder.itemView.setOnClickListener(v -> {
            SharedPreferences settings = holder.itemView.getContext().getSharedPreferences(
                    ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(holder.itemView.getContext().getString(R.string.preference_key_selectedCameraEffect), String.valueOf(availableEffects.get(position).getEffectId()));
            editor.apply();

            mOnRecyclerItemListener.onItemClick(position);
        });

        holder.name.setText(getEffectName(availableEffects.get(position).getEffectId()));
        setEffectImage(availableEffects.get(position).getEffectId(), holder.imageView);
    }

    public static String getEffectName(int effectId) {
        switch (effectId) {
            case CONTROL_EFFECT_MODE_OFF:
                return "NO LOOK APPLIED";
            case CONTROL_EFFECT_MODE_MONO:
                return "Mono";
            case CONTROL_EFFECT_MODE_NEGATIVE:
                return "Negative";
            case CONTROL_EFFECT_MODE_SOLARIZE:
                return "Solarize";
            case CONTROL_EFFECT_MODE_SEPIA:
                return "Sepia";
            case CONTROL_EFFECT_MODE_POSTERIZE:
                return "Posterize";
            case CONTROL_EFFECT_MODE_WHITEBOARD:
                return "Whiteboard";
            case CONTROL_EFFECT_MODE_BLACKBOARD:
                return "Blackboard";
            case CONTROL_EFFECT_MODE_AQUA:
                return "Aqua";
            default:
                return "NO LOOK APPLIED";
        }
    }

    private void setEffectImage(int effectId, ImageView imageView) {
        switch (effectId) {
            case CONTROL_EFFECT_MODE_OFF:
                imageView.setImageResource(R.drawable.look_example);
                break;
            case CONTROL_EFFECT_MODE_MONO:
                imageView.setImageResource(R.drawable.look_mono);
                break;
            case CONTROL_EFFECT_MODE_NEGATIVE:
                imageView.setImageResource(R.drawable.look_negative);
                break;
            case CONTROL_EFFECT_MODE_SOLARIZE:
                break;
            case CONTROL_EFFECT_MODE_SEPIA:
                imageView.setImageResource(R.drawable.look_sepia);
                break;
            case CONTROL_EFFECT_MODE_POSTERIZE:
                imageView.setImageResource(R.drawable.look_posterize);
                break;
            case CONTROL_EFFECT_MODE_WHITEBOARD:
                break;
            case CONTROL_EFFECT_MODE_BLACKBOARD:
                break;
            case CONTROL_EFFECT_MODE_AQUA:
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return availableEffects.size();
    }

    public class LooksViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;
        public ImageView trash;

        public LooksViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.look_row_name);
            imageView = view.findViewById(R.id.look_row_imageView);
            trash = view.findViewById(R.id.look_row_trash);
        }
    }
}
