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
package at.tugraz.ist.catroid.test.utils;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import at.tugraz.ist.catroid.test.R;

public class dummyPaintroidActivity extends Activity {

	public File testImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String path = at.tugraz.ist.catroid.common.Consts.DEFAULT_ROOT + "testImage.png";
		try {
			testImage = TestUtils.createTestMediaFile(path, R.raw.icon, getApplicationContext());
		} catch (IOException e) {
			e.printStackTrace();
		}
		killThisActivity();
	}

	public void killThisActivity() {
		Bundle bundle = new Bundle();
		bundle.putString(
				getApplicationContext().getString(at.tugraz.ist.catroid.R.string.extra_picture_path_paintroid),
				at.tugraz.ist.catroid.common.Consts.DEFAULT_ROOT + "testImage.png");
		Intent intent = new Intent();
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
	}

}
