/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.utils;

import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class UtilDeviceInfo {
	public static final String SERVER_VALUE_FOR_UNDEFINED_COUNTRY = "undef";

	public static String getUserEmail(Context context) {
		if (context == null) {
			return null;
		}
		Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
		for (Account account : accounts) {
			return account.name;
		}
		return null;
	}

	public static String getUserLanguageCode(Context context) {
		return Locale.getDefault().getLanguage();
	}

	public static String getUserCountryCode(Context context) {
		String country = Locale.getDefault().getCountry();
		if (country.length() == 0) {
			country = SERVER_VALUE_FOR_UNDEFINED_COUNTRY;
		}
		return country;
	}

}
