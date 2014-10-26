package com.ic.googletelus;

import java.util.Date;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GlassDatabaseHelper {

	private MySQLiteHelper sqlOpenHelper;
	private SQLiteDatabase db;

	public static final String TABLE_NAME = "telusLoc";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LAT = "latitude";
	public static final String COLUMN_LONG = "longitude";
	public static final String COLUMN_PHONE_NUM = "phoneNum";
	public static final String COLUMN_HOURS = "storeHours";
	public static final String COLUMN_SERVICES = "availServices";
	public static final String COLUMN_FAV = "favOrNot";
	public static final String COLUMN_STORE_NAME = "storeName";
	public static final String COLUMN_STORE_ADDRESS = "storeAddress";
	
	public static final String[] LAT_COLUMN_ARR = new String[] {COLUMN_LAT};
	public static final String[] LONG_COLUMN_ARR = new String[] {COLUMN_LONG};
	
	private static final String DATABASE_NAME = "Db";
	private static final int DATABASE_VERSION = 10;

	public GlassDatabaseHelper(Context context){
		sqlOpenHelper = new MySQLiteHelper(context);
	}
	
	public void open() {
		db = sqlOpenHelper.getWritableDatabase();
	}
	
	public void close(){
		sqlOpenHelper.close();
	}
	
	public Cursor getAll(){		// Get all locations
		String where = null;
		Cursor c = 	db.query(true, TABLE_NAME, null, 
							where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	
	public boolean isFav(){
		String where = COLUMN_FAV + "= 1" ;
		Cursor c = db.query(true, TABLE_NAME, null, where, null, null, null, null, null, null);
		
		if (c != null) {
			return true;
		}
		return false;
	}
	
	public int getCount(){
		String where = null;
		Cursor c = 	db.query(true, TABLE_NAME, null, 
							where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c.getCount();
	}
	
	public double getLat(int row){
		String where = COLUMN_ID + "=" + row;
		Cursor c = db.query(true, TABLE_NAME, LAT_COLUMN_ARR, where, null, null, null, null, null);
		return c.getDouble(0);
	}
	
	public double getLong(int row){
		String where = COLUMN_ID + "=" + row;
		Cursor c = db.query(true, TABLE_NAME, LONG_COLUMN_ARR, where, null, null, null, null, null);
		return c.getDouble(0);
	}
	
	public void addStore(double lati, double longi, String pNum, String hrs, String services, int fav, String storeName, String address){
		
		ContentValues values = new ContentValues();
		
		// Adding into the db
		values.put(COLUMN_LAT, lati);
		values.put(COLUMN_LONG, longi);
		values.put(COLUMN_PHONE_NUM, pNum);
		values.put(COLUMN_HOURS, hrs);
		values.put(COLUMN_SERVICES, services);
		values.put(COLUMN_FAV, fav);
		values.put(COLUMN_STORE_NAME, storeName);
		values.put(COLUMN_STORE_ADDRESS, address);
		
		long newRowId;
		newRowId = db.insert(TABLE_NAME, null, values);	// null means if db is empty, add nowhere?
	}
	
	public class MySQLiteHelper extends SQLiteOpenHelper {


		
		public MySQLiteHelper(Context context) {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
		  }
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table " + TABLE_NAME + "( " 
					+ COLUMN_ID + " INTEGER PRIMARY KEY, " 
					+ COLUMN_LAT + " REAL, " 
					+ COLUMN_LONG + " REAL, "
					+ COLUMN_PHONE_NUM + " TEXT, "
					+ COLUMN_HOURS + "TEXT, "
					+ COLUMN_SERVICES + "TEXT, "
					+ COLUMN_FAV + "INTEGER, "
					+ COLUMN_STORE_NAME + "TEXT, " 
					+ COLUMN_STORE_ADDRESS + "TEXT)"
					);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS telusLoc");
			onCreate(db);
			System.out.println("Updated!");

		}


	}
}
