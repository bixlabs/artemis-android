package com.chemicalwedding.artemis;

import android.app.Application;
import android.util.Log;

public class ArtemisApplication extends Application {

	private final static String logTag = "ArtemisApplication";
	private WorkerThread worker;

	@Override
	public void onCreate() {
		Log.i(logTag, "Starting Artemis Application");

		super.onCreate();

		worker = new WorkerThread();
		worker.start();
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
