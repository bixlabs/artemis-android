package com.chemicalwedding.artemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
import com.chemicalwedding.artemis.database.Look;

import java.util.ArrayList;
import java.util.List;

public class LooksActivity extends Activity {

    private ArtemisDatabaseHelper mDBHelper;
    private RecyclerView recyclerView;
    private ImageView deleteButton;
    private Button addNewLookButton;
    private LooksAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.looks);

        mDBHelper = new ArtemisDatabaseHelper(this);
        if (mDBHelper.getLooks().isEmpty()) {
            for (int i : CameraPreview21.availableEffects) {
                Look look = new Look();
                look.setEffectId(i);
                look.setName(LooksAdapter.getEffectName(i));
                mDBHelper.insertLook(look);
            }
        }

        ArrayList<Look> availableEffects = mDBHelper.getLooks();

        recyclerView = findViewById(R.id.looks_recycler_view);

        mAdapter = new LooksAdapter(availableEffects);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GalleryGridItemDecoration(200, 2));
        mAdapter.setRecyclerItemListener(position ->
                finish()
        );


        deleteButton = findViewById(R.id.delete_looks);
        setDeleteModeVisibility();
        deleteButton.setAlpha(0.5f);
        deleteButton.setOnClickListener(view -> {
            if (deleteButton.getAlpha() == 0.5f) {
                deleteButton.setAlpha(1.0f);
            } else {
                deleteButton.setAlpha(0.5f);
            }
            mAdapter.switchDeleteMode();
        });

        addNewLookButton = findViewById(R.id.add_new_look);
        addNewLookButton.setOnClickListener(view -> {
            Intent addLookIntent = new Intent(getBaseContext(), AddLookActivity.class);
            startActivity(addLookIntent);
        });

        mAdapter.setOnLooksTrashClickListener(position -> {
            Log.i("bixlabs", "Selected look: " + position);

            new AlertDialog.Builder(recyclerView.getContext())
                    .setTitle("SURE?")
                    .setMessage("Are you sure you want to delete this?")
                    .setPositiveButton("DELETE", (dialogInterface, i) -> {
                        mDBHelper.deleteLookByPK(mAdapter.availableEffects.get(position).getPk());
                        mAdapter.availableEffects = mDBHelper.getLooks();
                        setDeleteModeVisibility();
                        mAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        });
        recyclerView.setAdapter(mAdapter);
    }

    private void setDeleteModeVisibility() {
        deleteButton.setVisibility(View.GONE);
        for (Look look : mAdapter.availableEffects) {
            if(look.isCustomLook()) {
                deleteButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdapter.availableEffects = mDBHelper.getLooks();
        setDeleteModeVisibility();
        mAdapter.notifyDataSetChanged();
    }
}
