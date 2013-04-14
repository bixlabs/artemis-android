package com.chemicalwedding.artemis;

import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

public class ArtemisApplication extends Application {

	private final static String logTag = "ArtemisApplication";
	private WorkerThread worker;

	static Locale locale = null;

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

	@Override
	public void onCreate() {
		Log.i(logTag, "Starting Artemis Application");

		super.onCreate();

		worker = new WorkerThread();
		worker.start();

		SharedPreferences settings = this.getSharedPreferences(
				ArtemisPreferences.class.getSimpleName(), MODE_PRIVATE);

		Configuration config = getBaseContext().getResources()
				.getConfiguration();

		String lang = settings.getString(ArtemisPreferences.SELECTED_LANGUAGE,
				"");
		if (!"".equals(lang)) {
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
}
