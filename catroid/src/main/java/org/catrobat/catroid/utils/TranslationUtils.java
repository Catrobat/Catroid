/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.utils;

import android.content.Context;
import android.util.Log;

import com.google.common.base.CharMatcher;

import org.catrobat.catroid.common.Constants;

import java.util.List;
import java.util.Locale;

public final class TranslationUtils {
	private static final String TAG = TranslationUtils.class.getSimpleName();

	private static final CharMatcher MATCHER =
			CharMatcher.javaLetterOrDigit()
					.or(CharMatcher.is('.'))
					.or(CharMatcher.is('_'))
					.or(CharMatcher.whitespace())
					.precomputed();

	private TranslationUtils() {
	}

	public static String getStringResourceByName(String originalName, Context context) {
		String key = getStringResourceWithPrefixByName(originalName);

		String packageName = context.getPackageName();
		int resId = context.getResources().getIdentifier(key, "string", packageName);
		Log.d(TAG, "Loading resId:" + resId + " for key: " + key);
		if (resId != 0) {
			return context.getString(resId);
		} else {
			Log.d(TAG, "No resId for key: " + key);
		}
		return originalName;
	}

	public static String getStringResourceByName(String name) {
		return getStringResourceName(name);
	}

	public static void addToTranslationList(List<String> stringEntries, String value) {
		addIfNotInList(stringEntries, createStringEntry(value));
	}

	public static void writeStringEntries(List<String> stringEntries) {
		UtilFile.writeFile(Utils.buildPath(Constants.TMP_TRANSLATION_PATH), stringEntries);
	}

	private static String getStringResourceWithPrefixByName(String name) {
		return getStringResourceName(Constants.TEMPLATE_TRANSLATION_PREFIX.concat(name));
	}

	private static void addIfNotInList(List<String> stringEntriesList, String stringEntry) {
		if (!contains(stringEntry, stringEntriesList)) {
			stringEntriesList.add(stringEntry);
		}
	}

	private static String createStringEntry(String key, String value) {
		return "    <string name=\""
				+ key
				+ "\" tools:ignore=\"UnusedResources\">"
				+ value.replace("'", "\\'").replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;").replace("-", "–").replace("%", "%%").replaceAll("\\s+", " ").trim()
				+ "</string>\n";
	}

	private static String createStringEntry(String value) {
		String key = getStringResourceWithPrefixByName(value);
		return createStringEntry(key, value);
	}

	private static String getStringResourceName(String key) {
		return removeInvalidCharacters(key).toLowerCase(Locale.US);
	}

	private static String removeInvalidCharacters(String string) {
		return MATCHER.retainFrom(string).trim().replaceAll("\\s+", "_").replace("'", "\\'");
	}

	private static boolean contains(String stringToCheck, List<String> list) {
		String keyToCheck = getKeyOfStringEntry(stringToCheck);

		for (String stringInList : list) {
			String keyInList = getKeyOfStringEntry(stringInList);
			if (keyToCheck.equalsIgnoreCase(keyInList)) {
				return true;
			}
		}
		return false;
	}

	private static String getKeyOfStringEntry(String entry) {
		String key = entry.substring(entry.indexOf('\"') + 1);
		return key.substring(0, key.indexOf('\"'));
	}
}
