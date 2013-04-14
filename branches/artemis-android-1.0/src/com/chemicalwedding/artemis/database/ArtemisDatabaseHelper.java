package com.chemicalwedding.artemis.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

public class ArtemisDatabaseHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 5;

	private SQLiteDatabase _artemisDatabase;

	private final Context _context;
	private final static String TAG = "ArtemisDatabaseHelper";
	private final static String DB_NAME = "artemisdb";
	
	public ArtemisDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this._context = context;
		_artemisDatabase = this.getWritableDatabase();
	}

	@Override
	public synchronized void close() {
		if (_artemisDatabase != null)
			_artemisDatabase.close();
		super.close();
	}

	/*
	 * Camera database functions
	 */

	public ArrayList<String> getCameraFormats() {
		// get distinct camera formats
		Cursor cursor = _artemisDatabase.query(true, "zcameras",
				new String[] { "zformat" }, null, null, null, null, null, null);
		ArrayList<String> cameraFormats = new ArrayList<String>();
		while (cursor.moveToNext()) {
			cameraFormats.add(cursor.getString(0));
		}
		cursor.close();
		return cameraFormats;
	}

	public ArrayList<CustomCamera> getCustomCameras() {
		// get distinct camera formats
		Cursor cursor = null;
		try {
			cursor = _artemisDatabase.query(true, "custom_cameras",
					new String[] { "label", "horiz", "vertical", "FL",
							"distance", "width", "height", "rowid" }, null,
					null, null, null, null, null);
		} catch (SQLiteException sle) {

		}
		ArrayList<CustomCamera> cameras = new ArrayList<CustomCamera>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				CustomCamera c = new CustomCamera();
				c.setLabel(cursor.getString(0));
				c.setHoriz(cursor.getFloat(1));
				c.setVertical(cursor.getFloat(2));
				c.setFL(cursor.getFloat(3));
				c.setDistance(cursor.getFloat(4));
				c.setWidth(cursor.getFloat(5));
				c.setHeight(cursor.getFloat(6));
				c.setRowid(cursor.getInt(7));
				cameras.add(c);
			}
			cursor.close();
		}
		return cameras;
	}

	public ArrayList<String> getCameraSensorsForFormat(String format) {
		// get distinct camera sensors for format
		Cursor cursor = _artemisDatabase.query(true, "zcameras",
				new String[] { "zsensor" }, "zformat = ?",
				new String[] { format }, null, null, null, null);
		ArrayList<String> cameraSensors = new ArrayList<String>();
		while (cursor.moveToNext()) {
			cameraSensors.add(cursor.getString(0));
		}
		cursor.close();
		return cameraSensors;
	}

	public ArrayList<Pair<Integer, String>> getCameraRatiosForSensor(
			String sensor) {
		// get distinct camera ratios for sensor, return each ratio paired with
		// rowid
		Cursor cursor = _artemisDatabase.query(true, "zcameras", new String[] {
				"zrowid", "zratio" }, "zsensor = ?", new String[] { sensor },
				null, null, null, null);
		ArrayList<Pair<Integer, String>> cameraRatios = new ArrayList<Pair<Integer, String>>();
		while (cursor.moveToNext()) {
			Pair<Integer, String> data = new Pair<Integer, String>(
					cursor.getInt(0), cursor.getString(1));
			cameraRatios.add(data);
		}
		cursor.close();
		return cameraRatios;
	}

	public Camera getCameraDetailsForRowId(Integer rowid) {
		// get camera details for the camera rowid (index)
		Cursor cursor = _artemisDatabase.query("zcameras", new String[] {
				"zhorizontal", "zvertical", "zsqz", "zsensor", "zratio",
				"zlenses" }, "zrowid = ?", new String[] { rowid.toString() },
				null, null, null, null);
		cursor.moveToFirst();
		Camera camera = new Camera();
		camera.setHoriz(cursor.getFloat(0));
		camera.setVertical(cursor.getFloat(1));
		camera.setSqz(cursor.getFloat(2));
		camera.setSensor(cursor.getString(3));
		camera.setRatio(cursor.getString(4));
		camera.setLenses(cursor.getString(5));
		camera.setRowid(rowid);
		cursor.close();
		return camera;
	}

	public CustomCamera getCustomCameraDetailsForRowId(Integer rowid) {
		// get camera details for the camera rowid (index)
		Cursor cursor = _artemisDatabase.query("custom_cameras", new String[] {
				"label", "horiz", "vertical", "FL", "distance", "width",
				"height", "rowid" }, "rowid = ?",
				new String[] { rowid.toString() }, null, null, null, null);
		cursor.moveToFirst();
		CustomCamera camera = new CustomCamera();
		camera.setLabel(cursor.getString(0));
		camera.setHoriz(cursor.getFloat(1));
		camera.setVertical(cursor.getFloat(2));
		camera.setFL(cursor.getFloat(3));
		camera.setDistance(cursor.getFloat(4));
		camera.setWidth(cursor.getFloat(5));
		camera.setHeight(cursor.getFloat(6));
		camera.setRowid(cursor.getInt(7));
		cursor.close();
		return camera;
	}

	/*
	 * Lens database functions
	 */

	public ArrayList<Lens> getLensesForMake(String make) {
		// Get all the lenses for the specified make
		Cursor cursor = _artemisDatabase.query("zlenses", new String[] {
				"zlensSet", "zFL", "zrowid" }, "zLensMake = ?",
				new String[] { make }, null, null, null);
		ArrayList<Lens> lenses = new ArrayList<Lens>();
		while (cursor.moveToNext()) {
			Lens lens = new Lens();
			lens.setLensSet(cursor.getString(0));
			lens.setFL(cursor.getFloat(1));
			lens.setRowid(cursor.getInt(2));
			lenses.add(lens);
		}
		cursor.close();
		return lenses;
	}

	public ArrayList<String> getLensMakeForLensFormat(String[] lensFormats) {
		// get distinct lens make for camera format
		String whereClause = "zformat = ?";
		for (int currentFormat = 1; currentFormat < lensFormats.length;) {
			whereClause += " OR zformat = ?";
			currentFormat++;
		}
		Cursor cursor = _artemisDatabase.query(true, "zlenses",
				new String[] { "zlensmake" }, whereClause, lensFormats, null,
				null, null, null);
		ArrayList<String> lensMake = new ArrayList<String>();
		while (cursor.moveToNext()) {
			lensMake.add(cursor.getString(0));
		}
		cursor.close();
		return lensMake;
	}

	public void updateLensSelections(Collection<Lens> lensesToUpdate) {
		_artemisDatabase.beginTransaction();
		try {
			for (Lens lens : lensesToUpdate) {
				ContentValues cv = new ContentValues();
				cv.put("zlensSet", lens.getLensSet());
				_artemisDatabase.update("zlenses", cv, "zrowid = ?",
						new String[] { "" + lens.getRowid() });
				// Log.i(logTag, result+" rows updated.");
			}
			_artemisDatabase.setTransactionSuccessful();
		} finally {
			_artemisDatabase.endTransaction();
		}
	}

	public void insertCustomCamera(CustomCamera customCamera) {
		_artemisDatabase.beginTransaction();
		ContentValues initialValues = new ContentValues();
		initialValues.put("Label", customCamera.getLabel());
		initialValues.put("Horiz", customCamera.getHoriz());
		initialValues.put("Vertical", customCamera.getVertical());
		initialValues.put("FL", customCamera.getFL());
		initialValues.put("distance", customCamera.getDistance());
		initialValues.put("width", customCamera.getWidth());
		initialValues.put("height", customCamera.getHeight());

		_artemisDatabase.insert("custom_cameras", null, initialValues);
		_artemisDatabase.setTransactionSuccessful();
		_artemisDatabase.endTransaction();
	}

	public void updateCustomCamera(CustomCamera customCamera) {
		_artemisDatabase.beginTransaction();
		ContentValues updatedValues = new ContentValues();
		updatedValues.put("Label", customCamera.getLabel());
		updatedValues.put("Horiz", customCamera.getHoriz());
		updatedValues.put("Vertical", customCamera.getVertical());
		updatedValues.put("FL", customCamera.getFL());
		updatedValues.put("distance", customCamera.getDistance());
		updatedValues.put("width", customCamera.getWidth());
		updatedValues.put("height", customCamera.getHeight());

		_artemisDatabase.update("custom_cameras", updatedValues, "rowid = ?",
				new String[] { customCamera.getRowid() + "" });
		_artemisDatabase.setTransactionSuccessful();
		_artemisDatabase.endTransaction();
	}

	public void deleteCustomCameraByRowId(int rowid) {
		_artemisDatabase.beginTransaction();
		// int deleted =
		_artemisDatabase.delete("custom_cameras", "rowid = ?",
				new String[] { "" + rowid });
		// Log.v(logTag, "Deleted Custom Cameras: "+deleted);
		_artemisDatabase.setTransactionSuccessful();
		_artemisDatabase.endTransaction();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(TAG, "Creating a brand new database.");
		executeDatabaseSQL(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(TAG, "DB onUpgrade called.");
		if (newVersion > oldVersion) {
			Log.v(TAG, "Database version higher than old one.  Upgrading.");
			db.execSQL("drop table zcameras");
			db.execSQL("drop table zlenses");
			executeDatabaseSQL(db);
		}
	}
	
	private void executeDatabaseSQL(SQLiteDatabase db) {
		try {
			BufferedReader ios = new BufferedReader(new InputStreamReader(_context.getResources().getAssets().open("artemis.sql")));
			
			String line = null;
			while ((line = ios.readLine()) != null) {
				Log.v(TAG, line);
				db.execSQL(line);
			}
		} catch (IOException e) {

		}
	}
}