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

public class SplashScreenActivity extends Activity {

    private static final String TAG = "SplashScreen";
    // License checking
    private LicenseCheckerCallback _licenseCheckerCallback;
    private LicenseChecker _checker;
    private Handler mHandler;
    private static boolean validLicenseFound = false;
    private AlertDialog mDialog;

    private boolean mDeviceHasNoCamera;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mHandler = new Handler();

        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            mDeviceHasNoCamera = true;
            showNoCameraError();
            return;
        }

        // Hide the system ui on create so it doesn't show for a split second
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

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
    protected void onStart() {
        super.onStart();

        Tracker tracker = ((ArtemisApplication) getApplication()).getTracker();
        tracker.setScreenName("SplashScreenActivity");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
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
            Intent i = new Intent(SplashScreenActivity.this, CloudDataUpdateActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(SplashScreenActivity.this, ArtemisActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            finish();
        }
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}


