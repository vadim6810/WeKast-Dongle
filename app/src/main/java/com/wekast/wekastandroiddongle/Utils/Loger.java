package com.wekast.wekastandroiddongle.Utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressLint("SimpleDateFormat")
//	public class Loger implements Constants {
public class Loger {
//	private static final String SYSTEM_SERVICE_PATH = "mnt/sdcard/weKast/system/";
	// for dongle path
//	private static final String SYSTEM_SERVICE_PATH = "/data/user/0/com.wekast.wekastandroiddongle";
	private static String SYSTEM_SERVICE_PATH;
	private static final String LOGS_PATH = "/logs/";
	private static Loger instance = null;

	public void setAppPath(String string) {
		SYSTEM_SERVICE_PATH = string;
	}

	public static Loger getInstance() {
		if (instance == null)
			instance = new Loger();
		return instance;
	}

	public void createLogger(String exception) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy:MM:dd||k:mm:ss - ");
		String date = df.format(Calendar.getInstance().getTime()) + exception;
		Log.d("Info", date);

		createLogsFolder();
		
		try {
			df = new SimpleDateFormat("yyyyMMdd-kmm");//changed DateFormat!!!!!
			String fileName = df.format(Calendar.getInstance().getTime());
			File file = new File(SYSTEM_SERVICE_PATH + LOGS_PATH + fileName + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream out = new FileOutputStream(file, true);
			PrintStream printer = new PrintStream(out);
			printer.println(date);
			printer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.d("Info", "FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("Info", "IOException: " + e.getMessage());
		}
	}

	private void createLogsFolder() {
		File folder = new File(SYSTEM_SERVICE_PATH + LOGS_PATH);
//		boolean success = true;
		if (!folder.exists()) {
//			success = folder.mkdir();
			folder.mkdir();
		}
//		if (success) {
//			// Do something on success
//		} else {
//			// Do something else on failure
//		}
	}

}
