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
package org.catrobat.catroid.camera;

import android.util.Log;

import com.badlogic.gdx.graphics.Pixmap;

import org.catrobat.catroid.common.LookData;

public class VideoLookData extends LookData {
	private static final long serialVersionUID = 1L; // TODO how can it be serialized?

	private Pixmap videoFramePixmap;

	private byte[] videoFrameData;
	private boolean dataChanged = false;

	public void setVideoFrameData(byte[] data) {
		videoFrameData = data;
		dataChanged = true;
		Log.d("Blah", "video data changed");
	}

	@Override
	public Pixmap getPixmap() {
		if (videoFrameData == null) {
			return super.getPixmap();
		}
		if (dataChanged) {
			dataChanged = false;
			if (videoFramePixmap != null) {
				videoFramePixmap.dispose();
			}
			videoFramePixmap = new Pixmap(videoFrameData, 0, videoFrameData.length);
			Log.d("Blah", "return new pixmap");
		}
		return videoFramePixmap;
	}

}
