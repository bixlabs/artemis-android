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

import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
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

public class SplashScreenActivity extends Activity {

    private static final String TAG = "SplashScreen";
    // License checking
    private LicenseCheckerCallback _licenseCheckerCallback;
    private LicenseChecker _checker;
    private Handler mHandler;
    private static boolean validLicenseFound = false;
    private AlertDialog mDialog;

    private static final int SHOW_CLOUD_UPDATE_FIRST_TIME = 0, SHOW_CLOUD_UPDATE_DIALOG = 1, CLOUD_UPDATE_COMPLETE = 2;
    private boolean mDeviceHasNoCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mHandler = new MyHandler(this);

        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            mDeviceHasNoCamera = true;
            showNoCameraError();
            return;
        }

        // check license (start main app when valid response comes back)
        bindLicenseCheckingObjects();
    }

    private void bindLicenseCheckingObjects() {
        // Try to use more data here. ANDROID_ID is a single point of attack.
        String deviceId = Secure.getString(getContentResolver(),
                Secure.ANDROID_ID);

        _licenseCheckerCallback = new MyLicenseCheckerCallback();
        // Construct the LicenseChecker with a policy.
        _checker = new LicenseChecker(this, new ServerManagedPolicy(this,
                new AESObfuscator(SALT, getPackageName(), deviceId)),
                BASE64_PUBLIC_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDeviceHasNoCamera) {
            // Do nothing if we have no camera
            return;
        }

        if (!validLicenseFound) {
            // Check license any time the splash screen starts, rather than just
            // onCreate (fix bug leo found)
            _checker.checkAccess(_licenseCheckerCallback);
        } else {
            // We've already found a license, just start normally
            Log.v(TAG,
                    "Already found a license previously, starting normally.");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startArtemis();
                }
            }, 1000);
        }
    }

    private void startArtemis() {
        // First check for database updates
        if (isNetworkAvailable()) {
            checkForCloudDBUpdates();
        }

        Intent i = new Intent(SplashScreenActivity.this, ArtemisActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
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

        final ArtemisDatabaseHelper db = new ArtemisDatabaseHelper(SplashScreenActivity.this);

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
                String sqlInsertOrUpdate;
                if (!exists) {
                    sqlInsertOrUpdate = "INSERT INTO `ZCAMERA` (ZHOROZONTALSIZE, ZSQUEEZERATIO, ZVERTICALSIZE, ZASPECTRATIO, ZCAMERAGENRE, ZCAPTUREMEDIUM, ZFORMATNAME, ZLENSTYPE, ZORDER, ZSENSORNAME, ZOBJECTID) VALUES ('%f','%f','%f','%s','%s','%s','%s','%s','%d','%s','%s')";
                } else {
                    sqlInsertOrUpdate = "UPDATE `ZCAMERA` SET ZHOROZONTALSIZE = '%f', ZSQUEEZERATIO = '%f', ZVERTICALSIZE = '%f', ZASPECTRATIO = '%s', ZCAMERAGENRE = '%s', ZCAPTUREMEDIUM = '%s', ZFORMATNAME = '%s', ZLENSTYPE = '%s', ZORDER = '%d', ZSENSORNAME = '%s' WHERE ZOBJECTID = '%s'";
                }
                String sql = String.format(
                        sqlInsertOrUpdate,
                        camera.getDouble("horozontalSize"),
                        camera.getDouble("squeezeRatio"),
                        camera.getDouble("verticalSize"),
                        camera.getString("aspectRatio").replaceAll("\'", "\'\'"),
                        camera.getString("cameraGenre").replaceAll("\'", "\'\'"),
                        camera.getString("captureMedium").replaceAll("\'", "\'\'"),
                        camera.getString("formatName").replaceAll("\'", "\'\'"),
                        camera.getString("lensType").replaceAll("\'", "\'\'"),
                        camera.getInt("order"),
                        camera.getString("sensorName").replaceAll("\'", "\'\'"),
                        camera.getObjectId()
                );
                db.executeSQL(sql);
            }
        } catch (ParseException e) {
            // Query failed
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
                String sqlInsertOrUpdate;
                if (!exists) {
                    sqlInsertOrUpdate = "INSERT INTO `ZLENSOBJECT` (ZLENSMM, ZLENSSET, ZSQUEEZERATIO, ZFORMATNAME, ZLENSCODE, ZLENSMAKE, ZOBJECTID) VALUES ('%f','%d','%f','%s','%s','%s', '%s')";
                } else {
                    sqlInsertOrUpdate = "UPDATE `ZLENSOBJECT` SET ZLENSMM = '%f', ZLENSSET = '%d', ZSQUEEZERATIO = '%f', ZFORMATNAME = '%s', ZLENSCODE = '%s', ZLENSMAKE = '%s' WHERE ZOBJECTID = '%s'";
                }
                String sql = String.format(
                        sqlInsertOrUpdate,
                        lens.getDouble("FL"),
                        lens.getInt("lensSet"),
                        lens.getDouble("Squeeze"),
                        lens.getString("Format").replaceAll("\'", "\'\'"),
                        lens.getString("LensCode").replaceAll("\'", "\'\'"),
                        lens.getString("LensMake").replaceAll("\'", "\'\'"),
                        lens.getObjectId()
                );
                db.executeSQL(sql);
            }
        } catch (ParseException e) {
            // Query failed
            failed = true;
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_checker != null) {
            _checker.onDestroy();
        }
        _licenseCheckerCallback = null;
        _checker = null;
    }

    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAueBLc9/HMW2/OdoeuXKZLxdoovcj327x9ESeV4BFuxjWIsQb3G61Awf7sfIfIZPbzAJc2vypctf5gosRgBfL6xUY1sBQiS79Xrg3DDqKc2oVHd+wLBKTTJARA7/JAC/2xDAryYJqYRkkKFO7DLEsLpP+B45/1r881uSwyEv1bC4SXIpisYhx5F859oxUtQzGvvunEgHNlo35yf7O0HiZB0iZoonHymCZMoBXV2rgRxiemBEmoKHsdDp0zP7bZTkVr1uIj9u39YWx4UVmZnB+fJphtgAiB4nVvxkzu97JiqCeyj86yN8hcLrLEFMNb8pgDKMKaBg21QzcjHfx07fsOwIDAQAB";
    private static final byte[] SALT = new byte[]{39, -15, 60, -48, 23, -30,
            44, 73, -25, 18, 125, -110, -17, 96, 126, 13, 1, 3, -82, -11};

    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
        @Override
        public void allow(int reason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            Log.i(TAG, "Valid license found");
            // Should allow user access.
            validLicenseFound = true;

            SharedPreferences.Editor editor = SplashScreenActivity.this.getSharedPreferences(
                    ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE).edit();
            editor.putBoolean("_l", true);
            editor.apply();

            startArtemis();
        }

        @Override
        public void dontAllow(int reason) {
            Log.i(TAG, "No license found");

            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }

            if (reason == Policy.RETRY) {

                mDialog = new AlertDialog.Builder(
                        SplashScreenActivity.this)
                        .setTitle(R.string.error_communicating_title)
                        .setMessage(R.string.error_communicating_dialog_body)
                        .setPositiveButton(R.string.retry_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface mDialog, int which) {
                                        _checker.checkAccess(_licenseCheckerCallback);
                                        mDialog.dismiss();
                                        mDialog = null;
                                    }
                                })
                        .setNegativeButton(R.string.quit_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface mDialog, int which) {
                                        mDialog.dismiss();
                                        mDialog = null;
                                        finish();
                                    }
                                }).setCancelable(false).create();
                mDialog.show();

            } else if (reason == Policy.NOT_LICENSED) {

                SharedPreferences.Editor editor = SplashScreenActivity.this.getSharedPreferences(
                        ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE).edit();
                editor.putBoolean("_l", false);
                editor.commit();

                mDialog = new AlertDialog.Builder(SplashScreenActivity.this)
                        .setTitle(R.string.unlicensed_dialog_title)
                        .setMessage(R.string.unlicensed_dialog_body)
                        .setPositiveButton(R.string.buy_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface mDialog, int which) {
                                        Intent marketIntent = new Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("http://market.android.com/details?id="
                                                        + getPackageName()));
                                        mDialog.dismiss();
                                        mDialog = null;
                                        startActivity(marketIntent);
                                    }
                                })
                        .setNegativeButton(R.string.quit_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface mDialog, int which) {
                                        mDialog.dismiss();
                                        mDialog = null;
                                        finish();
                                    }
                                }).create();
                mDialog.show();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 10000);
            }
        }

        @Override
        public void applicationError(int errorCode) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }

            Log.i(TAG, "Licensing Error: " + errorCode);

            SharedPreferences prefs = SplashScreenActivity.this.getSharedPreferences(
                    ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE);

            if (prefs.getBoolean("_l", false)) {
                Log.i(TAG, "custom license cached: starting artemis");
                startArtemis();
                return;
            }

            mDialog = new AlertDialog.Builder(SplashScreenActivity.this)
                    .setTitle(R.string.error_dialog_title)
                    .setMessage(R.string.error_dialog_body)
                    .setPositiveButton(R.string.quit_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface mDialog,
                                                    int which) {
                                    mDialog.dismiss();
                                    mDialog = null;
                                    finish();
                                }
                            }).create();
            mDialog.show();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 10000);
        }

    }

    public void showNoCameraError() {
        if (isFinishing()) {
            // Don't update UI if Activity is finishing.
            return;
        }

        mDialog = new AlertDialog.Builder(SplashScreenActivity.this)
                .setTitle(R.string.no_camera_detected)
                .setMessage(R.string.could_not_start)
                .setPositiveButton(R.string.quit_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface mDialog,
                                                int which) {
                                mDialog.dismiss();
                                mDialog = null;
                                finish();
                            }
                        }).create();
        mDialog.show();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 10000);
    }


    static class MyHandler extends Handler {
        private Dialog mDialog;
        private Context mContext;

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
                mDialog.show();
            } else if (msg.what == CLOUD_UPDATE_COMPLETE) {
                if (mDialog != null) {
                    mDialog.dismiss();
                    mDialog = null;
                }
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}


