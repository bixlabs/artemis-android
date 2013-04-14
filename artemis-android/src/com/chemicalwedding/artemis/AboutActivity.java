package com.chemicalwedding.artemis;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		initializeAboutActivity();
	}

	private void initializeAboutActivity() {
		((Button) findViewById(R.id.about_back))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

		((ImageView) findViewById(R.id.loon_films_logo))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.addCategory(Intent.CATEGORY_BROWSABLE);
						intent.setData(Uri
								.parse("http://loonfilms.com"));
						startActivity(intent);
					}
				});

		((ImageView) findViewById(R.id.chemical_wedding_logo))
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_VIEW);
						intent.addCategory(Intent.CATEGORY_BROWSABLE);
						intent.setData(Uri
								.parse("http://www.chemicalwedding.tv"));
						startActivity(intent);
					}
				});
		
		String about_title = getString(R.string.artemis_about_title);
		try {
		    PackageInfo manager=getPackageManager().getPackageInfo(getPackageName(), 0);
		    about_title += manager.versionName;
		} catch (NameNotFoundException e) {
		    //Handle exception
		}
		((TextView) findViewById(R.id.artemis_about_title)).setText(about_title);
	}
}