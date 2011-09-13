/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		if (bundle == null) {
			return;
		}

		try {
			String pathToImage = bundle.getString("at.tugraz.ist.extra.PAINTROID_PICTURE_PATH");
			imageFile = new File(pathToImage);

			String secondPath = bundle.getString("secondExtra");
			secondImageFile = new File(secondPath);
		} catch (Exception e) {
			//lol who cares
		}
		try {
			@SuppressWarnings("unused")
			String secondPath = bundle.getString("thirdExtra");
			finish(); //no bundle returned
		} catch (Exception e) {

		}

		killThisActivity();
	}

	public void killThisActivity() {

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
