package com.nimbits.android.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nimbits.client.model.Const;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class DatabaseHelperImpl extends SQLiteOpenHelper implements DatabaseHelper {

    //The Android's default system path of your application database.


   // private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DatabaseHelperImpl(Context context) {

        super(context, Const.ANDROID_DB_NAME, null, 1);
        this.myContext = context;
    }

    boolean dbFileExists() {
        java.io.File file = new java.io.File(Const.ANDROID_DB_PATH + Const.ANDROID_DB_NAME);
        return file.exists();
    }

    @Override
    public boolean checkDatabase()   {
        if (! dbFileExists()) {
            try {
                dbCreate();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        else {
            return true;
        }

    }






    @Override
    public boolean isDatabaseEmpty() {
        SQLiteDatabase db1 = getDB(false);
        final Cursor c = db1.query(Const.ANDROID_TABLE_LEVEL_TWO_DISPLAY, new String[] {"_id", Const.ANDROID_COL_CATEGORY, Const.ANDROID_COL_DESCRIPTION, Const.ANDROID_COL_DISPLAY_TYPE},  null , null, null, null, Const.ANDROID_COL_DISPLAY_TYPE);
        final boolean retVal = c.isAfterLast();
        c.close();
        db1.close();
        return retVal;
    }



    void dbCreate() throws IOException {
        final AssetManager assetManager = myContext.getAssets();
        this.getWritableDatabase();
        final InputStream myInput =assetManager.open(Const.ANDROID_DB_NAME);
        final String outFileName = Const.ANDROID_DB_PATH + Const.ANDROID_DB_NAME;
        final OutputStream myOutput = new FileOutputStream(outFileName);
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();


    }

    public SQLiteDatabase getDB(boolean writable)  {
       // DatabaseHelperImpl myDbHelper = new DatabaseHelperImpl(c);
        SQLiteDatabase db1;
        if (writable) {
            db1 = getWritableDatabase();
        }
        else {
            db1 = getReadableDatabase();
        }
        db1.setVersion(1);
        db1.setLocale(Locale.getDefault());
        db1.setLockingEnabled(false);
        return db1;
    }

    @Override
    public synchronized void close() {

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}