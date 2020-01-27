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

import com.parse.ParseObject;

public class ArtemisDatabaseHelper extends SQLiteOpenHelper {

<<<<<<< HEAD
    private static final int DB_VERSION = 15;
=======
    private static final int DB_VERSION = 14;
>>>>>>> 4c62fd4 (3.0.5.1 uploaded to the app store for fix remembering lens selections)

    private SQLiteDatabase _artemisDatabase;

    private final Context _context;
    private final static String TAG = "ArtemisDatabaseHelper";
    private final static String DB_NAME = "artemisdb";
    private final static String CAMERA_TABLE = "zcamera";
    private final static String LENS_TABLE = "zlensobject";
    private final static String LENS_ADAPTERS_TABLE = "zcustomlensadapters";


    public ArtemisDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this._context = context;
        if (_artemisDatabase != null) {
            _artemisDatabase.close();
        }
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
        Cursor cursor = _artemisDatabase.query(true, CAMERA_TABLE,
                new String[]{"zformatname"}, null, null, null, null, "zformatorder",
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
                    new String[]{"zsensorheight", "zsensorwidth",
                            "zcameraname", "zsqueezeratio", "z_pk"}, null,
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

    public ArrayList<String> getCameraManufacturersForFormat(String formatName) {
        // get distinct camera sensors for format
        Cursor cursor = _artemisDatabase.query(true, CAMERA_TABLE,
                new String[]{"zcameramanufacturer"}, "zformatname = ?",
                new String[]{formatName}, null, null, "zsensororder", null);
        ArrayList<String> cameraSensors = new ArrayList<String>();
        while (cursor.moveToNext()) {
            cameraSensors.add(cursor.getString(0));
        }
        cursor.close();
        return cameraSensors;
    }

    public ArrayList<String> getCameraSensorsForManufacturer(String cameraManufacturer) {
        // get distinct camera sensors for format
        Cursor cursor = _artemisDatabase.query(true, CAMERA_TABLE,
                new String[]{"zsensorname"}, "zcameramanufacturer = ?",
                new String[]{cameraManufacturer}, null, null, "zsensororder", null);
        ArrayList<String> cameraSensors = new ArrayList<String>();
        while (cursor.moveToNext()) {
            cameraSensors.add(cursor.getString(0));
        }
        cursor.close();
        return cameraSensors;
    }

    public ArrayList<Pair<Integer, String>> getCameraRatiosForSensor(
            String cameraFormat, String sensor) {
        // get distinct camera ratios for sensor, return each ratio paired with
        // rowid
        Cursor cursor = _artemisDatabase.query(true, CAMERA_TABLE, new String[]{
                        "zrowid", "zaspectratio"}, "zsensorname = ? and zformatname = ?",
                new String[]{sensor, cameraFormat}, null, null, null, null);
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
        Cursor cursor = _artemisDatabase.query(CAMERA_TABLE, new String[]{
                        "zhorozontalsize", "zverticalsize",
                        "zsensorname", "zaspectratio"}, "zrowid = ?",
                new String[]{rowid.toString()}, null, null, null, null);
        cursor.moveToFirst();
        Camera camera = new Camera();
        camera.setHoriz(cursor.getFloat(0));
        camera.setVertical(cursor.getFloat(1));
//        camera.setSqz(cursor.getFloat(2));
        camera.setSensor(cursor.getString(2));
        camera.setRatio(cursor.getString(3));
//        camera.setLenses(cursor.getString(5));
        camera.setRowid(rowid);
        cursor.close();
        return camera;
    }

    public CustomCamera getCustomCameraDetailsForRowId(Integer rowid) {
        // get camera details for the camera rowid (index)
        Cursor cursor = _artemisDatabase.query("zcustomcamera",
                new String[]{"zsensorheight", "zsensorwidth", "zcameraname"}, "z_pk = ?",
                new String[]{rowid.toString()}, null, null, null, null);
        if (cursor.moveToFirst()) {
            CustomCamera camera = new CustomCamera();
            camera.setSensorheight(cursor.getFloat(0));
            camera.setSensorwidth(cursor.getFloat(1));
            camera.setName(cursor.getString(2));
//            camera.setSqueeze(cursor.getFloat(3));
            camera.setPk(rowid);
            cursor.close();
            return camera;
        }
        return null;
    }

	/*
     * Lens database functions
	 */

    public ArrayList<String> getLensManufacturers() {
        // Get all the lenses for the specified make
        Cursor cursor = _artemisDatabase.query(true, LENS_TABLE,
                new String[]{"zmanufacturer"}, null, null, null,
                null, null, null);
        ArrayList<String> lenses = new ArrayList<String>();
        while (cursor.moveToNext()) {
            lenses.add(cursor.getString(0));
        }
        cursor.close();
        return lenses;
    }

    public ArrayList<Lens> getLensesForMake(String make) {
        // Get all the lenses for the specified make
        Cursor cursor = _artemisDatabase.query(LENS_TABLE, new String[]{
                        "zlensSet", "zlensmm", "zrowid", "zsqueezeratio"}, "zLensMake = ?",
                new String[]{make}, null, null, "zlensmm");
        ArrayList<Lens> lenses = new ArrayList<Lens>();
        while (cursor.moveToNext()) {
            Lens lens = new Lens();
            lens.setLensSet(cursor.getString(0));
            lens.setFL(cursor.getFloat(1));
            lens.setPk(cursor.getInt(2));
            lens.setSqueeze(cursor.getFloat(3));
            lenses.add(lens);
        }
        cursor.close();
        return lenses;
    }

    public Lens getLensByRowId(int id) {
        Cursor cursor = _artemisDatabase.query(LENS_TABLE, new String[]{
                        "zlensSet", "zlensmm", "zlensMake", "zzoom",
                        "zzoomrange", "zsqueezeratio"}, "zrowid = ?",
                new String[]{"" + id}, null, null, null);
        cursor.moveToNext();
        Lens lens = new Lens();
        lens.setLensSet(cursor.getString(0));
        lens.setFL(cursor.getFloat(1));
        lens.setPk(id);
        lens.setLensMake(cursor.getString(2));
        lens.setZoom(cursor.getString(3));
        lens.setZoomRange(cursor.getString(4));
//        lens.setFormat(cursor.getString(5));
        lens.setSqueeze(cursor.getFloat(5));
        cursor.close();
        return lens;
    }

    public ArrayList<String> getLensMakeForLensManufacturer(String manufacturer) {
        // get distinct lens makes for manufacturer
        Cursor cursor = _artemisDatabase.query(true, LENS_TABLE,
                new String[]{"zlensmake"}, "zmanufacturer = ?", new String[]{manufacturer}, null,
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
                _artemisDatabase.update(LENS_TABLE, cv, "zrowid = ?",
                        new String[]{"" + lens.getPk()});
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
        //long result =
        _artemisDatabase.insert("zcustomcamera", null,
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
                new String[]{customCamera.getPk() + ""});
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public void deleteCustomCameraByRowId(int rowid) {
        _artemisDatabase.beginTransaction();
        // int deleted =
        _artemisDatabase.delete("zcustomcamera", "z_pk = ?", new String[]{""
                + rowid});
        // Log.v(logTag, "Deleted Custom Cameras: "+deleted);
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "Creating a brand new database.");
        dropTablesAndCreate(db, true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "DB onUpgrade called.");
        if (newVersion > oldVersion) {
            Log.v(TAG, "Database version higher than old one.  Upgrading.");
            dropTablesAndCreate(db, false);
        }
    }

    public void dropTablesAndCreate(SQLiteDatabase db, boolean createCustomTables) {
        if (db == null && this._artemisDatabase != null) {
            db = this._artemisDatabase;
        } else if (db == null) {
            return;
        }

        db.execSQL("drop table if exists zcameras");
        db.execSQL("drop table if exists zcamera");
        db.execSQL("drop table if exists zlenses");
        db.execSQL("drop table if exists zlensobject");

        if (createCustomTables) {
            db.execSQL("drop table if exists zcustomcamera");
            db.execSQL("drop table if exists zcustomzoomlens");
            db.execSQL("drop table if exists zcustomlensapdaters");
        }

        db.execSQL("drop index if exists 'ZCAMERA-ZOBJECTID'");
        db.execSQL("drop index if exists 'ZCAMERA-ZSENSORNAME'");
        db.execSQL("drop index if exists 'ZCAMERA-ZFORMATNAME'");
        db.execSQL("drop index if exists 'ZLENSOBJECT-ZOBJECTID'");

        db.execSQL("CREATE TABLE \"ZLENSOBJECT\" ( " +
                " \"Z_PK\" INTEGER, " +
                " \"ZROWID\" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " \"Z_ENT\" INTEGER, " +
                " \"Z_OPT\" INTEGER, " +
                " \"ZCUSTOMLENS\" INTEGER, " +
                " \"ZDEFAULTLENS\" INTEGER, " +
                " \"ZLENSSET\" INTEGER, " +
                " \"ZLIVE\" INTEGER, " +
                " \"ZZOOM\" INTEGER, " +
                " \"ZLENSBAG\" INTEGER, " +
                " \"ZLENSMM\" FLOAT, " +
                " \"ZSQUEEZERATIO\" FLOAT, " +
                " \"ZLENSMAKE\" VARCHAR, " +
                " \"ZMANUFACTURER\" VARCHAR, " +
                " \"ZUUID\" VARCHAR, " +
                " \"ZZOOMRANGE\" VARCHAR " +
                ");");

        db.execSQL("CREATE TABLE \"ZCAMERA\" ( " +
                " \"Z_PK\" INTEGER, " +
                " \"ZROWID\" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " \"Z_ENT\" INTEGER, " +
                " \"Z_OPT\" INTEGER, " +
                " \"ZCAMERAMATCH\" INTEGER, " +
                " \"ZCURRENTCAMERA\" INTEGER, " +
                " \"ZDEFAULTCAMERA\" INTEGER, " +
                " \"ZFORMATORDER\" INTEGER, " +
                " \"ZLENSFORMAT\" INTEGER, " +
                " \"ZLIVE\" INTEGER, " +
                " \"ZPROONLY\" INTEGER, " +
                " \"ZSENSORORDER\" INTEGER, " +
                " \"ZHOROZONTALSIZE\" FLOAT, " +
                " \"ZLASTUSED\" TIMESTAMP, " +
                " \"ZVERTICALSIZE\" FLOAT, " +
                " \"ZASPECTRATIO\" VARCHAR, " +
                " \"ZCAMERAMANUFACTURER\" VARCHAR, " +
                " \"ZFORMATNAME\" VARCHAR, " +
                " \"ZLASTLENSMANUFACTURER\" VARCHAR, " +
                " \"ZSENSORNAME\" VARCHAR, " +
                " \"ZUUID\" VARCHAR " +
                ");");
        db.execSQL("CREATE INDEX 'ZCAMERA-ZSENSORNAME' ON ZCAMERA (ZSENSORNAME)");
        db.execSQL("CREATE INDEX 'ZCAMERA-ZFORMATNAME' ON ZCAMERA (ZFORMATNAME)");
        db.execSQL("CREATE INDEX 'ZCAMERA-ZCAMERAMANUFACTURER' ON ZCAMERA (ZCAMERAMANUFACTURER)");

        if (createCustomTables) {
            db.execSQL("CREATE TABLE ZCUSTOMCAMERA ( Z_PK INTEGER PRIMARY KEY AUTOINCREMENT, ZSENSORWIDTH FLOAT, ZSENSORHEIGHT FLOAT, ZSQUEEZERATIO FLOAT, ZCAMERANAME VARCHAR );");
            db.execSQL("CREATE TABLE ZCUSTOMZOOMLENS ( Z_PK INTEGER PRIMARY KEY AUTOINCREMENT, ZNAME VARCHAR, ZMINFL FLOAT, ZMAXFL FLOAT );");
            db.execSQL("CREATE TABLE ZCUSTOMLENSADAPTERS (z_PK INTEGER PRIMARY KEY AUTOINCREMENT, ZFACTOR FLOAT, ZISCUSTOM INTEGER DEFAULT 1);");
        }

        executeDatabaseSQL(db);
        addPredefinedLensAdapters(db);
    }


    private void executeDatabaseSQL(SQLiteDatabase db) {
        try {
            BufferedReader ios = new BufferedReader(new InputStreamReader(
                    _context.getResources().getAssets().open("artemisv13.sql")));

            String line = null;
            while ((line = ios.readLine()) != null) {
                Log.v(TAG, line);
                db.execSQL(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPredefinedLensAdapters(SQLiteDatabase db) {
        _artemisDatabase = db;
        LensAdapter adapter1 = new LensAdapter();
        adapter1.setMagnificationFactor(0.45);
        adapter1.setCustomAdapter(false);

        LensAdapter adapter2 = new LensAdapter();
        adapter2.setMagnificationFactor(0.55);
        adapter2.setCustomAdapter(false);

        LensAdapter adapter3 = new LensAdapter();
        adapter3.setMagnificationFactor(0.65);
        adapter3.setCustomAdapter(false);

        insertLensAdapters(adapter1);
        insertLensAdapters(adapter2);
        insertLensAdapters(adapter3);
    }

    public ArrayList<String> findSensorsForQuery(String query) {
        // query cameras based on sensor
        query = "%" + query + "%";
        Cursor cursor = _artemisDatabase.query(
                CAMERA_TABLE,
                new String[]{
                        // "zhorozontalsize", "zverticalsize", "zsqueezeratio",
                        // "zsensorname", "zaspectratio",
                        // LENS_TABLE, "zrowid"
                        "zsensorname"}, "zsensorname like ?", new String[]{query},
                "zsensorname", null, "zsensorname", null);
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
                "SELECT MAX(zrowid) + 1 FROM zlensobject", new String[]{});
        c.moveToNext();
        long nextrowid = c.getLong(0);

        ContentValues initialValues = new ContentValues();
        initialValues.put("zcustomlens", true);
        initialValues.put("zlensmm", customLens.getFL());
//        initialValues.put("zformatname", customLens.getFormat());
//        initialValues.put("zlenscode", customLens.getLensCode());
        initialValues.put("zlensmake", customLens.getLensMake());
        initialValues.put("zlensset", customLens.getLensSet());
        initialValues.put("zzoom", customLens.getZoom());
        initialValues.put("zsqueezeratio", customLens.getSqueeze());
        initialValues.put("zzoomrange", customLens.getZoomRange());
        initialValues.put("zrowid", nextrowid);
        long result = _artemisDatabase.insert(LENS_TABLE, null,
                initialValues);
        Log.v(TAG,
                String.format("new custom lens: %s\n Result: %d",
                        customLens.toString(), result));
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
        c.close();
    }

    public ArrayList<LensAdapter> getLensAdapters() {
        Cursor cursor = _artemisDatabase.query("zcustomlensadapters", new String[] {
                "z_pk", "zfactor", "ziscustom" },
                null, null, null, null, "ziscustom asc, zfactor asc") ;
        ArrayList<LensAdapter> lensAdapters = new ArrayList<>();
        while(cursor.moveToNext()) {
            LensAdapter adapter = new LensAdapter();
            adapter.setPk(cursor.getInt(0));
            adapter.setMagnificationFactor(cursor.getDouble(1));
            adapter.setCustomAdapter(cursor.getInt(2) != 0);
            lensAdapters.add(adapter);
        }

        cursor.close();
        return lensAdapters;
    }

    public void insertLensAdapters(LensAdapter lensAdapter) {
        _artemisDatabase.beginTransaction();
        ContentValues initialValues = new ContentValues();
        initialValues.put("ZFACTOR", lensAdapter.getMagnificationFactor());
        initialValues.put("ZISCUSTOM", lensAdapter.isCustomAdapter());

        _artemisDatabase.insert("ZCUSTOMLENSADAPTERS", null, initialValues);
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public void deleteLensAdapterById(int lensAdapterId) {
        _artemisDatabase.beginTransaction();
        _artemisDatabase.delete("ZCUSTOMLENSADAPTERS", "z_pk = ?", new String[] { "" + lensAdapterId });
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public ArrayList<ZoomLens> getZoomLenses() {
        Cursor cursor = _artemisDatabase.query("zcustomzoomlens", new String[]{
                        "z_pk", "zname", "zminfl", "zmaxfl"}, null, null, null, null,
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
        cursor.close();
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
                .query("zcustomzoomlens", new String[]{"z_pk", "zname",
                        "zminfl", "zmaxfl"}, "z_pk = ?", new String[]{""
                        + selectedZoomLensPK}, null, null, null, "1");
        cursor.moveToNext();
        ZoomLens lens = new ZoomLens();
        lens.setPk(cursor.getInt(0));
        lens.setName(cursor.getString(1));
        lens.setMinFL(cursor.getFloat(2));
        lens.setMaxFL(cursor.getFloat(3));
        cursor.close();
        return lens;
    }

    public void deleteZoomLensByPK(int zoomLensPk) {
        _artemisDatabase.beginTransaction();
        _artemisDatabase.delete("zcustomzoomlens", "z_pk = ?", new String[]{""
                + zoomLensPk});
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public void updateZoomLens(ZoomLens edited) {
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("zname", edited.getName());
        updatedValues.put("zminfl", edited.getMinFL());
        updatedValues.put("zmaxfl", edited.getMaxFL());
        _artemisDatabase.update("zcustomzoomlens",
                updatedValues, "z_pk = ?", new String[]{"" + edited.getPk()});
    }

    public void executeSQL(String sqlString) {
        this._artemisDatabase.execSQL(sqlString);
    }

    public int findDefaultCameraID() {
        Cursor cursor = this._artemisDatabase.query(true, CAMERA_TABLE, new String[]{"zrowid"}, "zdefaultcamera = 1", null, null, null, null, "1");

        if (cursor.moveToNext()) {
            int defaultID = cursor.getInt(0);
            cursor.close();
            return defaultID;
        }
        cursor.close();

        return -1;
    }

    public boolean checkCameraByRemoteObjectID(String objectId) {
        Cursor cursor = this._artemisDatabase.query(true, CAMERA_TABLE, new String[]{"zrowid"}, "zobjectid = ?", new String[]{objectId}, null, null, null, "1");
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean checkLensByRemoteObjectID(String objectId) {
        Cursor cursor = this._artemisDatabase.query(true, LENS_TABLE, new String[]{"zrowid"}, "zobjectid = ?", new String[]{objectId}, null, null, null, "1");
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean addUpdateCloudCamera(ParseObject camera, boolean isUpdate) {
        ContentValues vals = new ContentValues();
        vals.put("ZHOROZONTALSIZE", camera.getDouble("horozontalSize"));
        vals.put("ZSQUEEZERATIO", camera.getDouble("squeezeRatio"));
        vals.put("ZVERTICALSIZE", camera.getDouble("verticalSize"));
        vals.put("ZASPECTRATIO", camera.getString("aspectRatio"));
        vals.put("ZCAMERAGENRE", camera.getString("cameraGenre"));
        vals.put("ZCAPTUREMEDIUM", camera.getString("captureMedium"));
        vals.put("ZFORMATNAME", camera.getString("formatName"));
        vals.put("ZLENSTYPE", camera.getString("lensType"));
        vals.put("ZORDER", camera.getInt("order"));
        vals.put("ZSENSORNAME", camera.getString("sensorName"));
        if (!isUpdate)
            vals.put("ZOBJECTID", camera.getObjectId());

        if (!isUpdate) {
            if (this._artemisDatabase.insert(CAMERA_TABLE, null, vals) > -1) {
                return true;
            }
        } else {
            if (this._artemisDatabase.update(CAMERA_TABLE, vals, "ZOBJECTID = ?", new String[]{camera.getObjectId()}) > 0) {
                return true;
            }
        }
        return false;
    }


    public boolean addUpdateCloudLens(ParseObject lens, boolean isUpdate) {
        ContentValues vals = new ContentValues();
        vals.put("ZLENSMM", lens.getDouble("FL"));
        vals.put("ZLENSSET", lens.getInt("lensSet"));
        vals.put("ZSQUEEZERATIO", lens.getDouble("Squeeze"));
        vals.put("ZFORMATNAME", lens.getString("Format"));
        vals.put("ZLENSCODE", lens.getString("LensCode"));
        vals.put("ZLENSMAKE", lens.getString("LensMake"));
        if (!isUpdate)
            vals.put("ZOBJECTID", lens.getObjectId());

        if (!isUpdate) {
            if (this._artemisDatabase.insert(LENS_TABLE, null, vals) > -1) {
                return true;
            }
        } else {
            if (this._artemisDatabase.update(LENS_TABLE, vals, "ZOBJECTID = ?", new String[]{lens.getObjectId()}) > 0) {
                return true;
            }
        }
        return false;
    }
}