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
package at.tugraz.ist.catroid.test.content.sprite;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.Values;
import at.tugraz.ist.catroid.content.sprite.Costume;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.test.R;

public class CostumeTest extends InstrumentationTestCase{
	private static final int IMAGE_FILE_ID = R.raw.icon;
    private File testImage;
    
    // width and height of testImage
    int width;
    int height;

    @Override
    protected void setUp() throws Exception {
        final int fileSize = 4147;
        final String imagePath = "/mnt/sdcard/catroid/testImage.png"; 
        testImage = new File(imagePath);
        if(!testImage.exists()) {
            testImage.createNewFile();
        }
        InputStream in   = getInstrumentation().getContext().getResources().openRawResource(IMAGE_FILE_ID);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), fileSize);
        byte[] buffer = new byte[fileSize];
        int length = 0;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        
        in.close();
        out.flush();
        out.close();
        
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, o);

        width = o.outWidth;
        height = o.outHeight;
    }
    
    @Override
    protected void tearDown() throws Exception {
        if(testImage != null && testImage.exists()){
            testImage.delete();
        }
    }
	
    public void testConstructor() {
        final String imagePath = "invalid/image/path.png";
        Sprite testSprite = new Sprite("testSprite");
        
        Costume costume = new Costume(testSprite, imagePath);
        assertEquals("The imagepath is false", imagePath,costume.getImagePath());
    }  
//    
//    public void testGetBitmap() throws IOException {
//        
//        Values.SCREEN_HEIGHT = 400;
//        Values.SCREEN_WIDTH = 400;
//		
//        Sprite testSprite = new Sprite("testSprite");
//    	Costume costume = new Costume(testSprite, testImage.getAbsolutePath());
//    	Bitmap bitmap = costume.getBitmap();
//    	assertEquals("Width of loaded bitmap is not the same as width of original image", width, bitmap.getWidth());
//    	assertEquals("Height of loaded bitmap is not the same as height of original image", height, bitmap.getHeight());
//
//    }
//    
//    public void testScaleBitmap() throws IOException {
//        
//        Values.SCREEN_HEIGHT = 400;
//        Values.SCREEN_WIDTH = 400;
//        
//        Sprite testSprite = new Sprite("testSprite");
//        testSprite.setScale(200);
//    	Costume costume = new Costume(testSprite, testImage.getAbsolutePath());
//    	
//    	Bitmap bitmap = costume.getBitmap();
//
//    	assertEquals("Width of loaded bitmap is not the same as width of original image", width*2, bitmap.getWidth());
//    	assertEquals("Height of loaded bitmap is not the same as height of original image", height*2, bitmap.getHeight());
//
//    }
//    
//    public void testScaleBitmapScreenTooSmall() throws IOException {
//        
//        Values.SCREEN_HEIGHT = 200;
//        Values.SCREEN_WIDTH = 200;
//        
//        Sprite testSprite = new Sprite("testSprite");
//        testSprite.setScale(200);
//    	Costume costume = new Costume(testSprite, testImage.getAbsolutePath());
//    	
//    	Bitmap bitmap = costume.getBitmap();
//    	
//    	int maxDimension = Math.max(bitmap.getWidth(), bitmap.getHeight());
//
//    	assertEquals("Max bitmap dimension not equal to screen size", Values.SCREEN_WIDTH, maxDimension);
//
//    }
//    
//    public void testGetThumbnail() {
//    	Sprite testSprite = new Sprite("testSprite");
//    	Costume costume = new Costume(testSprite, testImage.getAbsolutePath());
//    	Bitmap thumbnail = costume.getThumbnailBitmap();
//    	assertEquals("Thumbnail width is not correct", width / 2, thumbnail.getWidth());
//    	assertEquals("Thumbnail height is not correct", height / 2, thumbnail.getHeight());
//    }
    
}
