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
package at.tugraz.ist.catroid.uitest.mockups;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

public class MockPaintroidActivity extends Activity {

	private File imageFile;
	private File secondImageFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(getLocalClassName(), "MOCKING PAINTROIDDDDDDDDDDDDDDD*****************");

		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null) {
			Log.v(getLocalClassName(), "no bundel");
			return;
		}

		if (bundle.containsKey("at.tugraz.ist.extra.PAINTROID_PICTURE_PATH")) {
			String pathToImage = bundle.getString("at.tugraz.ist.extra.PAINTROID_PICTURE_PATH");
			imageFile = new File(pathToImage);
			Log.v(getLocalClassName(), "at.tugraz.ist.extra.PAINTROID_PICTURE_PATH=" + pathToImage + " imageFile="
					+ imageFile);
		}

		if (bundle.containsKey("secondExtra")) {
			String secondPath = bundle.getString("secondExtra");
			secondImageFile = new File(secondPath);
			Log.v(getLocalClassName(), "secondExtra=" + secondPath + " imageFile=" + secondImageFile);
		}

		if (bundle.containsKey("thirdExtra")) {
			Log.v(getLocalClassName(), "finish");
			finish(); //no bundle returned
		} else if (bundle.containsKey("fourthExtra")) {
			String pathToImage = bundle.getString("at.tugraz.ist.extra.PAINTROID_PICTURE_PATH");
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = bundle.getInt("fourthExtra");
			Bitmap image = BitmapFactory.decodeFile(pathToImage, options);
			OutputStream stream = null;
			//imageFile = new File(pathToImage);
			Log.v(getLocalClassName(), "before try");
			try {
				imageFile.setWritable(true);
				Log.v(getLocalClassName(), "try");
				boolean write = imageFile.canWrite();
				boolean read = imageFile.canRead();
				Log.v(getLocalClassName(), "write=" + write + "read=" + read);
				stream = new FileOutputStream(imageFile);

				Log.v(getLocalClassName(), "stream=" + stream);
				boolean success = image.compress(Bitmap.CompressFormat.PNG, 80, stream); //-->png
				Log.v(getLocalClassName(), "success=" + success);
				stream.flush();
				stream.close();
			} catch (IOException e) {
				Log.v(getLocalClassName(), "IO Exception in 4th extra");
				e.printStackTrace();
			}
			Log.v(getLocalClassName(), "fourthExtra path=" + pathToImage + " image=" + image + "image file="
					+ imageFile + " sample size=" + options.inSampleSize + " width=" + options.outWidth + " heigth="
					+ options.outHeight);
		} else {
			sendBundleBackToCatroidAndFinish();
		}
	}

	public void sendBundleBackToCatroidAndFinish() {
		Bundle bundle = new Bundle();
		if (secondImageFile != null) {
			bundle.putString("at.tugraz.ist.extra.PAINTROID_PICTURE_PATH", secondImageFile.getAbsolutePath());
		} else {
			bundle.putString("at.tugraz.ist.extra.PAINTROID_PICTURE_PATH", imageFile.getAbsolutePath());
		}

		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}
}
