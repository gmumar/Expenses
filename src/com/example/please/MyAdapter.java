package com.example.please;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MyAdapter extends ArrayAdapter<expense> {

	private Context c;
	// private List<expense> expenses;
	private expense e;
	private DataSource dbs;
	private float angle = 0;

	public MyAdapter(Context context, int resource, List<expense> values) {
		super(context, resource, values);
		// TODO Auto-generated constructor stub
		this.c = context;
		dbs = new DataSource(c);
		dbs.open();
	}

	@SuppressLint("WrongCall")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View row = inflater.inflate(R.layout.expense_row, parent, false);

		// Log.i(MainActivity.TAG, "Getting View " + MainActivity.animate_flag);
		e = MainActivity.all_expenses.get(position);

		TextView item = (TextView) row.findViewById(R.id.row_item);
		TextView price = (TextView) row.findViewById(R.id.row_price);
		TextView date = (TextView) row.findViewById(R.id.row_date);

		ImageButton del = (ImageButton) row.findViewById(R.id.row_del);
		ImageButton edit = (ImageButton) row.findViewById(R.id.row_edit);

		item.setTextSize(18);
		item.setTypeface(MainActivity.app_typeface);
		item.setText(e.getItem());// e.getItem()

		price.setText("$ " + Float.toString(e.getPrice()));
		price.setTypeface(MainActivity.app_typeface);

		date.setTextColor(Color.GRAY);
		date.setText(e.getDatePretty());
		date.setTextSize(10);

		row.setBackgroundColor(Color.WHITE);
		angle = (e.getPrice() / MainActivity.CurrentTotal) * 360;
		MyImageView chart = (MyImageView) row.findViewById(R.id.imageView1);
		chart.angle = angle;
		chart.setWillNotDraw(false);

		row.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final expense exp = MainActivity.all_expenses.get(position);
				final EditText repeatTxt;

				Dialog rowOptions = new Dialog(c);
				rowOptions.setContentView(R.layout.row_options_dialog);
				repeatTxt = (EditText) rowOptions.findViewById(R.id.RepeatText);

				rowOptions.findViewById(R.id.RepeatButton).setOnClickListener(
						new OnClickListener() {
							Integer count = 0;

							@Override
							public void onClick(View v) {
								if (!repeatTxt.getText().toString().isEmpty()) {
									count = Integer.parseInt(repeatTxt
											.getText().toString());
									for (int i = 1; i < count; i++) {
										exp.setDate(exp.addMonthsToDate(i));
										MainActivity.dbs.createExpenseWithDate(
												exp, exp.addMonthsToDate(i));
									}

								} else {
									Toast.makeText(c,
											"Enter Number of months to repeat",
											Toast.LENGTH_SHORT).show();
								}

							}
						});

				rowOptions.show();
			}
		});

		del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				e = MainActivity.all_expenses.get(position);
				MainActivity.all_expenses.remove(position);
				dbs.invalidateExpense(e);
				MainActivity.update_list();
				Log.i(MainActivity.TAG, "Delete " + e.getId());
			}

		});

		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				e = MainActivity.all_expenses.get(position);
				expense newExpense = null;
				try {
					String string = MainActivity.readExpenseField();
					newExpense = dbs.updateExpense(e, string, 0);
					MainActivity.all_expenses.remove(position);
					MainActivity.all_expenses.add(position, newExpense);
					MainActivity.update_list();
				} catch (EmptyInputException e1) {
					Toast.makeText(c,
							"Please enter item followed by its price.",
							Toast.LENGTH_SHORT).show();
					return;
				}

			}

		});

		if (MainActivity.animate_flag == true) {

			if (position == MainActivity.all_expenses.size() - 1) {
				row.setTranslationX(1000);
				row.animate().setDuration(1000).translationX(0)
						.withEndAction(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								MainActivity.animate_flag = false;
							}
						});
			}

		}
		return row;
	}

}
