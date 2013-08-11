/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;

public class ImagePreviewActivity extends Activity {

	public static final String FILE_EXTRA_NAME = "fileName";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Bundle extras = getIntent().getExtras();
		if (extras == null || !extras.containsKey(FILE_EXTRA_NAME)) {
			Log.e("ImagePreviewActivity", "missing extra \"" + FILE_EXTRA_NAME + "\"");
			finish();
		}

		String fileName = extras.getString(FILE_EXTRA_NAME);
		File imageFile = new File(fileName);
		if (!imageFile.exists()) {
			Log.e("ImagePreviewActivity", "file \"" + fileName + "\" does not exist.");
			finish();
		}
		Bitmap imageBitmap = BitmapFactory.decodeFile(fileName);
		ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(imageBitmap);
		imageView.setBackgroundColor(Color.BLACK);

		setContentView(imageView);
	}
}
