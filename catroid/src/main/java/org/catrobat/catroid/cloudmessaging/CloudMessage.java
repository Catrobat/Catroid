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

package org.catrobat.catroid.cloudmessaging;

import android.support.annotation.VisibleForTesting;
import android.webkit.URLUtil;

import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

public class CloudMessage {

	public static final String TITLE = "title";
	public static final String MESSAGE = "message";
	public static final String WEB_PAGE_URL = "link";

	private String title;
	private String message;
	private String url;

	@VisibleForTesting
	public CloudMessage() {
	}

	@VisibleForTesting
	public CloudMessage(HashMap<String, String> data) {
		setNotificationTitle(data.get(TITLE));
		setNotificationMessage(data.get(MESSAGE));
		setNotificationUrl(data.get(WEB_PAGE_URL));
	}

	public CloudMessage(RemoteMessage remoteMessage) {
		setNotificationTitle(remoteMessage.getNotification().getTitle());
		setNotificationMessage(remoteMessage.getNotification().getBody());
		setNotificationUrl(remoteMessage.getData().get(WEB_PAGE_URL));
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public String getWebPageUrl() {
		return url;
	}

	public void setNotificationTitle(String title) {
		this.title = title;
	}

	public void setNotificationMessage(String message) {
		this.message = message;
	}

	public void setNotificationUrl(String url) {
		if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
			this.url = url;
		}
	}

	public boolean isValidData() {
		return !(isStringNullOrEmpty(getTitle()) || isStringNullOrEmpty(getMessage()) || isStringNullOrEmpty(getWebPageUrl()));
	}

	private boolean isStringNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}
}
