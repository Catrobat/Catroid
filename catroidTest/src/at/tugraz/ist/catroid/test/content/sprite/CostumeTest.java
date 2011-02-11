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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.content.sprite.Costume;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.test.R;



public class CostumeTest extends InstrumentationTestCase{
	private static final int IMAGE_FILE_ID = R.raw.icon;
	private File testImage;
    public void testConstructor() {
        final String imagePath = "invalid image path";
        Sprite testSprite = new Sprite("testSprite");
        
        Costume costume = new Costume(testSprite, imagePath);
        assertEquals("The imagepath is false", imagePath,costume.getImagePath());

    }  
    
    public void testGetBitmap() throws IOException {
		BufferedInputStream inputStream = new BufferedInputStream(getInstrumentation().getContext().getResources().openRawResource(IMAGE_FILE_ID));
    	testImage = File.createTempFile("testImage", ".png");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(testImage), 4147);
		StageActivity.SCREEN_HEIGHT = 200;
		StageActivity.SCREEN_WIDTH = 200;
		
		byte[] buffer = new byte[4147];
		int length = 0;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
		
        Sprite testSprite = new Sprite("testSprite");
    	Costume costume = new Costume(testSprite, testImage.getAbsolutePath());
    	Bitmap bitmap = costume.getBitmap();
    	assertEquals("Width of loaded bitmap is not the same as width of original image", 72, bitmap.getWidth());
    	assertEquals("Height of loaded bitmap is not the same as height of original image", 72, bitmap.getHeight());

    }
    
    public void testScaleBitmap() throws IOException {
		BufferedInputStream inputStream = new BufferedInputStream(getInstrumentation().getContext().getResources().openRawResource(IMAGE_FILE_ID));
    	testImage = File.createTempFile("testImage", ".png");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(testImage), 4147);
		StageActivity.SCREEN_HEIGHT = 200;
		StageActivity.SCREEN_WIDTH = 200;
		
		byte[] buffer = new byte[4147];
		int length = 0;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
        
        Sprite testSprite = new Sprite("testSprite");
        testSprite.setScale(2);
    	Costume costume = new Costume(testSprite, testImage.getAbsolutePath());
    	
    	Bitmap bitmap = costume.getBitmap();

    	assertEquals("Width of loaded bitmap is not the same as width of original image", 144, bitmap.getWidth());
    	assertEquals("Height of loaded bitmap is not the same as height of original image", 144, bitmap.getHeight());

    }
    
    public void testScaleBitmapScreenTooSmall() throws IOException {
		BufferedInputStream inputStream = new BufferedInputStream(getInstrumentation().getContext().getResources().openRawResource(IMAGE_FILE_ID));
    	testImage = File.createTempFile("testImage", ".png");
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(testImage), 4147);
		StageActivity.SCREEN_HEIGHT = 100;
		StageActivity.SCREEN_WIDTH = 100;
		
		byte[] buffer = new byte[4147];
		int length = 0;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
        
        Sprite testSprite = new Sprite("testSprite");
        testSprite.setScale(2);
    	Costume costume = new Costume(testSprite, testImage.getAbsolutePath());
    	
    	Bitmap bitmap = costume.getBitmap();

    	assertEquals("Width of loaded bitmap is not the same as width of original image", 102, bitmap.getWidth());
    	assertEquals("Height of loaded bitmap is not the same as height of original image", 102, bitmap.getHeight());

    }
    
}
