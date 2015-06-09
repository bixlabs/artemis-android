package com.chemicalwedding.artemis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class SplashScreenActivity extends Activity {

	// License checking
	private LicenseCheckerCallback _licenseCheckerCallback;
	private LicenseChecker _checker;
	private Handler mHandler = new Handler();
	private static final String logTag = "SplashScreen";
	private static boolean validLicenseFound = false;
	private AlertDialog mDialog;
	private boolean mDeviceHasNoCamera;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		int numCameras = Camera.getNumberOfCameras();
		if (numCameras == 0) {
			mDeviceHasNoCamera = true;
			showNoCameraError();
			return;
		}
		
		// check license (start main app when valid response comes back)
		bindLicenseCheckingObjects();
		
		CameraPreview14.initCameraDetails();
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
			Log.v(logTag,
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
		Intent i = new Intent(SplashScreenActivity.this, ArtemisActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(i);
		finish();
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
	private static final byte[] SALT = new byte[] { 39, -15, 60, -48, 23, -30,
			44, 73, -25, 18, 125, -110, -17, 96, 126, 13, 1, 3, -82, -11 };

	private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
		@Override
		public void allow(int reason) {
			if (isFinishing()) {
				// Don't update UI if Activity is finishing.
				return;
			}
			Log.i(logTag, "Valid license found");
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
			Log.i(logTag, "No license found");

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

            Log.i(logTag, "Licensing Error: " + errorCode);

            SharedPreferences prefs = SplashScreenActivity.this.getSharedPreferences(
                    ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE);

			if  (prefs.getBoolean("_l", false))  {
                Log.i(logTag, "custom license cached: starting artemis");
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
}