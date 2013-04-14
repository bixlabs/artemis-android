package com.chemicalwedding.artemis;

import android.os.Handler;
import android.os.Looper;

public class WorkerThread extends Thread {
    public Handler mHandler;

    public void run() {
        Looper.prepare();

        mHandler = new Handler();

        Looper.loop();
    }
    
    public void stopThread() {
    	Looper.getMainLooper().quit();
    }
     
    public void post(Runnable r) {
    	mHandler.post(r);
    }
    
    public void postDelayed(Runnable r, long ms) {
    	mHandler.postDelayed(r, ms);
    }
}