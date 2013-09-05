package com.chemicalwedding.artemis;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

            if (!CameraPreview14.isAutoFocusSupported) {
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
                hAngleVal = CameraPreview14.deviceHAngle;
                vAngleVal = CameraPreview14.deviceVAngle;
            } else {
                hAngleVal = getSharedPreferences().getFloat(
                        getString(R.string.preference_key_cameralenshangle),
                        CameraPreview14.deviceHAngle);
                vAngleVal = getSharedPreferences().getFloat(
                        getString(R.string.preference_key_cameralensvangle),
                        CameraPreview14.deviceVAngle);
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
                                                .format(CameraPreview14.deviceHAngle));
                                setAnglePreferenceTitle(
                                        vanglePref,
                                        NumberFormat
                                                .getNumberInstance()
                                                .format(CameraPreview14.deviceVAngle));
                                CameraPreview14.effectiveHAngle = CameraPreview14.deviceHAngle;
                                CameraPreview14.effectiveVAngle = CameraPreview14.deviceVAngle;
                            } else {
                                CameraPreview14.effectiveHAngle = getSharedPreferences()
                                        .getFloat(
                                                getString(R.string.preference_key_cameralenshangle),
                                                CameraPreview14.deviceHAngle);
                                CameraPreview14.effectiveVAngle = getSharedPreferences()
                                        .getFloat(
                                                getString(R.string.preference_key_cameralensvangle),
                                                CameraPreview14.deviceVAngle);
                                setAnglePreferenceTitle(
                                        hanglePref,
                                        NumberFormat
                                                .getNumberInstance()
                                                .format(CameraPreview14.effectiveHAngle));
                                setAnglePreferenceTitle(
                                        vanglePref,
                                        NumberFormat
                                                .getNumberInstance()
                                                .format(CameraPreview14.effectiveVAngle));
                            }
                            return true;
                        }
                    });

            // Camera white balance
            ListPreference whiteBalancePref = (ListPreference) findPreference(getString(R.string.preference_key_selectedwhitebalance));
            if (CameraPreview14.supportedWhiteBalance != null) {
                CharSequence[] values = new CharSequence[CameraPreview14.supportedWhiteBalance
                        .size()];
                for (int i = 0; i < values.length; i++) {
                    values[i] = CameraPreview14.supportedWhiteBalance.get(i);
                }
                whiteBalancePref.setEntries(values);
                whiteBalancePref.setEntryValues(values);
                whiteBalancePref
                        .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                            @Override
                            public boolean onPreferenceChange(
                                    Preference preference, Object newValue) {
                                String newValueString = (String) newValue;
                                getSharedPreferences()
                                        .edit()
                                        .putString(
                                                getString(R.string.preference_key_selectedwhitebalance),
                                                newValueString);
                                return true;
                            }
                        });
            }
            ListPreference exposurePref = (ListPreference) findPreference(getString(R.string.preference_key_selectedexposurelevel));
            if (CameraPreview14.supportedExposureLevels != null) {
                CharSequence[] exposureValues = new CharSequence[CameraPreview14.supportedExposureLevels
                        .size()];
                CharSequence[] exposureLabels = new CharSequence[CameraPreview14.supportedExposureLevels
                        .size()];
                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMinimumFractionDigits(2);

                int defaultVal = 0;
                for (int i = 0; i < exposureValues.length; i++) {
                    exposureValues[i] = CameraPreview14.supportedExposureLevels
                            .get(i).toString();
                    if (exposureValues[i].equals("0")) {
                        defaultVal = i;
                    }
                    exposureLabels[i] =
                            numberFormat.format(CameraPreview14.supportedExposureLevels
                                    .get(i) * CameraPreview14.exposureStep) + " eV";
                }
                exposurePref.setEntries(exposureLabels);
                exposurePref.setEntryValues(exposureValues);

                int exposureIndex = getSharedPreferences()
                        .getInt(getString(R.string.preference_key_selectedexposurelevel),
                                defaultVal);

                exposurePref.setValue(CameraPreview14.supportedExposureLevels
                        .get(exposureIndex) + "");
//                exposurePref
//                        .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//                            @Override
//                            public boolean onPreferenceChange(
//                                    Preference preference, Object newValue) {
//                                Integer newValueInt = Integer
//                                        .parseInt((String) newValue);
//                                        getSharedPreferences()
//                                                .edit()
//                                                .putInt(getString(R.string.preference_key_selectedexposurelevel),
//                                                        index).commit();
//                                    }
//                                    index++;
//                                }
//
//                                return true;
//                            }
//                        });
            }
            if (CameraPreview14.isAutoFocusSupported) {
                findPreference(
                        getString(R.string.preference_key_longpressshutter))
                        .setEnabled(true);
                findPreference(
                        getString(R.string.preference_key_autofocusonpicture))
                        .setEnabled(true);
            } /*else {
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
			}*/
        }

        @Override
        public void onPause() {
            super.onPause();
            if (!getSharedPreferences().getBoolean(
                    getString(R.string.preference_key_automaticlensangles),
                    true)) {
                CameraPreview14.effectiveHAngle = getSharedPreferences()
                        .getFloat(
                                getString(R.string.preference_key_cameralenshangle),
                                CameraPreview14.deviceHAngle);
                CameraPreview14.effectiveVAngle = getSharedPreferences()
                        .getFloat(
                                getString(R.string.preference_key_cameralensvangle),
                                CameraPreview14.deviceVAngle);
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
