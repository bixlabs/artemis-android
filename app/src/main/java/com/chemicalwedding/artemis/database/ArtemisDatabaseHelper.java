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
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;

import com.chemicalwedding.artemis.model.Extender;
import com.chemicalwedding.artemis.model.Frameline;
import com.chemicalwedding.artemis.model.FramelineRate;
import com.chemicalwedding.artemis.model.Shotplan;
import com.parse.ParseObject;

public class ArtemisDatabaseHelper extends SQLiteOpenHelper {

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
    private static final int DB_VERSION = 15;
=======
    private static final int DB_VERSION = 14;
>>>>>>> 4c62fd4 (3.0.5.1 uploaded to the app store for fix remembering lens selections)
=======
    private static final int DB_VERSION = 15;
>>>>>>> ed0b9bd (Look and feel changes)
=======
    private static final int DB_VERSION = 15;
>>>>>>> 449fcf5 (Add looks interface. Apply look to stills and video mode. Delete looks)
=======
    private static final int DB_VERSION = 16;
>>>>>>> 178ddf9 (Save custom look finished)
=======
    private static final int DB_VERSION = 17;
>>>>>>> 5e1520f (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
=======
    private static final int DB_VERSION = 17;
>>>>>>> d36402c (fix - temporal images no longer shown in gallery, database updates, stand ins menu adjustments...)
=======
    private static final int DB_VERSION = 18;
>>>>>>> f7ec138 (version 3.1.5 - Shotplan, camera selection, bug fixes)

    private SQLiteDatabase _artemisDatabase;

    private final Context _context;
    private final static String TAG = "ArtemisDatabaseHelper";
    private final static String DB_NAME = "artemisdb";
    private final static String CAMERA_TABLE = "zcamera";
    private final static String LENS_TABLE = "zlensobject";
<<<<<<< HEAD
    private final static String LENS_ADAPTERS_TABLE = "zcustomlensadapters";
=======
    private final static String LOOKS_TABLE = "zlooks";
<<<<<<< HEAD
>>>>>>> 449fcf5 (Add looks interface. Apply look to stills and video mode. Delete looks)
=======
    private final static String SHOTPLAN_TABLE = "shotplan";
>>>>>>> f7ec138 (version 3.1.5 - Shotplan, camera selection, bug fixes)


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
     * Looks database functions
     */
    public ArrayList<Look> getLooks() {
        Cursor cursor = null;
        try {
            cursor = _artemisDatabase.query(true, LOOKS_TABLE,
                    new String[]{"z_pk", "zeffectid", "zname", "zgamma", "zcontrast", "zsaturation", "zwhitebalance",
                            "zred", "zgreen", "zblue"}, null,
                    null, null, null, "zeffectid ASC", null);
        } catch (SQLiteException sle) {

        }

        ArrayList<Look> looks = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Look l = new Look();
                l.setPk(cursor.getInt(0));
                l.setEffectId(cursor.getInt(1));
                l.setName(cursor.getString(2));
                l.setGamma(cursor.getDouble(3));
                l.setContrast(cursor.getDouble(4));
                l.setSaturation(cursor.getDouble(5));
                l.setWhiteBalance(cursor.getDouble(6));
                l.setRed(cursor.getDouble(7));
                l.setGreen(cursor.getDouble(8));
                l.setBlue(cursor.getDouble(9));
                looks.add(l);
            }
            cursor.close();
        }
        return looks;
    }

    public Look getLook(Integer lookPk) {
        Cursor cursor = _artemisDatabase.query(LOOKS_TABLE,
                new String[]{"z_pk", "zeffectid", "zname", "zgamma", "zcontrast", "zsaturation", "zwhitebalance",
                        "zred", "zgreen", "zblue"}, "z_pk = ?",
                new String[]{lookPk.toString()}, null, null, null, null);

        if (cursor.moveToFirst()) {
            Look look = new Look();
            look.setPk(cursor.getInt(0));
            look.setEffectId(cursor.getInt(1));
            look.setName(cursor.getString(2));
            look.setGamma(cursor.getDouble(3));
            look.setContrast(cursor.getDouble(4));
            look.setSaturation(cursor.getDouble(5));
            look.setWhiteBalance(cursor.getDouble(6));
            look.setRed(cursor.getDouble(7));
            look.setGreen(cursor.getDouble(8));
            look.setBlue(cursor.getDouble(9));

            cursor.close();
            return look;
        } else {
            cursor.close();
            return null;
        }
    }

    public void insertLook(Look look) {
        _artemisDatabase.beginTransaction();
        ContentValues initialValues = new ContentValues();
        initialValues.put("zeffectid", look.getEffectId());
        initialValues.put("zname", look.getName());
        initialValues.put("zgamma", look.getGamma());
        initialValues.put("zcontrast", look.getContrast());
        initialValues.put("zsaturation", look.getSaturation());
        initialValues.put("zwhitebalance", look.getWhiteBalance());
        initialValues.put("zred", look.getRed());
        initialValues.put("zgreen", look.getGreen());
        initialValues.put("zblue", look.getBlue());

        _artemisDatabase.insert(LOOKS_TABLE, null,
                initialValues);
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public void deleteLookByPK(int lookPk) {
        _artemisDatabase.beginTransaction();
        _artemisDatabase.delete(LOOKS_TABLE, "z_pk = ?", new String[]{""
                + lookPk});
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
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

    public ArrayList<String> getExtenderManufacturers() {
        Cursor cursor = _artemisDatabase.query(true, "ZEXTENDERS",
                new String[] { "zextmanufacturer" }, null,
                null, null, null, "zextenderorder", null);
        ArrayList<String> extenders = new ArrayList<>();
        while(cursor.moveToNext()){
            extenders.add(cursor.getString(0));
        }
        cursor.close();
        return extenders;
    }

    public ArrayList<Extender> getExtenderForManufacturer(String manufacturer) {
        Cursor cursor = _artemisDatabase.query(true, "ZEXTENDERS",
                new String[] { "z_pk", "zextmanufacturer", "zmodel", "zmagnification", "zsqueezeextender", "zextenderorder" },
                "zextmanufacturer = ?", new String [] { manufacturer },
                null, null, "zextenderorder", null);
        ArrayList<Extender> models = new ArrayList<>();

        while(cursor.moveToNext()){
            Extender extender = new Extender();
            extender.setId(cursor.getInt(0));
            extender.setManufacturer(cursor.getString(1));
            extender.setModel(cursor.getString(2));
            extender.setMagnification(cursor.getFloat(3));
            extender.setSqueeze(cursor.getFloat(4));
            extender.setOrder(cursor.getInt(5));

            models.add(extender);
        }
        cursor.close();
        return models;
    }

    public Extender getExtenderById(int id) {
        Cursor cursor = _artemisDatabase.query("zextenders",
                new String[] { "z_pk", "zextmanufacturer", "zmodel", "zmagnification", "zsqueezeextender", "zextenderorder" },
                "z_pk = ?", new String[] { "" + id  }, null, null, null);

        if(cursor.moveToFirst()) {
            Extender extender = new Extender();
            extender.setId(cursor.getInt(0));
            extender.setManufacturer(cursor.getString(1));
            extender.setModel(cursor.getString(2));
            extender.setMagnification(cursor.getFloat(3));
            extender.setSqueeze(cursor.getFloat(4));
            extender.setOrder(cursor.getInt(5));

            cursor.close();
            return extender;
        } else {
            cursor.close();
            return null;
        }
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
                new String[]{"zmanufacturer"}, "zmanufacturer is not null", null, null,
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

    public void deleteFrameline(Frameline frameline) {
        if (frameline.getId() > 0) {
            _artemisDatabase.beginTransaction();
            _artemisDatabase.delete("ZCUSTOMFRAMELINES", "pk = ?", new String[]{""
                    + frameline.getId()});
            _artemisDatabase.setTransactionSuccessful();
            _artemisDatabase.endTransaction();
        }
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
            dropTablesAndCreate(db, true);
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
        db.execSQL("drop table if exists zlooks");
        db.execSQL("drop table if exists zextenders");
        db.execSQL("drop table if exists shotplan");

        if (createCustomTables) {
            db.execSQL("drop table if exists zcustomcamera");
            db.execSQL("drop table if exists zcustomzoomlens");
<<<<<<< HEAD
            db.execSQL("drop table if exists zcustomlensapdaters");
=======
            db.execSQL("drop table if exists zcustomlensadapters");
            db.execSQL("drop table if exists zcustomframelinerates");
            db.execSQL("drop table if exists zcustomframelines");
<<<<<<< HEAD
>>>>>>> 4d00701 (Fixed crashes after merge)
=======
            db.execSQL("drop table if exists shotplan");
>>>>>>> f7ec138 (version 3.1.5 - Shotplan, camera selection, bug fixes)
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

        db.execSQL("CREATE TABLE \"ZLOOKS\" ( " +
                " \"Z_PK\" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " \"ZEFFECTID\" INTEGER, " +
                " \"ZNAME\" VARCHAR, " +
                " \"ZGAMMA\" FLOAT, " +
                " \"ZCONTRAST\" FLOAT, " +
                " \"ZSATURATION\" FLOAT, " +
                " \"ZWHITEBALANCE\" FLOAT, " +
                " \"ZRED\" FLOAT, " +
                " \"ZGREEN\" FLOAT, " +
                " \"ZBLUE\" FLOAT " +
                ");");

        db.execSQL("CREATE TABLE zextenders(\n" +
                "   z_pk             INTEGER  NOT NULL PRIMARY KEY \n" +
                "  ,zextmanufacturer VARCHAR(22) NOT NULL\n" +
                "  ,zmodel           VARCHAR(48) NOT NULL\n" +
                "  ,zmagnification   NUMERIC(5,3) NOT NULL\n" +
                "  ,zsqueezeextender NUMERIC(4,2) NOT NULL\n" +
                "  ,zextenderorder   INTEGER  NOT NULL\n" +
                ");");

        db.execSQL("create table shotplan(\n" +
                "  id integer primary key autoincrement,\n" +
                "  file_path varchar,\n" +
                "  camera varchar,\n" +
                "  lens varchar,\n" +
                "  title varchar,\n" +
                "  notes varchar,\n" +
                "  latitude real,\n" +
                "  longitude real\n" +
                ");");

        if (createCustomTables) {
            db.execSQL("CREATE TABLE ZCUSTOMCAMERA ( Z_PK INTEGER PRIMARY KEY AUTOINCREMENT, ZSENSORWIDTH FLOAT, ZSENSORHEIGHT FLOAT, ZSQUEEZERATIO FLOAT, ZCAMERANAME VARCHAR );");
            db.execSQL("CREATE TABLE ZCUSTOMZOOMLENS ( Z_PK INTEGER PRIMARY KEY AUTOINCREMENT, ZNAME VARCHAR, ZMINFL FLOAT, ZMAXFL FLOAT );");
            db.execSQL("CREATE TABLE ZCUSTOMLENSADAPTERS (z_PK INTEGER PRIMARY KEY AUTOINCREMENT, ZFACTOR FLOAT, ZISCUSTOM INTEGER DEFAULT 1);");
            db.execSQL("CREATE TABLE ZCUSTOMFRAMELINERATES (PK INTEGER PRIMARY KEY AUTOINCREMENT, RATE FLOAT, IS_CUSTOM INTEGER DEFAULT 1);");
            db.execSQL("CREATE TABLE ZCUSTOMFRAMELINES (PK INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " RATE INTEGER," +
                    " SCALE INTEGER, " +
                    " SHADING_TYPE INTEGER, " +
                    " VERTICAL_OFFSET INTEGER, " +
                    " HORIZONTAL_OFFSET INTEGER, " +
                    " FRAMELINE_TYPE INTEGER, " +
                    " COLOR INTEGER, " +
                    " IS_DOTTED INTEGER DEFAULT 0, " +
                    " LINE_WIDTH INTEGER DEFAULT 1, " +
                    " CENTER_MARKER_TYPE INTEGER DEFAULT 1, " +
                    " CENTER_MARKER_LINE_WIDTH INTEGER DEFAULT 1, " +
                    " IS_APPLIED INTEGER DEFAULT 0" +
                    ");");
        }

        executeDatabaseSQL(db);
        addPredefinedLensAdapters(db);
        addPredefinedFramelineRates(db);
//        addPredefinedFramelines(db);
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

    private void addPredefinedFramelineRates(SQLiteDatabase db) {
        _artemisDatabase = db;
        FramelineRate rate1 = new FramelineRate(1.78, false);
        FramelineRate rate2 = new FramelineRate(1.64, false);
        FramelineRate rate3 = new FramelineRate(1.55, false);
        FramelineRate rate4 = new FramelineRate(1.33, false);

        insertFramelineRate(rate1);
        insertFramelineRate(rate2);
        insertFramelineRate(rate3);
        insertFramelineRate(rate4);
    }

    private void addPredefinedFramelines(SQLiteDatabase db) {
        _artemisDatabase = db;
        ArrayList<FramelineRate> framelineRates = getFramelineRates();
        Frameline frameline = new Frameline();
        frameline.setRate(framelineRates.get(0));
        frameline.setScale(50);
        frameline.setShading(2);
        frameline.setVerticalOffset(0);
        frameline.setHorizontalOffset(0);
        frameline.setDotted(false);

        frameline.setFramelineType(1);
        frameline.setColor(Color.WHITE);
        frameline.setLineWidth(1);
        frameline.setCenterMarkerType(0);
        frameline.setCenterMarkerLineWidth(1);
        frameline.setApplied(false);

        insertFrameline(frameline);
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
        Cursor cursor = _artemisDatabase.query("zcustomlensadapters", new String[]{
                        "z_pk", "zfactor", "ziscustom"},
                null, null, null, null, "ziscustom asc, zfactor asc");
        ArrayList<LensAdapter> lensAdapters = new ArrayList<>();
        while (cursor.moveToNext()) {
            LensAdapter adapter = new LensAdapter();
            adapter.setPk(cursor.getInt(0));
            adapter.setMagnificationFactor(cursor.getDouble(1));
            adapter.setCustomAdapter(cursor.getInt(2) != 0);
            lensAdapters.add(adapter);
        }

        cursor.close();
        return lensAdapters;
    }

    public ArrayList<FramelineRate> getFramelineRates() {
        Cursor cursor = _artemisDatabase.query("ZCUSTOMFRAMELINERATES", new String[]{
                        "PK", "RATE", "IS_CUSTOM"},
                null, null, null, null, "IS_CUSTOM ASC"
        );

        ArrayList<FramelineRate> framelineRates = new ArrayList<>();
        while (cursor.moveToNext()) {
            FramelineRate framelineRate = new FramelineRate();
            framelineRate.setId(cursor.getInt(0));
            framelineRate.setRate(cursor.getDouble(1));
            framelineRate.setCustom(cursor.getInt(2) == 1);
            framelineRates.add(framelineRate);
        }

        cursor.close();
        return framelineRates;
    }

    public FramelineRate getFramelineRate(int id) {
        Cursor cursor = _artemisDatabase.query("ZCUSTOMFRAMELINERATES", new String[]{
                        "PK", "RATE", "IS_CUSTOM"},
                "PK = ?", new String[]{String.valueOf(id)}, null, null, "IS_CUSTOM ASC"
        );

        ArrayList<FramelineRate> framelineRates = new ArrayList<>();
        cursor.moveToNext();
        FramelineRate framelineRate = new FramelineRate();
        framelineRate.setId(cursor.getInt(0));
        framelineRate.setRate(cursor.getDouble(1));
        framelineRate.setCustom(cursor.getInt(2) == 1);
        framelineRates.add(framelineRate);

        cursor.close();
        return framelineRate;
    }

    public ArrayList<Frameline> getFramelines() {
        ArrayList<FramelineRate> rates = getFramelineRates();
        Cursor cursor = _artemisDatabase.query("ZCUSTOMFRAMELINES", new String[]{
                "PK", "RATE", "SCALE", "SHADING_TYPE", "VERTICAL_OFFSET", "HORIZONTAL_OFFSET",
                "FRAMELINE_TYPE", "COLOR", "IS_DOTTED", "LINE_WIDTH",
                "CENTER_MARKER_TYPE", "CENTER_MARKER_LINE_WIDTH", "IS_APPLIED"
        }, null, null, null, null, null);

        ArrayList<Frameline> framelines = new ArrayList<>();
        while (cursor.moveToNext()) {
            Frameline frameline = new Frameline();
            frameline.setId(cursor.getInt(0));
            frameline.setRate(getFramelineRate(cursor.getInt(1)));
            frameline.setScale(cursor.getInt(2));
            frameline.setShading(cursor.getInt(3));
            frameline.setVerticalOffset(cursor.getInt(4));
            frameline.setHorizontalOffset(cursor.getInt(5));

            frameline.setFramelineType(cursor.getInt(6));
            frameline.setColor(cursor.getInt(7));
            frameline.setDotted(cursor.getInt(8) == 1);
            frameline.setLineWidth(cursor.getInt(9));
            frameline.setCenterMarkerType(cursor.getInt(10));
            frameline.setCenterMarkerLineWidth(cursor.getInt(11));
            frameline.setApplied(cursor.getInt(12) == 1);

            framelines.add(frameline);
        }

        cursor.close();
        return framelines;
    }

    public ArrayList<Frameline> getAppliedFramelines() {
        ArrayList<FramelineRate> rates = getFramelineRates();
        Cursor cursor = _artemisDatabase.query("ZCUSTOMFRAMELINES", new String[]{
                "PK", "RATE", "SCALE", "SHADING_TYPE", "VERTICAL_OFFSET", "HORIZONTAL_OFFSET",
                "FRAMELINE_TYPE", "COLOR", "IS_DOTTED", "LINE_WIDTH",
                "CENTER_MARKER_TYPE", "CENTER_MARKER_LINE_WIDTH", "IS_APPLIED"
        }, "IS_APPLIED = 1", null, null, null, null);

        ArrayList<Frameline> framelines = new ArrayList<>();
        while (cursor.moveToNext()) {
            Frameline frameline = new Frameline();
            frameline.setId(cursor.getInt(0));
            frameline.setRate(getFramelineRate(cursor.getInt(1)));
            frameline.setScale(cursor.getInt(2));
            frameline.setShading(cursor.getInt(3));
            frameline.setVerticalOffset(cursor.getInt(4));
            frameline.setHorizontalOffset(cursor.getInt(5));

            frameline.setFramelineType(cursor.getInt(6));
            frameline.setColor(cursor.getInt(7));
            frameline.setDotted(cursor.getInt(8) == 1);
            frameline.setLineWidth(cursor.getInt(9));
            frameline.setCenterMarkerType(cursor.getInt(10));
            frameline.setCenterMarkerLineWidth(cursor.getInt(11));
            frameline.setApplied(cursor.getInt(12) == 1);

            framelines.add(frameline);
        }

        cursor.close();
        return framelines;
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

    public void insertShotplan(Shotplan shotplan) {
        _artemisDatabase.beginTransaction();
        ContentValues initialValues = new ContentValues();
        initialValues.put("file_path", shotplan.getPath());
        initialValues.put("camera", shotplan.getCameraName());
        initialValues.put("lens", shotplan.getLens());
        initialValues.put("title", shotplan.getTitle());
        initialValues.put("notes", shotplan.getNotes());
        initialValues.put("latitude", shotplan.getLatitude());
        initialValues.put("longitude", shotplan.getLongitude());

        _artemisDatabase.insert("shotplan", null, initialValues);
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public void insertFrameline(Frameline frameline) {
        _artemisDatabase.beginTransaction();
        ContentValues initialValues = new ContentValues();
        initialValues.put("RATE", frameline.getRate().getId());
        initialValues.put("SCALE", frameline.getScale());
        initialValues.put("shading_type", frameline.getShading());
        initialValues.put("vertical_offset", frameline.getVerticalOffset());
        initialValues.put("horizontal_offset", frameline.getHorizontalOffset());
        initialValues.put("frameline_type", frameline.getFramelineType());
        initialValues.put("color", frameline.getColor());
        initialValues.put("is_dotted", frameline.isDotted() ? 1 : 0);
        initialValues.put("line_width", frameline.getLineWidth());
        initialValues.put("center_marker_type", frameline.getCenterMarkerType());
        initialValues.put("center_marker_line_width", frameline.getCenterMarkerLineWidth());
        initialValues.put("is_applied", frameline.isApplied() ? 1 : 0);

        _artemisDatabase.insert("ZCUSTOMFRAMELINES", null, initialValues);
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public void saveFrameline(Frameline frameline) {
        if (frameline.getId() == 0) {
            insertFrameline(frameline);
        } else {
            updateFrameline(frameline);
        }
    }

    public void updateFrameline(Frameline frameline) {
        _artemisDatabase.beginTransaction();
        ContentValues initialValues = new ContentValues();
        initialValues.put("RATE", frameline.getRate().getId());
        initialValues.put("SCALE", frameline.getScale());
        initialValues.put("shading_type", frameline.getShading());
        initialValues.put("vertical_offset", frameline.getVerticalOffset());
        initialValues.put("horizontal_offset", frameline.getHorizontalOffset());
        initialValues.put("frameline_type", frameline.getFramelineType());
        initialValues.put("color", frameline.getColor());
        initialValues.put("is_dotted", frameline.isDotted() ? 1 : 0);
        initialValues.put("line_width", frameline.getLineWidth());
        initialValues.put("center_marker_type", frameline.getCenterMarkerType());
        initialValues.put("center_marker_line_width", frameline.getCenterMarkerLineWidth());
        initialValues.put("is_applied", frameline.isApplied() ? 1 : 0);

        _artemisDatabase.update("ZCUSTOMFRAMELINES", initialValues, "PK = ?", new String[]{String.valueOf(frameline.getId())});
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public void updateShotplan(Shotplan shotplan) {
        _artemisDatabase.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("id", shotplan.getId());
        values.put("file_path", shotplan.getPath());
        values.put("camera", shotplan.getCameraName());
        values.put("lens", shotplan.getLens());
        values.put("title", shotplan.getTitle());
        values.put("notes", shotplan.getNotes());
        values.put("latitude", shotplan.getLatitude());
        values.put("longitude", shotplan.getLongitude());

        _artemisDatabase.update("shotplan", values, "id = ?", new String[] { String.valueOf(shotplan.getId()) });
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public void insertFramelineRate(FramelineRate framelineRate) {
        _artemisDatabase.beginTransaction();
        ContentValues initialValues = new ContentValues();
        initialValues.put("RATE", framelineRate.getRate());
        initialValues.put("IS_CUSTOM", framelineRate.isCustom() ? 1 : 0);

        _artemisDatabase.insert("ZCUSTOMFRAMELINERATES", null, initialValues);
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public void deleteLensAdapterById(int lensAdapterId) {
        _artemisDatabase.beginTransaction();
        _artemisDatabase.delete("ZCUSTOMLENSADAPTERS", "z_pk = ?", new String[]{"" + lensAdapterId});
        _artemisDatabase.setTransactionSuccessful();
        _artemisDatabase.endTransaction();
    }

    public void deleteShotplan(int shotplanId) {
        _artemisDatabase.beginTransaction();
        _artemisDatabase.delete("shotplan", "id = ?", new String[] { String.valueOf(shotplanId) });
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

    public Shotplan getShotplanByPath(String path) {
        Cursor cursor = _artemisDatabase.query("shotplan",
                new String[] { "id", "file_path", "camera", "lens", "title", "notes", "latitude", "longitude" },
                "file_path = ?", new String[] { path },
                null, null, null, "1"
                );

        cursor.moveToNext();

        Shotplan shotplan = new Shotplan(
            cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getDouble(6),
                cursor.getDouble(7)
        );

        cursor.close();

        return shotplan;
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
