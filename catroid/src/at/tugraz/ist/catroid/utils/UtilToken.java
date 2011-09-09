/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.utils;

import android.util.Log;

public class UtilToken {
	private static final String LOG_TAG = UtilToken.class.getSimpleName();

	public static String calculateToken(String username, String password) {

		String md5Username = Utils.md5Checksum(username).toLowerCase();
		String md5Password = Utils.md5Checksum(password).toLowerCase();

		String token = Utils.md5Checksum(md5Username + ":" + md5Password);

		Log.i(LOG_TAG, "token calculated: " + token);
		return token.toLowerCase();
	}

}