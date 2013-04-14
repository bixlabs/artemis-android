package com.chemicalwedding.artemis;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class ArtemisPreferenceFragment extends PreferenceFragment {

	private SharedPreferences mSharedPreferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the preference file name to be the same as the legacy ArtemisSettingsActivity
		getPreferenceManager().setSharedPreferencesName(ArtemisPreferences.class.getSimpleName());
		mSharedPreferences = getPreferenceManager().getSharedPreferences();
	}
	
	public SharedPreferences getSharedPreferences(){
		return mSharedPreferences;
	}
}
