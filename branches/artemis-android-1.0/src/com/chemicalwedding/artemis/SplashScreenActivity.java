package com.chemicalwedding.artemis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
 
public class SplashScreenActivity extends Activity {
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.splash);

      Thread splashThread = new Thread() {
         @Override
         public void run() {
            try {
               int waited = 0;
               while (waited < 800) {
                  sleep(100);
                  waited += 100;
               }
            } catch (InterruptedException e) {
               // do nothing
            } finally {
               Intent i = new Intent();
               i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
               i.setClass(getBaseContext(), ArtemisActivity.class);
               startActivity(i);
               finish();
            }
         }
      };
      splashThread.start();
   }
   
   @Override
	protected void onDestroy() {
		super.onDestroy();
		
		ImageView splashscreen = (ImageView)findViewById(R.id.splashscreen);
		splashscreen.setImageDrawable(null);
		splashscreen.setBackgroundDrawable(null);
	}
}