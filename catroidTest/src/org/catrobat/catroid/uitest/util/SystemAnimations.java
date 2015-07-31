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

package org.catrobat.catroid.uitest.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

// Taken from https://gist.github.com/xrigau/11284124
class SystemAnimations {

	private static final String ANIMATION_PERMISSION = "android.permission.SET_ANIMATION_SCALE";
	private static final float DISABLED = 0.0f;
	private static final float DEFAULT = 1.0f;

	private final Context context;

	SystemAnimations(Context context) {
		this.context = context;
	}

	void disableAll() {
		int permStatus = context.checkCallingOrSelfPermission(ANIMATION_PERMISSION);
		if (permStatus == PackageManager.PERMISSION_GRANTED) {
			setSystemAnimationsScale(DISABLED);
		}
	}

	void enableAll() {
		int permStatus = context.checkCallingOrSelfPermission(ANIMATION_PERMISSION);
		if (permStatus == PackageManager.PERMISSION_GRANTED) {
			setSystemAnimationsScale(DEFAULT);
		}
	}

	private void setSystemAnimationsScale(float animationScale) {
		try {
			Class<?> windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
			Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder.class);
			Class<?> serviceManagerClazz = Class.forName("android.os.ServiceManager");
			Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
			Class<?> windowManagerClazz = Class.forName("android.view.IWindowManager");
			Method setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales", float[].class);
			Method getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales");

			IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
			Object windowManagerObj = asInterface.invoke(null, windowManagerBinder);
			float[] currentScales = (float[]) getAnimationScales.invoke(windowManagerObj);
			for (int i = 0; i < currentScales.length; i++) {
				currentScales[i] = animationScale;
			}
			setAnimationScales.invoke(windowManagerObj, new Object[] { currentScales });
		} catch (Exception e) {
			Log.e("SystemAnimations", "Could not change animation scale to " + animationScale + " :'(");
		}
	}
}
