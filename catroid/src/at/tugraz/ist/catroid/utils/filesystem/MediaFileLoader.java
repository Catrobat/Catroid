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

package at.tugraz.ist.catroid.utils.filesystem;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import at.tugraz.ist.catroid.ConstructionSiteActivity;

/**
 * this class provides reading media files like pictures from sd card
 * @author niko
 *
 */
public class MediaFileLoader {

	//private ArrayList<HashMap<String, String>> mPictureContent;
	private Context mCtx;
	
	public final static int GALLERY_INTENT_CODE = 1111;
	
	public final static String PICTURE_NAME = "pic_name";
	public final static String PICTURE_PATH = "pic_path";
	public final static String PICTURE_THUMB = "pic_thumb";
	public final static String PICTURE_ID = "pic_id";
	
	public final static String NO_DATA_FOUND = "noDataFound";
	
	public MediaFileLoader(Context ctx){
		mCtx = ctx;
	}
	
	public void openPictureGallery(int elementPosition, ImageView pictureView){
		
		((ConstructionSiteActivity) mCtx).rememberLastSelectedElementAndView(elementPosition, pictureView);
		
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        try {
			((Activity) mCtx).startActivityForResult(intent, GALLERY_INTENT_CODE);
		} catch ( ActivityNotFoundException e) {
			Log.e("MediaFileLoader", "No Gallery found");
		}
	}
	
	
	
}
