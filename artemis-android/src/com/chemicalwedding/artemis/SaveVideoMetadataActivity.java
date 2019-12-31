package com.chemicalwedding.artemis;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SaveVideoMetadataActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_video_metadata);

        Button cancelButton = findViewById(R.id.cancelVideoMetadata);
        Button saveButton = findViewById(R.id.saveVideoMetadata);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Save video metadata here
            }
        });
    }
}
