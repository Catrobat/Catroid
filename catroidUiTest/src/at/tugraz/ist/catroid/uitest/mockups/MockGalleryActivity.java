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
import android.net.Uri;

/**
 * A mock gallery activity that simply returns an image file from the drawable resources.
 */
public class MockGalleryActivity extends Activity {
	private static final String RESOURCE_LOCATION = "res/drawable/catroid_sunglasses";

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish();
	}

	@Override
	protected void onDestroy() {
		Intent resultIntent = new Intent();
		File resourceFile = new File(RESOURCE_LOCATION);
		if (!resourceFile.exists() || !resourceFile.canRead())
			throw new RuntimeException("Could not open resource file: " + resourceFile.getAbsolutePath());
		resultIntent.setData(Uri.fromFile(resourceFile));

		setResult(RESULT_OK, resultIntent);
		super.onDestroy();
	}
}
