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

	private static final int DB_VERSION = 8;

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
				new String[] { "zformatname" }, null, null, null, null, null,
				null);
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
			cursor = _artemisDatabase.query(true, "zcustomcamera",
					new String[] { "zsensorheight", "zsensorwidth",
							"zcameraname", "zsqueezeratio", "z_pk" }, null,
					null, null, null, null, null);
		} catch (SQLiteException sle) {

		}
		ArrayList<CustomCamera> cameras = new ArrayList<CustomCamera>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				CustomCamera c = new CustomCamera();
				c.setSensorheight(cursor.getFloat(0));
				c.setSensorwidth(cursor.getFloat(1));
				c.setName(cursor.getString(2));
				c.setSqueeze(cursor.getFloat(3));
				c.setPk(cursor.getInt(4));
				cameras.add(c);
			}
			cursor.close();
		}
		return cameras;
	}

	public ArrayList<String> getCameraSensorsForFormat(String format) {
		// get distinct camera sensors for format
		Cursor cursor = _artemisDatabase.query(true, "zcameras",
				new String[] { "zsensorname" }, "zformatname = ?",
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
				"zrowid", "zaspectratio" }, "zsensorname = ?",
				new String[] { sensor }, null, null, null, null);
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
				"zhorozontalsize", "zverticalsize", "zsqueezeratio",
				"zsensorname", "zaspectratio", "zlenstype" }, "zrowid = ?",
				new String[] { rowid.toString() }, null, null, null, null);
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
		Cursor cursor = _artemisDatabase.query("zcustomcamera",
				new String[] { "zsensorheight", "zsensorwidth", "zcameraname",
						"zsqueezeratio" }, "z_pk = ?",
				new String[] { rowid.toString() }, null, null, null, null);
		cursor.moveToFirst();
		CustomCamera camera = new CustomCamera();
		camera.setSensorheight(cursor.getFloat(0));
		camera.setSensorwidth(cursor.getFloat(1));
		camera.setName(cursor.getString(2));
		camera.setSqueeze(cursor.getFloat(3));
		camera.setPk(rowid);
		cursor.close();
		return camera;
	}

	/*
	 * Lens database functions
	 */

	public ArrayList<Lens> getLensesForMake(String make) {
		// Get all the lenses for the specified make
		Cursor cursor = _artemisDatabase.query("zlensobject", new String[] {
				"zlensSet", "zlensmm", "zrowid" }, "zLensMake = ?",
				new String[] { make }, null, null, "zlensmm");
		ArrayList<Lens> lenses = new ArrayList<Lens>();
		while (cursor.moveToNext()) {
			Lens lens = new Lens();
			lens.setLensSet(cursor.getString(0));
			lens.setFL(cursor.getFloat(1));
			lens.setPk(cursor.getInt(2));
			lenses.add(lens);
		}
		cursor.close();
		return lenses;
	}

	public Lens getLensByRowId(int id) {
		Cursor cursor = _artemisDatabase.query("zlensobject", new String[] {
				"zlensSet", "zlensmm", "zlensCode", "zlensMake", "zzoom",
				"zzoomrange", "zformatname", "zsqueezeratio", }, "zrowid = ?",
				new String[] { "" + id }, null, null, null);
		cursor.moveToNext();
		Lens lens = new Lens();
		lens.setLensSet(cursor.getString(0));
		lens.setFL(cursor.getFloat(1));
		lens.setPk(id);
		lens.setLensCode(cursor.getString(2));
		lens.setLensMake(cursor.getString(3));
		lens.setZoom(cursor.getString(4));
		lens.setZoomRange(cursor.getString(5));
		lens.setFormat(cursor.getString(6));
		lens.setSqueeze(cursor.getFloat(7));
		cursor.close();
		return lens;
	}

	public ArrayList<String> getLensMakeForLensFormat(String[] lensFormats) {
		// get distinct lens make for camera format
		String whereClause = "zformatname = ?";
		for (int currentFormat = 1; currentFormat < lensFormats.length;) {
			whereClause += " OR zformatname = ?";
			currentFormat++;
		}
		Cursor cursor = _artemisDatabase.query(true, "zlensobject",
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
				_artemisDatabase.update("zlensobject", cv, "zrowid = ?",
						new String[] { "" + lens.getPk() });
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
		initialValues.put("ZSENSORHEIGHT", customCamera.getSensorheight());
		initialValues.put("ZSENSORWIDTH", customCamera.getSensorwidth());
		initialValues.put("ZCAMERANAME", customCamera.getName());
		initialValues.put("ZSQUEEZERATIO", customCamera.getSqueeze());
		long result = _artemisDatabase.insert("zcustomcamera", null,
				initialValues);
		_artemisDatabase.setTransactionSuccessful();
		_artemisDatabase.endTransaction();
	}

	public void updateCustomCamera(CustomCamera customCamera) {
		_artemisDatabase.beginTransaction();
		ContentValues updatedValues = new ContentValues();
		updatedValues.put("ZSENSORHEIGHT", customCamera.getSensorheight());
		updatedValues.put("ZSENSORWIDTH", customCamera.getSensorwidth());
		updatedValues.put("ZCAMERANAME", customCamera.getName());
		updatedValues.put("ZSQUEEZERATIO", customCamera.getSqueeze());

		_artemisDatabase.update("zcustomcamera", updatedValues, "z_pk = ?",
				new String[] { customCamera.getPk() + "" });
		_artemisDatabase.setTransactionSuccessful();
		_artemisDatabase.endTransaction();
	}

	public void deleteCustomCameraByRowId(int rowid) {
		_artemisDatabase.beginTransaction();
		// int deleted =
		_artemisDatabase.delete("zcustomcamera", "z_pk = ?", new String[] { ""
				+ rowid });
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
			db.execSQL("drop table if exists zcameras");
			db.execSQL("drop table if exists zlenses");
			db.execSQL("drop table if exists zlensobject");

			if (newVersion == 7) {
				db.execSQL("drop table if exists zcustomcamera");
			}
			executeDatabaseSQL(db);
		}
	}

	private void executeDatabaseSQL(SQLiteDatabase db) {
		try {
			BufferedReader ios = new BufferedReader(new InputStreamReader(
					_context.getResources().getAssets().open("artemisv3.sql")));

			String line = null;
			while ((line = ios.readLine()) != null) {
				Log.v(TAG, line);
				db.execSQL(line);
			}
		} catch (IOException e) {

		}
	}

	public ArrayList<String> findSensorsForQuery(String query) {
		// query cameras based on sensor
		query = "%" + query + "%";
		Cursor cursor = _artemisDatabase.query(
				"zcameras",
				new String[] {
				// "zhorozontalsize", "zverticalsize", "zsqueezeratio",
				// "zsensorname", "zaspectratio",
				// "zlensobject", "zrowid"
				"zsensorname" }, "zsensorname like ?", new String[] { query },
				"zsensorname", null, null, null);
		ArrayList<String> matchedCameras = new ArrayList<String>();
		while (cursor.moveToNext()) {
			matchedCameras.add(cursor.getString(0));
		}
		cursor.close();

		return matchedCameras;
	}

	public void addCustomLens(Lens customLens) {
		_artemisDatabase.beginTransaction();
		Cursor c = _artemisDatabase.rawQuery(
				"SELECT MAX(zrowid) + 1 FROM zlensobject", new String[] {});
		c.moveToNext();
		long nextrowid = c.getLong(0);

		ContentValues initialValues = new ContentValues();
		initialValues.put("zcustomlens", true);
		initialValues.put("zlensmm", customLens.getFL());
		initialValues.put("zformatname", customLens.getFormat());
		initialValues.put("zlenscode", customLens.getLensCode());
		initialValues.put("zlensmake", customLens.getLensMake());
		initialValues.put("zlensset", customLens.getLensSet());
		initialValues.put("zzoom", customLens.getZoom());
		initialValues.put("zzoomrange", customLens.getZoomRange());
		initialValues.put("zrowid", nextrowid);
		long result = _artemisDatabase.insert("zlensobject", null,
				initialValues);
		Log.v(TAG,
				String.format("new custom lens: %s\n Result: %d",
						customLens.toString(), result));
		_artemisDatabase.setTransactionSuccessful();
		_artemisDatabase.endTransaction();
	}

	public ArrayList<ZoomLens> getZoomLenses() {
		Cursor cursor = _artemisDatabase.query("zcustomzoomlens", new String[] {
				"z_pk", "zname", "zminfl", "zmaxfl" }, null, null, null, null,
				null, null);
		ArrayList<ZoomLens> zoomLenses = new ArrayList<ZoomLens>();
		while (cursor.moveToNext()) {
			ZoomLens lens = new ZoomLens();
			lens.setPk(cursor.getInt(0));
			lens.setName(cursor.getString(1));
			lens.setMinFL(cursor.getFloat(2));
			lens.setMaxFL(cursor.getFloat(3));
			zoomLenses.add(lens);
		}
		return zoomLenses;
	}

	public long addZoomLens(ZoomLens lens) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("zname", lens.getName());
		initialValues.put("zminfl", lens.getMinFL());
		initialValues.put("zmaxfl", lens.getMaxFL());
		long result = _artemisDatabase.insert("zcustomzoomlens", null,
				initialValues);
		return result;
	}

	public ZoomLens getZoomLens(int selectedZoomLensPK) {
		Cursor cursor = _artemisDatabase
				.query("zcustomzoomlens", new String[] { "z_pk", "zname",
						"zminfl", "zmaxfl" }, "z_pk = ?", new String[] { ""
						+ selectedZoomLensPK }, null, null, null, "1");
		cursor.moveToNext();
		ZoomLens lens = new ZoomLens();
		lens.setPk(cursor.getInt(0));
		lens.setName(cursor.getString(1));
		lens.setMinFL(cursor.getFloat(2));
		lens.setMaxFL(cursor.getFloat(3));
		return lens;
	}
}