package com.chemicalwedding.artemis;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AddLookActivity extends Activity {

    private ImageButton reloadButton;
    private SeekBar liftSeekBar;
    private SeekBar gammaSeekBar;
    private SeekBar gainSeekBar;
    private SeekBar contrastSeekBar;
    private SeekBar saturationSeekBar;
    private SeekBar whiteBalanceSeekBar;
    private SeekBar tintSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private TextView liftTextView;
    private TextView gammaTextView;
    private TextView gainTextView;
    private TextView contrastTextView;
    private TextView saturationTextView;
    private TextView whiteBalanceTextView;
    private TextView tintTextView;
    private TextView redTextView;
    private TextView greenTextView;
    private TextView blueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_look);

        liftSeekBar = findViewById(R.id.lift_seek_bar);
        liftTextView = findViewById(R.id.lift_value);
        liftSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                NumberFormat nf = new DecimalFormat("#.#");
                liftTextView.setText(nf.format((double) i / 10));
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

        gammaSeekBar = findViewById(R.id.gamma_seek_bar);
        gammaTextView = findViewById(R.id.gamma_value);
        gammaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                NumberFormat nf = new DecimalFormat("#.#");
                gammaTextView.setText(nf.format((double) i / 10));
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

        gainSeekBar = findViewById(R.id.gain_seek_bar);
        gainTextView = findViewById(R.id.gain_value);
        gainSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                NumberFormat nf = new DecimalFormat("#.#");
                gainTextView.setText(nf.format((double) i / 10));
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
                contrastTextView.setText(nf.format((double) i / 10));
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
                saturationTextView.setText(nf.format((double) i / 10));
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

        tintSeekBar = findViewById(R.id.tint_seek_bar);
        tintSeekBar.setProgress(0);
        tintTextView = findViewById(R.id.tint_value);
        tintSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tintTextView.setText(String.valueOf(i));
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
                redTextView.setText(nf.format((double) i / 10));
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
                greenTextView.setText(nf.format((double) i / 10));
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
                blueTextView.setText(nf.format((double) i / 10));
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
            liftSeekBar.setProgress(0);
            gammaSeekBar.setProgress(15);
            gainSeekBar.setProgress(0);
            contrastSeekBar.setProgress(10);
            saturationSeekBar.setProgress(10);
            whiteBalanceSeekBar.setProgress(5000);
            tintSeekBar.setProgress(0);
            redSeekBar.setProgress(10);
            greenSeekBar.setProgress(10);
            blueSeekBar.setProgress(10);
        });

    }
}
