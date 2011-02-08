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

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.sprite.Costume;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.utils.Utils;


public class CustomeTest extends AndroidTestCase{
    
    Bitmap bitmap1;
    Bitmap bitmap2;

    @Override
    protected void setUp(){
        Bitmap bitmap1 = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
        Bitmap bitmap2 = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
        Utils.saveBitmapOnSDCardAsPNG("/sdcard/test1.png", bitmap1);
        Utils.saveBitmapOnSDCardAsPNG("/sdcard/test2.png", bitmap2);
    }
    
    public void testConstructor(){
        final String imagePath = "invalid image path";
        Sprite testSprite = new Sprite("testSprite");
        try {
            Costume costume = new Costume(testSprite,imagePath);
            assertEquals("The imagepath is false",imagePath,costume.getImagePath());
        } catch (Exception e) {
            fail("Exception in Costume constructor");
        }
    }
    
    public void testSetBitmap(){
        String imagePath = "invalid image path";
        Sprite testSprite = new Sprite("testSprite");
        try {
            Costume costume = new Costume(testSprite,imagePath);
            costume.setBitmap();
            fail("Error, should fail because of invalid image path");
        } catch (Exception e) {
            //expected because of invalid imagepath
        }
        
        imagePath = "/sdcard/test1.png";
        try {
            Costume costume = new Costume(testSprite,imagePath);
            costume.setBitmap();
        } catch (Exception e) {
            fail("No Exception should be thrown here");
        }
    
//        ImageView imageView = (ImageView) findViewById(R.id.myimageview);
//        imageView.setImageResource(R.drawable.icon);
        
    }
    
    
    
}
