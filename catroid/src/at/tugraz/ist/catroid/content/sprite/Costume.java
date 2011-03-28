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
import android.util.Pair;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.Values;
import at.tugraz.ist.catroid.utils.ImageEditing;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class Costume {
	private static final long serialVersionUID = 1L; 
	private String imagePath;
	private Sprite sprite;
	private int drawPositionX;
	private int drawPositionY;
	@XStreamOmitField
	private transient Bitmap thumbnailBitmap; 
	
	public Costume(){}

    public Costume(Sprite sprite, String imagePath) {
		this.setImagePath(imagePath);
		this.sprite = sprite;
		setDrawPosition();
		
		//creating thumbnailBitmap:
		thumbnailBitmap = getDownsizedBitmap(Consts.THUMBNAIL_WIDTH, Consts.THUMBNAIL_HEIGHT);
	} 

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImagePath() {
		return imagePath;
	}

	public Bitmap getBitmap() {
		
		Bitmap bitmap = getDownsizedBitmap(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);

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
        drawPositionX = Math.round(((Values.SCREEN_WIDTH / (2f * Consts.MAX_REL_COORDINATES)) * sprite.getXPosition())
                + Values.SCREEN_WIDTH / 2f);
        drawPositionY = Math.round((Values.SCREEN_HEIGHT / 2f)
                - ((Values.SCREEN_HEIGHT / (2f * Consts.MAX_REL_COORDINATES)) * sprite.getYPosition()));
    }

	public int getDrawPositionX() {
		return this.drawPositionX;
	}

	public int getDrawPositionY() {
		return this.drawPositionY;
	}
	
	private Bitmap getDownsizedBitmap(int width, int height){
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, o);
        
        int origWidth = o.outWidth;
        int origHeight = o.outHeight;
        int sampleSize = 1;
    
        int tempWidht = origWidth;
        int tempHeight = origHeight;
        while (true) {
            if(tempWidht < width && tempHeight < height) 
                break;
            tempWidht = origWidth / sampleSize;
            tempHeight = tempHeight / sampleSize;
            sampleSize++;
        } 
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = sampleSize;
        
        return BitmapFactory.decodeFile(imagePath, o2);
	} 

    public Bitmap getThumbnailBitmap() {
        if(imagePath == null){
            return null;
        }
        if(thumbnailBitmap == null){
            thumbnailBitmap = getDownsizedBitmap(Consts.THUMBNAIL_WIDTH, Consts.THUMBNAIL_HEIGHT);
        }
        System.out.println("###### Costume thumb size: " + thumbnailBitmap.getHeight());
        System.out.println("###### Costume thumb size: " + thumbnailBitmap.getWidth());
        return thumbnailBitmap;
    }
    
    public Pair<Integer,Integer> getImageWidthHeight(){
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, o);
        return new Pair<Integer,Integer>((int)(o.outWidth*sprite.getScale()/100),(int)(o.outHeight*sprite.getScale()/100));
    }

}
