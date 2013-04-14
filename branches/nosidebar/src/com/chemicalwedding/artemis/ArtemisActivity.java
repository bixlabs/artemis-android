package com.chemicalwedding.artemis;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
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
import android.os.PowerManager;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewFlipper;

import com.android.vending.licensing.AESObfuscator;
import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.ServerManagedPolicy;
import com.chemicalwedding.artemis.database.ArtemisDatabaseHelper;
import com.chemicalwedding.artemis.database.Camera;
import com.chemicalwedding.artemis.database.CustomCamera;
import com.chemicalwedding.artemis.database.Lens;

public class ArtemisActivity extends Activity {

	private static final String _logTag = ArtemisActivity.class.getSimpleName();

	private CameraPreview14 _cameraPreview;
	private LinearLayout mCameraContainer;
	private static ImageView _nextLensButton;
	private static ImageView _prevLensButton;
	protected static ViewFlipper viewFlipper;
	private ViewFlipper _cameraSettingsFlipper;
	private ViewFlipper _lensSettingsFlipper;
	private ViewFlipper savePictureViewFlipper;
	protected static TextView _cameraDetailsText;
	protected static TextView _lensMakeText;
	protected static TextView headingTiltText;
	private ListView _lensListView;
	private Context _activityContext;
	protected static TextView _lensFocalLengthText;
	protected CameraOverlay mCameraOverlay;
	protected CameraAngleDetailView mCameraAngleDetailView;

	private ArtemisDatabaseHelper _artemisDBHelper;

	private int _currentViewId;
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

	// use to handle math calculations
	private static ArtemisMath _artemisMath = ArtemisMath.getInstance();

	protected static ImageView pictureSavePreview;

	private LocationManager locationManager;
	private SensorManager sensorManager;
	private String locationProvider;
	private static Location lastKnownLocation;
	protected static Location pictureSaveLocation;
	protected static String pictureSaveHeadingTiltString;
	private Handler handler;
	protected static String savePictureFolder;

	protected static boolean gpsEnabled = false, sensorEnabled = false;
	protected static int headingDisplaySelection = 2;

	// License checking
	private LicenseCheckerCallback _licenseCheckerCallback;
	private LicenseChecker _checker;

	public static boolean languageChanged = false;
	private boolean wasFocalLengthButtonPressed = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(_logTag, "Creating Artemis Activity");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		_currentViewId = R.id.artemisPreview;
		_activityContext = this;
		handler = new Handler();

		// keep the screen on
		getWindow().addFlags(
				android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		startArtemis();

		// check license
		_checker.checkAccess(_licenseCheckerCallback);
	}

