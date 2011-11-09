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

package at.tugraz.ist.catroid.test.filesystem;

import android.test.AndroidTestCase;

public class MediaFileLoaderTest extends AndroidTestCase {

	//private MediaFileLoader mMediaFileLoader;
	//private Context mCtx;

	protected void setUp() throws Exception {
		super.setUp();
		//mCtx = getContext().createPackageContext("at.tugraz.ist.catroid", Context.CONTEXT_IGNORE_SECURITY);
		//mMediaFileLoader = new MediaFileLoader(mCtx);
	}

	//	/**
	//	 * test if files exist
	//	 */
	//	public void testLoadPictureContent(){
	//		mMediaFileLoader.loadPictureContent();
	//		ArrayList<HashMap<String, String>> content =  mMediaFileLoader.getPictureContent();
	//		File file;
	//		
	//		Log.d("TEST", "number of image files: "+content.size());
	//		assertNotNull(content);
	//		
	//		for(int i = 0; i < content.size(); i++){
	//			file = new File(content.get(i).get(MediaFileLoader.PICTURE_PATH));
	//			assertTrue(file.exists());
	//			assertNotNull(BitmapFactory.decodeFile(content.get(i).get(MediaFileLoader.PICTURE_PATH)));
	////			Log.d("TEST", content.get(i).get(MediaFileLoader.PICTURE_NAME));
	////			Log.d("TEST", content.get(i).get(MediaFileLoader.PICTURE_PATH));
	//		}
	//		
	//	}

}
