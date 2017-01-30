package com.chemicalwedding.artemis;

import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;

public class ArtemisApplication extends Application {

	private final static String logTag = "ArtemisApplication";
    private Tracker mTracker;
    private WorkerThread worker;

    private static final String PROPERTY_ID = "UA-10781805-6";

    @Override
	public void onCreate() {
		Log.i(logTag, "Starting Artemis Application");

		super.onCreate();

		worker = new WorkerThread();
		worker.start();

		initLanguage();

        // Parse init:
		Parse.Configuration config = new Parse.Configuration.Builder(this)
				.server("https://thawing-basin-57979.herokuapp.com/parse")
				.applicationId("ez9BXRSkZpILFZIue7cg7peT4ZdsJ9LdODQD741L")
				.clientKey("kK6HYoIwSAySCqxgJQfUIhu0Kc0JyMrK315EZvBi")
				.build();
        Parse.initialize(config);
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

    synchronized Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(PROPERTY_ID);
            // Enable Display Features.
            mTracker.enableAdvertisingIdCollection(true);

            Thread.UncaughtExceptionHandler myHandler = new ExceptionReporter(
                    mTracker,                                        // Currently used Tracker.
                    Thread.getDefaultUncaughtExceptionHandler(),      // Current default uncaught exception handler.
                    getApplicationContext());                                         // Context of the application.

            // Make myHandler the new default uncaught exception handler.
            Thread.setDefaultUncaughtExceptionHandler(myHandler);
        }
        return mTracker;
    }
}
