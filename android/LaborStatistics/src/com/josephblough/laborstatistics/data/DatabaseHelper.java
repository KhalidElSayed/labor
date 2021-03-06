package com.josephblough.laborstatistics.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.josephblough.laborstatistics.R;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "data2010.db";
    private SQLiteDatabase database;
    private final Context context;
    
    public DatabaseHelper(Context context) {
	super(context, DATABASE_NAME, null, 1);
	this.context = context;
    }
    
    @Override
    public void onCreate(SQLiteDatabase arg0) {
	// TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	// TODO Auto-generated method stub

    }

    public String databasePath() {
	return context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
    }
    
    public void createDatabase() throws IOException {
	boolean exists = checkDatabase();
	
	if (!exists) {
	    database = this.getReadableDatabase();
	    try {
		copyDatabase();
	    }
	    catch (IOException e) {
		throw new Error("Error copying database");
	    }
	    
	    if (database != null) {
		database.close();
		database = null;
	    }
	}
    }
    
    public boolean checkDatabase() {
	SQLiteDatabase checkDB = null;
	
	try {
	    String path = databasePath();
	    checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	}
	catch (SQLiteException e) {
	    // Database doesn't exist
	}
	
	if (checkDB != null) {
	    checkDB.close();
	}
	
	return (checkDB != null);
    }
    
    private void copyDatabase() throws IOException {
	String destination = databasePath();
	Log.d(TAG, "opening output stream for database " + destination);
	OutputStream output = new FileOutputStream(destination);

	int[] resources = new int[] { R.raw.data2010a, R.raw.data2010b, R.raw.data2010c };
	for (int resource : resources) {
	    InputStream input = context.getResources()
		    .openRawResource(resource);

	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = input.read(buffer)) > 0) {
		output.write(buffer, 0, length);
	    }

	    output.flush();
	    input.close();
	}
	output.close();
    }
    
    public SQLiteDatabase openDatabase() throws SQLException {
	String path = databasePath();
	database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	return database;
    }
    
    public synchronized void close() {
	if (database != null) {
	    database.close();
	}
	
	super.close();
    }
}
