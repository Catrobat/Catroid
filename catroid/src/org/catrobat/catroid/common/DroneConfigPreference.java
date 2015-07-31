/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.common;

public abstract class DroneConfigPreference {

	public enum Preferences {
		FIRST,
		SECOND,
		THIRD,
		FOURTH,
		FIFTH;

		public static String[] getPreferenceCodes() {
			String[] valueStrings = new String[values().length];

			for (int i = 0; i < values().length; ++i) {
				valueStrings[i] = values()[i].name();
			}

			return valueStrings;
		}

		public String getPreferenceCode() {
			return getPreferenceCode(this);
		}

		public static String getPreferenceCode(DroneConfigPreference.Preferences preference) {
			return preference.name();
		}

		public static DroneConfigPreference.Preferences getPreferenceFromPreferenceCode(String preferenceCode) {
			if (preferenceCode == null) {
				return Preferences.FIRST;
			}

			try {
				return valueOf(preferenceCode);
			} catch (IllegalArgumentException e) {
				return Preferences.FIRST;
			}
		}
	}

	public static final String TAG = DroneConfigPreference.class.getSimpleName();
}
