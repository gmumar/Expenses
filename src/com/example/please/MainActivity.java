package com.example.please;

import java.text.DateFormatSymbols;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxException;

class location {
	double lat, lon;
}

public class MainActivity extends Activity {

	public static final String TAG = "Exlist";
	public static final String spliter = "qazxswedc";
	public static final String time_format = "%A at %I:%M %p";
	public static final int MONTH_OFFSET = 1;
	public static float CurrentTotal = 0;

	public static Typeface app_typeface;

	static DataSource dbs;

	private ImageButton submit, settings, prevMonth, nextMonth;
	//private Button prevMonth, nextMonth;
	private static EditText e_field;
	public static ListView expense_list;
	static MyAdapter adapter;
	public static Time currentTime;
	private static TextView total;
	private TextView month;
	private static Context c;
	public static int lookForMonth;
	public static int lookForYear;
	volatile static boolean animate_flag;
	volatile static boolean del_animation_flag;

	private DropboxManager dropbox;

	// static String expense_item_price;
	// String[] expense;
	static List<expense> all_expenses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbs = new DataSource(this);
		dbs.open();

		animate_flag = false;
		del_animation_flag = false;
		c = this;

		dropbox = new DropboxManager(this);

		currentTime = new Time(Time.getCurrentTimezone());
		submit = (ImageButton) findViewById(R.id.submit);
		prevMonth = (ImageButton) findViewById(R.id.PreviousMonth1);
		nextMonth = (ImageButton) findViewById(R.id.NextMonth);
		settings = (ImageButton) findViewById(R.id.settings);
		e_field = (EditText) findViewById(R.id.e_field);
		expense_list = (ListView) findViewById(R.id.expense_list);
		total = (TextView) findViewById(R.id.total);
		month = (TextView) findViewById(R.id.month);

		app_typeface = Typeface.createFromAsset(getAssets(),
				"fonts/Champagne.ttf"); // Opificio-Serif-rounded.ttf");
		total.setTypeface(app_typeface);
		total.setTextColor(ColorStateList.valueOf(Color.RED));

		month.setTypeface(app_typeface);

		e_field.setTypeface(app_typeface);

		currentTime.setToNow();

		lookForMonth = currentTime.month + MONTH_OFFSET;
		lookForYear = currentTime.year;

		init_list();
		

