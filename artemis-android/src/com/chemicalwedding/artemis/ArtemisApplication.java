package com.chemicalwedding.artemis;

import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import com.parse.Parse;

//import com.parse.Parse;

public class ArtemisApplication extends Application {

	private final static String logTag = "ArtemisApplication";
	private WorkerThread worker;

	@Override
	public void onCreate() {
		Log.i(logTag, "Starting Artemis Application");

		super.onCreate();

		worker = new WorkerThread();
		worker.start();

		initLanguage();

        // Parse init:
        Parse.initialize(this, "ez9BXRSkZpILFZIue7cg7peT4ZdsJ9LdODQD741L", "kK6HYoIwSAySCqxgJQfUIhu0Kc0JyMrK315EZvBi");
	}

	protected void initLanguage() {
		Log.v(logTag, "Initializing language");
		SharedPreferences settings = this.getSharedPreferences(
				ArtemisPreferences.class.getSimpleName(), Context.MODE_PRIVATE);

		Configuration config = getBaseContext().getResources()
				.getConfiguration();

		String lang = settings.getString(
				getString(R.string.preference_key_selectedlanguage), "");
		if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
			locale = new Locale(lang);
			Locale.setDefault(locale);
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());
		}

	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		worker.stopThread();
	}

	public void postOnWorkerThread(Runnable r) {
		worker.post(r);
	}

	public void postDelayedOnWorkerThread(Runnable r, long ms) {
		worker.postDelayed(r, ms);
	}

	private Locale locale = null;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (locale != null) {
			newConfig.locale = locale;
			Locale.setDefault(locale);
			getBaseContext().getResources().updateConfiguration(newConfig,
					getBaseContext().getResources().getDisplayMetrics());
		}
	}

}
