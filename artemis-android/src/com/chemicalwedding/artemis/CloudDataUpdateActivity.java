package com.chemicalwedding.artemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;

import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CloudDataUpdateActivity extends Activity {

    private static final String TAG = "CloudDataUpdate";
    private Handler mHandler;
    private AlertDialog mDialog;

    private static final int SHOW_CLOUD_UPDATE_FIRST_TIME = 0, SHOW_CLOUD_UPDATE_DIALOG = 1, CLOUD_UPDATE_COMPLETE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mHandler = new MyHandler(this);

        // Hide the system ui on create so it doesn't show for a split second
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker tracker = ((ArtemisApplication) getApplication()).getTracker();
        tracker.setScreenName(TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        ((ArtemisApplication) getApplication()).postOnWorkerThread(new Runnable() {
            @Override
            public void run() {
                checkForCloudDBUpdates();
            }
        });
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
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkForCloudDBUpdates() {
        boolean failed = false;
        final SharedPreferences artemisPrefs = this
                .getApplicationContext().getSharedPreferences(
                        ArtemisPreferences.class.getSimpleName(),
                        Context.MODE_PRIVATE);
        final long lastUpdateCheck = artemisPrefs.getLong(ArtemisPreferences.DB_LAST_UPDATE_CHECK, -1);
        boolean isFirstInitialization = lastUpdateCheck == -1;
        Date lastUpdateDate = null;
        if (lastUpdateCheck > 0) {
            lastUpdateDate = new Date(lastUpdateCheck);
        }

        ParseQuery<ParseObject> cameraQuery = ParseQuery.getQuery("cameras");
        cameraQuery.orderByAscending("cameraGenre");
        cameraQuery.addAscendingOrder("formatName");
        cameraQuery.addAscendingOrder("sensorName");
        cameraQuery.whereEqualTo("live", true);
        cameraQuery.setLimit(1000);
        if (lastUpdateDate != null) {
            cameraQuery.whereGreaterThanOrEqualTo("updatedAt", lastUpdateDate);
        }

        final ArtemisDatabaseHelper db = new ArtemisDatabaseHelper(CloudDataUpdateActivity.this);

        try {
            List<ParseObject> cameraList = null;
            cameraList = cameraQuery.find();
            if (!cameraList.isEmpty() && isFirstInitialization) {
                // First time download -- drop bundled data only if we have query data
                db.dropTablesAndCreate(null, false);
            }

            Log.d(TAG, String.format("Updating %d cameras", cameraList.size()));

            if (!cameraList.isEmpty()) {
                mHandler.sendEmptyMessage(isFirstInitialization ? SHOW_CLOUD_UPDATE_FIRST_TIME : SHOW_CLOUD_UPDATE_DIALOG);
            }
            for (ParseObject camera : cameraList) {
                boolean exists = false;
                if (!isFirstInitialization) {
                    exists = db.checkCameraByRemoteObjectID(camera.getObjectId());
                }
                db.addUpdateCloudCamera(camera, exists);
            }
        } catch (ParseException e) {
            // Query failed
            e.printStackTrace();
            failed = true;
        }

        if (failed) {
            Log.e(TAG, "parseexception on cameras");
            return;
        }

        // Now update the lenses too
        ParseQuery<ParseObject> lensQuery = ParseQuery.getQuery("lenses");
        lensQuery.orderByAscending("Format");
        lensQuery.addAscendingOrder("LensMake");
        lensQuery.addAscendingOrder("FL");
        lensQuery.whereEqualTo("live", true);
        lensQuery.setLimit(3000);
        if (lastUpdateDate != null) {
            lensQuery.whereGreaterThanOrEqualTo("updatedAt", lastUpdateDate);
        }

        try {
            List<ParseObject> lensList = lensQuery.find();

            Log.d(TAG, String.format("Updating %d lenses", lensList.size()));

            if (!lensList.isEmpty()) {
                mHandler.sendEmptyMessage(isFirstInitialization ? SHOW_CLOUD_UPDATE_FIRST_TIME : SHOW_CLOUD_UPDATE_DIALOG);
            }
            for (ParseObject lens : lensList) {
                boolean exists = false;
                if (!isFirstInitialization) {
                    exists = db.checkLensByRemoteObjectID(lens.getObjectId());
                }
                db.addUpdateCloudLens(lens, exists);
            }
        } catch (ParseException e) {
            // Query failed
            failed = true;
        }
        db.close();

        if (failed) {
            Log.e(TAG, "parseexception on lenses");
            return;
        }

        mHandler.sendEmptyMessage(CLOUD_UPDATE_COMPLETE);

        artemisPrefs.edit().putLong(ArtemisPreferences.DB_LAST_UPDATE_CHECK, new Date().getTime()).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    static class MyHandler extends Handler {
        private Context mContext;
        private AlertDialog mDialog;

        public MyHandler(Context context) {
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == SHOW_CLOUD_UPDATE_FIRST_TIME) {
                if (mDialog == null) {
                    mDialog = ProgressDialog.show(mContext, "Downloading Cameras & Lenses", "Please wait while the latest cameras and lenses are downloaded for the first time", true, false);
                }
            } else if (msg.what == SHOW_CLOUD_UPDATE_DIALOG) {
                mDialog = ProgressDialog.show(mContext, "Updating Cameras & Lenses", "Please wait while the most recent cameras and lenses are updated from the cloud", true, false);
            } else if (msg.what == CLOUD_UPDATE_COMPLETE) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
                Activity cloudDataActivity = (CloudDataUpdateActivity) mContext;

                Intent i = new Intent(cloudDataActivity, ArtemisActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                cloudDataActivity.startActivity(i);
                cloudDataActivity.finish();
            }
        }
    }
}


