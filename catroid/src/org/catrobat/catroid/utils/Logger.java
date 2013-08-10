/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.utils;

import org.catrobat.catroid.BuildConfig;

import android.util.Log;

public class Logger {

	private static boolean debugging = BuildConfig.DEBUG; //If it is a release, it is turned off by default

	public static boolean isDebugging() {
		return debugging;
	}

	public static void setDebugging(boolean debug) {
		debugging = debug;
	}

	public static int d(String tag, String msg) {
		if (debugging) {
			return Log.d(tag, msg);
		}
		return -1;
	}

	public static int e(String tag, String msg) {
		if (debugging) {
			return Log.e(tag, msg);
		}
		return -1;
	}

	public static int v(String tag, String msg) {
		if (debugging) {
			return Log.v(tag, msg);
		}
		return -1;
	}

	public static int i(String tag, String msg) {
		if (debugging) {
			return Log.i(tag, msg);
		}
		return -1;
	}

	public static int w(String tag, String msg) {
		if (debugging) {
			return Log.w(tag, msg);
		}
		return -1;
	}

	public static int d(String tag, String msg, Throwable tr) {
		if (debugging) {
			return Log.d(tag, msg, tr);
		}
		return -1;
	}

	public static int e(String tag, String msg, Throwable tr) {
		if (debugging) {
			return Log.e(tag, msg, tr);
		}
		return -1;
	}

	public static int v(String tag, String msg, Throwable tr) {
		if (debugging) {
			return Log.v(tag, msg, tr);
		}
		return -1;
	}

	public static int i(String tag, String msg, Throwable tr) {
		if (debugging) {
			return Log.i(tag, msg, tr);
		}
		return -1;
	}

	public static int w(String tag, String msg, Throwable tr) {
		if (debugging) {
			return Log.w(tag, msg, tr);
		}
		return -1;
	}

}
