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

import android.app.PendingIntent;
import android.content.Context;
import at.tugraz.ist.catroid.ui.MainMenuActivity;

public class NotificationData {

	private PendingIntent pendingIntent;
	private Context context;
	private String name;
	private String notificationTitle;
	private MainMenuActivity activity;
	private int id;

	public NotificationData(PendingIntent pendingIntent, Context context, String name, String notificationTitle,
			MainMenuActivity activity, int id) {
		this.pendingIntent = pendingIntent;
		this.context = context;
		this.name = name;
		this.notificationTitle = notificationTitle;
		this.activity = activity;
		this.id = id;
	}

	public PendingIntent getPendingIntent() {
		return pendingIntent;
	}

	public void setPendingIntent(PendingIntent pendingIntent) {
		this.pendingIntent = pendingIntent;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNotificationTitle() {
		return notificationTitle;
	}

	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}

	public MainMenuActivity getActivity() {
		return activity;
	}

	public void setActivity(MainMenuActivity activity) {
		this.activity = activity;
	}
}
