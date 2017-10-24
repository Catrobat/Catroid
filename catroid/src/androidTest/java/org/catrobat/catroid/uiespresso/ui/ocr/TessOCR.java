/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.ocr;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

class TessOCR {
	private final TessBaseAPI tess;

	TessOCR(String language) {
		tess = new TessBaseAPI();
		String dataPath = Environment.getExternalStorageDirectory().getPath() + "/tesseract/";
		tess.init(dataPath, language);
	}

	String getOCRResult(Bitmap bitmap) {
		tess.setImage(bitmap);
		Log.e("OCR Text ", tess.getUTF8Text());
		return tess.getUTF8Text();
	}

	void onDestroy() {
		if (tess != null) {
			tess.end();
		}
	}
}
