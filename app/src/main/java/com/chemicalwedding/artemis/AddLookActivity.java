package com.chemicalwedding.artemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
import com.chemicalwedding.artemis.database.Look;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageRGBFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageWhiteBalanceFilter;

public class AddLookActivity extends Activity {
    private ArtemisDatabaseHelper mDBHelper;
    private GPUImageView gpuImageView;
    private Button saveFilterButton;
    private ImageButton reloadButton;
    private SeekBar gammaSeekBar;
    private SeekBar contrastSeekBar;
    private SeekBar saturationSeekBar;
    private SeekBar whiteBalanceSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private TextView gammaTextView;
    private TextView contrastTextView;
    private TextView saturationTextView;
    private TextView whiteBalanceTextView;
    private TextView redTextView;
    private TextView greenTextView;
    private TextView blueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_look);

        mDBHelper = new ArtemisDatabaseHelper(this);

        gammaSeekBar = findViewById(R.id.gamma_seek_bar);
        gammaTextView = findViewById(R.id.gamma_value);
        gammaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                NumberFormat nf = new DecimalFormat("#.#");
                double newValue = (double) i / 10;
                gammaTextView.setText(nf.format(newValue));
                gpuImageView.setFilter(getFilterGroup());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "start tracking seekBar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "stop tracking seekBar");
            }
        });

        contrastSeekBar = findViewById(R.id.contrast_seek_bar);
        contrastTextView = findViewById(R.id.contrast_value);
        contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                NumberFormat nf = new DecimalFormat("#.#");
                double newValue = (double) i / 10;
                contrastTextView.setText(nf.format(newValue));
                gpuImageView.setFilter(getFilterGroup());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "start tracking seekBar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "stop tracking seekBar");
            }
        });

        saturationSeekBar = findViewById(R.id.saturation_seek_bar);
        saturationTextView = findViewById(R.id.saturation_value);
        saturationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                NumberFormat nf = new DecimalFormat("#.#");
                double newValue = (double) i / 10;
                saturationTextView.setText(nf.format(newValue));
                gpuImageView.setFilter(getFilterGroup());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "start tracking seekBar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "stop tracking seekBar");
            }
        });

        whiteBalanceSeekBar = findViewById(R.id.white_balance_seek_bar);
        whiteBalanceTextView = findViewById(R.id.white_balance_value);
        whiteBalanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.i("bixlabs", "value: " + i);
                whiteBalanceTextView.setText(String.valueOf(i));
                gpuImageView.setFilter(getFilterGroup());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "start tracking seekBar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "stop tracking seekBar");
            }
        });

        redSeekBar = findViewById(R.id.red_seek_bar);
        redTextView = findViewById(R.id.red_value);
        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                NumberFormat nf = new DecimalFormat("#.#");
                double newValue = (double) i / 10;
                redTextView.setText(nf.format(newValue));
                gpuImageView.setFilter(getFilterGroup());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "start tracking seekBar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "stop tracking seekBar");
            }
        });

        greenSeekBar = findViewById(R.id.green_seek_bar);
        greenTextView = findViewById(R.id.green_value);
        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                NumberFormat nf = new DecimalFormat("#.#");
                double newValue = (double) i / 10;
                greenTextView.setText(nf.format(newValue));
                gpuImageView.setFilter(getFilterGroup());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "start tracking seekBar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "stop tracking seekBar");
            }
        });

        blueSeekBar = findViewById(R.id.blue_seek_bar);
        blueTextView = findViewById(R.id.blue_value);
        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                NumberFormat nf = new DecimalFormat("#.#");
                double newValue = (double) i / 10;
                blueTextView.setText(nf.format(newValue));
                gpuImageView.setFilter(getFilterGroup());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "start tracking seekBar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i("bixlabs", "stop tracking seekBar");
            }
        });

        reloadButton = findViewById(R.id.custom_look_reload);
        reloadButton.setOnClickListener(view -> {
            gammaSeekBar.setProgress(15);
            contrastSeekBar.setProgress(10);
            saturationSeekBar.setProgress(10);
            whiteBalanceSeekBar.setProgress(5000);
            redSeekBar.setProgress(10);
            greenSeekBar.setProgress(10);
            blueSeekBar.setProgress(10);

            gpuImageView.setFilter(new GPUImageContrastFilter(1.0f));
            gpuImageView.setFilter(new GPUImageSaturationFilter(1.0f));
            gpuImageView.setFilter(new GPUImageGammaFilter(1.5f));
            gpuImageView.setFilter(new GPUImageWhiteBalanceFilter(5000.0f, 0.0f));
            gpuImageView.setFilter(new GPUImageRGBFilter(1.0f, 1.0f, 1.0f));
        });

        saveFilterButton = findViewById(R.id.custom_look_save);
        saveFilterButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddLookActivity.this);
            builder.setTitle("FILTER NAME");

            // Set up the input
            final EditText input = new EditText(getBaseContext());
            input.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.white));
            input.setInputType(InputType.TYPE_CLASS_TEXT);

            FrameLayout container = new FrameLayout(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 55;
            params.rightMargin = 55;
            input.setLayoutParams(params);
            container.addView(input);

            builder.setView(container);

            builder.setPositiveButton("SAVE FILTER", (dialog, which) -> {
            });
            builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view1 -> {
                String name = input.getText().toString();

                if (name.isEmpty()) {
                    input.setError("You need to enter a name");
                } else {
                    input.setError("");

                    Double gamma = (double) gammaSeekBar.getProgress() / 10;
                    Double contrast = (double) contrastSeekBar.getProgress() / 10;
                    Double saturation = (double) saturationSeekBar.getProgress() / 10;
                    Double whiteBalance = (double) whiteBalanceSeekBar.getProgress();
                    Double red = (double) redSeekBar.getProgress() / 10;
                    Double blue = (double) blueSeekBar.getProgress() / 10;
                    Double green = (double) greenSeekBar.getProgress() / 10;

                    Look look = new Look();
                    look.setEffectId(-1);
                    look.setName(name);
                    look.setGamma(gamma);
                    look.setContrast(contrast);
                    look.setSaturation(saturation);
                    look.setWhiteBalance(whiteBalance);
                    look.setRed(red);
                    look.setGreen(green);
                    look.setBlue(blue);

                    mDBHelper.insertLook(look);

                    alertDialog.dismiss();
                    finish();
                }
            });
        });

        gpuImageView = findViewById(R.id.custom_look_imageView);
        Bitmap lookBitmap = BitmapFactory.decodeResource(getBaseContext().getResources(),
                R.drawable.look_example);
        gpuImageView.setImage(lookBitmap);
    }

    private GPUImageFilterGroup getFilterGroup() {
        List<GPUImageFilter> filterList = new ArrayList<>();

        double gamma = (double) gammaSeekBar.getProgress() / 10;
        double contrast = (double) contrastSeekBar.getProgress() / 10;
        double saturation = (double) saturationSeekBar.getProgress() / 10;
        double whiteBalance = whiteBalanceSeekBar.getProgress();
        double red = (double) redSeekBar.getProgress() / 10;
        double blue = (double) blueSeekBar.getProgress() / 10;
        double green = (double) greenSeekBar.getProgress() / 10;

        filterList.add(new GPUImageGammaFilter((float) gamma));
        filterList.add(new GPUImageContrastFilter((float) contrast));
        filterList.add(new GPUImageSaturationFilter((float) saturation));
        filterList.add(new GPUImageWhiteBalanceFilter((float) whiteBalance, 0.0f));
        filterList.add(new GPUImageRGBFilter((float) red, (float) green, (float) blue));

        return new GPUImageFilterGroup(filterList);
    }
}
