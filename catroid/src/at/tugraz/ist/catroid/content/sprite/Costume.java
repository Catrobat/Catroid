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
package at.tugraz.ist.catroid.content.sprite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.tugraz.ist.catroid.Values;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class Costume {
	private static final long serialVersionUID = 1L;
	private String imagePath;
	private Sprite sprite;
	private int drawPositionX;
	private int drawPositionY;
	private int maxRelCoordinates = 1000; //TODO global variable for range of relative coordinates?

    public Costume(Sprite sprite, String imagePath) {
		this.setImagePath(imagePath);
		this.sprite = sprite;
		setDrawPosition();
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public Bitmap getBitmap() {
	    BitmapFactory.Options o = new BitmapFactory.Options();
	    o.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(imagePath, o);
	    
	    int tmpWidth = o.outWidth;
	    int tmpHeight = o.outHeight;
	    int sampleSize = 1;
	
	    while (true) {
	        if(tmpWidth < Values.SCREEN_WIDTH || tmpHeight < Values.SCREEN_HEIGHT) 
                break;
	        tmpWidth /= 2;
	        tmpHeight /= 2;
	        sampleSize++;
	    }

	    BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = sampleSize;
        
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, o2);

		bitmap = ImageEditing.scaleBitmap(bitmap, sprite.getScale()/100, false); // /100 because we need times and not %

		if (bitmap.getHeight() > Values.SCREEN_HEIGHT) {
			double backgroundScaleFactor = ((double) Values.SCREEN_HEIGHT + 2)
					/ (double) bitmap.getHeight(); // SCREEN_HEIGHT + 2
													// because of rounding
													// errors in set to
													// center
			bitmap = ImageEditing.scaleBitmap(bitmap, backgroundScaleFactor, false);
		}
		return bitmap;
	}
	
    public void setDrawPosition() {
        drawPositionX = Math.round(((Values.SCREEN_WIDTH / (2f * maxRelCoordinates)) * sprite.getXPosition()) + Values.SCREEN_WIDTH / 2f);
        drawPositionY = Math.round((Values.SCREEN_HEIGHT / 2f)
                - ((Values.SCREEN_HEIGHT / (2f * maxRelCoordinates)) * sprite.getYPosition()));
    }

	public int getDrawPositionX() {
		return this.drawPositionX;
	}

	public int getDrawPositionY() {
		return this.drawPositionY;
	}

}
