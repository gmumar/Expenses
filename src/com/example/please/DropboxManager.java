package com.example.please;

import java.util.List;

import android.util.Log;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastore;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFields;
import com.dropbox.sync.android.DbxRecord;
import com.dropbox.sync.android.DbxTable;

public class DropboxManager {

	private DbxAccountManager instance = null;
	private DbxAccount userAccount = null;
	private static MainActivity mainActivity = null;

	public static String DBX_APP_KEY = "uambwgvleq5envr";
	public static String DBX_APP_SECRET = "9mpaakjbkor1vof";

	public DropboxManager(MainActivity act) {
		mainActivity = act;
		Log.i("dbx", "Context set");

		if (mainActivity != null && instance == null) {
			Log.i("dbx", "dbx manager called");
			instance = DbxAccountManager.getInstance(
					mainActivity.getApplicationContext(), DBX_APP_KEY,
					DBX_APP_SECRET);
		} else {
			Log.e("dbx", "instance not gotten");
		}
	}

	public void connect() {
		if (!isLinked()) {
			instance.startLink(mainActivity, 5);
		} else {
			Log.i("dbx", "already linked");
		}

	}

	private void setAccount() {
		if (isLinked() && userAccount == null) {
			userAccount = instance.getLinkedAccount();
		}
	}

	public boolean isLinked() {
		return (instance.hasLinkedAccount());
	}

	public void connectSuccess() {
		Log.i("dbx", "connected");
		if (isLinked()) {
			Log.i("dbx", "Got account");
		}
	}

	public void connectFail() {

	}

	public void disconnect() {
		if (isLinked()) {
			instance.unlink();
		} else {
			Log.i("dbx", "already unlinked");
		}

	}

	public void syncAll() throws DbxException {
		// TODO Auto-generated method stub

		if (isLinked()) {
			setAccount();

			DbxDatastore store = DbxDatastore.openDefault(userAccount);
			DbxTable table = store.getTable(DBhelper.EXPENSES_TABLE);

			DataSource dbInstance = new DataSource(
					mainActivity.getApplicationContext());
			if (!dbInstance.isOpen())
				dbInstance.open();
			List<expense> expenses = dbInstance.getAllExpenses();

			DbxFields tempField = null;
			for (int i = 0; i < expenses.size(); i++) {
				tempField = expenseToField(expenses.get(i));

				if (expenses.get(i).getState() == DBhelper.states.deleted
						.ordinal()) {
					if (table.query(tempField).hasResults()) {
						table.query(tempField).asList().get(0).deleteRecord();
						store.sync();
					}
				} else if (expenses.get(i).getState() == DBhelper.states.changed
						.ordinal()) {
					List<DbxRecord> target = table.query(
							new DbxFields().set(DBhelper.COLUMN_ID, expenses
									.get(i).getId())).asList();
									
					if(target.size()!=0){
						target.get(0).deleteRecord();
					}
					
					
					if (!table.query(tempField).hasResults()) {
						table.insert(tempField);
						store.sync();
					}
				} else {

					if (!table.query(tempField).hasResults()) {
						table.insert(tempField);
						store.sync();
					}
				}

			}

			dbInstance.removeInvalid();
			if (dbInstance.isOpen())
				dbInstance.close();
			Toast.makeText(mainActivity.getApplicationContext(), "Upload Successful", Toast.LENGTH_LONG).show();
			store.close();
		}
	}

	public void getAll() throws DbxException {
		// TODO Auto-generated method stub

		if (isLinked()) {
			setAccount();

			DbxDatastore store = DbxDatastore.openDefault(userAccount);
			DbxTable table = store.getTable(DBhelper.EXPENSES_TABLE);
			store.sync();

			DataSource dbInstance = new DataSource(
					mainActivity.getApplicationContext());
			if (!dbInstance.isOpen())
				dbInstance.open();
			List<DbxRecord> expensesCloud = table.query().asList();
			// dbInstance.dropDatabase();
			expense tempExpense = null;
			expense expenseDb = null;

			//Log.i("DBX", Integer.toString(expensesCloud.size()));
			
			for (int i = 0; i < expensesCloud.size(); i++) {
				tempExpense = fieldToExpense(expensesCloud.get(i));
				expenseDb = dbInstance.findById(tempExpense.getId());
				
				//Log.i("DBX", tempExpense.getString());
				
				if (expenseDb == null) {
					// On the cloud but missing in local db
					//Log.i("BDX null", expenseDb.getString());
					if(dbInstance.findById(tempExpense.getId())==null)
						dbInstance.createExpense(tempExpense);
				} else {
					Log.i("dbx", expenseDb.getString());
					if (expenseDb.getState() == DBhelper.states.deleted
							.ordinal()) {
						// expenseDb.setState(DBhelper.states.clean.ordinal());
						dbInstance.validateExpense(expenseDb);
					}
				}

			}
			if (dbInstance.isOpen())
				dbInstance.close();

			store.close();
		}
	}

	private DbxFields expenseToField(expense e) {
		DbxFields field = new DbxFields();

		field.set(DBhelper.INPUT_STRING, e.getString());
		field.set(DBhelper.DATE, e.getDate());
		field.set(DBhelper.LOCATION_X, e.getLoc_x());
		field.set(DBhelper.LOCATION_Y, e.getLoc_y());
		field.set(DBhelper.COLUMN_ID, e.getId());

		return field;
	}

	private expense fieldToExpense(DbxFields f) {
		expense e = new expense();

		e.setDate(f.getString(DBhelper.DATE));
		e.setString(f.getString(DBhelper.INPUT_STRING));
		e.setLoc_x(f.getDouble(DBhelper.LOCATION_X));
		e.setLoc_y(f.getDouble(DBhelper.LOCATION_Y));
		e.setId(f.getLong(DBhelper.COLUMN_ID));

		return e;
	}

	public void clearDatastore() throws DbxException {
		if (isLinked()) {
			setAccount();
			DbxDatastore store = DbxDatastore.openDefault(userAccount);
			DbxTable table = store.getTable(DBhelper.EXPENSES_TABLE);

			List<DbxRecord> expensesCloud = table.query().asList();
			for (int i = 0; i < expensesCloud.size(); i++) {
				// Log.i("DBX", expensesCloud.get(i).getString("id"));
				expensesCloud.get(i).deleteRecord();
				store.sync();
			}
			store.close();
		}

	}

}
