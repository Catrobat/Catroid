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
    private int actHeight;
    private int actWidth;
    private int origHeight;
    private int origWidth;

    @XStreamOmitField
    private transient Bitmap thumbnailBitmap;
    @XStreamOmitField
    private transient Bitmap costumeBitmap;

    public Costume() {
    }

    public Costume(Sprite sprite, String imagePath) {
        this.sprite = sprite;
        this.setImagePath(imagePath);
    }

    public synchronized void setImagePath(String imagePath) {

        costumeBitmap = BitmapFactory.decodeFile(imagePath);
        if(costumeBitmap == null){
            return;
        }
        this.imagePath = imagePath;
        if (costumeBitmap.getHeight() > Values.SCREEN_HEIGHT){
            costumeBitmap = scaleBitmap(costumeBitmap, Values.SCREEN_HEIGHT, Values.SCREEN_WIDTH);
        }
        
        thumbnailBitmap = scaleBitmap(costumeBitmap, Consts.THUMBNAIL_HEIGHT, Consts.THUMBNAIL_WIDTH);

        actHeight = costumeBitmap.getHeight();
        actWidth = costumeBitmap.getWidth();

        origHeight = costumeBitmap.getHeight();
        origWidth = costumeBitmap.getWidth();
        setDrawPosition();


    }

    private Bitmap scaleBitmap(Bitmap bitmap, int height, int width) {
        if(bitmap == null){
            return null;
        }
        // dirty workaround for Stage Background
        // still on search for a better solution
        if (bitmap.getHeight() > Values.SCREEN_HEIGHT) {
            double backgroundScaleFactor = ((double) Values.SCREEN_HEIGHT + 2) / (double) bitmap.getHeight();
            bitmap = ImageEditing.scaleBitmap(bitmap, backgroundScaleFactor, true);
        } else {
            bitmap = ImageEditing.scaleBitmap(bitmap, width, height);
            
        }

        return bitmap;
    }
        
    public synchronized void scale(double scaleFactorPercent){
        double scaleFactor = scaleFactorPercent /100; 
        int newHeight = (int)((float) origHeight * scaleFactor);
        int newWidth = (int)((float) origWidth * scaleFactor);

        setPositionToSpriteTopLeft();

        if (newHeight > actHeight || newWidth > actWidth) {
                costumeBitmap.recycle();
                costumeBitmap = BitmapFactory.decodeFile(imagePath);

        }

        costumeBitmap = ImageEditing.scaleBitmap(costumeBitmap, newWidth, newHeight, true);
        actWidth = newWidth;
        actHeight = newHeight;

        setPositionToSpriteCenter();
       
        return;

    }

    public String getImagePath() {
        return imagePath;
    }

    public Bitmap getBitmap() {
        return costumeBitmap;
    }

    public synchronized void setDrawPosition() {
        
        setPositionToSpriteTopLeft();
        drawPositionX = Math.round(((Values.SCREEN_WIDTH / (2f * Consts.MAX_REL_COORDINATES)) * sprite.getXPosition())
                + Values.SCREEN_WIDTH / 2f);
        drawPositionY = Math.round((Values.SCREEN_HEIGHT / 2f)
                - ((Values.SCREEN_HEIGHT / (2f * Consts.MAX_REL_COORDINATES)) * sprite.getYPosition()));
        setPositionToSpriteCenter();
    }

    public int getDrawPositionX() {
        return this.drawPositionX;
    }

    public int getDrawPositionY() {
        return this.drawPositionY;
    }

    private Bitmap getDownsizedBitmap(int width, int height) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, o);

        int origWidth = o.outWidth;
        int origHeight = o.outHeight;

        int sampleSizeWidth = (int) Math.ceil(origWidth / (double) width);
        int sampleSizeHeight = (int) Math.ceil(origHeight / (double) height);
        int sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);

        o.inJustDecodeBounds = false;
        o.inSampleSize = sampleSize;

        return BitmapFactory.decodeFile(imagePath, o);
    }

    public synchronized Bitmap getThumbnailBitmap() {
        if (imagePath == null) {
            return null;
        }
        if (thumbnailBitmap == null) {
            thumbnailBitmap = getDownsizedBitmap(Consts.THUMBNAIL_WIDTH, Consts.THUMBNAIL_HEIGHT);
        }
        return thumbnailBitmap;
    }

    public Pair<Integer, Integer> getImageWidthHeight() {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, o);

        int origWidth = o.outWidth;
        int origHeight = o.outHeight;

        int sampleSizeWidth = (int) Math.ceil(origWidth / (double) Values.SCREEN_WIDTH);
        int sampleSizeHeight = (int) Math.ceil(origHeight / (double) Values.SCREEN_HEIGHT);
        int sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);

        o.inSampleSize = sampleSize;
        BitmapFactory.decodeFile(imagePath, o);
        return new Pair<Integer, Integer>((int) (o.outWidth * (sprite.getScale() / 100)), (int) (o.outHeight * (sprite.getScale() / 100)));
    }

    private synchronized void setPositionToSpriteCenter() {
        if (costumeBitmap == null) {
            return;
        }
        drawPositionX = drawPositionX - costumeBitmap.getWidth() / 2;
        drawPositionY = drawPositionY - costumeBitmap.getHeight() / 2;
    }

    private synchronized void setPositionToSpriteTopLeft() {
        if (costumeBitmap == null) {
            return;
        }
        drawPositionX = drawPositionX + costumeBitmap.getWidth() / 2;
        drawPositionY = drawPositionY + costumeBitmap.getHeight() / 2;
    }

}
