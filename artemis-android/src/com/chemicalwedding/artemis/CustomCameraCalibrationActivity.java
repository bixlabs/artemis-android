package com.chemicalwedding.artemis;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chemicalwedding.artemis.LongPressButton.ClickBoolean;
import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
import com.chemicalwedding.artemis.database.Camera;
import com.chemicalwedding.artemis.database.CustomCamera;
import com.chemicalwedding.artemis.database.Lens;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class CustomCameraCalibrationActivity extends Activity {
    private static final float WALL_DISTANCE = 1000f;
    public static final String LENS_ANGLE_CALIBRATION_EXTRA = "lensViewAngles";
    private static final float NORMAL_AMOUNT = 1.0f;
    private static final float FINE_AMOUNT = 0.2f;
    private static final float LENS_FINE_AMOUNT = 0.1f;

    private float chipWidth;
    private float chipHeight;
    private float aspectRatio, squeezeRatio, focalLength;
    private String cameraName;
    private TextView chipWidthView, chipHeightView;

    private NumberFormat chipSizeFormat;

    private ArtemisDatabaseHelper mDBHelper;
    private Handler mUiHandler = new Handler();
    private ClickBoolean nextClick, nextFineClick, prevClick, prevFineClick;
    protected static final long lensRepeatSpeed = 35;
    private CameraPreview21 mCameraPreview;
    private boolean mIsLensAngleCalibrationSetting;
    private float mVertAngle, mHorizAngle;
    private ArtemisMath artemisMath_ = ArtemisMath.getInstance();
    private float deviceHorizontalWidth, deviceVerticalWidth;
    private NumberFormat viewAngleFormat;
    private Button mCancelButton, mResetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLensAngleCalibrationSetting = getIntent().getBooleanExtra(LENS_ANGLE_CALIBRATION_EXTRA, false);

        setContentView(R.layout.calibrate_custom_camera);

        // Hide the system ui on create so it doesn't show for a split second
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

        mDBHelper = new ArtemisDatabaseHelper(this);

        chipWidthView = (TextView) findViewById(R.id.actualChipWidth);
        chipHeightView = (TextView) findViewById(R.id.actualChipHeight);

        if (!mIsLensAngleCalibrationSetting) {

            aspectRatio = getIntent().getFloatExtra("ratio", 1.78f);
            squeezeRatio = getIntent().getFloatExtra("squeeze", 1f);
            focalLength = getIntent().getFloatExtra("focalLength", 50);
            cameraName = getIntent().getStringExtra("name");
            if (cameraName == null || cameraName.length() == 0) {
                cameraName = getString(R.string.untitle_custom_cam);
            }

            deviceHorizontalWidth = ArtemisMath.deviceHorizontalWidth;
            deviceVerticalWidth = ArtemisMath.deviceVerticalWidth;

            chipSizeFormat = NumberFormat.getInstance();
            chipSizeFormat.setMaximumFractionDigits(6);
            chipSizeFormat.setMinimumFractionDigits(2);

            chipWidth = 24.96f;
            calculateChipDimensions();
            // Custom calibration requires changing the default orange(green originally) box
            createOrangeBox();
            Log.v("CustomCameraCalibration", String.format(
                    "Initial chip width: %f Height: %f", chipWidth,
                    chipHeight));

        } else {
            mHorizAngle = CameraPreview21.effectiveHAngle;
            mVertAngle = CameraPreview21.effectiveVAngle;

            viewAngleFormat = NumberFormat.getInstance();
            viewAngleFormat.setMaximumIntegerDigits(3);
            viewAngleFormat.setMaximumFractionDigits(1);

            TextView horizLabel = (TextView) findViewById(R.id.calibrateChipWidth);
            TextView vertLabel = (TextView) findViewById(R.id.calibrateChipHeight);

            mCancelButton = (Button) findViewById(R.id.calibrateCancelButton);
            mCancelButton.setVisibility(View.VISIBLE);
            mResetButton = (Button) findViewById(R.id.calibrateResetButton);
            mResetButton.setVisibility(View.VISIBLE);

            horizLabel.setText(R.string.horizontal_angle);
            vertLabel.setText(R.string.vertical_angle);

            Camera selectedCamera = artemisMath_.getSelectedCamera();
            Lens selectedLens = artemisMath_.getSelectedLenses().get(artemisMath_.get_selectedLensIndex());
            ArtemisRectF orangeBox = artemisMath_.getOutsideBox();
            for (Iterator<ArtemisRectF> it = artemisMath_.get_currentLensBoxes().iterator(); it.hasNext(); ) {
                ArtemisRectF box = it.next();
                if (box.getColor() != Color.BLACK) {
                    it.remove();
                }
            }

            calculateDeviceSizeForViewAngles();
            updateViewAngleUI();

            focalLength = selectedLens.getFL();
            aspectRatio = selectedCamera.getHoriz() / selectedCamera.getVertical();
            squeezeRatio = selectedCamera.getSqz();
            chipWidth = selectedCamera.getHoriz();
            chipHeight = chipWidth / aspectRatio;
        }
        this.setFocalLengthUI();

        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scalePreview();
            }
        }, 300);

        bindViewEvents();

        mCameraPreview = CameraPreview21.newInstance();
        getFragmentManager().beginTransaction()
                .add(R.id.cameraContainer, mCameraPreview)
                .commit();
    }

    private void calculateDeviceSizeForViewAngles() {
        deviceHorizontalWidth = (float) (2 * (Math.tan(Math
                .toRadians(mHorizAngle / 2)) * WALL_DISTANCE));
        deviceVerticalWidth = (float) (2 * (Math.tan(Math
                .toRadians(mVertAngle / 2)) * WALL_DISTANCE));
    }

    private void updateViewAngleUI() {
        chipWidthView.setText(viewAngleFormat.format(mHorizAngle));
        chipHeightView.setText(viewAngleFormat.format(mVertAngle));
    }

    private void setFocalLengthUI() {
        NumberFormat flFormat = NumberFormat.getInstance();
        flFormat.setMaximumIntegerDigits(4);
        flFormat.setMaximumFractionDigits(1);
        ((Button) findViewById(R.id.calibrateFocalLength)).setText(flFormat
                .format(focalLength) + "mm");
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (this.artemisMath_ != null) {
            this.artemisMath_.calculateRectBoxesAndLabelsForLenses();
            this.artemisMath_.resetTouchToCenter();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker tracker = ((ArtemisApplication) getApplication()).getTracker();
        tracker.setScreenName(mIsLensAngleCalibrationSetting ? "LensAngleCalibration" : "CustomCameraCalibrationActivity");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        if (mIsLensAngleCalibrationSetting) {
            new AlertDialog.Builder(this).setTitle(R.string.visual_lens_angle_calibration)
                    .setMessage("Please have the correct camera you want to calibrate selected prior to starting calibration")
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Return to Select Camera", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(SettingsActivity.RESULT_CONFIGURE_CAMERA_MAIN_ACTIVITY);
                            finish();
                        }
                    }).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    private final Runnable prevRunnable = new Runnable() {
        public void run() {
            if (prevClick.isDown()) {
                previous();
                mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
                        + lensRepeatSpeed);

            }
        }
    };

    private final Runnable prevFineRunnable = new Runnable() {
        public void run() {
            if (prevFineClick.isDown()) {
                previousFine();
                mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
                        + lensRepeatSpeed);

            }
        }
    };

    private final Runnable nextRunnable = new Runnable() {
        public void run() {
            if (nextClick.isDown()) {
                next();
                mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
                        + lensRepeatSpeed);
            }
        }
    };

    private final Runnable nextFineRunnable = new Runnable() {
        public void run() {
            if (nextFineClick.isDown()) {
                nextFine();
                mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
                        + lensRepeatSpeed);

            }
        }
    };

    protected void previous() {
        if (!mIsLensAngleCalibrationSetting) {
            chipWidth += NORMAL_AMOUNT;
            calculateChipDimensions();
        } else {
            lensAngleAdjust(-NORMAL_AMOUNT);
        }
        scalePreview();
    }

    protected void previousFine() {
        if (!mIsLensAngleCalibrationSetting) {
            chipWidth += FINE_AMOUNT;
            calculateChipDimensions();
        } else {
            lensAngleAdjust(-LENS_FINE_AMOUNT);
        }
        scalePreview();
    }

    protected void next() {
        if (!mIsLensAngleCalibrationSetting) {
            chipWidth -= NORMAL_AMOUNT;
            calculateChipDimensions();
        } else {
            lensAngleAdjust(NORMAL_AMOUNT);
        }
        scalePreview();
    }

    protected void nextFine() {
        if (!mIsLensAngleCalibrationSetting) {
            chipWidth -= FINE_AMOUNT;
        } else {
            lensAngleAdjust(LENS_FINE_AMOUNT);
        }
        scalePreview();
    }


    private void lensAngleAdjust(float amount) {
        float ratio = mVertAngle / mHorizAngle;
        mHorizAngle += amount;
        if (mHorizAngle < 1.0) {
            mHorizAngle = 1.0f;
        }
        mVertAngle = mHorizAngle * ratio;
        calculateDeviceSizeForViewAngles();
        updateViewAngleUI();
    }

    private void bindViewEvents() {
        LongPressButton prevButton = (LongPressButton) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });
        prevButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                prevClick.setDown(true);
                mUiHandler.post(prevRunnable);
                return true;
            }
        });

        LongPressButton finePrevButton = (LongPressButton) findViewById(R.id.finePrevButton);
        finePrevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                previousFine();
            }
        });
        finePrevButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                prevFineClick.setDown(true);
                mUiHandler.post(prevFineRunnable);
                return true;
            }
        });

        LongPressButton nextButton = (LongPressButton) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        nextButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                nextClick.setDown(true);
                mUiHandler.post(nextRunnable);
                return true;
            }
        });

        LongPressButton fineNextButton = (LongPressButton) findViewById(R.id.fineNextButton);
        fineNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextFine();
            }
        });
        fineNextButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                nextFineClick.setDown(true);
                mUiHandler.post(nextFineRunnable);
                return true;
            }
        });

        nextClick = nextButton.getClickBoolean();
        prevClick = prevButton.getClickBoolean();
        nextFineClick = fineNextButton.getClickBoolean();
        prevFineClick = finePrevButton.getClickBoolean();

        findViewById(R.id.calibrateSaveButton).setOnClickListener(
                saveClickListener);

        findViewById(R.id.calibrateFocalLength).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomCameraCalibrationActivity.this);
                        builder.setTitle("Lens Focal Length (mm)");

                        final EditText input = new EditText(CustomCameraCalibrationActivity.this);
                        input.setText(viewAngleFormat.format(focalLength));
                        input.setSelection(0, input.length());
                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                focalLength = Float.parseFloat(input.getText().toString());
                                setFocalLengthUI();
                                scalePreview();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });

        // settings view angle calibration related
        if (mCancelButton != null) {
            mCancelButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (mResetButton != null) {
            mResetButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(CustomCameraCalibrationActivity.this)
                            .setTitle("Reset Device Angles")
                            .setMessage("Are you sure you want to reset to the automatically detected angles for your device?")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHorizAngle = CameraPreview21.deviceHAngle;
                                    mVertAngle = CameraPreview21.deviceVAngle;
                                    calculateDeviceSizeForViewAngles();
                                    updateViewAngleUI();
                                    scalePreview();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }
            });
        }
    }

    public float[] calculateViewingAngle(float lensFocalLength) {
        float Hfraction = (chipWidth * squeezeRatio) / (2 * lensFocalLength);
        float Vfraction = (chipHeight * 1) / (2 * lensFocalLength);
        float HviewingAngle = (float) (2 * (Math
                .toDegrees(Math.atan(Hfraction))));
        float VviewingAngle = (float) (2 * (Math
                .toDegrees(Math.atan(Vfraction))));
        float HStandard = calculateWidthAndHeightLens(HviewingAngle);
        float VStandard = calculateWidthAndHeightLens(VviewingAngle);
        float hprop = (HStandard / deviceHorizontalWidth);
        float vprop = (VStandard / deviceVerticalWidth);
        return new float[]{HviewingAngle, VviewingAngle, HStandard, VStandard,
                hprop, vprop};
    }

    float[] viewAngles;

    protected void scalePreview() {

        viewAngles = calculateViewingAngle(focalLength);

        float hprop = viewAngles[4];
        float hWidth = viewAngles[2];

        mCameraPreview.scaleFactor = 1 / hprop;

        Log.v("CustomCameraCalibration", String.format(
                "CustomCam %f hWidth: %f hangle: %f",
                mCameraPreview.scaleFactor, hWidth, viewAngles[0]));
        mCameraPreview.calculateZoom(false);
    }

    private void createOrangeBox() {
        int screenWidth = artemisMath_.screenWidth;
        int screenHeight = artemisMath_.screenHeight;

        int lowerMargin = (int) (screenHeight * 0.899f);
        int topMargin = (int) (screenHeight * 0.037f);

        float myprop = aspectRatio;
        int maximumWidth = (int) ((lowerMargin - topMargin) * myprop);

        // center 4:3 view
        int xmin = (screenWidth - maximumWidth) / 2;
        int xmax = xmin + maximumWidth;
        ArtemisRectF greenBox = new ArtemisRectF("", xmin, topMargin, xmax,
                lowerMargin);
        greenBox.setColor(ArtemisMath.orangeBoxColor);

        ArrayList<ArtemisRectF> boxes = artemisMath_
                .get_currentLensBoxes();
        boxes.clear();
        artemisMath_.setOutsideBox(greenBox);

        ArtemisRectF blackBox1 = new ArtemisRectF(null, 0, 0, xmin,
                screenHeight);
        ArtemisRectF blackBox2 = new ArtemisRectF(null, xmax, 0, screenWidth,
                screenHeight);

        ArtemisRectF blackBox3 = new ArtemisRectF(null, 0, 0, screenWidth,
                topMargin);
        blackBox3.setColor(Color.BLACK);

        ArtemisRectF blackBox4 = new ArtemisRectF(null, 0, lowerMargin,
                screenWidth, screenHeight);
        blackBox4.setColor(Color.BLACK);

        boxes.add(blackBox1);
        boxes.add(blackBox2);
        boxes.add(blackBox3);
        boxes.add(blackBox4);

    }

    private static float calculateWidthAndHeightLens(float angle) {
        return (float) (WALL_DISTANCE * 2 * (Math.tan(Math
                .toRadians(angle / 2))));
    }

    protected void calculateChipDimensions() {
        chipHeight = chipWidth / aspectRatio;
        updateChipTextViews();
    }

    private void updateChipTextViews() {
        chipWidthView.setText(chipSizeFormat.format(chipWidth));
        chipHeightView.setText(chipSizeFormat.format(chipHeight));
    }

    private final OnClickListener saveClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!mIsLensAngleCalibrationSetting) {
                try {
                    // insert the new camera
                    CustomCamera customCam = new CustomCamera();
                    customCam.setSensorheight(chipHeight);
                    customCam.setSensorwidth(chipWidth);
                    customCam.setName(cameraName);
                    customCam.setSqueeze(squeezeRatio);
                    mDBHelper.insertCustomCamera(customCam);
                    Intent intent = new Intent();
                    intent.putExtra("newcustomcam", true);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialog errorDialog = new AlertDialog.Builder(
                            CustomCameraCalibrationActivity.this)
                            .setTitle(R.string.custom_camera_error)
                            .setMessage(R.string.custom_camera_error_message)
                            .setNegativeButton(R.string.ok, null).create();
                    errorDialog.show();
                }
            } else {
                // Put in effect, save lens hardware angles to preferences, clean up, and finish
                CameraPreview21.effectiveHAngle = mHorizAngle;
                CameraPreview21.effectiveVAngle = mVertAngle;

                SharedPreferences artemisPrefs = CustomCameraCalibrationActivity.this.getApplicationContext()
                        .getSharedPreferences(ArtemisPreferences.class.getSimpleName(),
                                Context.MODE_PRIVATE);
                artemisPrefs.edit()
                        .putFloat(ArtemisPreferences.CAMERA_LENS_H_ANGLE, mHorizAngle)
                        .putFloat(ArtemisPreferences.CAMERA_LENS_V_ANGLE, mVertAngle).commit();
                finish();
            }
        }
    };
}