		e_field.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					enter_into_database();
					return true;
				}
				return false;
			}

		});

		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				enter_into_database();
			}
		});

		prevMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				lookForMonth--;
				if (lookForMonth < 1) {
					lookForMonth = 12;
					lookForYear--;
				}
				init_list();
			}
		});

		nextMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				lookForMonth++;
				if (lookForMonth > 12) {
					lookForMonth = 1;
					lookForYear++;
				}
				init_list();
			}
		});

		settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Dialog settingsDialog = new Dialog(c);
				settingsDialog.setContentView(R.layout.settingdialogview);

				settingsDialog.findViewById(R.id.DBXSignIn).setEnabled(
						!dropbox.isLinked());

				settingsDialog.findViewById(R.id.DBXSignIn).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// Toast.makeText(c, "sign in",
								// Toast.LENGTH_SHORT).show();
								dropbox.connect();
								settingsDialog.dismiss();
							}
						});

				settingsDialog.findViewById(R.id.DBXUpload).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								// dropbox.disconnect();
								try {
									dropbox.syncAll();
									
									settingsDialog.dismiss();
									
								} catch (DbxException e) {
									// TODO Auto-generated catch block
									Toast.makeText(c, "Upload Failed", Toast.LENGTH_LONG).show();
									e.printStackTrace();
								}
							}
						});

				settingsDialog.findViewById(R.id.DBXDownLoad)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								try {
									dropbox.getAll();
									//init_list();
									init_list();
									settingsDialog.dismiss();
								} catch (DbxException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
				
				settingsDialog.findViewById(R.id.DBXClear)
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							try {
								if(readExpenseField().contentEquals("yup delete all"))
									dropbox.clearDatastore();
							} catch (EmptyInputException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							settingsDialog.dismiss();
						} catch (DbxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				settingsDialog.show();
			}
		});
	}

	private void enter_into_database() {
		String expenseString = null;
		try {
			expenseString = readExpenseField();
		} catch (EmptyInputException e) {
			Toast.makeText(this, "Please enter item followed by its price.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		int tempMonth = currentTime.month + MONTH_OFFSET;
		String curTime = currentTime.monthDay + "/" + tempMonth + "/" + currentTime.year + ":"
				+ currentTime.hour + "/" + currentTime.minute;

		Log.i(TAG, "Item " + expenseString);
		expense e_loc = dbs.createExpense(expenseString, curTime, 0);
		e_field.setText("");
		animate_flag = true;
		// all_expenses.add(e_loc);
		adapter.add(e_loc);
		update_list();

		//init_list();
	}
	
	static public void enter_into_db(expense nonlocal_e){
		dbs.createExpense(nonlocal_e.getString(),nonlocal_e.getDate(),nonlocal_e.getReceipt());
	}
	
	
	public static String readExpenseField() throws EmptyInputException  {
		String expense_item_price = e_field.getText().toString();
		e_field.setText("");
		
		if(expense_item_price.isEmpty())
			throw new EmptyInputException();

		return expense_item_price;
	}

	static public void update_list() {

		//Log.i(TAG, "Updating list");
		CurrentTotal = dbs.getCleanTotalFor(makeLookForDate(lookForMonth,lookForYear));
		total.setText("Total: $ "
				+ Float.toString(Math.round(CurrentTotal*10)/10f));
		
		adapter.notifyDataSetChanged();
		expense_list.setSelection(adapter.getCount() - 1);
		
	}

	public void init_list() {
		// TODO Auto-generated method stub
		// all_expenses = dbs.getAllExpenses();
		if(lookForMonth == currentTime.month + MONTH_OFFSET){
			month.setText(makeLookForDatePretty(lookForMonth, lookForYear)+" *");
		}else{
			month.setText(makeLookForDatePretty(lookForMonth, lookForYear));
		}
		
		all_expenses = dbs.getCleanExpensesFor(makeLookForDate(lookForMonth,
				lookForYear));
		adapter = new MyAdapter(this, R.layout.expense_row, all_expenses);
		expense_list.setAdapter(adapter);
		expense_list.setSelection(adapter.getCount() - 1);

		//total.setText("Total: $ "
		//		+ Float.toString(dbs.getCleanTotalFor(makeLookForDate(lookForMonth,
		//				lookForYear))));
		// total.setText("Total: " + Float.toString(dbs.getTotal()));
		update_list();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private static String makeLookForDate(int month, int year) {
		return "/" + Integer.toString(month) + "/" + Integer.toString(year)
				+ ":";
	}

	private String makeLookForDatePretty(int month, int year) {
		//Log.i("Month", Integer.toString(month - 1));
		return new DateFormatSymbols().getMonths()[month - 1] + " "
				+ Integer.toString(year);
	}

	public static location getLocation() {
		location pos = new location();

		LocationManager locationManager = (LocationManager) c
				.getSystemService(LOCATION_SERVICE);
		// Criteria criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// String bestProvider = locationManager.getBestProvider(criteria,
		// false);
		String bestProvider = LocationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(bestProvider);
		try {
			pos.lat = location.getLatitude();
			pos.lon = location.getLongitude();
		} catch (NullPointerException e) {
			pos.lat = -1.0;
			pos.lon = -1.0;
		}

		//Log.i(TAG + " position", pos.lat + " " + pos.lon);
		return pos;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 5) {
			if (resultCode == Activity.RESULT_OK) {
				dropbox.connectSuccess();
			} else {
				dropbox.connectFail();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

}
