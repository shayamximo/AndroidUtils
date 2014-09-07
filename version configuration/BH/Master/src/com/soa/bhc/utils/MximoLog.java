package com.soa.bhc.utils;

import android.util.Log;

public class MximoLog {

	public static void d(String tag, String msg) {
		if (ConfigurationFile.getInstance().getShowLogs())
			Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (ConfigurationFile.getInstance().getShowLogs())
			Log.e(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (ConfigurationFile.getInstance().getShowLogs())
			Log.i(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (ConfigurationFile.getInstance().getShowLogs())
			Log.v(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (ConfigurationFile.getInstance().getShowLogs())
			Log.w(tag, msg);
	}
}
