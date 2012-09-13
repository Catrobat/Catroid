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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MockPaintroidActivity extends Activity {

	private File imageFile;
	private File secondImageFile;
	private String pathToImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null) {
			return;
		}

		if (bundle.containsKey("org.catrobat.extra.PAINTROID_PICTURE_PATH")) {
			pathToImage = bundle.getString("org.catrobat.extra.PAINTROID_PICTURE_PATH");
			imageFile = new File(pathToImage);
		}

		if (bundle.containsKey("secondExtra")) {
			String secondPath = bundle.getString("secondExtra");
			secondImageFile = new File(secondPath);
		}

		if (bundle.containsKey("thirdExtra")) {
			finish(); //no bundle returned
		} else {
			sendBundleBackToCatroidAndFinish();
		}
	}

	public void sendBundleBackToCatroidAndFinish() {
		Bundle bundle = new Bundle();
		if (secondImageFile != null) {
			bundle.putString("org.catrobat.extra.PAINTROID_PICTURE_PATH", secondImageFile.getAbsolutePath());
		} else {
			bundle.putString("org.catrobat.extra.PAINTROID_PICTURE_PATH", imageFile.getAbsolutePath());
		}

		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}
}
