package com.example.please;

import java.util.Locale;

import android.annotation.SuppressLint;

@SuppressLint("DefaultLocale")
public class expense {
	private String date;
	private String string;
	private float price;
	private long receipt;
	private long id;
	private double loc_x;
	private double loc_y;
	private int state = 0;

	
	
	public expense(expense e) {
		super();
		this.date = e.getDate();
		this.string = e.getString();
		this.price = e.getPrice();
		this.receipt = e.getReceipt();
		this.id = e.getId();
		this.loc_x = e.getLoc_x();
		this.loc_y = e.getLoc_y();
		this.state = e.getState();
	}

	public expense() {
		super();
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public String addMonthsToDate(int increment) {
		String splitDate[] = date.split("/");

		String day = splitDate[0];
		String year = splitDate[2].split(":")[0];
		String time = date.split(":")[1];
		String hours = time.split("/")[0];
		String mins = time.split("/")[1];
		String month = date.split("/")[1];
		String maridian = "";

		Integer tmpMonth = Integer.parseInt(month) + increment;
		int yearIncrement = 0;

		while (tmpMonth > 12) {
			yearIncrement++;
			tmpMonth -= 12;
		}

		Integer tmpYear = Integer.parseInt(year) + yearIncrement;

		String curTime = day + "/" + tmpMonth.toString() + "/"
				+ tmpYear.toString() + ":" + hours + "/" + mins;

		return curTime;
	}

	public String getDatePretty() {

		String day = date.split("/")[0];
		String time = date.split(":")[1];
		String hours = time.split("/")[0];
		String mins = time.split("/")[1];
		String month = date.split("/")[1];
		String maridian = "";

		int hoursInt = Integer.parseInt(hours);
		if (hoursInt >= 12) {
			hoursInt -= 12;
			maridian = "pm";
		} else {
			maridian = "am";
		}

		if (mins.length() == 1) {
			mins = "0" + mins;
		}

		return day + "/" + month + " at " + hoursInt + ":" + mins + " "
				+ maridian;
	}

	public void setDate(String string) {
		this.date = string;
	}

	@SuppressLint("DefaultLocale")
	public String getItem() {
		String stage1 = string.replaceFirst("(\\D*)([0-9]*\\.?[0-9]+)(.*)",
				"$1" + MainActivity.spliter + "$2");

		if (!stage1.startsWith(MainActivity.spliter)) {
			String[] temp = stage1.split(MainActivity.spliter);

			if (temp.length < 2) {
				return "";
			}

			String item = temp[0].substring(0, 1).toUpperCase(Locale.US)
					+ temp[0].substring(1);

			if (item.isEmpty()) {
				return "";
			}
			return item;
		}

		return "";

	}

	public float getPrice() {

		String[] temp = string.replaceFirst("(\\D*)([0-9]*\\.?[0-9]+)(.*)",
				"$1" + MainActivity.spliter + "$2").split(MainActivity.spliter);

		if (temp.length < 2) {
			return 0;
		}

		float price = Float.parseFloat(temp[1]);

		if (Float.toString(price).toString().isEmpty()) {
			return 0;
		}

		return price;
	}

	public long getReceipt() {
		return receipt;
	}

	public void setReceipt(long receipt) {
		this.receipt = receipt;
	}

	@Override
	public String toString() {
		return (this.getItem() + " " + getPrice()+ " " +  getDate());
	}

	public double getLoc_x() {
		return loc_x;
	}

	public void setLoc_x(double lat) {
		this.loc_x = lat;
	}

	public double getLoc_y() {
		return loc_y;
	}

	public void setLoc_y(double d) {
		this.loc_y = d;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

}
