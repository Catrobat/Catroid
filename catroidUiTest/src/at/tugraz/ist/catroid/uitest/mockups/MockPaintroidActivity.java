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

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import at.tugraz.ist.catroid.common.Constants;

public class MockPaintroidActivity extends Activity {

	private File imageFile;
	private File secondImageFile;
	private ByteArrayOutputStream imageStream;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(getLocalClassName(), "********MOCKING PAINTROID*********");

		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null) {
			Log.v(getLocalClassName(), "no bundle");
			return;
		}

		if (bundle.containsKey("at.tugraz.ist.extra.PAINTROID_PICTURE_PATH")) {
			String pathToImage = bundle.getString("at.tugraz.ist.extra.PAINTROID_PICTURE_PATH");
			if (bundle.containsKey("crop")) {
				Log.v(getLocalClassName(), "crop --> so crop the image");
				BitmapFactory.Options options = new BitmapFactory.Options();
				Log.v(getLocalClassName(), "what size?");
				options.inSampleSize = bundle.getInt("crop");
				Bitmap imageBitmap = BitmapFactory.decodeFile(pathToImage, options);
				imageStream = new ByteArrayOutputStream();
				Log.v(getLocalClassName(), "Bitmap=" + imageBitmap + "w/h=" + imageBitmap.getWidth() + "/"
						+ imageBitmap.getHeight());
				Log.v(getLocalClassName(), "now compress - image stream = " + imageStream);
				imageBitmap.compress(Bitmap.CompressFormat.PNG, 50, imageStream);
				Log.v(getLocalClassName(), "Finally cropped :) imageStream= " + imageStream + " tostring:"
						+ imageStream.toString());
				//sendBundleBackToCatroidAndFinish();

				/*
				 * OutputStream stream = null;
				 * File imageFile3 = new File(pathToImage);
				 * Log.v(getLocalClassName(), "before try");
				 * try {
				 * boolean write = imageFile3.canWrite();
				 * //imageFile3.setWritable(true);
				 * Log.v(getLocalClassName(), "try");
				 * boolean write_new = imageFile3.canWrite();
				 * boolean read = imageFile3.canRead();
				 * boolean exec = false; //imageFile3.canExecute();
				 * Log.v(getLocalClassName(), "write=" + write + " write new" + write_new + "read=" + read + "exe=" +
				 * exec);
				 * stream = new FileOutputStream(imageFile3);
				 * 
				 * Log.v(getLocalClassName(), "stream=" + stream);
				 * boolean success = image.compress(Bitmap.CompressFormat.PNG, 80, stream); //-->png
				 * Log.v(getLocalClassName(), "success=" + success);
				 * stream.flush();
				 * stream.close();
				 * } catch (IOException e) {
				 * Log.v(getLocalClassName(), "IO Exception in 4th extra");
				 * e.printStackTrace();
				 * }
				 * Log.v(getLocalClassName(), "fourthExtra path=" + pathToImage + " image=" + image + "image file="
				 * + imageFile + " sample size=" + options.inSampleSize + " width=" + options.outWidth + " heigth="
				 * + options.outHeight);
				 */
			} else {
				imageFile = new File(pathToImage);
			}
		}

		if (bundle.containsKey("secondExtra")) {
			String secondPath = bundle.getString("secondExtra");
			secondImageFile = new File(secondPath);
			Log.v(getLocalClassName(), "secondExtra=" + secondPath + " imageFile=" + secondImageFile);
		}

		if (bundle.containsKey("thirdExtra")) {
			Log.v(getLocalClassName(), "finish");
			finish(); //no bundle returned
		} else {
			sendBundleBackToCatroidAndFinish();
		}
	}

	public void sendBundleBackToCatroidAndFinish() {
		Bundle bundle = new Bundle();
		Log.v(getLocalClassName(), "send bundle back to catroid + imageStream= " + imageStream);
		if (imageStream != null) {
			Log.v(getLocalClassName(), "imageStream != null");
			bundle.putByteArray("bitmapStream", imageStream.toByteArray());
			bundle.putString(Constants.EXTRA_PICTURE_PATH_PAINTROID, imageFile.getAbsolutePath());
			Log.v(getLocalClassName(),
					"imageStream=" + imageStream + " imageStreamBiteArray=" + imageStream.toByteArray());
		} else if (secondImageFile != null) {
			bundle.putString("at.tugraz.ist.extra.PAINTROID_PICTURE_PATH", secondImageFile.getAbsolutePath());
		} else {
			bundle.putString("at.tugraz.ist.extra.PAINTROID_PICTURE_PATH", imageFile.getAbsolutePath());
		}

		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		//CostumeFragmentTest test = new CostumeFragmentTest();
		//test.onActivityResult(1, RESULT_OK, intent);
		finish();
	}
}
