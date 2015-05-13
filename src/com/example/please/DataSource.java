package com.example.please;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataSource {
	private SQLiteDatabase database = null;
	private DBhelper myHelper;
	static public String[] allColumns = { DBhelper.INPUT_STRING, DBhelper.DATE,
			DBhelper.RECEIPT, DBhelper.COLUMN_ID, DBhelper.LOCATION_X,
			DBhelper.LOCATION_Y, DBhelper.STATE };

	public DataSource(Context context) {
		myHelper = new DBhelper(context);
	}

	public void open() throws SQLException {
		Log.i(MainActivity.TAG, "Opened Table");
		database = myHelper.getWritableDatabase();
	}

	public boolean isOpen() {
		if (database == null) {
			return false;
		} else {
			return true;
		}

	}

	public void close() {
		Log.i(MainActivity.TAG, "Closed Table");
		myHelper.close();
		database = null;
	}

	public expense updateExpense(expense initial, String string, long receipt) {
		ContentValues values = new ContentValues();
		values.put(DBhelper.INPUT_STRING, string);
		values.put(DBhelper.RECEIPT, receipt);
		values.put(DBhelper.STATE, DBhelper.states.changed.ordinal());

		long insertId = database.update(DBhelper.EXPENSES_TABLE, values,
				DBhelper.COLUMN_ID + " = " + initial.getId(), null);
		
		Cursor cursor = database.query(DBhelper.EXPENSES_TABLE, allColumns,
				DBhelper.COLUMN_ID + " = " + initial.getId(), null, null, null, null);
		
		if(!(cursor.moveToFirst())){
			Log.e("DataSource", "cursor is null");
			return null;
		}else{
			
			Log.i("Inserted Curser ", Integer.toString(cursor.getColumnCount()));
		}

		expense newExpense = cursorToExpense(cursor);
		cursor.close();

		Log.i(MainActivity.TAG, "expense created " + Long.toString(insertId));
		return newExpense;
	}

	public expense createExpense(String string, String date, long receipt) {
		ContentValues values = new ContentValues();
		values.put(DBhelper.INPUT_STRING, string);
		values.put(DBhelper.DATE, date);
		values.put(DBhelper.RECEIPT, receipt);
		location pos = MainActivity.getLocation();
		values.put(DBhelper.LOCATION_X, pos.lat);
		values.put(DBhelper.LOCATION_Y, pos.lon);
		values.put(DBhelper.STATE, DBhelper.states.clean.ordinal());

		long insertId = database.insert(DBhelper.EXPENSES_TABLE, null, values);
		Cursor cursor = database.query(DBhelper.EXPENSES_TABLE, allColumns,
				DBhelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		
		if(!(cursor.moveToFirst())){
			Log.e("DataSource", "cursor is null");
			return null;
		}else{
			
			Log.i("Inserted Curser ", Integer.toString(cursor.getColumnCount()));
		}
		expense newExpense = cursorToExpense(cursor);
		cursor.close();

		Log.i(MainActivity.TAG, "expense created " + Long.toString(insertId));
		return newExpense;
	}

	public expense createExpense(expense e_here) {
		// This create Expense will not set the raw data string
		// so you will not see the price and name of the item
		// being added to the db, use the createExpense above this

		ContentValues values = new ContentValues();
		values.put(DBhelper.INPUT_STRING, e_here.getItem());
		values.put(DBhelper.DATE, e_here.getDate());
		values.put(DBhelper.RECEIPT, e_here.getReceipt());
		values.put(DBhelper.LOCATION_X, e_here.getLoc_x());
		values.put(DBhelper.LOCATION_Y, e_here.getLoc_y());
		values.put(DBhelper.STATE, DBhelper.states.clean.ordinal());

		
		long insertId = database.insert(DBhelper.EXPENSES_TABLE, null, values);
		Cursor cursor = database.query(DBhelper.EXPENSES_TABLE, allColumns,
				DBhelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		
		expense newExpense = cursorToExpense(cursor);
		cursor.close();

		Log.i( "expense created ", newExpense.toString());
		return newExpense;
	}
	
	public expense createExpenseWithDate(expense e,String Date) {
		ContentValues values = new ContentValues();
		values.put(DBhelper.INPUT_STRING, e.getItem());
		values.put(DBhelper.DATE, Date);
		values.put(DBhelper.RECEIPT, e.getReceipt());
		values.put(DBhelper.LOCATION_X, e.getLoc_x());
		values.put(DBhelper.LOCATION_Y, e.getLoc_y());
		values.put(DBhelper.STATE, DBhelper.states.clean.ordinal());

		long insertId = database.insert(DBhelper.EXPENSES_TABLE, null, values);
		Cursor cursor = database.query(DBhelper.EXPENSES_TABLE, allColumns,
				DBhelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Log.i("Inserted Curser ", Integer.toString(cursor.getColumnCount()));

		expense newExpense = cursorToExpense(cursor);
		cursor.close();

		Log.i(MainActivity.TAG, "expense created " + Long.toString(insertId));
		return newExpense;
	}

	public void dropDatabase() {
		List<expense> all = getAllExpenses();
		for (int i = 0; i < all.size(); i++) {
			invalidateExpense(all.get(i));
		}
	}

	public expense findById(long id) {
		Cursor c = database.query(DBhelper.EXPENSES_TABLE, allColumns, DBhelper.COLUMN_ID
				+ " = " + id, null, null, null, null, null);
		if(c.moveToFirst()){
			return cursorToExpense(c);
		}
		 return null;

	}

	public void invalidateExpense(expense e) {
		long id = e.getId();
		Log.i(MainActivity.TAG, "expense delete: " + id);
		// database.delete(DBhelper.EXPENSES_TABLE, DBhelper.COLUMN_ID + " = "
		// + id, null);
		ContentValues values = new ContentValues();
		values.put(DBhelper.STATE, DBhelper.states.deleted.ordinal());
		database.update(DBhelper.EXPENSES_TABLE, values, DBhelper.COLUMN_ID
				+ " = " + id, null);
	}

	public void removeInvalid() {
		database.delete(DBhelper.EXPENSES_TABLE, DBhelper.STATE + " = "
				+ DBhelper.states.deleted.ordinal(), null);
	}

	public List<expense> getAllExpenses() {
		List<expense> expenses = new ArrayList<expense>();
		Cursor cursor = database.query(DBhelper.EXPENSES_TABLE, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			expense e = cursorToExpense(cursor);
			//Log.i(MainActivity.TAG, e.getItem());
			expenses.add(e);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		expense[] ret = new expense[expenses.size()];
		expenses.toArray(ret);

		return expenses;
	}

	public List<expense> getAllCleanExpenses() {
		List<expense> expenses = new ArrayList<expense>();
		Cursor cursor = database.query(DBhelper.EXPENSES_TABLE, allColumns,
				DBhelper.STATE + " = " + DBhelper.states.clean.ordinal() + " OR " +
						DBhelper.STATE + " = " + DBhelper.states.changed.ordinal(), null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			expense e = cursorToExpense(cursor);
			//Log.i(MainActivity.TAG, e.getItem());
			expenses.add(e);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		expense[] ret = new expense[expenses.size()];
		expenses.toArray(ret);

		return expenses;
	}

	public List<expense> getCleanExpensesFor(String time) {
		List<expense> expenses = new ArrayList<expense>();
		Cursor cursor = database.query(DBhelper.EXPENSES_TABLE, allColumns,
				DBhelper.STATE + " = " + DBhelper.states.clean.ordinal()+ " OR " +
						DBhelper.STATE + " = " + DBhelper.states.changed.ordinal(), null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			expense e = cursorToExpense(cursor);

			if (e.getDate().contains(time)) {
				expenses.add(e);
				//Log.i(MainActivity.TAG, e.getItem());
			}
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();

		expense[] ret = new expense[expenses.size()];
		expenses.toArray(ret);

		return expenses;
	}

	public float getTotal() {
		float sum = 0;
		Cursor cursor = database.query(DBhelper.EXPENSES_TABLE, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			expense e = cursorToExpense(cursor);
			sum += e.getPrice();
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return sum;
	}

	public float getCleanTotalFor(String time) {
		float sum = 0;
		Cursor cursor = database.query(DBhelper.EXPENSES_TABLE, allColumns,
				DBhelper.STATE + " = " + DBhelper.states.clean.ordinal()+ " OR " +
						DBhelper.STATE + " = " + DBhelper.states.changed.ordinal(), null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			expense e = cursorToExpense(cursor);
			if (e.getDate().contains(time)) {
				sum += e.getPrice();
			}
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return sum;
	}

	private expense cursorToExpense(Cursor cursor) {
		expense e = new expense();
		e.setString(cursor.getString(0));
		e.setDate(cursor.getString(1));
		e.setReceipt(cursor.getLong(2));
		e.setId(cursor.getLong(3));
		e.setLoc_x(cursor.getFloat(4));
		e.setLoc_y(cursor.getDouble(5));
		e.setState(cursor.getInt(6));
		return e;
	}

	public void validateExpense(expense e) {
		long id = e.getId();

		ContentValues values = new ContentValues();
		values.put(DBhelper.STATE, DBhelper.states.clean.ordinal());
		database.update(DBhelper.EXPENSES_TABLE, values, DBhelper.COLUMN_ID
				+ " = " + id, null);
	}

}
