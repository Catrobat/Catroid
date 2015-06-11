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
package org.catrobat.catroid.uitest.mockups;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * A mock gallery activity that simply returns an image file from the drawable resources.
 */
public class MockGalleryActivity extends Activity {
	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();

		String filePath = bundle.getString("filePath");

		Uri imageUri = Uri.parse(filePath);

		Intent resultIntent;
		boolean returnNullData = false;
		if (bundle.containsKey("returnNullData")) {
			returnNullData = bundle.getBoolean("returnNullData");
		}

		if (returnNullData) {
			resultIntent = null;
		} else {
			resultIntent = new Intent();
			resultIntent.setData(imageUri);
		}

		setResult(RESULT_OK, resultIntent);
		finish();
	}
}
