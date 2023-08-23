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

public class CancelDownloadBroadcastReceiver extends BroadcastReceiver {

	public static final String TAG = "CancelDownloadBroadcastReceiver";
	private final LanguageTranslator languageTranslator;

	public CancelDownloadBroadcastReceiver(LanguageTranslator languageTranslator) {
		this.languageTranslator = languageTranslator;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action == null || !action.equals(languageTranslator.NOTIFICATION_CANCEL_ACTION)) {
			return;
		}
		int notificationId = intent.getExtras().getInt(languageTranslator.NOTIFICATION_CANCEL_EXTRAS_KEY);
		onCancelDownload(notificationId);
	}

	public void onCancelDownload(int notificationId) {
		languageTranslator.cancelRunningDownload(notificationId);
	}
}
