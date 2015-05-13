package com.example.please;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelper extends SQLiteOpenHelper {

	public static final String INPUT_STRING = "string";
	public static final String RECEIPT = "receipt";
	public static final String DATE = "date";
	public static final String COLUMN_ID = "_id";
	public static final String LOCATION_X = "loc_x";
	public static final String LOCATION_Y = "loc_y";
	public static final String STATE = "state";

	public static final String DB_NAME = "expenses.db";
	public static final String EXPENSES_TABLE = "EXPENSES";

	public static enum states {
		clean, changed, deleted
	};

	public DBhelper(Context context) {
		super(context, DB_NAME, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i(MainActivity.TAG, "db created");
		db.execSQL("create table " + EXPENSES_TABLE + " (" + COLUMN_ID
				+ " integer primary key autoincrement, " + INPUT_STRING
				+ " text not null, " + RECEIPT + " integer, " + DATE
				+ " text not null, " + LOCATION_X + " real, " + LOCATION_Y
				+ " real, " + STATE + " integer not null)" );
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int old_version, int new_versions) {
		// TODO Auto-generated method stub
		Log.i(MainActivity.TAG, "db updated");
		db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
		onCreate(db);

	}

}