	protected void startArtemis() {
		// Connect member variables to view objects
		bindViewObjects();

		// Setup listeners for events
		bindViewEvents();

		// bind the license objects
		bindLicenseCheckingObjects();

		// connect to the database and load some initial data
		initDatabase();

		// init the user preferences
		initPreferences();

		// init dynamic screen and box drawing
		// initDisplay();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(_logTag, "Destroying Artemis");

		unbindViewEvents();

		// Close the database connection
		if (_artemisDBHelper != null) {
			_artemisDBHelper.close();
			_artemisDBHelper = null;
		}
		_checker.onDestroy();
		_licenseCheckerCallback = null;
		_checker = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(_logTag, "Pausing Artemis");
		if (_cameraPreview != null) {
			_cameraPreview.releaseCamera();
			_cameraPreview.removeAllViews();
			_cameraPreview.arrowLeft.recycle();
			_cameraPreview.arrowRight.recycle();
			_cameraPreview.arrowTop.recycle();
			_cameraPreview.arrowBottom.recycle();
			_cameraPreview = null;
			mCameraContainer.removeAllViews();

		}

		if (gpsEnabled)
			locationManager.removeUpdates(locationListener);

		if (sensorEnabled)
			sensorManager.unregisterListener(sensorEventListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(_logTag, "Resuming Artemis");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		if (pm.isScreenOn() && _cameraPreview == null
				&& mCameraContainer != null) {

			_cameraPreview = new CameraPreview14(this, null);

			mCameraContainer.addView((View) _cameraPreview,
					new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.MATCH_PARENT));
			Options o = new Options();
			o.inSampleSize = 2;
			_cameraPreview.arrowLeft = BitmapFactory.decodeResource(
					getResources(), R.drawable.zoom_arrows_left, o);
			_cameraPreview.arrowRight = BitmapFactory.decodeResource(
					getResources(), R.drawable.zoom_arrows_right, o);
			_cameraPreview.arrowTop = BitmapFactory.decodeResource(
					getResources(), R.drawable.zoom_arrows_top, o);
			_cameraPreview.arrowBottom = BitmapFactory.decodeResource(
					getResources(), R.drawable.zoom_arrows_bottom, o);

			_cameraPreview.startConversionThread();
		}

		initSensorManager();

		initLocationManager();

		// Setup the language
		SharedPreferences settings = this.getSharedPreferences(
				ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);
		String lang = settings.getString(ArtemisPreferences.SELECTED_LANGUAGE,
				"");
		if (!"".equals(lang)) {
			Locale locale = new Locale(lang);
			Locale.setDefault(locale);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());
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
			Log.i(_logTag, "Artemis GPS is enabled");
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
				Log.d(_logTag, "Last known location is "
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
			Log.d(_logTag, "Updating Location");
			lastKnownLocation = location;
			Log.d(_logTag, "Updated location is "
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

		mCameraContainer = (LinearLayout) findViewById(R.id.cameraContainer);
		mCameraOverlay = (CameraOverlay) findViewById(R.id.cameraOverlay);
		mCameraAngleDetailView = (CameraAngleDetailView) findViewById(R.id.CameraAngleDetailView);
		viewFlipper = (ViewFlipper) findViewById(R.id.mainViewFlipper);
		_lensSettingsFlipper = (ViewFlipper) findViewById(R.id.lens_settings_flipper);
		_cameraDetailsText = (TextView) findViewById(R.id.cameraDetailsText);
		_lensMakeText = (TextView) findViewById(R.id.lensMakeText);
		headingTiltText = (TextView) findViewById(R.id.headingTiltText);
		_nextLensButton = (ImageView) findViewById(R.id.nextButton);
		_prevLensButton = (ImageView) findViewById(R.id.prevButton);

		// keep binding more objects
		_lensFocalLengthText = (TextView) findViewById(R.id.lensFocalLengthText);
		pictureSavePreview = (ImageView) findViewById(R.id.savePicturePreview);
		savePictureViewFlipper = (ViewFlipper) findViewById(R.id.savePictureViewFlipper);
	}

	private void bindViewEvents() {

		final ImageView helpOverlay = (ImageView) findViewById(R.id.helpOverlay);
		final ImageView helpButton = (ImageView) findViewById(R.id.helpButton);

		// Setup help button and help overlay touch events
		((View) findViewById(R.id.helpButtonView))
				.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							if (helpOverlay.getVisibility() == View.INVISIBLE) {
								helpOverlay.setVisibility(View.VISIBLE);
								helpButton.setPressed(true);
							}
							break;
						case MotionEvent.ACTION_UP:
							if (helpOverlay.getVisibility() == View.VISIBLE) {
								helpOverlay.setVisibility(View.INVISIBLE);
								helpButton.setPressed(false);
							}
							break;
						}
						return false;
					}
				});

		((View) findViewById(R.id.cameraDetailsView))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						openCameraSettingsView();
						v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					}
				});

		((View) findViewById(R.id.lensMakeView))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						wasFocalLengthButtonPressed = false;
						openLensSettingsView();
						v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
					}
				});

		Button cancelLensesButton = (Button) findViewById(R.id.cancelLenses);
		cancelLensesButton
				.setOnClickListener(new LenseSelectionCancelClickListener());
		Button saveLensesButton = (Button) findViewById(R.id.saveLenses);
		saveLensesButton
				.setOnClickListener(new LenseSelectionSaveClickListener());

		// next / prev lens
		_nextLensButton.setOnClickListener(new NextLensClickListener());
		_prevLensButton.setOnClickListener(new PreviousLensClickListener());

		// view under the focal length label in the preview
		((ImageView) findViewById(R.id.focalLengthLensButton))
				.setOnClickListener(new focalLengthLensButtonViewClickListener());

		// fullscreen button
		((ImageView) findViewById(R.id.fullscreenButton))
				.setOnClickListener(new FullscreenViewClickListener());

		// shutter release (take picture) button
		((ImageView) findViewById(R.id.shutterButton))
				.setOnClickListener(new TakePictureClickListener());

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
						if (_lensSettingsFlipper.getDisplayedChild() > 0
								&& !wasFocalLengthButtonPressed) {
							_lensSettingsFlipper.setInAnimation(
									_activityContext, R.anim.slide_in_left);
							_lensSettingsFlipper.setOutAnimation(
									_activityContext, R.anim.slide_out_right);
							_lensSettingsFlipper.showPrevious();
						} else if (_lensSettingsFlipper.getDisplayedChild() == 0
								|| wasFocalLengthButtonPressed) {
							wasFocalLengthButtonPressed = false;
							openArtemisCameraPreviewView();
						}
					}
				});
	}

	private void unbindViewEvents() {

		ImageView helpOverlay = (ImageView) findViewById(R.id.helpOverlay);
		ImageView helpButton = (ImageView) findViewById(R.id.helpButton);
		helpButton.setImageDrawable(null);
		helpOverlay.setImageDrawable(null);
		helpOverlay.setBackgroundDrawable(null);
		((ImageView) findViewById(R.id.fullscreenButton))
				.setImageDrawable(null);
		((ImageView) findViewById(R.id.shutterButton)).setImageDrawable(null);
		// _nextLensButton.setImageDrawable(null);
		// _prevLensButton.setImageDrawable(null);

		((ImageView) findViewById(R.id.focalLengthLensButton))
				.setImageDrawable(null);
		// _previewSideBar.setImageDrawable(null);
		((ImageView) findViewById(R.id.add_custom_camera_image_diagram))
				.setImageDrawable(null);

		((ImageView) findViewById(R.id.savePicturePreview))
				.setImageDrawable(null);
		((ImageView) findViewById(R.id.lens_settings_back))
				.setImageDrawable(null);
		((ImageView) findViewById(R.id.camera_settings_back))
				.setImageDrawable(null);

		// Setup help button and help overlay touch events
		((View) findViewById(R.id.helpButtonView)).setOnTouchListener(null);

		((View) findViewById(R.id.cameraDetailsView)).setOnTouchListener(null);

		((View) findViewById(R.id.lensMakeView)).setOnTouchListener(null);

		Button cancelLensesButton = (Button) findViewById(R.id.cancelLenses);
		cancelLensesButton.setOnClickListener(null);
		Button saveLensesButton = (Button) findViewById(R.id.saveLenses);
		saveLensesButton.setOnClickListener(null);

		// next / prev lens
		// _nextLensButton.setOnClickListener(null);
		// _prevLensButton.setOnClickListener(null);

		// view under the focal length label in the preview
		((ImageView) findViewById(R.id.focalLengthLensButton))
				.setOnClickListener(null);

		// fullscreen button
		((ImageView) findViewById(R.id.fullscreenButton))
				.setOnClickListener(null);

		// shutter release (take picture) button
		((ImageView) findViewById(R.id.shutterButton)).setOnClickListener(null);

		// save picture buttons
		((Button) findViewById(R.id.savePictureCancelButton))
				.setOnClickListener(null);
		((Button) findViewById(R.id.save_edit_details_PictureCancelButton))
				.setOnClickListener(null);
		((Button) findViewById(R.id.savePictureAndEditButton))
				.setOnClickListener(null);
		((Button) findViewById(R.id.savePictureButton))
				.setOnClickListener(null);

		((Button) findViewById(R.id.savePictureDetails))
				.setOnClickListener(null);

		mCameraAngleDetailView.setOnClickListener(null);

		((ImageView) findViewById(R.id.camera_settings_back))
				.setOnClickListener(null);

		((ImageView) findViewById(R.id.lens_settings_back))
				.setOnClickListener(null);
	}

	private void initDatabase() {
		// Open Database
		_artemisDBHelper = new ArtemisDatabaseHelper(this);

		// load initialization data for the first camera selection
		((ArtemisApplication) getApplication())
				.postOnWorkerThread(new Runnable() {
					@Override
					public void run() {
						_allCameraFormats = _artemisDBHelper.getCameraFormats();
						_allCameraFormats
								.add(getString(R.string.add_custom_camera_list_item));
					}
				});
	}

	private static final String DEFAULT_LENS_MAKE = "Generic 35mm Lenses";
	private static final String DEFAULT_LENSES = "77,80,84,88,92,96,100";

	private static final int DEFAULT_CAMERA_ROW = 10;

	private void initPreferences() {
		SharedPreferences artemisPrefs = getApplication().getSharedPreferences(
				ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);
		// Retrieve the previously selected camera and lenses
		int selectedCameraRowId = artemisPrefs.getInt(
				ArtemisPreferences.SELECTED_CAMERA_ROW, DEFAULT_CAMERA_ROW);
		if (selectedCameraRowId > 0) {
			// Id is above 0, this is a normal Camera from the Camera's table
			setSelectedCamera(selectedCameraRowId, false, false);
		} else {
			// Set custom camera
			CustomCamera selectedCustomCamera = _artemisDBHelper
					.getCustomCameraDetailsForRowId(-selectedCameraRowId);

			Camera selectedCam = new Camera();
			selectedCam.setSqz(1f);
			selectedCam.setHoriz(selectedCustomCamera.getHoriz());
			selectedCam.setVertical(selectedCustomCamera.getVertical());
			selectedCam.setRatio("");
			selectedCam.setLenses("35mm");
			selectedCam.setRowid(-1); // to signify this is a custom camera

			_selectedCamera = selectedCam;
			tempSelectedCamera = selectedCam;
			_artemisMath.setSelectedCamera(_selectedCamera);

			_cameraDetailsText.setText(selectedCustomCamera.getLabel() + " "
					+ _selectedCamera.getRatio());
		}

		String lensMake = artemisPrefs.getString(
				ArtemisPreferences.SELECTED_LENS_MAKE, DEFAULT_LENS_MAKE);
		setSelectedLensMake(lensMake, false, false);

		String selectedLensesRowIds = artemisPrefs.getString(
				ArtemisPreferences.SELECTED_LENS_ROW_CSV, DEFAULT_LENSES);
		setSelectedLenses(selectedLensesRowIds, true, false);

		// check if gps is enabled
		gpsEnabled = artemisPrefs.getBoolean(ArtemisPreferences.GPS_ENABLED,
				true);

		// lock box setting
		ArtemisSettingsActivity.lockBoxEnabled = artemisPrefs.getBoolean(
				ArtemisPreferences.LOCK_BOXES_ENABLED, false);
		this.mCameraOverlay.lockBoxEnabled = ArtemisSettingsActivity.lockBoxEnabled;

		// quick shot save setting
		ArtemisSettingsActivity.quickshotEnabled = artemisPrefs.getBoolean(
				ArtemisPreferences.QUICKSHOT_ENABLED, false);

		// smooth image filter
		ArtemisSettingsActivity.smoothImagesEnabled = artemisPrefs.getBoolean(
				ArtemisPreferences.SMOOTH_IMAGE_ENABLED, true);

		// autofocus camera before picture
		// NOTE: MOVED INTO surfaceChanged
		// CameraPreview.autoFocusBeforePictureTake = artemisPrefs.getBoolean(
		// ArtemisPreferences.AUTO_FOCUS_ON_PICTURE, true);

		// setup the save files directory
		savePictureFolder = artemisPrefs.getString(
				ArtemisPreferences.SAVE_PICTURE_FOLDER, Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/Artemis");
		File saveFolder = new File(savePictureFolder);
		if (!saveFolder.isDirectory()) {
			saveFolder.mkdirs();
		}

		ArtemisActivity.headingDisplaySelection = artemisPrefs.getInt(
				ArtemisPreferences.HEADING_DISPLAY, 2);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera_settings:
			openCameraSettingsView();
			return true;
		case R.id.lens_settings:
			openLensSettingsView();
			return true;
		case R.id.artemis_settings:
			openArtemisSettingsView();
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

	@Override
	public void onBackPressed() {
		switch (_currentViewId) {
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
				_lensSettingsFlipper.setInAnimation(this, R.anim.slide_in_left);
				_lensSettingsFlipper.setOutAnimation(this,
						R.anim.slide_out_right);
				_lensSettingsFlipper
						.setDisplayedChild(currentDisplayedChild - 1);
			} else {
				// otherwise, at the first page. go back to main preview
				openArtemisCameraPreviewView();
			}
			break;
		case R.id.artemisPreview:
			break;
		}
	}

	private void openCameraSettingsView() {
		if (_cameraSettingsFlipper == null)
			_cameraSettingsFlipper = (ViewFlipper) findViewById(R.id.camera_settings_flipper);
		_cameraSettingsFlipper.setInAnimation(null);
		_cameraSettingsFlipper.setOutAnimation(null);
		_cameraSettingsFlipper.setDisplayedChild(0);

		viewFlipper.setDisplayedChild(1);
		_currentViewId = R.id.cameraSettings;

		ListView cameraFormatList = (ListView) findViewById(R.id.cameraFormatList);
		ArrayAdapter<String> formatAdapter = new ArrayAdapter<String>(this,
				R.layout.text_list_item, _allCameraFormats);
		cameraFormatList.setAdapter(formatAdapter);
		cameraFormatList.setTextFilterEnabled(true);

		cameraFormatList
				.setOnItemClickListener(new CameraFormatItemClickedListener());
	}

	private void openLensSettingsView() {

		_lensSettingsFlipper.setInAnimation(null);
		_lensSettingsFlipper.setDisplayedChild(0);

		View lensBackButton = findViewById(R.id.lens_settings_back);
		lensBackButton.setEnabled(true);
		if (tempSelectedCamera != null && tempSelectedCamera.getRowid() != null
				&& tempSelectedCamera.getRowid() == -1) {
			// This is a custom camera
			_lensSettingsFlipper.setDisplayedChild(1);
			findViewById(R.id.lens_settings_back).setEnabled(false);
			lensBackButton.setEnabled(false);
		}

		viewFlipper.setDisplayedChild(2);
		_currentViewId = R.id.lensSettings;

		String[] lensFormatsForCamera = tempSelectedCamera.getLenses().split(
				",");
		ArrayList<String> lensMakes = _artemisDBHelper
				.getLensMakeForLensFormat(lensFormatsForCamera);
		Log.i(_logTag, "Lens makers available: " + lensMakes.size());

		ListView lensMakerList = (ListView) findViewById(R.id.lensMakerList);
		ArrayAdapter<String> formatAdapter = new ArrayAdapter<String>(this,
				R.layout.text_list_item, lensMakes);
		lensMakerList.setAdapter(formatAdapter);
		lensMakerList.setTextFilterEnabled(true);

		lensMakerList
				.setOnItemClickListener(new LensMakerItemClickedListener());
	}

	/*
	 * Init the settings view if not already initialized and open the artemis
	 * settings view
	 */
	private void openArtemisSettingsView() {

		Intent i = new Intent();
		i.setClassName("com.chemicalwedding.artemis",
				"com.chemicalwedding.artemis.ArtemisSettingsActivity");
		startActivityForResult(i, 1);
	}

	private void openArtemisCameraPreviewView() {
		viewFlipper.setInAnimation(null);
		viewFlipper.setDisplayedChild(0);
		_currentViewId = R.id.artemisPreview;
	}

	private void openArtemisAboutView() {
		Intent i = new Intent();
		i.setClassName("com.chemicalwedding.artemis",
				"com.chemicalwedding.artemis.AboutActivity");
		startActivityForResult(i, 1);
	}

	private void openGallery() {

		SharedPreferences artemisPrefs = getApplication().getSharedPreferences(
				ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);
		String prefix = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/";
		String folder = artemisPrefs.getString(
				ArtemisPreferences.SAVE_PICTURE_FOLDER, prefix + "Artemis");

		String[] projection = { MediaStore.Images.Media._ID };
		// Create the cursor pointing to the SDCard
		Cursor cursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
				MediaStore.Images.Media.DATA + " like ? ", new String[] { "%"
						+ folder + "%" }, null);
		// Get the column index of the image ID
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
			final Toast toast = Toast.makeText(_activityContext,
					getString(R.string.gallery_no_images_found),
					Toast.LENGTH_LONG);
			toast.setDuration(2000);
			toast.show();
		}
	}

	public static String getBucketId(String bucketName) {
		bucketName = bucketName.toLowerCase();
		if (bucketName.charAt(bucketName.length() - 1) == '/') {
			bucketName = bucketName.substring(0, bucketName.length() - 1);
		}
		String bucketId = Integer.toString(bucketName.hashCode());
		Log.i(_logTag, "Gallery Intent Bucket ID: " + bucketId);
		return bucketId;
	}

	class CameraFormatItemClickedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View selectedItem,
				int arg2, long arg3) {
			TextView selectedTextView = (TextView) selectedItem;
			String selectedFormat = selectedTextView.getText().toString();

			if (getString(R.string.add_custom_camera_list_item).equals(
					selectedFormat)) {
				// Custom cameras was selected...
				if (customCameras == null) {
					customCameras = _artemisDBHelper.getCustomCameras();
					CustomCamera addnew = new CustomCamera();
					addnew.setLabel(getString(R.string.custom_camera_new_camera));
					customCameras.add(addnew);
					ListView customCameraList = (ListView) findViewById(R.id.customCameraList);
					ArrayAdapter<CustomCamera> adapter = new ArrayAdapter<CustomCamera>(
							_activityContext, R.layout.text_list_item,
							customCameras);
					customCameraList.setAdapter(adapter);
					customCameraList
							.setOnItemClickListener(new CustomCameraClickListener());
					registerForContextMenu(customCameraList);
				}

				_cameraSettingsFlipper.setInAnimation(_activityContext,
						R.anim.slide_in_right);
				_cameraSettingsFlipper.setOutAnimation(_activityContext,
						R.anim.slide_out_left);
				_cameraSettingsFlipper.setDisplayedChild(3);
				return;
			}

			Log.i(_logTag, "Camera format selected: " + selectedFormat);
			// _currentCameraFormat = selectedFormat;
			ArrayList<String> sensorListForCamera = _artemisDBHelper
					.getCameraSensorsForFormat(selectedFormat);
			Log.i(_logTag, "Sensors available: " + sensorListForCamera.size());

			ListView cameraSensorList = (ListView) findViewById(R.id.cameraSensorList);
			ArrayAdapter<String> sensorAdapter = new ArrayAdapter<String>(
					_activityContext, R.layout.text_list_item,
					sensorListForCamera);
			cameraSensorList.setAdapter(sensorAdapter);
			cameraSensorList.setTextFilterEnabled(true);
			cameraSensorList
					.setOnItemClickListener(new CameraSensorItemClickedListener());
			_cameraSettingsFlipper.setInAnimation(_activityContext,
					R.anim.slide_in_right);
			_cameraSettingsFlipper.setOutAnimation(_activityContext,
					R.anim.slide_out_left);

			_cameraSettingsFlipper.showNext();
		}

	}

	class CameraSensorItemClickedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View selectedItem,
				int arg2, long arg3) {
			TextView selectedTextView = (TextView) selectedItem;
			String selectedSensor = selectedTextView.getText().toString();
			Log.i(_logTag, "Camera sensor selected: " + selectedSensor);
			// _currentCameraSensor = selectedSensor;
			_ratiosListForCamera = _artemisDBHelper
					.getCameraRatiosForSensor(selectedSensor);
			Log.i(_logTag, "Ratios available: " + _ratiosListForCamera.size());

			// Add the ratio names to a list and bind to the list view
			ArrayList<String> ratioNames = new ArrayList<String>();
			for (Pair<Integer, String> pair : _ratiosListForCamera) {
				ratioNames.add(pair.second);
			}
			ListView cameraRatioList = (ListView) findViewById(R.id.cameraRatiosList);
			ArrayAdapter<String> ratioAdapter = new ArrayAdapter<String>(
					_activityContext, R.layout.text_list_item, ratioNames);
			cameraRatioList.setAdapter(ratioAdapter);
			cameraRatioList.setTextFilterEnabled(true);
			cameraRatioList
					.setOnItemClickListener(new CameraRatioItemClickedListener());

			_cameraSettingsFlipper.setInAnimation(_activityContext,
					R.anim.slide_in_right);
			_cameraSettingsFlipper.setOutAnimation(_activityContext,
					R.anim.slide_out_left);

			_cameraSettingsFlipper.showNext();
		}
	}

	class CameraRatioItemClickedListener implements OnItemClickListener {

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
			openLensSettingsView();
		}
	}

	class LensMakerItemClickedListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View selectedItem,
				int arg2, long arg3) {
			TextView selectedTextView = (TextView) selectedItem;
			tempSelectedLensMake = selectedTextView.getText().toString();
			// Log.i(_logTag, "Lens make selected: " + selectedLensMake);
			setSelectedLensMake(tempSelectedLensMake, false, true);
			// Log.i(_logTag, "Lenses available: " + _lensesForMake.size());

			loadLensesForLensMake();

			_lensSettingsFlipper.setInAnimation(_activityContext,
					R.anim.slide_in_right);
			_lensSettingsFlipper.setOutAnimation(_activityContext,
					R.anim.slide_out_left);

			_lensSettingsFlipper.showNext();
		}
	}

	class LenseSelectionCancelClickListener implements
			android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			wasFocalLengthButtonPressed = false;
			_lensSettingsFlipper.setOutAnimation(null);
			openArtemisCameraPreviewView();
		}
	}

	class LenseSelectionSaveClickListener implements
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
					selectedLensString += lens.getRowid();
					_selectedLenses.add(lens);
				}
			}
			Log.i(_logTag, "SELECTED LENSES: " + selectedLensString);

			if (tempSelectedCamera.getRowid() != -1) {
				setSelectedCamera(tempSelectedCamera.getRowid(), true, false);
				setSelectedLensMake(tempSelectedLensMake, true, false);
			}
			setSelectedLenses(selectedLensString, true, true);
			updateLensesInDB();
			_artemisMath.setFullscreen(false);
			_artemisMath.calculateLargestLens();
			// call twice fixes the refresh bug after select lenses.
			// otherwise it doesn't redraw, strange looking boxes appear ???
			_artemisMath.calculateRectBoxesAndLabelsForLenses();
			_artemisMath.selectFirstMeaningFullLens();
			_artemisMath.onFullscreenOffSelectLens();
			_artemisMath.resetTouchToCenter();
			_cameraPreview.calculateZoom();
			mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
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
		if (selectLensMakeLenses) {
			_selectedLenses.clear();
			String[] lensIdStrings = lensCSVString.split(",");
			for (Lens lens : tempLensesForMake) {
				String stringid = lens.getRowid().toString();
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
			appPrefsEditor.commit();
		}
	}

	class NextLensClickListener implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (_artemisMath.selectNextLens()) {
				mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
				if (_artemisMath.isFullscreen()) {
					_cameraPreview.calculateZoom();
				}
			}
			if (!_artemisMath.hasNextLens()) {
				_nextLensButton.setVisibility(View.INVISIBLE);
			}
			if (_artemisMath.hasPreviousLens()) {
				_prevLensButton.setVisibility(View.VISIBLE);
			}
			mCameraAngleDetailView.postInvalidate();
			
			v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		}
	}

	protected static void reconfigureNextAndPreviousLensButtons() {
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
	}

	class PreviousLensClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (_artemisMath.selectPreviousLens()) {
				mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
				if (_artemisMath.isFullscreen()) {
					_cameraPreview.calculateZoom();
				}
			}
			if (!_artemisMath.hasPreviousLens()) {
				_prevLensButton.setVisibility(View.INVISIBLE);
			}
			if (_artemisMath.hasNextLens()) {
				_nextLensButton.setVisibility(View.VISIBLE);
			}
			mCameraAngleDetailView.postInvalidate();
			
			v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		}
	}

	class focalLengthLensButtonViewClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			loadLensesForLensMake();

			View lensBackButton = findViewById(R.id.lens_settings_back);
			lensBackButton.setEnabled(true);
			if (tempSelectedCamera != null
					&& tempSelectedCamera.getRowid() != null
					&& tempSelectedCamera.getRowid() == -1) {
				// This is a custom camera
				lensBackButton.setEnabled(false);
			}

			_lensSettingsFlipper.setInAnimation(null);
			_lensSettingsFlipper.setOutAnimation(null);
			_lensSettingsFlipper.setDisplayedChild(1);
			viewFlipper.setDisplayedChild(2);

			wasFocalLengthButtonPressed = true;
			
			v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
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
				lensIdList += lens.getRowid() + ",";
			}
			++position;
		}
		lensIdList = lensIdList.substring(0, lensIdList.length() - 1);

		_lensListView = (ListView) findViewById(R.id.lensList);
		ArrayAdapter<String> lensAdapter = new ArrayAdapter<String>(
				_activityContext, R.layout.lens_list_item, focalLengths);
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

	class FullscreenViewClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (_artemisMath.isFullscreen()) {
				// non fullscreen with all lens boxes
				_artemisMath.setFullscreen(false);
				_artemisMath.onFullscreenOffSelectLens();
				mCameraAngleDetailView.postInvalidate();
			} else {
				_artemisMath.setFullscreen(true);
			}
			reconfigureNextAndPreviousLensButtons();
			mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
			_cameraPreview.calculateZoom();

			v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		}
	}

	class TakePictureClickListener implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {

			if (lastKnownLocation != null)
				pictureSaveLocation = lastKnownLocation;

			pictureSaveHeadingTiltString = headingTiltText.getText().toString();

			if (ArtemisSettingsActivity.quickshotEnabled) {
				_cameraPreview.takePicture();
				return;
			}

			savePictureViewFlipper.setInAnimation(null);
			savePictureViewFlipper.setDisplayedChild(0);
			// normal take picture (not quickshot)
			_cameraPreview.takePicture();
			
			v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		}
	}

	class CancelSavePictureClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			pictureSavePreview.setImageBitmap(null);
			_cameraPreview.restartPreview();
			openArtemisCameraPreviewView();
		}
	}

	class SavePictureClickListener implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			final Toast toast = Toast.makeText(_activityContext,
					getString(R.string.image_saved_success), Toast.LENGTH_LONG);
			toast.setDuration(2000);
			toast.show();

			new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... params) {
					_cameraPreview.renderPictureDetailsAndSave();
					return "";
				}

				@Override
				protected void onPostExecute(String result) {
					toast.cancel();
					sendBroadcast(new Intent(
							Intent.ACTION_MEDIA_MOUNTED,
							Uri.parse("file://"
									+ Environment.getExternalStorageDirectory())));
				}
			}.execute(new String[] {});

			_cameraPreview.restartPreview();
			openArtemisCameraPreviewView();
		}
	}

	class EditPictureDetailsClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			pictureSavePreview.setImageBitmap(null);
			if (!gpsEnabled) {
				ToggleButton gpsDetails = (ToggleButton) findViewById(R.id.gpsDetailsToggle);
				gpsDetails.setEnabled(false);
				gpsDetails.setChecked(false);
			}

			SharedPreferences artemisPrefs = getSharedPreferences(
					ArtemisPreferences.class.getSimpleName(),
					Context.MODE_PRIVATE);

			((EditText) findViewById(R.id.imageDescription))
					.setText(artemisPrefs.getString(
							ArtemisPreferences.SAVE_PICTURE_SHOW_DESCRIPTION,
							""));
			((ToggleButton) findViewById(R.id.cameraDetailsToggle))
					.setChecked(artemisPrefs
							.getBoolean(
									ArtemisPreferences.SAVE_PICTURE_SHOW_CAMERA_DETAILS,
									true));
			((ToggleButton) findViewById(R.id.lensDetailsToggle))
					.setChecked(artemisPrefs.getBoolean(
							ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_DETAILS,
							true));
			((ToggleButton) findViewById(R.id.gpsDetailsToggle))
					.setChecked(artemisPrefs.getBoolean(
							ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_DETAILS,
							true));
			((ToggleButton) findViewById(R.id.gpsLocationToggle))
					.setChecked(artemisPrefs.getBoolean(
							ArtemisPreferences.SAVE_PICTURE_SHOW_GPS_LOCATION,
							true));
			((ToggleButton) findViewById(R.id.h_and_v_angle_toggle))
					.setChecked(artemisPrefs
							.getBoolean(
									ArtemisPreferences.SAVE_PICTURE_SHOW_LENS_VIEW_ANGLES,
									true));
			((ToggleButton) findViewById(R.id.date_and_time_toggle))
					.setChecked(artemisPrefs.getBoolean(
							ArtemisPreferences.SAVE_PICTURE_SHOW_DATE_TIME,
							true));
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

			savePictureViewFlipper.showNext();
		}
	}

	class SavePictureEditDetailsClickListener implements
			android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {

			Editor editor = getApplication().getSharedPreferences(
					ArtemisPreferences.class.getSimpleName(),
					Context.MODE_PRIVATE).edit();

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
			editor.putString(ArtemisPreferences.SAVE_PICTURE_SHOW_DESCRIPTION,
					((EditText) findViewById(R.id.imageDescription)).getText()
							.toString());
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
			editor.commit();

			final Toast toast = Toast.makeText(_activityContext,
					getString(R.string.image_saved_success), Toast.LENGTH_LONG);
			toast.setDuration(2500);
			toast.show();

			new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... params) {
					_cameraPreview.renderPictureDetailsAndSave();
					return "";
				}

				@Override
				protected void onPostExecute(String result) {
					toast.cancel();
					sendBroadcast(new Intent(
							Intent.ACTION_MEDIA_MOUNTED,
							Uri.parse("file://"
									+ Environment.getExternalStorageDirectory())));
				}
			}.execute(new String[] {});

			_cameraPreview.restartPreview();
			savePictureViewFlipper.setDisplayedChild(0);
			savePictureViewFlipper.stopFlipping();
			openArtemisCameraPreviewView();
		}
	}

	public static String[] getGPSLocationDetailStrings(Context context) {
		String gpsDetails = "";
		String gpsLocationString = "";

		if (lastKnownLocation != null && gpsEnabled) {
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
					if (addr.getAddressLine(0) != null) {
						if (nItems > 0)
							gpsLocationString += " • ";
						gpsLocationString += addr.getAddressLine(0);
						++nItems;
					}
					if (addr.getLocality() != null) {
						if (nItems > 0)
							gpsLocationString += " • ";
						gpsLocationString += addr.getLocality();
						++nItems;
					}
					if (addr.getAdminArea() != null) {
						if (nItems > 0)
							gpsLocationString += " • ";
						gpsLocationString += addr.getAdminArea();
						++nItems;
					}
					if (addr.getPostalCode() != null) {
						if (nItems > 0)
							gpsLocationString += " • ";
						gpsLocationString += addr.getPostalCode();
						++nItems;
					}
					if (addr.getCountryName() != null) {
						if (nItems > 0)
							gpsLocationString += " • ";
						gpsLocationString += addr.getCountryCode();
						++nItems;
					}
				}
			} catch (IOException ioe) {
				Log.e(_logTag, "Error retrieving geocoder location data");
			}
		}
		return new String[] { gpsDetails, gpsLocationString };
	}

	class CustomCameraClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View selectedItem,
				int selectedIndex, long arg3) {
			TextView selectedTextView = (TextView) selectedItem;
			String selectedFormat = selectedTextView.getText().toString();

			if (getString(R.string.custom_camera_new_camera).equals(
					selectedFormat)) {
				// New custom camera was selected...
				((Button) findViewById(R.id.save_custom_camera))
						.setOnClickListener(new AddCustomCameraClickListener());

				_cameraSettingsFlipper.setInAnimation(_activityContext,
						R.anim.slide_in_right);
				_cameraSettingsFlipper.setOutAnimation(_activityContext,
						R.anim.slide_out_left);
				_cameraSettingsFlipper.setDisplayedChild(4);
				return;
			} else if (customCameras != null && customCameras.size() > 0) {
				CustomCamera selectedCustomCamera = customCameras
						.get(selectedIndex);
				// Create a Camera object to hold the CustomCamera and act as a
				// regular camera
				Camera selectedCam = new Camera();
				selectedCam.setSqz(1f);
				selectedCam.setHoriz(selectedCustomCamera.getHoriz());
				selectedCam.setVertical(selectedCustomCamera.getVertical());
				selectedCam.setRatio("");
				selectedCam.setLenses("35mm");
				selectedCam.setRowid(-1); // to signify this is a custom camera

				_selectedCamera = selectedCam;
				tempSelectedCamera = selectedCam;
				_artemisMath.setSelectedCamera(_selectedCamera);
				// _selectedLenses.clear();
				// set it to a negative value so we know to look in custom
				// camera table
				// setSelectedCamera(-selectedCustomCamera.getRowid(), true,
				// false);

				Editor appPrefsEditor = getApplication().getSharedPreferences(
						ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE)
						.edit();
				appPrefsEditor.putInt(ArtemisPreferences.SELECTED_CAMERA_ROW,
						-selectedCustomCamera.getRowid());
				appPrefsEditor.commit();

				setSelectedLensMake(DEFAULT_LENS_MAKE, false, false);

				setSelectedLenses(loadLensesForLensMake(), true, true);
				_cameraDetailsText.setText(selectedCustomCamera.getLabel()
						+ " " + _selectedCamera.getRatio());

				// Recalculate the view for new camera and lenses
				_artemisMath.setFullscreen(false);
				_artemisMath.calculateLargestLens();
				// call twice fixes the refresh bug after select lenses.
				// otherwise it doesn't redraw, strange looking boxes appear ???
				mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
				_artemisMath.selectFirstMeaningFullLens();
				_artemisMath.onFullscreenOffSelectLens();
				_artemisMath.resetTouchToCenter();
				mCameraOverlay.refreshLensBoxesAndLabelsForLenses();
				openArtemisCameraPreviewView();
			}

		}
	}

	class AddCustomCameraClickListener implements
			android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			EditText nameText = (EditText) findViewById(R.id.custom_camera_name);
			String cameraName = nameText.getText().toString();
			EditText focalLengthText = (EditText) findViewById(R.id.custom_camera_focallength);
			EditText distanceText = (EditText) findViewById(R.id.custom_camera_distance);
			EditText widthText = (EditText) findViewById(R.id.custom_camera_width);
			EditText heightText = (EditText) findViewById(R.id.custom_camera_height);

			try {
				float cameraDistance = Float.parseFloat(distanceText.getText()
						.toString());
				float cameraWidth = Float.parseFloat(widthText.getText()
						.toString());
				float cameraHeight = Float.parseFloat(heightText.getText()
						.toString());
				float focalLength = Float.parseFloat(focalLengthText.getText()
						.toString());

				// insert the new camera
				CustomCamera customCam = new CustomCamera();
				customCam.setLabel(cameraName);

				// calculate the viewing angles
				customCam.setHoriz(ArtemisMath.calculateAngleForCustomCamera(
						cameraDistance, cameraWidth));
				customCam.setVertical(ArtemisMath
						.calculateAngleForCustomCamera(cameraDistance,
								cameraHeight));

				customCam.setDistance(cameraDistance);
				customCam.setWidth(cameraWidth);
				customCam.setHeight(cameraHeight);
				customCam.setFL(focalLength);
				_artemisDBHelper.insertCustomCamera(customCam);

				// refresh the list in previous page
				refreshCustomCameraList();

				// clear the text off on success
				nameText.setText("");
				focalLengthText.setText("");
				distanceText.setText("");
				widthText.setText("");
				heightText.setText("");

				// flip back to the previous custom camera selection page
				_cameraSettingsFlipper.setInAnimation(_activityContext,
						R.anim.slide_in_left);
				_cameraSettingsFlipper.setOutAnimation(_activityContext,
						R.anim.slide_out_right);
				_cameraSettingsFlipper.setDisplayedChild(3);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void refreshCustomCameraList() {
		customCameras = _artemisDBHelper.getCustomCameras();
		CustomCamera addnew = new CustomCamera();
		addnew.setLabel(getString(R.string.custom_camera_new_camera));
		customCameras.add(addnew);
		ListView customCameraList = (ListView) findViewById(R.id.customCameraList);
		ArrayAdapter<CustomCamera> adapter = new ArrayAdapter<CustomCamera>(
				_activityContext, R.layout.text_list_item, customCameras);
		customCameraList.setAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View arg1,
			ContextMenuInfo contextMenuInfo) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) contextMenuInfo;
		if (menuInfo.position < customCameras.size() - 1) {
			menu.setHeaderTitle(getString(R.string.custom_camera_options));
			menu.add(0, 0, 0, getString(R.string.custom_camera_edit_camera));
			menu.add(0, 1, 1, getString(R.string.custom_camera_delete_camera));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case 0:
			// edit custom camera was selected...
			CustomCamera editCamera = customCameras.get(menuInfo.position);
			((Button) findViewById(R.id.save_custom_camera))
					.setOnClickListener(new UpdateCustomCameraClickListener(
							editCamera));

			((EditText) findViewById(R.id.custom_camera_name))
					.setText(editCamera.getLabel());
			((EditText) findViewById(R.id.custom_camera_focallength))
					.setText(editCamera.getFL() + "");
			((EditText) findViewById(R.id.custom_camera_distance))
					.setText(editCamera.getDistance() + "");
			((EditText) findViewById(R.id.custom_camera_width))
					.setText(editCamera.getWidth() + "");
			((EditText) findViewById(R.id.custom_camera_height))
					.setText(editCamera.getHeight() + "");

			_cameraSettingsFlipper.setInAnimation(_activityContext,
					R.anim.slide_in_right);
			_cameraSettingsFlipper.setOutAnimation(_activityContext,
					R.anim.slide_out_left);
			_cameraSettingsFlipper.setDisplayedChild(4);
			break;
		case 1:
			// delete custom camera
			_artemisDBHelper.deleteCustomCameraByRowId(customCameras.get(
					menuInfo.position).getRowid());
			refreshCustomCameraList();
			break;
		}
		return super.onContextItemSelected(item);
	}

	class UpdateCustomCameraClickListener implements
			android.view.View.OnClickListener {

		private CustomCamera updateCamera;

		public UpdateCustomCameraClickListener(CustomCamera customCameraToUpdate) {
			updateCamera = customCameraToUpdate;
		}

		@Override
		public void onClick(View v) {
			EditText nameText = (EditText) findViewById(R.id.custom_camera_name);
			String cameraName = nameText.getText().toString();
			EditText focalLengthText = (EditText) findViewById(R.id.custom_camera_focallength);
			EditText distanceText = (EditText) findViewById(R.id.custom_camera_distance);
			EditText widthText = (EditText) findViewById(R.id.custom_camera_width);
			EditText heightText = (EditText) findViewById(R.id.custom_camera_height);

			try {
				float cameraDistance = Float.parseFloat(distanceText.getText()
						.toString());
				float cameraWidth = Float.parseFloat(widthText.getText()
						.toString());
				float cameraHeight = Float.parseFloat(heightText.getText()
						.toString());
				float focalLength = Float.parseFloat(focalLengthText.getText()
						.toString());

				// update the new camera
				updateCamera.setLabel(cameraName);

				// calculate the viewing angles
				updateCamera.setHoriz(ArtemisMath
						.calculateAngleForCustomCamera(cameraDistance,
								cameraWidth));
				updateCamera.setVertical(ArtemisMath
						.calculateAngleForCustomCamera(cameraDistance,
								cameraHeight));

				updateCamera.setDistance(cameraDistance);
				updateCamera.setWidth(cameraWidth);
				updateCamera.setHeight(cameraHeight);
				updateCamera.setFL(focalLength);
				_artemisDBHelper.updateCustomCamera(updateCamera);

				// refresh the list in previous page
				refreshCustomCameraList();

				// clear the text off on success
				nameText.setText("");
				focalLengthText.setText("");
				distanceText.setText("");
				widthText.setText("");
				heightText.setText("");

				// flip back to the previous custom camera selection page
				_cameraSettingsFlipper.setInAnimation(_activityContext,
						R.anim.slide_in_left);
				_cameraSettingsFlipper.setOutAnimation(_activityContext,
						R.anim.slide_out_right);
				_cameraSettingsFlipper.setDisplayedChild(3);

			} catch (Exception e) {

			}

		}
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

	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAueBLc9/HMW2/OdoeuXKZLxdoovcj327x9ESeV4BFuxjWIsQb3G61Awf7sfIfIZPbzAJc2vypctf5gosRgBfL6xUY1sBQiS79Xrg3DDqKc2oVHd+wLBKTTJARA7/JAC/2xDAryYJqYRkkKFO7DLEsLpP+B45/1r881uSwyEv1bC4SXIpisYhx5F859oxUtQzGvvunEgHNlo35yf7O0HiZB0iZoonHymCZMoBXV2rgRxiemBEmoKHsdDp0zP7bZTkVr1uIj9u39YWx4UVmZnB+fJphtgAiB4nVvxkzu97JiqCeyj86yN8hcLrLEFMNb8pgDKMKaBg21QzcjHfx07fsOwIDAQAB";
	private static final byte[] SALT = new byte[] { 39, -15, 60, -48, 23, -30,
			44, 73, -25, 18, 125, -110, -17, 96, 126, 13, 1, 3, -82, -11 };

	private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
		public void allow() {
			if (isFinishing()) {
				// Don't update UI if Activity is finishing.
				return;
			}
			Log.i(_logTag, "Valid license found");
			// Should allow user access.
		}

		public void dontAllow() {
			if (isFinishing()) {
				// Don't update UI if Activity is finishing.
				return;
			}
			// Should not allow access. In most cases, the app should assume
			// the user has access unless it encounters this. If it does,
			// the app should inform the user of their unlicensed ways
			// and then either shut down the app or limit the user to a
			// restricted set of features.
			// In this example, we show a dialog that takes the user to Market.
			//
			//
			//
			//
			//
			//
			//
			//
			//
			Log.i(_logTag, "No license found");

			showDialog(0);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			}, 10000);
		}

		public void applicationError(ApplicationErrorCode errorCode) {
			if (isFinishing()) {
				// Don't update UI if Activity is finishing.
				return;
			}
			Log.i(_logTag, "Licensing Error: " + errorCode);
			showDialog(1);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			}, 10000);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		if (id == 0) {
			dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.unlicensed_dialog_title)
					.setMessage(R.string.unlicensed_dialog_body)
					.setPositiveButton(R.string.buy_button,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent marketIntent = new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("http://market.android.com/details?id="
													+ getPackageName()));
									startActivity(marketIntent);
								}
							})
					.setNegativeButton(R.string.quit_button,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).create();
		} else if (id == 1) {
			dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.error_dialog_title)
					.setMessage(R.string.error_dialog_body)
					.setPositiveButton(R.string.quit_button,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).create();
		}
		return dialog;
	}
}