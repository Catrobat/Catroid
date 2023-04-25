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

package org.catrobat.catroid.uiespresso.util.rules;

import android.annotation.TargetApi;
import android.app.UiAutomation;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

public class ScreenshotOnFailRule extends TestWatcher {
	private static final String TAG = ScreenshotOnFailRule.class.getSimpleName();

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

	@Override
	protected void failed(Throwable e, Description description) {
		try {
			Bitmap screenshotBitmap = takeScreenshot();
			File screenshotFile = makeFileDescriptor(description);
			saveScreenshot(screenshotBitmap, screenshotFile);
		} catch (TakeScreenShotException tse) {
			Log.e(TAG, "Take screenshot failed", tse);
		}
	}

	@TargetApi(18)
	private Bitmap takeScreenshot() throws TakeScreenShotException {
		UiAutomation uiAutomation = InstrumentationRegistry.getInstrumentation().getUiAutomation();
		Bitmap screenshot = uiAutomation.takeScreenshot();
		if (screenshot == null) {
			throw new TakeScreenShotException("Failed getting screenshot from uiAutomation");
		}
		return screenshot;
	}

	private File makeFileDescriptor(Description description) {
		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/PocketCodeEspressoScreenshots/" + ApplicationProvider.getApplicationContext().getPackageName());
		if (!path.exists()) {
			path.mkdirs();
		}

		String timeStamp = DATE_FORMAT.format(new Timestamp(System.currentTimeMillis()));
		String filename = description.getClassName() + "-" + description.getMethodName() + "-" + timeStamp + ".png";
		return new File(path, filename);
	}

	private void saveScreenshot(Bitmap screenshot, File path) {
		BufferedOutputStream fileStream = null;
		try {
			fileStream = new BufferedOutputStream(new FileOutputStream(path));
			screenshot.compress(Bitmap.CompressFormat.PNG, 90, fileStream);
			fileStream.flush();
		} catch (IOException e) {
			Log.e(TAG, "failed to save screen shot to file", e);
		} finally {
			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException ioe) {
					Log.e(TAG, ioe.getMessage());
				}
			}
			screenshot.recycle();
		}
	}

	private class TakeScreenShotException extends Exception {
		TakeScreenShotException(String message) {
			super(message);
		}
	}
}
