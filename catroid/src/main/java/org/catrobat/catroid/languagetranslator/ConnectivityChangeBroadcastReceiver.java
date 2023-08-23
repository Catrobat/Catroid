/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.languagetranslator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ConnectivityChangeBroadcastReceiver extends BroadcastReceiver {

	public static final String TAG = "ConnectivityChangeBR";
	private final LanguageTranslator languageTranslator;
	private boolean hasConnection = true;

	public ConnectivityChangeBroadcastReceiver(LanguageTranslator languageTranslator) {
		this.languageTranslator = languageTranslator;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		onConnectionChanged(languageTranslator.hasConnection(context));
	}

	public void onConnectionChanged(boolean hasConnection) {
		if (this.hasConnection != hasConnection) {
			String status = hasConnection ? "available" : "unavailable";
			Log.i(TAG, "Network connection " + status);
			this.hasConnection = hasConnection;
			languageTranslator.updateDownloadNotificationsConnectionAvailable(hasConnection);
		}
	}

	public boolean getHasConnection() {
		return hasConnection;
	}
}
