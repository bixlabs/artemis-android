package com.chemicalwedding.artemis;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.chemicalwedding.artemis.LongPressButton.ClickBoolean;
import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
import com.chemicalwedding.artemis.database.Camera;
import com.chemicalwedding.artemis.database.CustomCamera;
import com.chemicalwedding.artemis.database.Lens;
import com.chemicalwedding.artemis.database.ZoomLens;
import com.sbstrm.appirater.Appirater;

public class ArtemisActivity extends Activity implements
		SurfaceTextureListener, LoaderCallbacks<Cursor> {
	private static final String TAG = ArtemisActivity.class.getSimpleName();

	private static final String DEFAULT_LENS_MAKE = "Generic 35mm Lenses";
	private static final String DEFAULT_LENSES = "77,80,84,88,92,96,100";
	private static final int DEFAULT_CAMERA_ROW = 10;

	private static final int GALLERY_IMAGE_LOADER = 1;

	private Handler mUiHandler = new Handler();

	private CameraPreview14 mCameraPreview;
	private android.hardware.Camera mCamera;
	private TextureView mTextureView;

	private LongPressButton _nextLensButton;
	private LongPressButton _prevLensButton;
	private ClickBoolean nextClickBoolean;
	private ClickBoolean prevClickBoolean;
	protected static ViewFlipper viewFlipper;
	private ViewFlipper _cameraSettingsFlipper;
	private ViewFlipper _lensSettingsFlipper;
	private ViewFlipper savePictureViewFlipper;
	protected static TextView _cameraDetailsText;
	protected static TextView _lensMakeText;
	protected static TextView headingTiltText;
	private ListView _lensListView;
	protected static TextView _lensFocalLengthText;
	protected CameraOverlay mCameraOverlay;
	protected CameraAngleDetailView mCameraAngleDetailView;

	private ArtemisDatabaseHelper _artemisDBHelper;

	protected static int currentViewId;
	// camera related
	private Camera _selectedCamera, tempSelectedCamera;
	private ArrayList<String> _allCameraFormats;
	// camera rowid paired with ratio
	private ArrayList<Pair<Integer, String>> _ratiosListForCamera;

	// lens related
	private ArrayList<Lens> _lensesForMake, tempLensesForMake;
	String tempSelectedLensMake;
	private ArrayList<Lens> _selectedLenses = new ArrayList<Lens>();
	private ArrayList<CustomCamera> customCameras;
	private ArrayList<ZoomLens> zoomLenses;
	private RelativeLayout addCustomLensLayout;

	// use to handle math calculations
	private static ArtemisMath _artemisMath = ArtemisMath.getInstance();

	protected static ImageView pictureSavePreview;

	private LocationManager locationManager;
	private SensorManager sensorManager;
	private String locationProvider;
	private static Location lastKnownLocation;
	protected static Location pictureSaveLocation;
	protected static String pictureSaveHeadingTiltString;
	protected static String savePictureFolder;

	protected static boolean gpsEnabled = false, sensorEnabled = false;
	protected static int headingDisplaySelection = 2;

	protected static Bitmap arrowBackgroundImage;

	private boolean wasFocalLengthButtonPressed = false;
	private boolean isEditingPictureDetailsOnly = false;
	private boolean isHapticFeedbackEnabled = true;
	private String backPressedMode;
	protected boolean takePictureAfterAutoFocusAndLongClickShutter = true;
	protected boolean autoFocusAfterLongClickShutter = true;
	protected boolean takePictureAfterReleaseLongClickShutter = false;

	private boolean volumeUpAutoFocusAndPicture;
	private boolean volumeUpPicture;
	private boolean volumeUpAutoFocus;
	private boolean volumeDownAutoFocusAndPicture;
	private boolean volumeDownPicture;
	private boolean volumeDownAutoFocus;

	protected static final long lensRepeatSpeedCustomLens = 35;
	protected static final long lensRepeatSpeedNormal = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "Creating Artemis Activity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		currentViewId = R.id.artemisPreview;

		SharedPreferences settings = this.getSharedPreferences(
				ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);

		// Check settings if we should keep the screen on
		if (settings.getBoolean(
				getString(R.string.preference_key_keepscreenon), false)) {
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		// Avoid the top bar animation in fullscreen app
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
						| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

		// Appirator
		Appirater.appLaunched(this);

		startArtemis();
	}

	protected void startArtemis() {
		// Connect member variables to view objects
		bindViewObjects();

		// Setup listeners for events
		bindViewEvents();

		// connect to the database and load some initial data
		initDatabase();

		// setup the previous camera and lens selection
		initCameraAndLensSelection();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Destroying Artemis");

		// Close the database connection
		if (_artemisDBHelper != null) {
			_artemisDBHelper.close();
			_artemisDBHelper = null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "Pausing Artemis");
<<<<<<< HEAD
		_cameraPreview.releaseCamera();
<<<<<<< HEAD
		mCameraContainer.removeAllViews();
=======
>>>>>>> f0bf7d9 (Revert back galaxy nexus changes)
=======
>>>>>>> fde0e9e (Changes to make the preview start properly on the nexus 5)

		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

		if (gpsEnabled && locationManager != null)
			locationManager.removeUpdates(locationListener);

		if (sensorEnabled)
			sensorManager.unregisterListener(sensorEventListener);

		if (ArtemisActivity.arrowBackgroundImage != null) {
			ArtemisActivity.arrowBackgroundImage.recycle();
			ArtemisActivity.arrowBackgroundImage = null;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "Stopping Artemis");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "Starting Artemis");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "Resuming Artemis");

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
		_cameraPreview = new CameraPreview14(this, null);
		mCameraContainer.addView(_cameraPreview, new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

=======
>>>>>>> f0bf7d9 (Revert back galaxy nexus changes)
		if (_cameraPreview.isCameraReleased) {
			_cameraPreview.openCamera();
			// _artemisMath.calculateRectBoxesAndLabelsForLenses();
		}

<<<<<<< HEAD
=======
>>>>>>> fde0e9e (Changes to make the preview start properly on the nexus 5)
=======
		initPreferences();
<<<<<<< HEAD
<<<<<<< HEAD
		
>>>>>>> 71b640f (Fix resuming from settings and new changes not being applied)
=======

<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> 42f959f (Fix custom camera calibration activity on kitkat)
=======
		initCamera();
=======
		reinitCamera();
>>>>>>> e819b9d (Make resume work better, fix resume from settings)
=======
		if (Build.VERSION.SDK_INT < 19) {
			reinitCamera();
		}
>>>>>>> 7476643 (Fix bugs... don't show close lens to orange box, save and edit picture)
=======

		reinitCamera();
>>>>>>> a589c79 (Fix nexus 5 restart preview bug)

>>>>>>> 4c8ce40 (Finally nailed it, reuse the texture view on resume)
		initSensorManager();

		initLocationManager();

		// Set the background
		if (ArtemisActivity.arrowBackgroundImage == null) {
			Options o = new Options();
			o.inSampleSize = 2;
			ArtemisActivity.arrowBackgroundImage = BitmapFactory
					.decodeResource(getResources(), R.drawable.arrows, o);
		}
	}

	private boolean isSurfaceAvailable = false;

	private void reinitCamera() {
		if (mCamera == null && mTextureView != null && isSurfaceAvailable) {

			mCamera = android.hardware.Camera.open();

			try {
				mCamera.setPreviewTexture(mTextureView.getSurfaceTexture());
			} catch (IOException t) {
			}

			mCameraPreview.openCamera(mCamera, false);
			_artemisMath.calculateRectBoxesAndLabelsForLenses();
			this.reconfigureNextAndPreviousLensButtons();
			
		}
	}

	private void initSensorManager() {
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_UI);
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_UI);
		sensorEnabled = true;
	}

	private final SensorEventListener sensorEventListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				accelEvent(event);
			}

			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				magEvent(event);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		float[] lastAccelValue;

		private void accelEvent(SensorEvent event) {

			if (lastAccelValue == null) {
				lastAccelValue = new float[3];
			}
			System.arraycopy(event.values, 0, lastAccelValue, 0, 3);
		}

		float[] lastMagValue;

		private void magEvent(SensorEvent event) {
			if (lastMagValue == null) {
				lastMagValue = new float[3];
			}
			System.arraycopy(event.values, 0, lastMagValue, 0, 3);

			if (lastAccelValue != null) {
				computeOrientation();
			}
		}

		float[] inR = new float[16];
		float[] outR = new float[16];
		float[] orientationVector = new float[4];

		FloatAverage yawAvg = new FloatAverage(),
				pitchAvg = new FloatAverage(), rollAvg = new FloatAverage();

		private void computeOrientation() {

			if (SensorManager.getRotationMatrix(inR, null, lastAccelValue,
					lastMagValue)) {

				SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X,
						SensorManager.AXIS_Z, outR);

				SensorManager.getOrientation(outR, orientationVector);
				yawAvg.add(orientationVector[0]);
				pitchAvg.add(orientationVector[1]);
				rollAvg.add(orientationVector[2]);

				int yaw = (int) Math.toDegrees(yawAvg.average());
				if (yaw < 0) {
					yaw += 360;
				}
				int pitch = (int) Math.toDegrees(pitchAvg.average());
				int roll = (int) Math.toDegrees(rollAvg.average()) + 90;

				String heading = "";
				if (headingDisplaySelection > 0) {
					heading = azimuthToString(yaw);
					if (headingDisplaySelection > 1)
						heading += " | " + pitchToString(pitch) + " | "
								+ rollToString(roll);
				}
				if (headingTiltText != null)
					headingTiltText.setText(heading);
			}
		}

		final class FloatAverage {
			final static int SAMPLE = 5;
			ArrayList<Float> lastValues = new ArrayList<Float>();

			public void add(Float newFloat) {
				lastValues.add(newFloat);
				if (lastValues.size() > SAMPLE)
					lastValues.remove(0);
			}

			public Float average() {
				if (lastValues.size() > 0) {
					float sum = 0;
					for (Float val : lastValues) {
						sum += val;
					}
					return sum / lastValues.size();
				} else
					return null;
			}
		}

		private String azimuthToString(int azimuth) {
			String azString = "";

			// Add extra spaces to azimuth if required
			final String spaces = "  ";
			String azimuthFormatted = Integer.toString(azimuth);
			int spacesRequired = 3 - azimuthFormatted.length();
			azimuthFormatted = azimuthFormatted
					+ getString(R.string.degree_symbol)
					+ spaces.substring(0, spacesRequired);

			// 1st quadrant

			if (azimuth < 5 || azimuth > 355) {
				azString = azimuthFormatted + "N ";
			}

			else if (azimuth > 0 && azimuth < 22.5) {

				azString = azimuthFormatted + "N ";
			}

			else if (azimuth > 22.5 && azimuth < 67.5) {

				azString = azimuthFormatted + "NE";
			}
			// 2nd Quadrant
			else if (azimuth > 67.5 && azimuth < 112.5) {

				azString = azimuthFormatted + "E ";
			}

			else if (azimuth > 112.5 && azimuth < 157.5) {

				azString = azimuthFormatted + "SE";
			}
			// 3rd Quadrant
			else if (azimuth > 157.5 && azimuth < 202.5) {

				azString = azimuthFormatted + "S ";
			}

			else if (azimuth > 202.5 && azimuth < 247.5) {

				azString = azimuthFormatted + "SW";
			}
			// 4th Quadrant
			else if (azimuth > 247.5 && azimuth < 292.5) {

				azString = azimuthFormatted + "W ";
			}

			else if (azimuth > 292.5 && azimuth < 337.5) {

				azString = azimuthFormatted + "NW";
			}

			else if (azimuth > 337.5 && azimuth < 360) {

				azString = azimuthFormatted + "N ";
			}
			return azString;
		}

		private String pitchToString(int pitch) {

			final String spaces = "          ";
			String pitchFormatted = Integer.toString(Math.abs(pitch));
			final String degrees = getString(R.string.degree_symbol);
			if (pitch >= -1 && pitch <= 1) {
				pitchFormatted = "0" + degrees + getString(R.string.tilt_level)
						+ " ";
			} else if (pitch < -1) {
				pitchFormatted = pitchFormatted + degrees
						+ getString(R.string.tilt_up);
				int spacesRequired = 4 - pitchFormatted.length();
				pitchFormatted = pitchFormatted
						+ spaces.substring(0, spacesRequired);

			} else if (pitch > 1) {
				pitchFormatted = pitchFormatted + degrees
						+ getString(R.string.tilt_down);
				int spacesRequired = 4 - pitchFormatted.length();
				pitchFormatted = pitchFormatted
						+ spaces.substring(0, spacesRequired);
			}

			return pitchFormatted;
		}

		private String rollToString(int roll) {

			final String spaces = "            ";
			String rollFormatted = Integer.toString(Math.abs(roll));
			final String degrees = getString(R.string.degree_symbol);
			if (roll >= -1 && roll <= 1) {
				rollFormatted = "0" + degrees + getString(R.string.roll_level)
						+ "  ";
			} else if (roll < -1) {
				rollFormatted = rollFormatted + degrees
						+ getString(R.string.left_roll);
				int spacesRequired = 5 - rollFormatted.length();
				rollFormatted = rollFormatted
						+ spaces.substring(0, spacesRequired);

			} else if (roll > 1) {
				rollFormatted = rollFormatted + degrees
						+ getString(R.string.right_roll);
				int spacesRequired = 5 - rollFormatted.length();
				rollFormatted = rollFormatted
						+ spaces.substring(0, spacesRequired);
			}

			return rollFormatted;
		}
	};

	private void initLocationManager() {
		if (gpsEnabled) {
			Log.i(TAG, "Artemis GPS is enabled");
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(true);
			criteria.setBearingRequired(true);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);

			locationProvider = locationManager.getBestProvider(criteria, true);
			if (locationProvider != null) {
				lastKnownLocation = locationManager
						.getLastKnownLocation(locationProvider);
				Log.d(TAG, "Last known location is "
						+ (lastKnownLocation == null ? "null" : "not null"));
				locationManager.requestLocationUpdates(locationProvider, 60000, // 60
						// secs
						300, // 300 meters
						locationListener);
			}
		}
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			Log.d(TAG, "Updating Location");
			lastKnownLocation = location;
			Log.d(TAG, "Updated location is "
					+ (lastKnownLocation == null ? "null" : "not null"));

		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private void bindViewObjects() {
		mCameraPreview = (CameraPreview14) findViewById(R.id.cameraPreview);
		mTextureView = new TextureView(this);
		mTextureView.setSurfaceTextureListener(this);
		mCameraPreview.setTextureView(mTextureView);
		mCameraPreview.addView(mTextureView);

		mCameraOverlay = (CameraOverlay) findViewById(R.id.cameraOverlay);
		mCameraAngleDetailView = (CameraAngleDetailView) findViewById(R.id.CameraAngleDetailView);
		viewFlipper = (ViewFlipper) findViewById(R.id.mainViewFlipper);
		_lensSettingsFlipper = (ViewFlipper) findViewById(R.id.lens_settings_flipper);
		_cameraDetailsText = (TextView) findViewById(R.id.cameraDetailsText);
		_lensMakeText = (TextView) findViewById(R.id.lensMakeText);
		headingTiltText = (TextView) findViewById(R.id.headingTiltText);
		_nextLensButton = (LongPressButton) findViewById(R.id.nextButton);
		_prevLensButton = (LongPressButton) findViewById(R.id.prevButton);
		nextClickBoolean = _nextLensButton.getClickBoolean();
		prevClickBoolean = _prevLensButton.getClickBoolean();

		// keep binding more objects
		_lensFocalLengthText = (TextView) findViewById(R.id.lensFocalLengthText);
		pictureSavePreview = (ImageView) findViewById(R.id.savePicturePreview);
		savePictureViewFlipper = (ViewFlipper) findViewById(R.id.savePictureViewFlipper);

		addCustomLensLayout = (RelativeLayout) findViewById(R.id.addCustomLens);
	}

	private void bindViewEvents() {

		// Setup help button and help overlay touch events
		((View) findViewById(R.id.helpButtonView))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						openHelpFile();
						if (isHapticFeedbackEnabled) {
							buzz(v);
						}
					}
				});

		((View) findViewById(R.id.cameraDetailsView))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						openCameraSettingsView();
						if (isHapticFeedbackEnabled) {
							buzz(v);
						}
					}
				});

		((View) findViewById(R.id.lensMakeView))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						wasFocalLengthButtonPressed = false;
						addCustomLensLayout.setVisibility(View.INVISIBLE);
						openLensSettingsView();
						if (isHapticFeedbackEnabled) {
							buzz(v);
						}
					}
				});

		Button cancelLensesButton = (Button) findViewById(R.id.cancelLenses);
		cancelLensesButton
				.setOnClickListener(new LenseSelectionCancelClickListener());
		Button saveLensesButton = (Button) findViewById(R.id.saveLenses);
		saveLensesButton
				.setOnClickListener(new LenseSelectionSaveClickListener());

		// next / prev lens
		_nextLensButton.setOnClickListener(nextLensClickListener);
		_nextLensButton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				nextClickBoolean.setDown(true);
				mUiHandler.post(nextLensRunnable);
				if (isHapticFeedbackEnabled) {
					buzz(_lensFocalLengthText);
				}
				return true;
			}
		});
		_prevLensButton.setOnClickListener(previousLensClickListener);
		_prevLensButton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				prevClickBoolean.setDown(true);
				mUiHandler.post(previousLensRunnable);
				if (isHapticFeedbackEnabled) {
					buzz(_lensFocalLengthText);
				}
				return true;
			}
		});

		// view under the focal length label in the preview
		((ImageView) findViewById(R.id.focalLengthLensButton))
				.setOnClickListener(new focalLengthLensButtonViewClickListener());

		// fullscreen button
		((ImageView) findViewById(R.id.fullscreenButton))
				.setOnClickListener(fullscreenViewClickListener);

		// save picture buttons
		((Button) findViewById(R.id.savePictureCancelButton))
				.setOnClickListener(new CancelSavePictureClickListener());
		((Button) findViewById(R.id.save_edit_details_PictureCancelButton))
				.setOnClickListener(new CancelSavePictureClickListener());
		((Button) findViewById(R.id.savePictureAndEditButton))
				.setOnClickListener(new EditPictureDetailsClickListener());
		((Button) findViewById(R.id.savePictureButton))
				.setOnClickListener(new SavePictureClickListener());

		((Button) findViewById(R.id.savePictureDetails))
				.setOnClickListener(new SavePictureEditDetailsClickListener());

		mCameraAngleDetailView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (_artemisMath.isHAngleMode())
					_artemisMath.setHAngleMode(false);
				else
					_artemisMath.setHAngleMode(true);
				v.postInvalidate();
			}
		});

		((ImageView) findViewById(R.id.camera_settings_back))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});

		((ImageView) findViewById(R.id.lens_settings_back))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int displayed = _lensSettingsFlipper
								.getDisplayedChild();
						if (displayed > 0 && displayed < 2
								&& !wasFocalLengthButtonPressed) {
							_lensSettingsFlipper.setInAnimation(
									ArtemisActivity.this, R.anim.slide_in_left);
							_lensSettingsFlipper.setOutAnimation(
									ArtemisActivity.this,
									R.anim.slide_out_right);
							addCustomLensLayout.setVisibility(View.INVISIBLE);

							_lensSettingsFlipper.showPrevious();
						} else if (_lensSettingsFlipper.getDisplayedChild() == 0
								|| wasFocalLengthButtonPressed) {
							wasFocalLengthButtonPressed = false;
							openArtemisCameraPreviewView();
						} else if (displayed == 2) {
							// Custom zoom lens, return to first page
							wasFocalLengthButtonPressed = false;
							_lensSettingsFlipper.setInAnimation(
									ArtemisActivity.this, R.anim.slide_in_left);
							_lensSettingsFlipper.setOutAnimation(
									ArtemisActivity.this,
									R.anim.slide_out_right);
							_lensSettingsFlipper.setDisplayedChild(0);
						}
					}
				});

		findViewById(R.id.settingsButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						openArtemisSettings();
						if (isHapticFeedbackEnabled) {
							buzz(v);
						}
					}
				});

		((SearchView) findViewById(R.id.cameraSearch))
				.setOnQueryTextListener(new CameraSearchQueryTextListener());

		// Custom cam related
		// findViewById(R.id.save_custom_camera).setOnClickListener(
		// addCustomCameraClickListener);
		findViewById(R.id.save_custom_camera_active_sensor).setOnClickListener(
				addCustomCameraClickListener);
		findViewById(R.id.start_calib_custom_camera).setOnClickListener(
				startCustomCameraCalibration);
		findViewById(R.id.customCamera_active_sensor).setOnClickListener(
				startCustomCameraActiveSensorSize);

		findViewById(R.id.addCustomLensImage).setOnClickListener(
				addCustomLensClickListener);
		findViewById(R.id.addCustomLens).setOnClickListener(
				addCustomLensClickListener);

		// Gallery button
		findViewById(R.id.galleryButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						openGallery();
						if (isHapticFeedbackEnabled) {
							buzz(v);
						}
					}
				});

		findViewById(R.id.editMetaDataButton).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						isEditingPictureDetailsOnly = true; // indicate we
															// aren't in the
															// process of taking
															// a picture

						loadPictureDetailsSettings();

						viewFlipper.setDisplayedChild(3);
						savePictureViewFlipper.setDisplayedChild(1);
						if (isHapticFeedbackEnabled) {
							buzz(v);
						}
					}
				});
	}

	protected void buzz(View v) {
		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY,
				HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
						| HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING);
	}

	private void initDatabase() {
		// Open Database
		if (_artemisDBHelper == null) {
			_artemisDBHelper = new ArtemisDatabaseHelper(this);

			// load initialization data for the first camera selection
			// ((ArtemisApplication) getApplication())
			// .postOnWorkerThread(new Runnable() {
			// @Override
			// public void run() {
			_allCameraFormats = _artemisDBHelper.getCameraFormats();
			_allCameraFormats.add(getString(R.string.custom_cameras));
		}
		// }
		// });
	}

	private void initPreferences() {
		SharedPreferences artemisPrefs = getApplication().getSharedPreferences(
				ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);
		// check if gps is enabled
		gpsEnabled = artemisPrefs.getBoolean(ArtemisPreferences.GPS_ENABLED,
				true);

		// lock box setting
		CameraOverlay.lockBoxEnabled = artemisPrefs.getBoolean(
				ArtemisPreferences.LOCK_BOXES_ENABLED, false);

		// quick shot save setting
		CameraPreview14.quickshotEnabled = artemisPrefs.getBoolean(
				ArtemisPreferences.QUICKSHOT_ENABLED, false);

		// smooth image filter
		CameraPreview14.smoothImagesEnabled = artemisPrefs.getBoolean(
				ArtemisPreferences.SMOOTH_IMAGE_ENABLED, true);

		// autofocus camera before picture
		CameraPreview14.autoFocusBeforePictureTake = artemisPrefs.getBoolean(
				ArtemisPreferences.AUTO_FOCUS_ON_PICTURE, false);

		// black and white camera preview
		CameraPreview14.blackAndWhitePreview = artemisPrefs.getBoolean(
				getString(R.string.preference_key_previewblackwhite), false);

		// setup the save files directory
		savePictureFolder = Environment.getExternalStorageDirectory()
				.getAbsolutePath().toString()
				+ "/";
		savePictureFolder += artemisPrefs.getString(
				ArtemisPreferences.SAVE_PICTURE_FOLDER,
				getString(R.string.artemis_save_location_default));
		Log.v(TAG, "Save folder: " + savePictureFolder);

		File saveFolder = new File(savePictureFolder);
		if (!saveFolder.isDirectory()) {
			saveFolder.mkdirs();
		}

		ArtemisActivity.headingDisplaySelection = artemisPrefs.getInt(
				ArtemisPreferences.HEADING_DISPLAY, 2);

		isHapticFeedbackEnabled = artemisPrefs.getBoolean(
				getString(R.string.preference_key_hapticfeedback), true);

		CameraPreview14.savedImageJPEGQuality = Integer.parseInt(artemisPrefs
				.getString(
						getString(R.string.preference_key_savedImageQuality),
						getString(R.string.image_quality_default)));
		String savedImageSize = artemisPrefs.getString(
				getString(R.string.preference_key_savedImageScale),
				getString(R.string.image_size_default));
		if ("default".equals(savedImageSize)) {
			CameraPreview14.savedImageSizeIndex = 0;
		} else if ("large".equals(savedImageSize)) {
			CameraPreview14.savedImageSizeIndex = 1;
		} else {
			// small
			CameraPreview14.savedImageSizeIndex = 2;
		}

		backPressedMode = artemisPrefs.getString(
				getString(R.string.preference_key_backkeyOption),
				getString(R.string.backkey_default));

		String longPressShutterMode = artemisPrefs.getString(
				getString(R.string.preference_key_longpressshutter),
				getString(R.string.longPressShutter_default));

		autoFocusAfterLongClickShutter = false;
		takePictureAfterAutoFocusAndLongClickShutter = false;
		takePictureAfterReleaseLongClickShutter = false;

		if (!CameraPreview14.autoFocusBeforePictureTake) {
			if ("autofocusAndCapture".equals(longPressShutterMode)) {
				Log.v(TAG, "init long press autofocus and capture");
				autoFocusAfterLongClickShutter = true;
				takePictureAfterAutoFocusAndLongClickShutter = true;
				takePictureAfterReleaseLongClickShutter = false;
			} else if ("autofocusAndCaptureOnRelease"
					.equals(longPressShutterMode)) {
				Log.v(TAG, "init long press autofocus and capture on release");
				autoFocusAfterLongClickShutter = true;
				takePictureAfterAutoFocusAndLongClickShutter = false;
				takePictureAfterReleaseLongClickShutter = true;
			} else if ("autofocus".equals(longPressShutterMode)) {
				Log.v(TAG, "init long press only autofocus");
				autoFocusAfterLongClickShutter = true;
				takePictureAfterAutoFocusAndLongClickShutter = false;
				takePictureAfterReleaseLongClickShutter = false;
			}
		}

		// shutter release (take picture) button
		ImageView takePictureButton = (ImageView) findViewById(R.id.shutterButton);
		takePictureButton.setOnClickListener(takePictureClickListener);
		if (autoFocusAfterLongClickShutter) {
			takePictureButton.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					pictureSaveHeadingTiltString = headingTiltText.getText()
							.toString();

					mCameraPreview
							.autofocusCamera(takePictureAfterAutoFocusAndLongClickShutter);
					if (autoFocusAfterLongClickShutter
							&& !takePictureAfterReleaseLongClickShutter) {
						return true;
					}
					return false;
				}
			});
		}

		if (artemisPrefs.getBoolean(
				getString(R.string.preference_key_mapVolumeKeys), true)) {

			String volumeUpMode = artemisPrefs
					.getString(
							getString(R.string.preference_key_volumeUpAction),
							CameraPreview14.isAutoFocusSupported ? getString(R.string.volumeUpAction_default)
									: getString(R.string.volumeUpAction_noautofocus_default));

			if ("autofocusAndCapture".equals(volumeUpMode)) {
				volumeUpAutoFocusAndPicture = true;
			} else if ("takePicture".equals(volumeUpMode)) {
				volumeUpPicture = true;
			} else if ("autofocus".equals(volumeUpMode)) {
				volumeUpAutoFocus = true;
			}

			String volumeDownMode = artemisPrefs.getString(
					getString(R.string.preference_key_volumeDownAction),
					getString(R.string.volumeDownAction_default));

			if ("autofocusAndCapture".equals(volumeDownMode)) {
				volumeDownAutoFocusAndPicture = true;
			} else if ("takePicture".equals(volumeDownMode)) {
				volumeDownPicture = true;
			} else if ("autofocus".equals(volumeDownMode)) {
				volumeDownAutoFocus = true;
			}

		}
	}

	private void initCameraAndLensSelection() {
		SharedPreferences artemisPrefs = getApplication().getSharedPreferences(
				ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);

		// Retrieve the previously selected camera and lenses
		int selectedCameraRowId = artemisPrefs.getInt(
				ArtemisPreferences.SELECTED_CAMERA_ROW, DEFAULT_CAMERA_ROW);
		String lensMake = artemisPrefs.getString(
				ArtemisPreferences.SELECTED_LENS_MAKE, DEFAULT_LENS_MAKE);
		int selectedZoomLensPK = artemisPrefs.getInt(
				getString(R.string.preference_key_selectedzoomlens), -1);

		if (selectedCameraRowId > 0) {
			// Id is above 0, this is a normal Camera from the Camera's table
			setSelectedCamera(selectedCameraRowId, false, false);
		} else {
			// Set custom camera
			CustomCamera selectedCustomCamera = _artemisDBHelper
					.getCustomCameraDetailsForRowId(-selectedCameraRowId);

			Log.v(TAG, String.format("Custom cam loaded on start: %s",
					selectedCustomCamera));

			_selectedCamera = new Camera(selectedCustomCamera);
			tempSelectedCamera = _selectedCamera;
			_artemisMath.setSelectedCamera(_selectedCamera);
			lensMake = DEFAULT_LENS_MAKE;

			_cameraDetailsText.setText(selectedCustomCamera.getName() + " "
					+ _selectedCamera.getRatio());
		}

		if (selectedZoomLensPK < 0) {
			setSelectedLensMake(lensMake, false, false);

			String selectedLensesRowIds = artemisPrefs.getString(
					ArtemisPreferences.SELECTED_LENS_ROW_CSV, DEFAULT_LENSES);
			setSelectedLenses(selectedLensesRowIds, true, false);
		} else {
			setSelectedZoomLens(
					_artemisDBHelper.getZoomLens(selectedZoomLensPK), false);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Open the menu when the menu button is pressed
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main_options, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onSearchRequested() {
		// Do nothing when search button is pressed
		return false;
	};

	@Override
	public void onBackPressed() {
		switch (currentViewId) {
		case R.id.cameraSettings:
			int currentDisplayedChild = _cameraSettingsFlipper
					.getDisplayedChild();
			if (currentDisplayedChild > 0 && currentDisplayedChild != 3) {
				// If we aren't at the first page (or third), go back to the
				// previous page
				_cameraSettingsFlipper.setInAnimation(this,
						R.anim.slide_in_left);
				_cameraSettingsFlipper.setOutAnimation(this,
						R.anim.slide_out_right);
				_cameraSettingsFlipper
						.setDisplayedChild(currentDisplayedChild - 1);
			} else if (currentDisplayedChild == 3) {
				// Custom camera page, go back to the first page
				_cameraSettingsFlipper.setInAnimation(this,
						R.anim.slide_in_left);
				_cameraSettingsFlipper.setOutAnimation(this,
						R.anim.slide_out_right);

				findViewById(R.id.cameraSearch).setVisibility(View.VISIBLE);

				_cameraSettingsFlipper.setDisplayedChild(0);
			} else {
				// otherwise, at the first page. go back to main preview
				openArtemisCameraPreviewView();
			}
			break;
		case R.id.lensSettings:
			currentDisplayedChild = _lensSettingsFlipper.getDisplayedChild();
			if (currentDisplayedChild > 0 && !wasFocalLengthButtonPressed) {
				// If we aren't at the first page, go back to the previous page
				if (tempSelectedCamera != null
						&& tempSelectedCamera.getRowid() != null
						&& tempSelectedCamera.getRowid() == -1) {
					// This is a custom camera
					break;
				}

				addCustomLensLayout.setVisibility(View.INVISIBLE);

				_lensSettingsFlipper.setInAnimation(this, R.anim.slide_in_left);
				_lensSettingsFlipper.setOutAnimation(this,
						R.anim.slide_out_right);
				if (_lensSettingsFlipper.getDisplayedChild() == 2) {
					// On zoom lens page, go back to first page
					_lensSettingsFlipper.setDisplayedChild(0);
				} else {
					// normally just go back a page
					_lensSettingsFlipper
							.setDisplayedChild(currentDisplayedChild - 1);
				}
			} else {
				// otherwise, at the first page. go back to main preview
				openArtemisCameraPreviewView();
			}
			break;

		/*
		 * Lens settings back is handled both here and in the lens back button
		 * click listener
		 */

		case R.id.savePictureViewFlipper:
			openArtemisCameraPreviewView();
			break;
		case R.id.artemisPreview:
			if (!isEditingPictureDetailsOnly) {
				// we want to either quit, or present the quit dialog, or do
				// nothing
				if ("prompt".equals(backPressedMode)) {
					// Present alert dialog
					AlertDialog dialog = new AlertDialog.Builder(
							ArtemisActivity.this)
							.setMessage(R.string.sure_you_want_to_exit)
							.setTitle(R.string.exit_artemis)
							.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											_artemisMath
													.setInitializedFirstTime(false);
											finish();
										}
									})
							.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									}).create();
					dialog.show();
				} else if ("nothing".equals(backPressedMode)) {
					// Do nothing
				} else {
					// finish
					finish();
				}

			} else
				openArtemisCameraPreviewView();
			break;
		}
	}

	private void openCameraSettingsView() {
		if (_cameraSettingsFlipper == null)
			_cameraSettingsFlipper = (ViewFlipper) findViewById(R.id.camera_settings_flipper);
		_cameraSettingsFlipper.setInAnimation(null);
		_cameraSettingsFlipper.setOutAnimation(null);
		_cameraSettingsFlipper.setDisplayedChild(0);

		// Clear any search query
		((SearchView) findViewById(R.id.cameraSearch)).setQuery("", false);
		((SearchView) findViewById(R.id.cameraSearch))
				.setVisibility(View.VISIBLE);

		viewFlipper.setDisplayedChild(1);
		currentViewId = R.id.cameraSettings;

		ListView cameraFormatList = (ListView) findViewById(R.id.cameraFormatList);
		ArrayAdapter<String> formatAdapter = new ArrayAdapter<String>(this,
				R.layout.text_list_item, _allCameraFormats);
		cameraFormatList.setAdapter(formatAdapter);
		cameraFormatList.setTextFilterEnabled(true);

		cameraFormatList
				.setOnItemClickListener(new CameraFormatItemClickedListener());
		cameraFormatList.requestFocus();
	}

	private void openLensSettingsView() {

		_lensSettingsFlipper.setInAnimation(null);
		_lensSettingsFlipper.setDisplayedChild(0);
		viewFlipper.setDisplayedChild(2);
		currentViewId = R.id.lensSettings;
		addCustomLensLayout.setVisibility(View.INVISIBLE);

		ArrayList<String> lensMakes = null;
		if (tempSelectedCamera != null && tempSelectedCamera.getRowid() != null
				&& tempSelectedCamera.getRowid() == -1) {
			// This is a custom camera
			lensMakes = new ArrayList<String>();
			lensMakes.add(DEFAULT_LENS_MAKE);
		} else {
			// Regular db camera
			String[] lensFormatsForCamera = tempSelectedCamera.getLenses()
					.split(",");
			lensMakes = _artemisDBHelper
					.getLensMakeForLensFormat(lensFormatsForCamera);
		}
		lensMakes.add(getString(R.string.custom_zoom_lens));
		Log.i(TAG, "Lens makers available: " + lensMakes.size());

		ListView lensMakerList = (ListView) findViewById(R.id.lensMakerList);
		ArrayAdapter<String> formatAdapter = new ArrayAdapter<String>(this,
				R.layout.text_list_item, lensMakes);
		lensMakerList.setAdapter(formatAdapter);
		lensMakerList.setTextFilterEnabled(true);

		lensMakerList
				.setOnItemClickListener(new LensMakerItemClickedListener());
	}

	private void openArtemisCameraPreviewView() {
		viewFlipper.setInAnimation(null);
		viewFlipper.setDisplayedChild(0);
		currentViewId = R.id.artemisPreview;
	}

	private void openGallery() {
		getLoaderManager().initLoader(GALLERY_IMAGE_LOADER, null, this);
	}

	private void toastNoImageFound() {
		final Toast toast = Toast.makeText(ArtemisActivity.this,
				getString(R.string.gallery_no_images_found), Toast.LENGTH_LONG);
		toast.setDuration(2000);
		toast.show();
	}

	@SuppressLint("DefaultLocale")
	public static String getBucketId(String bucketName) {
		bucketName = bucketName.toLowerCase();
		if (bucketName.charAt(bucketName.length() - 1) == '/') {
			bucketName = bucketName.substring(0, bucketName.length() - 1);
		}
		String bucketId = Integer.toString(bucketName.hashCode());
		Log.i(TAG, "Gallery Intent Bucket ID: " + bucketId);
		return bucketId;
	}

	final class CameraFormatItemClickedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View selectedItem,
				int index, long arg3) {
			TextView selectedTextView = (TextView) selectedItem;
			String selectedFormat = selectedTextView.getText().toString();

			if (index == adapterView.getCount() - 1) {
				// Custom cameras was selected...
				if (customCameras == null) {
					customCameras = _artemisDBHelper.getCustomCameras();
					CustomCamera addnew = new CustomCamera();
					addnew.setName(getString(R.string.custom_camera_new_camera));
					customCameras.add(addnew);
					ListView customCameraList = (ListView) findViewById(R.id.customCameraList);
					ArrayAdapter<CustomCamera> adapter = new ArrayAdapter<CustomCamera>(
							ArtemisActivity.this, R.layout.text_list_item,
							customCameras);
					customCameraList.setAdapter(adapter);
					customCameraList
							.setOnItemClickListener(new CustomCameraClickListener());
					registerForContextMenu(customCameraList);
				}
				findViewById(R.id.cameraSearch).setVisibility(View.INVISIBLE);

				_cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
						R.anim.slide_in_right);
				_cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
						R.anim.slide_out_left);
				_cameraSettingsFlipper.setDisplayedChild(3);
				return;
			}

			Log.i(TAG, "Camera format selected: " + selectedFormat);
			// _currentCameraFormat = selectedFormat;
			ArrayList<String> sensorListForCamera = _artemisDBHelper
					.getCameraSensorsForFormat(selectedFormat);
			Log.i(TAG, "Sensors available: " + sensorListForCamera.size());

			ListView cameraSensorList = (ListView) findViewById(R.id.cameraSensorList);
			ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(
					ArtemisActivity.this, R.layout.text_list_item,
					sensorListForCamera);
			cameraSensorList.setAdapter(sensorAdapter);
			cameraSensorList.setTextFilterEnabled(true);
			cameraSensorList
					.setOnItemClickListener(new CameraSensorItemClickedListener());
			_cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
					R.anim.slide_in_right);
			_cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
					R.anim.slide_out_left);

			_cameraSettingsFlipper.showNext();
		}

	}

	private OnClickListener startCustomCameraCalibration = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent customCam = new Intent(ArtemisActivity.this,
					CustomCameraCalibrationActivity.class);

			String ratio = ((EditText) findViewById(R.id.custom_camera_aspectratio))
					.getText().toString();
			try {
				float ratioFloat = Float.parseFloat(ratio);
				customCam.putExtra("ratio", ratioFloat);
			} catch (NumberFormatException e) {

			}

			String focallength = ((EditText) findViewById(R.id.custom_camera_focallength))
					.getText().toString();
			try {
				float focalLengthFloat = Float.parseFloat(focallength);
				customCam.putExtra("focalLength", focalLengthFloat);
			} catch (NumberFormatException p) {

			}

			String squeeze = ((EditText) findViewById(R.id.custom_camera_squeezeratio))
					.getText().toString();
			try {
				float squeezeFloat = Float.parseFloat(squeeze);
				customCam.putExtra("squeeze", squeezeFloat);
			} catch (NumberFormatException p) {

			}

			CharSequence name = ((EditText) findViewById(R.id.custom_camera_name))
					.getText().toString();
			if (name.length() > 0) {
				customCam.putExtra("name", name);
			}

			startActivityForResult(customCam, 100);
		}
	};

	private OnClickListener startCustomCameraActiveSensorSize = new OnClickListener() {
		@Override
		public void onClick(View v) {
			_cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
					R.anim.slide_in_right);
			_cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
					R.anim.slide_out_left);
			_cameraSettingsFlipper.showNext();
		}
	};

	private final OnItemClickListener zoomLensListItemClicked = new OnItemClickListener() {

		AlertDialog addZoomLensDialog;

		@Override
		public void onItemClick(AdapterView<?> adapterView, View v,
				int selected, long id) {

			if (selected == adapterView.getCount() - 1) {
				if (addZoomLensDialog == null) {
					addZoomLensDialog = new AlertDialog.Builder(
							ArtemisActivity.this)
							.setView(
									View.inflate(ArtemisActivity.this,
											R.layout.create_zoom_lens_dialog,
											null))
							.setTitle(R.string.add_new_zoom_lens)
							.setNegativeButton(R.string.cancel,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									}).create();
					addZoomLensDialog.setButton(Dialog.BUTTON_POSITIVE,
							getString(R.string.add),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ZoomLens lens = new ZoomLens();
									String name = ((EditText) addZoomLensDialog
											.findViewById(R.id.zoomLensName))
											.getText().toString();
									if (name.isEmpty()) {
										name = getString(R.string.untitled_zoom_lens);
									}

									String min = ((EditText) addZoomLensDialog
											.findViewById(R.id.zoomLensMinFL))
											.getText().toString();
									String max = ((EditText) addZoomLensDialog
											.findViewById(R.id.zoomLensMaxFL))
											.getText().toString();

									Float minFL = null;
									try {
										minFL = Float.parseFloat(min);
									} catch (NumberFormatException e) {
									}
									Float maxFL = null;
									try {
										maxFL = Float.parseFloat(max);
									} catch (NumberFormatException e) {
									}

									if (minFL == null || maxFL == null
											|| minFL <= 0 || maxFL <= 0
											|| minFL >= maxFL) {
										new AlertDialog.Builder(
												ArtemisActivity.this)
												.setTitle(
														R.string.custom_zoom_lens_error)
												.setMessage(
														R.string.custom_zoom_lens_error_message)
												.setPositiveButton(
														R.string.ok,
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(
																	DialogInterface dialog,
																	int which) {
																addZoomLensDialog
																		.show();
															}
														}).create().show();

									} else {
										lens.setName(name);
										lens.setMinFL(minFL);
										lens.setMaxFL(maxFL);
										lens.setPk((int) _artemisDBHelper
												.addZoomLens(lens));
										zoomLenses.add(zoomLenses.size() - 1,
												lens);
										((ArrayAdapter<?>) ((ListView) findViewById(R.id.customZoomLensList))
												.getAdapter())
												.notifyDataSetChanged();
									}
								}
							});
				}
				addZoomLensDialog.show();
			} else {
				// Select zoom lens
				if (tempSelectedCamera.getRowid() != -1) {
					setSelectedCamera(tempSelectedCamera.getRowid(), true,
							false);
				}
				setSelectedZoomLens(zoomLenses.get(selected), true);
				_lensSettingsFlipper.setInAnimation(null);
				_lensSettingsFlipper.setOutAnimation(null);
				_lensSettingsFlipper.setDisplayedChild(0);
				mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
				mCameraAngleDetailView.postInvalidate();
				openArtemisCameraPreviewView();
			}
		}
	};

	public void setSelectedZoomLens(final ZoomLens zoomLens,
			boolean savePreference) {
		_artemisMath.setSelectedLenses(new ArrayList<Lens>());
		_artemisMath.selectedZoomLens = zoomLens;
		_artemisMath.get_currentLensBoxes().clear();
		_artemisMath.setSelectedLens(null);

		((TextView) findViewById(R.id.lensMakeText))
				.setText(_artemisMath.selectedZoomLens.toString());
		_artemisMath.resetTouchToCenter();
		_artemisMath.calculateZoomLenses();

		_artemisMath.calculateRectBoxesAndLabelsForLenses();
		_artemisMath.selectFirstMeaningFullLens();
		_artemisMath.onFullscreenOffSelectLens();
		mCameraPreview.calculateZoom(true);
		reconfigureNextAndPreviousLensButtons();

		if (savePreference) {
			Editor appPrefsEditor = getApplication().getSharedPreferences(
					ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
					.edit();
			appPrefsEditor.putInt(
					getString(R.string.preference_key_selectedzoomlens),
					zoomLens.getPk());
			appPrefsEditor.commit();
		}

	}

	final class CameraSensorItemClickedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View selectedItem,
				int arg2, long arg3) {
			TextView selectedTextView = (TextView) selectedItem;
			String selectedSensor = selectedTextView.getText().toString();
			Log.i(TAG, "Camera sensor selected: " + selectedSensor);
			// _currentCameraSensor = selectedSensor;
			_ratiosListForCamera = _artemisDBHelper
					.getCameraRatiosForSensor(selectedSensor);
			Log.i(TAG, "Ratios available: " + _ratiosListForCamera.size());

			// Add the ratio names to a list and bind to the list view
			ArrayList<String> ratioNames = new ArrayList<String>();
			for (Pair<Integer, String> pair : _ratiosListForCamera) {
				ratioNames.add(pair.second);
			}
			ListView cameraRatioList = (ListView) findViewById(R.id.cameraRatiosList);
			ArrayAdapter<String> ratioAdapter = new ArrayAdapter<String>(
					ArtemisActivity.this, R.layout.text_list_item, ratioNames);
			cameraRatioList.setAdapter(ratioAdapter);
			cameraRatioList.setTextFilterEnabled(true);
			cameraRatioList
					.setOnItemClickListener(new CameraRatioItemClickedListener());

			_cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
					R.anim.slide_in_right);
			_cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
					R.anim.slide_out_left);

			_cameraSettingsFlipper.showNext();

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(
					_cameraSettingsFlipper.getWindowToken(), 0);
			_cameraSettingsFlipper.requestFocus();
		}
	}

	final class CameraRatioItemClickedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View selectedItem,
				int selectedIndex, long arg3) {
			// TextView selectedTextView = (TextView) selectedItem;
			// String selectedRatio = selectedTextView.getText().toString();
			// Log.i(_logTag, "Camera ratio selected: " + selectedRatio);
			// _selectedCameraRatioIndex = selectedIndex;
			int rowid = _ratiosListForCamera.get(selectedIndex).first;
			// tempCameraRowId = rowid;
			setSelectedCamera(rowid, false, true);

			addCustomLensLayout.setVisibility(View.INVISIBLE);

			openLensSettingsView();
		}
	}

	final class CameraSearchQueryTextListener implements OnQueryTextListener {

		@Override
		public boolean onQueryTextChange(String newText) {
			if (newText.isEmpty() && _cameraSettingsFlipper != null) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						_cameraSettingsFlipper.getWindowToken(), 0);
				_cameraSettingsFlipper.requestFocus();

				_cameraSettingsFlipper.setInAnimation(null);
				_cameraSettingsFlipper.setOutAnimation(null);
				_cameraSettingsFlipper.setDisplayedChild(0);

				return true;
			}

			ArrayList<String> sensorListForCamera = _artemisDBHelper
					.findSensorsForQuery(newText);

			ListView cameraSensorList = (ListView) findViewById(R.id.cameraSensorList);
			ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(
					ArtemisActivity.this, R.layout.text_list_item,
					sensorListForCamera);
			cameraSensorList.setAdapter(sensorAdapter);
			cameraSensorList.setTextFilterEnabled(true);
			cameraSensorList
					.setOnItemClickListener(new CameraSensorItemClickedListener());

			if (_cameraSettingsFlipper != null) {
				_cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
						R.anim.slide_in_right);
				_cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
						R.anim.slide_out_left);

				if (_cameraSettingsFlipper.getDisplayedChild() != 1) {
					_cameraSettingsFlipper.setInAnimation(null);
					_cameraSettingsFlipper.setOutAnimation(null);
					_cameraSettingsFlipper.setDisplayedChild(1);
				}
			}
			return true;
		}

		@Override
		public boolean onQueryTextSubmit(String query) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(
					_cameraSettingsFlipper.getWindowToken(), 0);
			_cameraSettingsFlipper.requestFocus();
			return true;
		}

	}

	final class LensMakerItemClickedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View selectedItem,
				int index, long id) {
			if (index < adapterView.getCount() - 1) {
				TextView selectedTextView = (TextView) selectedItem;
				tempSelectedLensMake = selectedTextView.getText().toString();
				// Log.i(_logTag, "Lens make selected: " + selectedLensMake);
				setSelectedLensMake(tempSelectedLensMake, false, true);
				// Log.i(_logTag, "Lenses available: " + _lensesForMake.size());

				loadLensesForLensMake();

				_lensSettingsFlipper.setInAnimation(ArtemisActivity.this,
						R.anim.slide_in_right);
				_lensSettingsFlipper.setOutAnimation(ArtemisActivity.this,
						R.anim.slide_out_left);

				addCustomLensLayout.setVisibility(View.VISIBLE);

				_lensSettingsFlipper.showNext();
			} else {
				// Display the zoom lens list (w/ add zoom lens button)

				if (zoomLenses == null) {
					zoomLenses = _artemisDBHelper.getZoomLenses();
					ZoomLens addnew = new ZoomLens();
					addnew.setName(getString(R.string.new_zoom_lens));
					zoomLenses.add(addnew);
					ListView zoomLensList = (ListView) findViewById(R.id.customZoomLensList);
					ArrayAdapter<ZoomLens> adapter = new ArrayAdapter<ZoomLens>(
							ArtemisActivity.this, R.layout.text_list_item,
							zoomLenses);
					zoomLensList.setAdapter(adapter);
					zoomLensList
							.setOnItemClickListener(zoomLensListItemClicked);
					registerForContextMenu(zoomLensList);
				}

				_lensSettingsFlipper.setInAnimation(ArtemisActivity.this,
						R.anim.slide_in_right);
				_lensSettingsFlipper.setOutAnimation(ArtemisActivity.this,
						R.anim.slide_out_left);
				_lensSettingsFlipper.setDisplayedChild(2);
			}
		}
	}

	final class LenseSelectionCancelClickListener implements
			android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			wasFocalLengthButtonPressed = false;
			_lensSettingsFlipper.setOutAnimation(null);
			openArtemisCameraPreviewView();
		}
	}

	final class LenseSelectionSaveClickListener implements
			android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			wasFocalLengthButtonPressed = false;
			_lensSettingsFlipper.setOutAnimation(null);
			SparseBooleanArray checkedValuesArray = _lensListView
					.getCheckedItemPositions();
			_selectedLenses = new ArrayList<Lens>();
			String selectedLensString = "";
			for (int listIndex = 0; listIndex < checkedValuesArray.size(); listIndex++) {
				if (checkedValuesArray.valueAt(listIndex)) { // if it's checked
					int lensMakeIndex = checkedValuesArray.keyAt(listIndex);
					Lens lens = tempLensesForMake.get(lensMakeIndex);
					if (listIndex > 0) {
						selectedLensString += ",";
					}
					selectedLensString += lens.getPk();
					_selectedLenses.add(lens);
				}
			}
			Log.i(TAG, "SELECTED LENSES: " + selectedLensString);

			if (tempSelectedCamera.getRowid() != -1) {
				setSelectedCamera(tempSelectedCamera.getRowid(), true, false);
			}
			setSelectedLensMake(tempSelectedLensMake, true, false);
			setSelectedLenses(selectedLensString, true, true);
			updateLensesInDB();
			_artemisMath.setFullscreen(false);
			_artemisMath.calculateLargestLens();
			_artemisMath.calculateRectBoxesAndLabelsForLenses();
			_artemisMath.selectFirstMeaningFullLens();
			_artemisMath.onFullscreenOffSelectLens();
			_artemisMath.resetTouchToCenter();
			mCameraPreview.calculateZoom(true);
			mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
			mCameraAngleDetailView.postInvalidate();
			reconfigureNextAndPreviousLensButtons();
			openArtemisCameraPreviewView();
		}

	}

	private void setSelectedCamera(int cameraRowId,
			boolean saveToAppPreferences, boolean saveTemporary) {
		if (saveTemporary) {
			tempSelectedCamera = _artemisDBHelper
					.getCameraDetailsForRowId(cameraRowId);

		} else {
			_selectedCamera = _artemisDBHelper
					.getCameraDetailsForRowId(cameraRowId);
			tempSelectedCamera = _selectedCamera;
			_artemisMath.setSelectedCamera(_selectedCamera);

			_cameraDetailsText.setText(_selectedCamera.getSensor() + " "
					+ _selectedCamera.getRatio());
		}
		if (saveToAppPreferences) {
			Editor appPrefsEditor = getApplication().getSharedPreferences(
					ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
					.edit();
			appPrefsEditor.putInt("selectedCameraRowId", cameraRowId);
			appPrefsEditor.commit();
		}
	}

	private void setSelectedLensMake(String lensMake,
			boolean saveToAppPreferences, boolean saveTemporary) {
		if (!saveTemporary) {
			_lensesForMake = _artemisDBHelper.getLensesForMake(lensMake);
			tempLensesForMake = _lensesForMake;
			tempSelectedLensMake = lensMake;

			((TextView) findViewById(R.id.lensMakeText)).setText(lensMake);
		} else {
			tempLensesForMake = _artemisDBHelper.getLensesForMake(lensMake);
			tempSelectedLensMake = lensMake;
		}
		if (saveToAppPreferences) {
			Editor appPrefsEditor = getApplication().getSharedPreferences(
					ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
					.edit();
			appPrefsEditor.putString("selectedLensMake", lensMake);
			appPrefsEditor.commit();
		}
	}

	private void setSelectedLenses(String lensCSVString,
			boolean selectLensMakeLenses, boolean saveToAppPreferences) {
		_artemisMath.setSelectedLenses(_selectedLenses);
		_artemisMath.selectedZoomLens = null;
		if (selectLensMakeLenses) {
			_selectedLenses.clear();
			String[] lensIdStrings = lensCSVString.split(",");
			for (Lens lens : tempLensesForMake) {
				String stringid = lens.getPk().toString();
				for (String selectedId : lensIdStrings) {
					if (stringid.equals(selectedId)) {
						_selectedLenses.add(lens);
					}
				}
			}
		}
		if (saveToAppPreferences) {
			Editor appPrefsEditor = getApplication().getSharedPreferences(
					ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
					.edit();
			appPrefsEditor.putString("selectedLensesCSVString", lensCSVString);
			appPrefsEditor
					.remove(getString(R.string.preference_key_selectedzoomlens));
			appPrefsEditor.commit();
		}
	}

	private final OnClickListener nextLensClickListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {
			nextLens();
			if (isHapticFeedbackEnabled) {
				buzz(v);
			}
		}
	};

	private final Runnable nextLensRunnable = new Runnable() {
		public void run() {
			if (nextClickBoolean.isDown()) {
				if ((!isZoomLensSelected() && _artemisMath.hasNextLens())
						|| (isZoomLensSelected() && _artemisMath
								.hasNextZoomLens())) {
					nextLens();
					mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
							+ (isZoomLensSelected() ? lensRepeatSpeedCustomLens
									: lensRepeatSpeedNormal));
				}
			}
		}
	};

	private void nextLens() {
		if (!_artemisMath.isFullscreen()
				|| _artemisMath.selectedZoomLens == null) {

			if (_artemisMath.selectNextLens()) {
				mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
				if (_artemisMath.isFullscreen()) {
					mCameraPreview.calculateZoom(true);
				}
			}
			if (!_artemisMath.hasNextLens()) {
				_nextLensButton.setVisibility(View.INVISIBLE);
			}
			if (_artemisMath.hasPreviousLens()) {
				_prevLensButton.setVisibility(View.VISIBLE);
			}
		} else if (_artemisMath.isFullscreen()
				&& _artemisMath.selectedZoomLens != null) {
			_artemisMath.incrementFullscreenZoomLens();
			mCameraPreview.calculateZoom(true);

			_lensFocalLengthText.setText(_artemisMath.lensFLNumberFormat
					.format(_artemisMath.zoomLensFullScreenFL));
			if (!_artemisMath.hasNextZoomLens()) {
				_nextLensButton.setVisibility(View.INVISIBLE);
			}
			if (_artemisMath.hasPreviousZoomLens()) {
				_prevLensButton.setVisibility(View.VISIBLE);
			}
		}
		mCameraAngleDetailView.postInvalidate();
	}

	protected void reconfigureNextAndPreviousLensButtons() {
		if (_artemisMath.selectedZoomLens == null) {
			if (_artemisMath.hasNextLens()) {
				_nextLensButton.setVisibility(View.VISIBLE);
			} else {
				_nextLensButton.setVisibility(View.INVISIBLE);
			}
			if (_artemisMath.hasPreviousLens()) {
				_prevLensButton.setVisibility(View.VISIBLE);
			} else {
				_prevLensButton.setVisibility(View.INVISIBLE);
			}
		} else {
			if (!_artemisMath.hasNextZoomLens()) {
				_nextLensButton.setVisibility(View.INVISIBLE);
			}
			if (_artemisMath.hasPreviousZoomLens()) {
				_prevLensButton.setVisibility(View.VISIBLE);
			}
		}
	}

	private final OnClickListener previousLensClickListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {
			previousLens();
			if (isHapticFeedbackEnabled) {
				buzz(v);
			}
		}
	};

	private boolean isZoomLensSelected() {
		return _artemisMath != null && _artemisMath.selectedZoomLens != null ? true
				: false;
	}

	private final Runnable previousLensRunnable = new Runnable() {
		public void run() {
			if (prevClickBoolean.isDown()) {
				if ((!isZoomLensSelected() && _artemisMath.hasPreviousLens())
						|| (isZoomLensSelected() && _artemisMath
								.hasPreviousZoomLens())) {
					previousLens();
					mUiHandler.postAtTime(this, SystemClock.uptimeMillis()
							+ (isZoomLensSelected() ? lensRepeatSpeedCustomLens
									: lensRepeatSpeedNormal));

				}
			}
		}
	};

	private void previousLens() {
		if (!_artemisMath.isFullscreen()
				|| _artemisMath.selectedZoomLens == null) {
			if (_artemisMath.selectPreviousLens()) {
				mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
				if (_artemisMath.isFullscreen()) {
					mCameraPreview.calculateZoom(true);
				}
			}
			if (!_artemisMath.hasPreviousLens()) {
				_prevLensButton.setVisibility(View.INVISIBLE);
			}
			if (_artemisMath.hasNextLens()) {
				_nextLensButton.setVisibility(View.VISIBLE);
			}
		} else if (_artemisMath.isFullscreen()
				&& _artemisMath.selectedZoomLens != null) {
			_artemisMath.decrementFullscreenZoomLens();
			mCameraPreview.calculateZoom(true);

			_lensFocalLengthText.setText(_artemisMath.lensFLNumberFormat
					.format(_artemisMath.zoomLensFullScreenFL));

			if (!_artemisMath.hasPreviousZoomLens()) {
				_prevLensButton.setVisibility(View.INVISIBLE);
			}
			if (_artemisMath.hasNextZoomLens()) {
				_nextLensButton.setVisibility(View.VISIBLE);
			}

		}
		mCameraAngleDetailView.postInvalidate();
	}

	final class focalLengthLensButtonViewClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			addCustomLensLayout.setVisibility(View.VISIBLE);

			_lensSettingsFlipper.setInAnimation(null);
			_lensSettingsFlipper.setOutAnimation(null);

			if (_artemisMath.selectedZoomLens == null) {
				// Go to the final lens setting page
				loadLensesForLensMake();
				_lensSettingsFlipper.setDisplayedChild(1);

				currentViewId = R.id.lensSettings;
				viewFlipper.setDisplayedChild(2);

				wasFocalLengthButtonPressed = true;

			} else {
				// Zoom lens selected
				openLensSettingsView();
			}

			if (isHapticFeedbackEnabled) {
				buzz(v);
			}
		}
	}

	public String loadLensesForLensMake() {
		ArrayList<String> focalLengths = new ArrayList<String>();
		ArrayList<Integer> checked = new ArrayList<Integer>();
		int position = 0;
		String lensIdList = "";
		NumberFormat nf = NumberFormat.getInstance();
		for (Lens lens : tempLensesForMake) {
			focalLengths.add(nf.format(lens.getFL()) + " mm");
			if ("1".equals(lens.getLensSet())) {
				// if lensSet is "1" then the item is checked
				checked.add(position);
				lensIdList += lens.getPk() + ",";
			}
			++position;
		}
		lensIdList = lensIdList.substring(0, lensIdList.length() - 1);

		_lensListView = (ListView) findViewById(R.id.lensList);
		ArrayAdapter<String> lensAdapter = new ArrayAdapter<String>(
				ArtemisActivity.this, R.layout.lens_list_item, focalLengths);
		_lensListView.setAdapter(lensAdapter);
		_lensListView.setTextFilterEnabled(true);
		_lensListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// set the checked items
		for (Integer pos : checked) {
			_lensListView.setItemChecked(pos, true);
		}
		return lensIdList;
	}

	private void updateLensesInDB() {
		for (Lens lensInSelectList : tempLensesForMake) {
			if (_selectedLenses.contains(lensInSelectList)) {
				lensInSelectList.setLensSet("1");
			} else {
				lensInSelectList.setLensSet("0");
			}
		}
		_artemisDBHelper.updateLensSelections(tempLensesForMake);
	}

	final OnClickListener fullscreenViewClickListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (_artemisMath.isFullscreen()) {
				// non fullscreen with all lens boxes
				_artemisMath.setFullscreen(false);
				_artemisMath.onFullscreenOffSelectLens();
				mCameraAngleDetailView.postInvalidate();
			} else {
				_artemisMath.setFullscreen(true);
				if (_artemisMath.selectedZoomLens != null) {
					_artemisMath.onFullscreenSetupZoomLens();
				}
			}
			reconfigureNextAndPreviousLensButtons();
			mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
			mCameraPreview.calculateZoom(true);

			if (isHapticFeedbackEnabled) {
				buzz(v);
			}
		}
	};

	final OnClickListener takePictureClickListener = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.v(TAG, "Artemis taking a picture");
			shutterButtonPressed(v);
		}
	};

	private void shutterButtonPressed(View sender) {
		if (lastKnownLocation != null)
			pictureSaveLocation = lastKnownLocation;

		pictureSaveHeadingTiltString = headingTiltText.getText().toString();

		if (CameraPreview14.quickshotEnabled) {
			mCameraPreview.takePicture();
			return;
		}

		savePictureViewFlipper.setInAnimation(null);
		savePictureViewFlipper.setDisplayedChild(0);
		// normal take picture (not quickshot)
		mCameraPreview.takePicture();

		if (isHapticFeedbackEnabled) {
			buzz(sender);
		}
	}

	final class CancelSavePictureClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (isEditingPictureDetailsOnly) {
				// was just editing the picture details from the main screen
				isEditingPictureDetailsOnly = false;
			} else {
				// just previously took a picture
				pictureSavePreview.setImageBitmap(null);
				System.gc();

				if (mCameraPreview != null)
					mCameraPreview.restartPreview();
			}
			savePictureViewFlipper.setDisplayedChild(0); // reset to first view
			openArtemisCameraPreviewView();
		}
	}

	final class SavePictureClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			final Toast toast = Toast.makeText(ArtemisActivity.this,
					getString(R.string.image_saved_success), Toast.LENGTH_LONG);
			toast.setDuration(2000);
			toast.show();

			new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... params) {
					mCameraPreview.renderPictureDetailsAndSave();
					System.gc();
					return "";
				}

				// @Override
				// protected void onPostExecute(String result) {
				// toast.cancel();
				// sendBroadcast(new Intent(
				// Intent.ACTION_MEDIA_MOUNTED,
				// Uri.parse("file:/"
				// + Environment.getExternalStorageDirectory())));
				// }
			}.execute(new String[] {});

			mCameraPreview.restartPreview();
			openArtemisCameraPreviewView();
		}
	}

	final class EditPictureDetailsClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			pictureSavePreview.setImageBitmap(null);
			System.gc();

			loadPictureDetailsSettings();

			savePictureViewFlipper.showNext();
		}
	}

	private void loadPictureDetailsSettings() {
		if (!gpsEnabled) {
			ToggleButton gpsDetails = (ToggleButton) findViewById(R.id.gpsDetailsToggle);
			gpsDetails.setEnabled(false);
			gpsDetails.setChecked(false);
		}

		SharedPreferences artemisPrefs = getSharedPreferences(
				ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE);

		((EditText) findViewById(R.id.imageDescription))
				.setText(artemisPrefs.getString(
						ArtemisPreferences.SAVE_PICTURE_SHOW_DESCRIPTION, ""));
		((EditText) findViewById(R.id.imageNotes)).setText(artemisPrefs
				.getString(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES, ""));
		((ToggleButton) findViewById(R.id.cameraDetailsToggle))
				.setChecked(artemisPrefs.getBoolean(
						ArtemisPreferences.SAVE_PICTURE_SHOW_CAMERA_DETAILS,
						true));
		((ToggleButton) findViewById(R.id.lensDetailsToggle))
				.setChecked(artemisPrefs
						.getBoolean(
								ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_DETAILS,
								true));
		((ToggleButton) findViewById(R.id.gpsDetailsToggle))
				.setChecked(artemisPrefs.getBoolean(
						ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_DETAILS, true));
		((ToggleButton) findViewById(R.id.gpsLocationToggle))
				.setChecked(artemisPrefs
						.getBoolean(
								ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION,
								true));
		((ToggleButton) findViewById(R.id.h_and_v_angle_toggle))
				.setChecked(artemisPrefs.getBoolean(
						ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_VIEW_ANGLES,
						true));
		((ToggleButton) findViewById(R.id.date_and_time_toggle))
				.setChecked(artemisPrefs.getBoolean(
						ArtemisPreferences.SAVE_PICTURE_SHOW_DATE_TIME, true));
		ToggleButton headingToggle = (ToggleButton) findViewById(R.id.compass_heading_toggle);
		headingToggle.setChecked(artemisPrefs.getBoolean(
				ArtemisPreferences.SAVE_PICTURE_SHOW_HEADING, true));
		if (ArtemisActivity.headingDisplaySelection == 0) {
			headingToggle.setEnabled(false);
		}

		ToggleButton tiltRollToggle = (ToggleButton) findViewById(R.id.tilt_and_roll_toggle);
		tiltRollToggle.setChecked(artemisPrefs.getBoolean(
				ArtemisPreferences.SAVE_PICTURE_SHOW_TILT_ROLL, true));
		if (ArtemisActivity.headingDisplaySelection == 0
				|| ArtemisActivity.headingDisplaySelection == 1) {
			tiltRollToggle.setEnabled(false);
		}
		((ToggleButton) findViewById(R.id.save_raw_image_toggle))
				.setChecked(artemisPrefs.getBoolean(
						getString(R.string.preference_key_saveRawImage), false));

	}

	final class SavePictureEditDetailsClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {

			Editor editor = getApplication().getSharedPreferences(
					ArtemisPreferences.class.getSimpleName(),
					Context.MODE_PRIVATE).edit();

			editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_DESCRIPTION,
					((EditText) findViewById(R.id.imageDescription)).getText()
							.toString());
			editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_NOTES,
					((EditText) findViewById(R.id.imageNotes)).getText()
							.toString());
			editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_DETAILS,
					((ToggleButton) findViewById(R.id.gpsDetailsToggle))
							.isChecked());
			editor.putBoolean(
					ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION,
					((ToggleButton) findViewById(R.id.gpsLocationToggle))
							.isChecked());
			editor.putBoolean(
					ArtemisPreferences.SAVE_PICTURE_SHOW_CAMERA_DETAILS,
					((ToggleButton) findViewById(R.id.cameraDetailsToggle))
							.isChecked());
			editor.putBoolean(
					ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_DETAILS,
					((ToggleButton) findViewById(R.id.lensDetailsToggle))
							.isChecked());
			editor.putBoolean(
					ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_VIEW_ANGLES,
					((ToggleButton) findViewById(R.id.h_and_v_angle_toggle))
							.isChecked());
			editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_DATE_TIME,
					((ToggleButton) findViewById(R.id.date_and_time_toggle))
							.isChecked());
			editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_TILT_ROLL,
					((ToggleButton) findViewById(R.id.tilt_and_roll_toggle))
							.isChecked());
			editor.putBoolean(ArtemisPreferences.SAVE_PICTURE_SHOW_HEADING,
					((ToggleButton) findViewById(R.id.compass_heading_toggle))
							.isChecked());
			editor.putBoolean(getString(R.string.preference_key_saveRawImage),
					((ToggleButton) findViewById(R.id.save_raw_image_toggle))
							.isChecked());
			editor.commit();

			if (isEditingPictureDetailsOnly) {
				// Do nothing else -- used for the state when the user presses
				// the pencil icon in the main screen
				isEditingPictureDetailsOnly = false;
			} else {
				final Toast toast = Toast.makeText(ArtemisActivity.this,
						getString(R.string.image_saved_success),
						Toast.LENGTH_LONG);
				toast.setDuration(2500);
				toast.show();

				new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... params) {
						mCameraPreview.renderPictureDetailsAndSave();
						System.gc();
						return "";
					}

					// @Override
					// protected void onPostExecute(String result) {
					// toast.cancel();
					// sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					// Uri.parse("file://"
					// + Environment
					// .getExternalStorageDirectory())));
					// }
				}.execute(new String[] {});

				mCameraPreview.restartPreview();
			}

			savePictureViewFlipper.setDisplayedChild(0);
			savePictureViewFlipper.stopFlipping();
			openArtemisCameraPreviewView();
		}
	}

	public static String[] getGPSLocationDetailStrings(Context context) {
		String gpsDetails = "";
		String gpsLocationString = "";

		if (pictureSaveLocation != null && gpsEnabled) {
			gpsDetails = "Lat: "
					+ Location.convert(pictureSaveLocation.getLatitude(),
							Location.FORMAT_DEGREES)
					+ ", Long: "
					+ Location.convert(pictureSaveLocation.getLongitude(),
							Location.FORMAT_DEGREES);
			Geocoder geocoder = new Geocoder(context);
			try {
				List<Address> addressList = geocoder.getFromLocation(
						pictureSaveLocation.getLatitude(),
						pictureSaveLocation.getLongitude(), 1);
				if (addressList.iterator().hasNext()) {
					Address addr = addressList.iterator().next();
					int nItems = 0;
					String bullet = " " + context.getString(R.string.bullet)
							+ " ";
					if (addr.getAddressLine(0) != null) {
						if (nItems > 0)
							gpsLocationString += bullet;
						gpsLocationString += addr.getAddressLine(0);
						++nItems;
					}
					if (addr.getLocality() != null) {
						if (nItems > 0)
							gpsLocationString += bullet;
						gpsLocationString += addr.getLocality();
						++nItems;
					}
					if (addr.getAdminArea() != null) {
						if (nItems > 0)
							gpsLocationString += bullet;
						gpsLocationString += addr.getAdminArea();
						++nItems;
					}
					if (addr.getPostalCode() != null) {
						if (nItems > 0)
							gpsLocationString += bullet;
						gpsLocationString += addr.getPostalCode();
						++nItems;
					}
					if (addr.getCountryName() != null) {
						if (nItems > 0)
							gpsLocationString += bullet;
						gpsLocationString += addr.getCountryCode();
						++nItems;
					}
				}
			} catch (IOException ioe) {
				Log.e(TAG, "Error retrieving geocoder location data");
			}
		}
		return new String[] { gpsDetails, gpsLocationString };
	}

	final class CustomCameraClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View selectedItem,
				int selectedIndex, long arg3) {
			TextView selectedTextView = (TextView) selectedItem;
			String selectedFormat = selectedTextView.getText().toString();

			if (getString(R.string.custom_camera_new_camera).equals(
					selectedFormat)) {
				// New custom camera was selected...
				_cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
						R.anim.slide_in_right);
				_cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
						R.anim.slide_out_left);
				_cameraSettingsFlipper.setDisplayedChild(4);
				return;
			} else if (customCameras != null && customCameras.size() > 0) {
				CustomCamera selectedCustomCamera = customCameras
						.get(selectedIndex);
				// Create a Camera object to hold the CustomCamera and act as a
				// regular camera
				Camera selectedCam = new Camera(selectedCustomCamera);
				// selectedCam.setSqz(1f);
				// camera
				//
				_selectedCamera = selectedCam;
				tempSelectedCamera = selectedCam;
				_artemisMath.setSelectedCamera(_selectedCamera);
				// // _selectedLenses.clear();
				// // set it to a negative value so we know to look in custom
				// // camera table
				// // setSelectedCamera(-selectedCustomCamera.getRowid(), true,
				// // false);
				//
				Editor appPrefsEditor = getApplication().getSharedPreferences(
						ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
						.edit();
				appPrefsEditor.putInt(ArtemisPreferences.SELECTED_CAMERA_ROW,
						-selectedCustomCamera.getPk());
				appPrefsEditor.commit();

				setSelectedLensMake(DEFAULT_LENS_MAKE, false, false);

				setSelectedLenses(loadLensesForLensMake(), true, true);
				_cameraDetailsText.setText(selectedCustomCamera.getName() + " "
						+ _selectedCamera.getRatio());
				//
				// // Recalculate the view for new camera and lenses
				_artemisMath.setFullscreen(false);
				_artemisMath.calculateLargestLens();
				// // call twice fixes the refresh bug after select lenses.
				// // otherwise it doesn't redraw, strange looking boxes appear
				// ???
				mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
				_artemisMath.selectFirstMeaningFullLens();
				_artemisMath.onFullscreenOffSelectLens();
				_artemisMath.resetTouchToCenter();
				mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
				mCameraAngleDetailView.postInvalidate();
				openLensSettingsView();
			}

		}
	}

	private final OnClickListener addCustomCameraClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			EditText nameText = (EditText) findViewById(R.id.custom_camera_active_sensor_name);
			String cameraName = nameText.getText().toString();
			if (cameraName.length() == 0) {
				cameraName = getString(R.string.untitle_custom_cam);
			}
			EditText squeezeText = (EditText) findViewById(R.id.custom_camera_squeezeratio_as);
			if (squeezeText.getText().toString().length() == 0) {
				squeezeText.setText("1");
			}
			EditText widthText = (EditText) findViewById(R.id.custom_camera_width);
			EditText heightText = (EditText) findViewById(R.id.custom_camera_height);

			try {
				float cameraWidth = Float.parseFloat(widthText.getText()
						.toString());
				float cameraHeight = Float.parseFloat(heightText.getText()
						.toString());
				float squeeze = Float.parseFloat(squeezeText.getText()
						.toString());

				// insert the new camera
				CustomCamera customCam = new CustomCamera();
				customCam.setName(cameraName);
				customCam.setSensorwidth(cameraWidth);
				customCam.setSensorheight(cameraHeight);
				customCam.setSqueeze(squeeze);
				_artemisDBHelper.insertCustomCamera(customCam);

				// refresh the list in previous page
				refreshCustomCameraList();

				// clear the text off on success
				nameText.setText("");
				squeezeText.setText("");
				widthText.setText("");
				heightText.setText("");

				// flip back to the previous custom camera selection page
				_cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
						R.anim.slide_in_left);
				_cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
						R.anim.slide_out_right);
				_cameraSettingsFlipper.setDisplayedChild(3);

			} catch (Exception e) {
				e.printStackTrace();
				AlertDialog errorDialog = new AlertDialog.Builder(
						ArtemisActivity.this)
						.setTitle(R.string.custom_camera_error)
						.setMessage(R.string.custom_camera_error_message)
						.setNegativeButton(R.string.ok, null).create();
				errorDialog.show();
			}

		}

	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			if (data != null && data.getBooleanExtra("newcustomcam", false)) {
				refreshCustomCameraList();

				_cameraSettingsFlipper.setInAnimation(null);
				_cameraSettingsFlipper.setOutAnimation(null);
				_cameraSettingsFlipper.setDisplayedChild(_cameraSettingsFlipper
						.getDisplayedChild() - 1);
			}
		}
	}

	private void refreshCustomCameraList() {
		customCameras = _artemisDBHelper.getCustomCameras();
		CustomCamera addnew = new CustomCamera();
		addnew.setName(getString(R.string.custom_camera_new_camera));
		customCameras.add(addnew);
		ListView customCameraList = (ListView) findViewById(R.id.customCameraList);
		ArrayAdapter<CustomCamera> adapter = new ArrayAdapter<CustomCamera>(
				ArtemisActivity.this, R.layout.text_list_item, customCameras);
		customCameraList.setAdapter(adapter);
	}

	private void refreshZoomLensList() {
		zoomLenses = _artemisDBHelper.getZoomLenses();
		ZoomLens addnew = new ZoomLens();
		addnew.setName(getString(R.string.new_zoom_lens));
		zoomLenses.add(addnew);
		ListView zoomLensList = (ListView) findViewById(R.id.customZoomLensList);
		ArrayAdapter<ZoomLens> adapter = new ArrayAdapter<ZoomLens>(
				ArtemisActivity.this, R.layout.text_list_item, zoomLenses);
		zoomLensList.setAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo contextMenuInfo) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) contextMenuInfo;
		Log.d(TAG, String.format("%s %s", view.toString(), view.getParent()
				.toString()));
		if (((View) view.getParent()).getId() == R.id.camera_settings_flipper
				&& menuInfo.position < customCameras.size() - 1) {
			menu.setHeaderTitle(getString(R.string.custom_camera_options));
			menu.add(0, 0, 0, getString(R.string.custom_camera_edit_camera));
			menu.add(0, 1, 1, getString(R.string.custom_camera_delete_camera));
		} else if (((View) view.getParent()).getId() == R.id.lens_settings_flipper
				&& menuInfo.position < zoomLenses.size() - 1) {
			menu.setHeaderTitle(getString(R.string.zoom_lens_options));
			menu.add(1, 0, 0, getString(R.string.zoom_lens_edit));
			menu.add(1, 1, 1, getString(R.string.zoom_lens_delete));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		if (item.getGroupId() == 0 && item.getItemId() == 0) {
			// edit custom camera was selected...
			CustomCamera editCamera = customCameras.get(menuInfo.position);

			((Button) findViewById(R.id.save_custom_camera_active_sensor))
					.setOnClickListener(new UpdateCustomCameraClickListener(
							editCamera));

			((EditText) findViewById(R.id.custom_camera_active_sensor_name))
					.setText(editCamera.getName());
			EditText squeezeText = (EditText) findViewById(R.id.custom_camera_squeezeratio_as);
			if (squeezeText.getText().toString().length() == 0) {
				squeezeText.setText("1");
			}

			((EditText) findViewById(R.id.custom_camera_width))
					.setText(editCamera.getSensorwidth() + "");
			((EditText) findViewById(R.id.custom_camera_height))
					.setText(editCamera.getSensorheight() + "");

			_cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
					R.anim.slide_in_right);
			_cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
					R.anim.slide_out_left);
			_cameraSettingsFlipper.setDisplayedChild(5);
		} else if (item.getGroupId() == 0 && item.getItemId() == 1) {
			// delete custom camera
			_artemisDBHelper.deleteCustomCameraByRowId(customCameras.get(
					menuInfo.position).getPk());
			refreshCustomCameraList();
		} else if (item.getGroupId() == 1 && item.getItemId() == 0) {
			// edit zoom lens
			final ZoomLens edited = zoomLenses.get(menuInfo.position);
			View alertView = View.inflate(ArtemisActivity.this,
					R.layout.create_zoom_lens_dialog, null);
			((EditText) alertView.findViewById(R.id.zoomLensName))
					.setText(edited.getName());
			((EditText) alertView.findViewById(R.id.zoomLensMinFL))
					.setText(edited.getMinFL() + "");
			EditText finalEditText = (EditText) alertView
					.findViewById(R.id.zoomLensMaxFL);
			finalEditText.setText(edited.getMaxFL() + "");
			finalEditText
					.setOnEditorActionListener(new EditText.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int action,
								KeyEvent event) {
							if (action == EditorInfo.IME_ACTION_DONE) {
								((View) v.getParent().getParent())
										.requestFocus();
							}
							return false;
						}
					});

			final AlertDialog addZoomLensDialog = new AlertDialog.Builder(
					ArtemisActivity.this)
					.setView(alertView)
					.setTitle(R.string.edit_zoom_lens)
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).create();
			addZoomLensDialog.setButton(Dialog.BUTTON_POSITIVE,
					getString(R.string.save),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							String name = ((EditText) addZoomLensDialog
									.findViewById(R.id.zoomLensName)).getText()
									.toString();
							if (name.isEmpty()) {
								name = getString(R.string.untitled_zoom_lens);
							}

							String min = ((EditText) addZoomLensDialog
									.findViewById(R.id.zoomLensMinFL))
									.getText().toString();
							String max = ((EditText) addZoomLensDialog
									.findViewById(R.id.zoomLensMaxFL))
									.getText().toString();

							Float minFL = null;
							try {
								minFL = Float.parseFloat(min);
							} catch (NumberFormatException e) {
							}
							Float maxFL = null;
							try {
								maxFL = Float.parseFloat(max);
							} catch (NumberFormatException e) {
							}

							if (minFL == null || maxFL == null || minFL <= 0
									|| maxFL <= 0 || minFL >= maxFL) {
								new AlertDialog.Builder(ArtemisActivity.this)
										.setTitle(
												R.string.custom_zoom_lens_error)
										.setMessage(
												R.string.custom_zoom_lens_error_message)
										.setPositiveButton(
												R.string.ok,
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog,
															int which) {
														addZoomLensDialog
																.show();
													}
												}).create().show();

							} else {
								edited.setName(name);
								edited.setMinFL(minFL);
								edited.setMaxFL(maxFL);
								_artemisDBHelper.updateZoomLens(edited);
								((ArrayAdapter<?>) ((ListView) findViewById(R.id.customZoomLensList))
										.getAdapter()).notifyDataSetChanged();
							}
						}
					});
			addZoomLensDialog.show();

		} else if (item.getGroupId() == 1 && item.getItemId() == 1) {
			// delete zoom lens
			_artemisDBHelper.deleteZoomLensByPK(zoomLenses.get(
					menuInfo.position).getPk());
			refreshZoomLensList();

		}

		return super.onContextItemSelected(item);
	}

	final class UpdateCustomCameraClickListener implements
			android.view.View.OnClickListener {

		private CustomCamera updateCamera;

		public UpdateCustomCameraClickListener(CustomCamera camera) {
			updateCamera = camera;
		}

		@Override
		public void onClick(View v) {
			EditText nameText = (EditText) findViewById(R.id.custom_camera_active_sensor_name);
			String cameraName = nameText.getText().toString();
			EditText squeezeText = (EditText) findViewById(R.id.custom_camera_squeezeratio_as);
			EditText widthText = (EditText) findViewById(R.id.custom_camera_width);
			EditText heightText = (EditText) findViewById(R.id.custom_camera_height);

			try {
				float cameraWidth = Float.parseFloat(widthText.getText()
						.toString());
				float cameraHeight = Float.parseFloat(heightText.getText()
						.toString());
				if (squeezeText.getText().toString().length() == 0) {
					squeezeText.setText("1");
				}
				float squeeze = Float.parseFloat(squeezeText.getText()
						.toString());

				// // update the new camera
				updateCamera.setName(cameraName);
				//
				// // calculate the viewing angles
				updateCamera.setSensorwidth(cameraWidth);
				updateCamera.setSensorheight(cameraHeight);
				updateCamera.setSqueeze(squeeze);
				_artemisDBHelper.updateCustomCamera(updateCamera);

				// refresh the list in previous page
				refreshCustomCameraList();

				// clear the text off on success
				nameText.setText("");
				widthText.setText("");
				heightText.setText("");
				squeezeText.setText("");

				// flip back to the previous custom camera selection page
				_cameraSettingsFlipper.setInAnimation(ArtemisActivity.this,
						R.anim.slide_in_left);
				_cameraSettingsFlipper.setOutAnimation(ArtemisActivity.this,
						R.anim.slide_out_right);
				_cameraSettingsFlipper.setDisplayedChild(3);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private OnClickListener addCustomLensClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final EditText input = new EditText(ArtemisActivity.this);
			input.setRawInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			new AlertDialog.Builder(ArtemisActivity.this)
					.setTitle(getString(R.string.add_custom_lens))
					.setMessage(getString(R.string.input_focal_length))
					.setView(input)
					.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String value = input.getText().toString();
									Float focalLength;
									try {
										focalLength = Float.parseFloat(value);
									} catch (NumberFormatException nfe) {
										AlertDialog errorDialog = new AlertDialog.Builder(
												ArtemisActivity.this)
												.setTitle(
														R.string.custom_lens_error)
												.setMessage(
														R.string.custom_lens_error_message)
												.setNegativeButton(R.string.ok,
														null).create();
										errorDialog.show();
										return;
									}

									Lens customLens = new Lens(_artemisDBHelper
											.getLensByRowId(_artemisMath
													.getSelectedLenses().get(0)
													.getPk()));
									customLens.setCustomLens(true);
									customLens.setFL(focalLength);
									customLens
											.setLensMake(tempSelectedLensMake);
									customLens.setLensSet("1");
									_artemisDBHelper.addCustomLens(customLens);

									setSelectedLensMake(
											customLens.getLensMake(), true,
											false);
									setSelectedLenses(loadLensesForLensMake(),
											false, true);

								}
							})
					.setNegativeButton(getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).show();
		}
	};

	private void openHelpFile() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri
				.parse("https://docs.google.com/document/d/1aS4i-ipOhgPgQRC47QCYq7Tji-dxeIQLJzhdgd_mNL8/edit?hl=en&pli=1&rm=demo"));
		startActivity(intent);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (action == KeyEvent.ACTION_DOWN) {
				pictureSaveHeadingTiltString = headingTiltText.getText()
						.toString();
				if (volumeUpAutoFocusAndPicture) {
					mCameraPreview.autofocusCamera(true);
				} else if (volumeUpPicture) {
					mCameraPreview.takePicture();
				} else if (volumeUpAutoFocus) {
					mCameraPreview.autofocusCamera(false);
				}
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (action == KeyEvent.ACTION_DOWN) {
				pictureSaveHeadingTiltString = headingTiltText.getText()
						.toString();
				if (volumeDownAutoFocusAndPicture) {
					mCameraPreview.autofocusCamera(true);
				} else if (volumeDownPicture) {
					mCameraPreview.takePicture();
				} else if (volumeDownAutoFocus) {
					mCameraPreview.autofocusCamera(false);
				}
			}
			return true;
		case KeyEvent.KEYCODE_CAMERA:
			if (action == KeyEvent.ACTION_DOWN && mCameraPreview != null) {
				// just pass a view to the shutter event
				pictureSaveHeadingTiltString = headingTiltText.getText()
						.toString();
				shutterButtonPressed(_lensFocalLengthText);
			}
			return true;
		default:
			return super.dispatchKeyEvent(event);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case GALLERY_IMAGE_LOADER:
			SharedPreferences artemisPrefs = getApplication()
					.getSharedPreferences(
							ArtemisPreferences.class.getSimpleName(),
							MODE_PRIVATE);
			String prefix = Environment.getExternalStorageDirectory()
					.getAbsolutePath().toString();
			String folder = prefix
					+ "/"
					+ artemisPrefs.getString(
							ArtemisPreferences.SAVE_PICTURE_FOLDER,
							getString(R.string.artemis_save_location_default));
			String[] projection = { MediaStore.Images.Media._ID };
			// Create the cursor pointing to the SDCard

			return new CursorLoader(ArtemisActivity.this,
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
					MediaStore.Images.Media.DATA + " like ? ",
					new String[] { "%" + folder + "%" }, null);
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try {
			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

			if (cursor.moveToLast()) {
				int imageID = cursor.getInt(columnIndex);
				// obtain the image URI
				Uri uri = Uri.withAppendedPath(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						Integer.toString(imageID));

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(uri);

				Log.i("GalleryActivity", uri.toString());
				startActivity(intent);
			} else {
				toastNoImageFound();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		getLoaderManager().destroyLoader(GALLERY_IMAGE_LOADER);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera_settings:
			openCameraSettingsView();
			return true;
		case R.id.lens_settings:
			openLensSettingsView();
			return true;
		case R.id.artemis_settings:
			openArtemisSettings();
			return true;
		case R.id.artemis_about:
			openArtemisAboutView();
			return true;
		case R.id.artemis_gallery:
			openGallery();
			return true;
		case R.id.artemis_quit:
			finish();
			return false;
		}

		return false;
	}

	private void openArtemisAboutView() {
		Intent i = new Intent(ArtemisActivity.this, AboutActivity.class);
		startActivityForResult(i, 1);
	}

	private void openArtemisSettings() {
		Intent settingsIntent = new Intent(ArtemisActivity.this,
				SettingsActivity.class);
		startActivity(settingsIntent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
			int height) {
		// Set the background
		if (ArtemisActivity.arrowBackgroundImage == null) {
			Options o = new Options();
			o.inSampleSize = 2;
			ArtemisActivity.arrowBackgroundImage = BitmapFactory
					.decodeResource(getResources(), R.drawable.arrows, o);
		}

		if (mCamera == null) {
			mCamera = android.hardware.Camera.open();

			try {
				mCamera.setPreviewTexture(surface);
			} catch (IOException t) {
			}

			mCameraPreview.openCamera(mCamera, true);
			this.reconfigureNextAndPreviousLensButtons();
		}

		isSurfaceAvailable = true;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
			int height) {
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		isSurfaceAvailable = false;
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		// Invoked every time there's a new Camera preview frame
	}
}
