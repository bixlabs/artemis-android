package com.chemicalwedding.artemis;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class ArtemisSettingsActivity extends Activity {

	private static final String logTag = "ArtemisSettingsActivity";
	protected static List<Integer> supportedExposureLevels;
	protected static List<String> supportedWhiteBalance, supportedFlashModes;
	protected static List<Size> supportedPreviewSizes;
	protected static boolean lockBoxEnabled = false, quickshotEnabled = false,
			smoothImagesEnabled = false;
	protected static float deviceHAngle, effectiveHAngle, deviceVAngle,
			effectiveVAngle;
	protected static boolean autoFocusBeforePictureTake = false;
	protected static Size previewSize;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artemis_settings);
		initializeArtemisSettings();
	}

	/*
	 * Init the artemis settings view
	 */
	private void initializeArtemisSettings() {
		Log.i(logTag, "Initialize Artemis Settings");

		final SharedPreferences artemisPrefs = getApplication()
				.getSharedPreferences(ArtemisPreferences.class.getSimpleName(),
						MODE_PRIVATE);

		boolean artemisGpsSetting = artemisPrefs.getBoolean(
				ArtemisPreferences.GPS_ENABLED, true);
		((ToggleButton) findViewById(R.id.settings_gps_enabled))
				.setChecked(artemisGpsSetting);

		if (ArtemisSettingsActivity.supportedExposureLevels != null
				&& !ArtemisSettingsActivity.supportedExposureLevels.isEmpty()) {
			ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
					android.R.layout.simple_spinner_item,
					ArtemisSettingsActivity.supportedExposureLevels);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Spinner exposureSpinner = (Spinner) findViewById(R.id.settings_exposure_spinner);
			exposureSpinner.setAdapter(adapter);
			if (ArtemisSettingsActivity.supportedExposureLevels.size() > 0) {
				int index = ArtemisSettingsActivity.supportedExposureLevels
						.size() / 2;
				int level = artemisPrefs.getInt(
						ArtemisPreferences.SELECTED_EXPOSURE_LEVEL, 0);
				int i = 0;
				for (Integer expo : ArtemisSettingsActivity.supportedExposureLevels) {
					if (expo.equals(level)) {
						index = i;
						break;
					}
					i++;
				}
				exposureSpinner.setSelection(index, true);
			}
		}
		if (ArtemisSettingsActivity.supportedWhiteBalance != null
				&& !ArtemisSettingsActivity.supportedWhiteBalance.isEmpty()) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item,
					ArtemisSettingsActivity.supportedWhiteBalance);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Spinner whiteBalanceSpinner = (Spinner) findViewById(R.id.settings_white_balance_spinner);
			whiteBalanceSpinner.setAdapter(adapter);
			String whiteBalance = artemisPrefs.getString(
					ArtemisPreferences.SELECTED_WHITE_BALANCE, "");
			int wbIndex = 0;
			for (String wb : ArtemisSettingsActivity.supportedWhiteBalance) {
				if (wb.equals(whiteBalance)) {
					whiteBalanceSpinner.setSelection(wbIndex, true);
					break;
				}
				++wbIndex;
			}

		}
		Log.i(logTag, "Supported Flash Modes "
				+ ArtemisSettingsActivity.supportedFlashModes == null ? "null"
				: "not null");
		if (ArtemisSettingsActivity.supportedFlashModes != null) {
			Log.i(logTag, "Supported Flash Modes Size: "
					+ ArtemisSettingsActivity.supportedFlashModes.size());
			Log.i(logTag,
					"Supported Flash Modes Contains torch: "
							+ (ArtemisSettingsActivity.supportedFlashModes
									.contains("torch") ? "true" : "false"));
		}
		if (ArtemisSettingsActivity.supportedFlashModes != null
				&& ArtemisSettingsActivity.supportedFlashModes
						.contains("torch")) {

			ToggleButton flashModeSpinner = (ToggleButton) findViewById(R.id.set_flash_mode_spinner);
			flashModeSpinner.setEnabled(true);
			boolean flashSetting = artemisPrefs.getBoolean(
					ArtemisPreferences.FLASH_ENABLED, false);
			flashModeSpinner.setChecked(flashSetting);
		}

		if (ArtemisSettingsActivity.supportedPreviewSizes != null) {
			ArrayList<String> stringSizes = new ArrayList<String>();
			int index = -1;
			int currentPreviewIndex = -1;
			for (Size size : ArtemisSettingsActivity.supportedPreviewSizes) {
				++index;
				stringSizes.add(size.width + " x " + size.height);
				if (size.equals(ArtemisSettingsActivity.previewSize)) {
					currentPreviewIndex = index;
				}
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, stringSizes);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Spinner previewSizeSpinner = (Spinner) findViewById(R.id.set_preview_size_spinner);
			previewSizeSpinner.setAdapter(adapter);
			if (currentPreviewIndex >= 0)
				previewSizeSpinner.setSelection(currentPreviewIndex, true);
		}
		((ToggleButton) findViewById(R.id.quickshot_toggle))
				.setChecked(artemisPrefs.getBoolean(
						ArtemisPreferences.QUICKSHOT_ENABLED, false));
		((ToggleButton) findViewById(R.id.lock_boxes_toggle))
				.setChecked(artemisPrefs.getBoolean(
						ArtemisPreferences.LOCK_BOXES_ENABLED, false));
		((ToggleButton) findViewById(R.id.smooth_filter_toggle))
				.setChecked(artemisPrefs.getBoolean(
						ArtemisPreferences.SMOOTH_IMAGE_ENABLED, true));

		((Spinner) findViewById(R.id.heading_tilt_roll_spinner)).setSelection(
				artemisPrefs.getInt(ArtemisPreferences.HEADING_DISPLAY, 2),
				true);

		((Button) findViewById(R.id.save_artemis_settings))
				.setOnClickListener(saveClickListener);

		((Button) findViewById(R.id.cancel_artemis_settings))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

		String[] codes = getResources().getStringArray(
				R.array.select_language_codes);
		String currentLangCode = Locale.getDefault().getLanguage();
		int selectedIndex = -1, index = 0;
		int enIndex = 0;
		for (String code : codes) {
			if (code.equals(currentLangCode)) {
				selectedIndex = index;
				break;
			} else if (code.equals("en")) {
				enIndex = index;
			}
			++index;
		}
		if (selectedIndex > -1) {
			((Spinner) findViewById(R.id.select_language_spinner))
					.setSelection(selectedIndex, true);
		} else {
			((Spinner) findViewById(R.id.select_language_spinner))
					.setSelection(enIndex, true);
		}

		boolean useAutomaticLensAngleDetection = artemisPrefs.getBoolean(
				ArtemisPreferences.AUTOMATIC_LENS_ANGLES, true);
		((ToggleButton) findViewById(R.id.auto_device_h_and_v_angles_toggle))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						initHAngleVAnglePreferences(artemisPrefs, isChecked);
					}
				});
		((Button) findViewById(R.id.reset_device_angles))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						((EditText) findViewById(R.id.edit_h_angle_textedit))
								.setText(""
										+ ArtemisSettingsActivity.deviceHAngle);
						((EditText) findViewById(R.id.edit_v_angle_textedit))
								.setText(""
										+ ArtemisSettingsActivity.deviceVAngle);
					}
				});

		boolean useAutoFocusOnPicture = artemisPrefs.getBoolean(
				ArtemisPreferences.AUTO_FOCUS_ON_PICTURE, false);
		((ToggleButton) findViewById(R.id.auto_focus_toggle))
				.setChecked(useAutoFocusOnPicture);

		initHAngleVAnglePreferences(artemisPrefs,
				useAutomaticLensAngleDetection);

		String prefix = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/";
		String folder = artemisPrefs.getString(
				ArtemisPreferences.SAVE_PICTURE_FOLDER, prefix + "Artemis");
		Log.d(logTag, "Setting Folder: " + folder);
		Log.d(logTag, "Evironment Folder: " + prefix);
		int folderPrefix = folder.indexOf(prefix);
		String suffix = folder.substring(folderPrefix + prefix.length());
		((EditText) findViewById(R.id.choose_path)).setText(suffix);
	}

	private void initHAngleVAnglePreferences(SharedPreferences artemisPrefs,
			boolean isChecked) {
		if (isChecked) {
			((ToggleButton) findViewById(R.id.auto_device_h_and_v_angles_toggle))
					.setChecked(true);
			float hAngle = ArtemisSettingsActivity.deviceHAngle;
			EditText editHAngle = (EditText) findViewById(R.id.edit_h_angle_textedit);
			editHAngle.setEnabled(false);
			// editHAngle.requestFocus(View.FOCUS_DOWN);
			editHAngle.setFocusable(false);
			editHAngle.setText(NumberFormat.getNumberInstance().format(hAngle));

			float vAngle = ArtemisSettingsActivity.deviceVAngle;
			EditText editVAngle = (EditText) findViewById(R.id.edit_v_angle_textedit);
			editVAngle.setEnabled(false);
			// editVAngle.requestFocus(View.FOCUS_DOWN);
			editVAngle.setFocusable(false);
			editVAngle.setText(NumberFormat.getNumberInstance().format(vAngle));
			((Button) findViewById(R.id.reset_device_angles)).setEnabled(false);

		} else {
			((ToggleButton) findViewById(R.id.auto_device_h_and_v_angles_toggle))
					.setChecked(false);
			float hAngle = artemisPrefs.getFloat(
					ArtemisPreferences.CAMERA_LENS_H_ANGLE,
					ArtemisSettingsActivity.effectiveHAngle);
			EditText editHAngle = (EditText) findViewById(R.id.edit_h_angle_textedit);
			editHAngle.setFocusable(true);
			editHAngle.setFocusableInTouchMode(true);
			editHAngle.setEnabled(true);
			editHAngle.setText(NumberFormat.getNumberInstance().format(hAngle));

			float vAngle = artemisPrefs.getFloat(
					ArtemisPreferences.CAMERA_LENS_V_ANGLE,
					ArtemisSettingsActivity.effectiveVAngle);
			EditText editVAngle = (EditText) findViewById(R.id.edit_v_angle_textedit);
			editVAngle.setFocusable(true);
			editVAngle.setFocusableInTouchMode(true);
			editVAngle.setEnabled(true);
			editVAngle.setText(NumberFormat.getNumberInstance().format(vAngle));

			((Button) findViewById(R.id.reset_device_angles)).setEnabled(true);
		}

	}

	OnClickListener saveClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(logTag, "Saving Artemis Settings");
			boolean artemisGpsSetting = ((ToggleButton) findViewById(R.id.settings_gps_enabled))
					.isChecked();

			SharedPreferences artemisPrefs = getApplication()
					.getSharedPreferences(
							ArtemisPreferences.class.getSimpleName(),
							MODE_PRIVATE);

			Editor editor = artemisPrefs.edit();

			editor.putBoolean(ArtemisPreferences.GPS_ENABLED, artemisGpsSetting);
			// if (artemisGpsSetting != gpsEnabled) {
			// gpsEnabled = artemisGpsSetting;
			// if (gpsEnabled) {
			// initLocationManager();
			// } else if (locationManager != null) {
			// locationManager.removeUpdates(locationListener);
			// locationManager = null;
			// }
			// }

			if (!ArtemisSettingsActivity.supportedExposureLevels.isEmpty()) {
				Integer selected = (Integer) ((Spinner) findViewById(R.id.settings_exposure_spinner))
						.getSelectedItem();
				editor.putInt(ArtemisPreferences.SELECTED_EXPOSURE_LEVEL,
						selected);
			}
			if (!ArtemisSettingsActivity.supportedWhiteBalance.isEmpty()) {
				int selectedPos = ((Spinner) findViewById(R.id.settings_white_balance_spinner))
						.getSelectedItemPosition();
				String whiteBalance = ArtemisSettingsActivity.supportedWhiteBalance
						.get(selectedPos);
				editor.putString(ArtemisPreferences.SELECTED_WHITE_BALANCE,
						whiteBalance);
			}

			if (ArtemisSettingsActivity.supportedFlashModes != null
					&& ArtemisSettingsActivity.supportedFlashModes
							.contains("torch")) {
				ToggleButton flashModeSpinner = (ToggleButton) findViewById(R.id.set_flash_mode_spinner);
				boolean isEnabled = flashModeSpinner.isChecked();
				editor.putBoolean(ArtemisPreferences.FLASH_ENABLED, isEnabled);
			}

			boolean quickshotSetting = ((ToggleButton) findViewById(R.id.quickshot_toggle))
					.isChecked();
			editor.putBoolean(ArtemisPreferences.QUICKSHOT_ENABLED,
					quickshotSetting);
			ArtemisSettingsActivity.quickshotEnabled = quickshotSetting;

			boolean lockboxSetting = ((ToggleButton) findViewById(R.id.lock_boxes_toggle))
					.isChecked();
			editor.putBoolean(ArtemisPreferences.LOCK_BOXES_ENABLED,
					lockboxSetting);
			ArtemisSettingsActivity.lockBoxEnabled = lockboxSetting;
			CameraOverlay.lockBoxEnabled = lockboxSetting;

			boolean smoothImageSetting = ((ToggleButton) findViewById(R.id.smooth_filter_toggle))
					.isChecked();
			editor.putBoolean(ArtemisPreferences.SMOOTH_IMAGE_ENABLED,
					smoothImageSetting);
			ArtemisSettingsActivity.smoothImagesEnabled = smoothImageSetting;

			int selectedHeadingIndex = ((Spinner) findViewById(R.id.heading_tilt_roll_spinner))
					.getSelectedItemPosition();
			ArtemisActivity.headingDisplaySelection = selectedHeadingIndex;
			editor.putInt(ArtemisPreferences.HEADING_DISPLAY,
					selectedHeadingIndex);

			// preview size
			int selectedIndex = ((Spinner) findViewById(R.id.set_preview_size_spinner))
					.getSelectedItemPosition();
			Size selectedPreviewSize = ArtemisSettingsActivity.supportedPreviewSizes
					.get(selectedIndex);
			if (!selectedPreviewSize
					.equals(ArtemisSettingsActivity.previewSize)) {
				editor.putInt(ArtemisPreferences.PREVIEW_SELECTION,
						selectedIndex);
				ArtemisSettingsActivity.previewSize = selectedPreviewSize;
			}

			int selectedLanguageIndex = ((Spinner) findViewById(R.id.select_language_spinner))
					.getSelectedItemPosition();
			String codes[] = getResources().getStringArray(
					R.array.select_language_codes);
			String selectedLanguage = codes[selectedLanguageIndex];
			String currentLanguage = artemisPrefs.getString(
					ArtemisPreferences.SELECTED_LANGUAGE, "");
			if (!selectedLanguage.equals(currentLanguage)) {
				editor.putString(ArtemisPreferences.SELECTED_LANGUAGE,
						selectedLanguage);
				Locale locale = new Locale(selectedLanguage);
				Locale.setDefault(locale);
				Configuration config = new Configuration();
				config.locale = locale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());
				ArtemisActivity.languageChanged = true;
			}

			boolean automaticDeviceLensAngle = ((ToggleButton) findViewById(R.id.auto_device_h_and_v_angles_toggle))
					.isChecked();
			editor.putBoolean(ArtemisPreferences.AUTOMATIC_LENS_ANGLES,
					automaticDeviceLensAngle);

			float hAngle = ArtemisSettingsActivity.deviceHAngle;
			float vAngle = ArtemisSettingsActivity.deviceVAngle;
			try {
				float tempHAngle = Float
						.parseFloat(((EditText) findViewById(R.id.edit_h_angle_textedit))
								.getText().toString());
				float tempVAngle = Float
						.parseFloat(((EditText) findViewById(R.id.edit_v_angle_textedit))
								.getText().toString());
				if (tempHAngle > 0 && tempHAngle < 180 && tempVAngle > 0
						&& tempVAngle < 180) {
					hAngle = tempHAngle;
					vAngle = tempVAngle;
				}
			} catch (NumberFormatException nfe) {
			}

			editor.putFloat(ArtemisPreferences.CAMERA_LENS_H_ANGLE, hAngle);
			editor.putFloat(ArtemisPreferences.CAMERA_LENS_V_ANGLE, vAngle);
			ArtemisSettingsActivity.effectiveHAngle = hAngle;
			ArtemisSettingsActivity.effectiveVAngle = vAngle;
			ArtemisMath.getInstance().setCustomViewAngle(hAngle, vAngle);

			ArtemisMath.getInstance().calculateLargestLens();
			ArtemisMath.getInstance().calculateRectBoxesAndLabelsForLenses();
			ArtemisMath.getInstance().selectFirstMeaningFullLens();
			ArtemisMath.getInstance().calculateRectBoxesAndLabelsForLenses();

			String prefix = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/";
			String suffix = ((EditText) findViewById(R.id.choose_path))
					.getText().toString();
			if (!ArtemisActivity.savePictureFolder.equals(prefix + suffix)) {
				// try creating the new folder
				File newFolder = new File(prefix + suffix);
				boolean createdOrExists = false;
				if (newFolder.exists()) {
					createdOrExists = true;
				} else if (newFolder.mkdirs()) {
					createdOrExists = true;
				}

				if (createdOrExists) {
					// save the new save image folder selection
					editor.putString(ArtemisPreferences.SAVE_PICTURE_FOLDER,
							prefix + suffix);
					ArtemisActivity.savePictureFolder = prefix + suffix;
				}
			}

			// autofocus before picture
			boolean autoFocusOnPicture = ((ToggleButton) findViewById(R.id.auto_focus_toggle))
					.isChecked();
			ArtemisSettingsActivity.autoFocusBeforePictureTake = autoFocusOnPicture;
			editor.putBoolean(ArtemisPreferences.AUTO_FOCUS_ON_PICTURE,
					autoFocusOnPicture);

			editor.commit();

			finish();
		}
	};
}