package com.chemicalwedding.artemis;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected boolean isValidFragment(String fragmentName) {
		if (fragmentName.equals(GeneralSettingsFragment.class.getName())
				|| fragmentName.equals(CameraSettingsFragment.class.getName())
				|| fragmentName.equals(SavedImageSettingsFragment.class
						.getName())
				|| fragmentName.equals(ResetDefaultSettingsFragment.class
						.getName())
				|| fragmentName.equals(SendFeedbackFragment.class.getName())) {
			return true;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
	}

	public static class GeneralSettingsFragment extends
			ArtemisPreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.general_settings_preferences);

			ListPreference headingPref = (ListPreference) findPreference(getString(R.string.preference_key_headingdisplay));
			Integer valIndex = getSharedPreferences().getInt(
					getString(R.string.preference_key_headingdisplay), 2);
			headingPref.setValue("" + valIndex);
			headingPref
					.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							Integer newValueInt = Integer
									.valueOf((String) newValue);
							getSharedPreferences()
									.edit()
									.putInt(getString(R.string.preference_key_headingdisplay),
											newValueInt).commit();
							ArtemisActivity.headingDisplaySelection = newValueInt;
							return true;
						}
					});

			ListPreference languagePref = (ListPreference) findPreference(getString(R.string.preference_key_selectedlanguage));

			// Setup language pref
			String[] codes = getResources().getStringArray(
					R.array.select_language_codes);
			String currentLangCode = Locale.getDefault().getLanguage();
			String selectedCode = "en";
			for (String code : codes) {
				if (code.equals(currentLangCode)) {
					selectedCode = code;
					break;
				}
			}
			languagePref.setValue(selectedCode);
			languagePref
					.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							String selectedLanguage = (String) newValue;
							String currentLanguage = getSharedPreferences()
									.getString(
											ArtemisPreferences.SELECTED_LANGUAGE,
											"");
							if (!selectedLanguage.equals(currentLanguage)) {
								getSharedPreferences()
										.edit()
										.putString(
												ArtemisPreferences.SELECTED_LANGUAGE,
												selectedLanguage).commit();
								((ArtemisApplication) getActivity()
										.getApplication()).initLanguage();
							}

							return true;
						}
					});

			if (!CameraPreview21.isAutoFocusSupported) {
				ListPreference volUp = ((ListPreference) findPreference(getString(R.string.preference_key_volumeUpAction)));
				volUp.setEntries(R.array.volumeAction_entries_noautofocus);
				volUp.setEntryValues(R.array.volumeAction_values_noautofocus);
				String defaultVal = getString(R.string.volumeUpAction_noautofocus_default);
				String val = getSharedPreferences().getString(
						getString(R.string.preference_key_volumeUpAction),
						defaultVal);
				if (!"nothing".equals(val)) {
					val = defaultVal;
				}
				volUp.setValue(val);

				ListPreference volDown = ((ListPreference) findPreference(getString(R.string.preference_key_volumeDownAction)));
				volDown.setEntries(R.array.volumeAction_entries_noautofocus);
				volDown.setEntryValues(R.array.volumeAction_values_noautofocus);
			}

		}

		@Override
		public void onPause() {
			super.onPause();
			CameraOverlay.lockBoxEnabled = getSharedPreferences().getBoolean(
					getString(R.string.preference_key_lockboxesenabled), false);
		}
	}

	public static class CameraSettingsFragment extends
			ArtemisPreferenceFragment {
		// Listener for changing the EditTextPreference title's on changing
		// angles
		private OnPreferenceChangeListener anglePreferenceChangeListener = new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				String newValueString = (String) newValue;
				Float newValueFloat = null;
				try {
					newValueFloat = Float.valueOf(newValueString);
				} catch (NumberFormatException nfe) {
					// Handle error
					AlertDialog dialog = new AlertDialog.Builder(getActivity())
							.create();
					dialog.setMessage("Invalid float value");
					dialog.show();
					return true;
				}
				// Set the title for the preference
				setAnglePreferenceTitle(preference, newValueString);

				// persist the value
				getSharedPreferences().edit()
						.putFloat(preference.getKey(), newValueFloat).commit();
				return true;
			}
		};

		private void setAnglePreferenceTitle(Preference preference,
				String newAngleString) {
			String currentTitle = preference.getTitle().toString();
			preference.setTitle(currentTitle.subSequence(0,
					currentTitle.indexOf('-') - 1)
					+ " - "
					+ newAngleString
					+ getString(R.string.degree_symbol));

		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.camera_settings_preferences);

			Float hAngleVal, vAngleVal;
			if (getSharedPreferences().getBoolean(
					getString(R.string.preference_key_automaticlensangles),
					true)) {
				hAngleVal = CameraPreview21.deviceHAngle;
				vAngleVal = CameraPreview21.deviceVAngle;
			} else {
				hAngleVal = getSharedPreferences().getFloat(
						getString(R.string.preference_key_cameralenshangle),
						CameraPreview21.deviceHAngle);
				vAngleVal = getSharedPreferences().getFloat(
						getString(R.string.preference_key_cameralensvangle),
						CameraPreview21.deviceVAngle);
			}

			final EditTextPreference hanglePref = (EditTextPreference) findPreference(getString(R.string.preference_key_cameralenshangle));
			hanglePref
					.setOnPreferenceChangeListener(anglePreferenceChangeListener);
			String hangle = NumberFormat.getNumberInstance().format(hAngleVal);
			hanglePref.setText("" + hangle);
			hanglePref.setTitle(getString(R.string.horizontal_lens_angle)
					+ " - " + hangle + getString(R.string.degree_symbol));

			final EditTextPreference vanglePref = (EditTextPreference) findPreference(getString(R.string.preference_key_cameralensvangle));
			vanglePref
					.setOnPreferenceChangeListener(anglePreferenceChangeListener);
			String vangle = NumberFormat.getNumberInstance().format(vAngleVal);
			vanglePref.setText("" + vangle);
			vanglePref.setTitle(getString(R.string.vertical_lens_angle) + " - "
					+ vangle + getString(R.string.degree_symbol));

			CheckBoxPreference autoAnglesPref = (CheckBoxPreference) findPreference(getString(R.string.preference_key_automaticlensangles));
			autoAnglesPref
					.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							Boolean autoAngles = (Boolean) newValue;
							if (autoAngles) {
								setAnglePreferenceTitle(
										hanglePref,
										NumberFormat
												.getNumberInstance()
												.format(CameraPreview21.deviceHAngle));
								setAnglePreferenceTitle(
										vanglePref,
										NumberFormat
												.getNumberInstance()
												.format(CameraPreview21.deviceVAngle));
								CameraPreview21.effectiveHAngle = CameraPreview21.deviceHAngle;
								CameraPreview21.effectiveVAngle = CameraPreview21.deviceVAngle;
							} else {
								CameraPreview21.effectiveHAngle = getSharedPreferences()
										.getFloat(
												getString(R.string.preference_key_cameralenshangle),
												CameraPreview21.deviceHAngle);
								CameraPreview21.effectiveVAngle = getSharedPreferences()
										.getFloat(
												getString(R.string.preference_key_cameralensvangle),
												CameraPreview21.deviceVAngle);
								setAnglePreferenceTitle(
										hanglePref,
										NumberFormat
												.getNumberInstance()
												.format(CameraPreview21.effectiveHAngle));
								setAnglePreferenceTitle(
										vanglePref,
										NumberFormat
												.getNumberInstance()
												.format(CameraPreview21.effectiveVAngle));
							}
							return true;
						}
					});


			ListPreference exposurePref = (ListPreference) findPreference(getString(R.string.preference_key_selectedexposurelevel));
			if (CameraPreview21.supportedExposureLevels != null) {
				CharSequence[] exposureValues = new CharSequence[CameraPreview21.supportedExposureLevels
						.size()];
				CharSequence[] exposureLabels = new CharSequence[CameraPreview21.supportedExposureLevels
						.size()];
				NumberFormat numberFormat = NumberFormat.getNumberInstance();
				numberFormat.setMinimumFractionDigits(2);

				int exposureLevel = getSharedPreferences()
						.getInt(getString(R.string.preference_key_selectedexposurelevel),
								0);
				CharSequence selectedValue = null;
				for (int i = 0; i < exposureValues.length; i++) {
					exposureValues[i] = CameraPreview21.supportedExposureLevels
							.get(i).toString();
					if (CameraPreview21.supportedExposureLevels.get(i) == exposureLevel) {
						selectedValue = exposureValues[i];

					}
					exposureLabels[i] = numberFormat
							.format(CameraPreview21.supportedExposureLevels
									.get(i) * CameraPreview21.exposureStep)
							+ " EV";
				}
				exposurePref.setEntries(exposureLabels);
				exposurePref.setEntryValues(exposureValues);

				exposurePref.setValue(selectedValue.toString());
				exposurePref
						.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
							@Override
							public boolean onPreferenceChange(
									Preference preference, Object newValue) {
								Integer newValueInt = Integer
										.parseInt((String) newValue);
								Log.d("Exposure", "Selected " + newValueInt);
								getSharedPreferences()
										.edit()
										.putInt(getString(R.string.preference_key_selectedexposurelevel),
												newValueInt).commit();

								return true;
							}
						});
			} else {
				exposurePref.setEnabled(false);
			}

			ListPreference focusPref = (ListPreference) findPreference(getString(R.string.preference_key_selectedfocusmode));
			if (CameraPreview21.availableAutoFocusModes != null) {
                String[] labelArray = getActivity().getResources().getStringArray(R.array.set_auto_focus_entries);
				CharSequence[] values = new CharSequence[CameraPreview21.availableAutoFocusModes
						.length];
                CharSequence[] entries = new CharSequence[CameraPreview21.availableAutoFocusModes
                        .length];
				for (int i = 0; i < values.length; i++) {
					values[i] = CameraPreview21.availableAutoFocusModes[i]+"";
                    entries[i] = labelArray[Integer.parseInt(""+values[i])];
				}
				focusPref.setEntries(entries);
				focusPref.setEntryValues(values);

//				focusPref
//						.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//							@Override
//							public boolean onPreferenceChange(
//									Preference preference, Object newValue) {
////								setAutoFocusOptionsState((String) newValue);
//								return true;
//							}
//						});
			} else {
				focusPref.setEnabled(false);
			}

            setAutoFocusOptionsState(focusPref.getValue());

            ListPreference whiteBalancePref = (ListPreference) findPreference(getString(R.string.preference_key_selectedwhitebalance));
            if (CameraPreview21.availableWhiteBalanceModes != null) {
                String[] labelArray = getActivity().getResources().getStringArray(R.array.set_white_balance_entries);
                CharSequence[] values = new CharSequence[CameraPreview21.availableWhiteBalanceModes.length];
                CharSequence[] entries = new CharSequence[CameraPreview21.availableWhiteBalanceModes.length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = CameraPreview21.availableWhiteBalanceModes[i]+"";
                    entries[i] = labelArray[Integer.parseInt(""+values[i])];
                }
                whiteBalancePref.setEntries(entries);
                whiteBalancePref.setEntryValues(values);

            } else {
				whiteBalancePref.setEnabled(false);
            }

			ListPreference sceneModePref = (ListPreference) findPreference(getString(R.string.preference_key_selectedscenemode));
			if (CameraPreview21.availableSceneModes != null
					&& CameraPreview21.availableSceneModes.size() > 0) {
                String[] labelArray = getActivity().getResources().getStringArray(R.array.set_scene_mode_entries);
				CharSequence[] values = new CharSequence[CameraPreview21.availableSceneModes.size()];
                CharSequence[] entries = new CharSequence[CameraPreview21.availableSceneModes.size()];
                int index = 0;
				for (Integer mode: CameraPreview21.availableSceneModes) {
					values[index] =  mode+"";
                    entries[index] = labelArray[Integer.parseInt(""+values[index])];
                    ++index;
				}
				sceneModePref.setEntries(entries);
				sceneModePref.setEntryValues(values);

			} else {
				sceneModePref.setEnabled(false);
			}

            ListPreference cameraEffectModePref = (ListPreference) findPreference(getString(R.string.preference_key_selectedCameraEffect));
            if (CameraPreview21.availableEffects != null
                    && CameraPreview21.availableEffects.length > 0) {
                String[] labelArray = getActivity().getResources().getStringArray(R.array.set_camera_effect_entries);
                CharSequence[] values = new CharSequence[CameraPreview21.availableEffects.length];
                CharSequence[] entries = new CharSequence[CameraPreview21.availableEffects.length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = CameraPreview21.availableEffects[i] + "";
                    entries[i] = labelArray[Integer.parseInt(""+values[i])];
                }
                cameraEffectModePref.setEntries(entries);
                cameraEffectModePref.setEntryValues(values);

            } else {
                cameraEffectModePref.setEnabled(false);
            }
		}

		private void setAutoFocusOptionsState(String val) {
			boolean enabled = false;
			if (Camera.Parameters.FOCUS_MODE_AUTO.equals(val)
					|| Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
							.equals(val)
					|| Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
							.equals(val)
					|| Camera.Parameters.FOCUS_MODE_MACRO.equals(val)) {
				enabled = true;
			}
//			findPreference(getString(R.string.preference_key_longpressshutter))
//					.setEnabled(enabled);
//			findPreference(
//					getString(R.string.preference_key_autofocusonpicture))
//					.setEnabled(enabled);

		}

		@Override
		public void onPause() {
			super.onPause();
			if (!getSharedPreferences().getBoolean(
					getString(R.string.preference_key_automaticlensangles),
					true)) {

				CameraPreview21.effectiveHAngle = getSharedPreferences()
						.getFloat(
								getString(R.string.preference_key_cameralenshangle),
								CameraPreview21.deviceHAngle);
				CameraPreview21.effectiveVAngle = getSharedPreferences()
						.getFloat(
								getString(R.string.preference_key_cameralensvangle),
								CameraPreview21.deviceVAngle);

				ArtemisMath.getInstance().setCustomViewAngle(
						CameraPreview21.effectiveHAngle,
						CameraPreview21.effectiveVAngle);
			} else {
				ArtemisMath.getInstance().setCustomViewAngle(
						CameraPreview21.deviceHAngle,
						CameraPreview21.deviceVAngle);
			}
		}
	}

	public static class SavedImageSettingsFragment extends
			ArtemisPreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.saved_image_settings_preferences);

			findPreference(getString(R.string.preference_key_artemissavefolder))
					.setOnPreferenceChangeListener(
							new OnPreferenceChangeListener() {

								@Override
								public boolean onPreferenceChange(
										Preference preference, Object newValue) {

									getSharedPreferences()
											.edit()
											.putString(preference.getKey(),
													(String) newValue).commit();

									ArtemisActivity.savePictureFolder = Environment
											.getExternalStorageDirectory()
											.getAbsolutePath().toString()
											+ "/";
									ArtemisActivity.savePictureFolder += (String) newValue;

									File file = new File(
											ArtemisActivity.savePictureFolder);
									if (!file.exists())
										file.mkdirs();
									return true;
								}
							});

            ListPreference savedImageSizePref = (ListPreference) findPreference(getString(R.string.preference_key_savedImageSize));
            if (CameraPreview21.availablePictureSizes != null) {
                CharSequence[] entries = new CharSequence[CameraPreview21.availablePictureSizes
                        .length];
                CharSequence[] values = new CharSequence[CameraPreview21.availablePictureSizes
                        .length];
                for (int i = 0; i < entries.length; i++) {
                    entries[i] = CameraPreview21.availablePictureSizes[i].toString();
                    values[i] = i+"";
                }
                savedImageSizePref.setEntries(entries);
                savedImageSizePref.setEntryValues(values);
            }
        }

		@Override
		public void onPause() {
			super.onPause();

		}
	}

	public static class ResetDefaultSettingsFragment extends
			ArtemisPreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setMessage(R.string.reset_to_default_settings)
					.setTitle(R.string.reset_artemis_settings)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									SharedPreferences settings = getActivity()
											.getSharedPreferences(
													ArtemisPreferences.class
															.getSimpleName(),
													MODE_PRIVATE);
									settings.edit().clear().commit();
									getActivity().finish();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									getActivity().finish();
								}
							}).create();
			dialog.show();

		}
	}

	public static class SendFeedbackFragment extends ArtemisPreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
					Uri.fromParts("mailto", "android@chemicalwedding.tv", null));
			startActivity(Intent.createChooser(emailIntent,
					getString(R.string.email_support)));
			getActivity().finish();
		}
	}

}
