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
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class Costume {
    private static final long serialVersionUID = 1L;
    private String imagePath;
    private Bitmap bitmap;
    private int origHeight;
    private int origWidth;
    private Sprite sprite = null;
    private Pair<Integer, Integer> size; // what is this .. I don't even..

    public Costume() { // TODO do we need this?
        this.setImagePath("");
    }

    public Costume(Sprite sprite, String imagePath) throws Exception {
        this.setImagePath(imagePath);
        this.sprite = sprite;
        //this.setBitmap();

    }
    
    public synchronized void setBitmap() throws Exception {
        Bitmap tempBitmap = BitmapFactory.decodeFile(this.imagePath);
        // TODO: now left the positioning out --> right?

        // dirty workaround for Stage Background
        // still on search for a better solution
        if (tempBitmap.getHeight() > StageActivity.SCREEN_HEIGHT) {
            double backgroundScaleFactor = ((double) StageActivity.SCREEN_HEIGHT + 2) / (double) tempBitmap.getHeight();
            tempBitmap = ImageEditing.scaleBitmap(tempBitmap, backgroundScaleFactor, true);
        }
        bitmap = tempBitmap;

        size = new Pair<Integer, Integer>(bitmap.getWidth(), bitmap.getHeight());
        origHeight = bitmap.getHeight();
        origWidth = bitmap.getWidth();

        // mToDraw = true;
    }

    public synchronized void scaleBitmap(double scaleFactor) {
        scaleFactor /= 100f;

        if (bitmap == null) {
            return;
        }
        if (scaleFactor == 0) {
            return;
        }

        int newHeight = (int) ((float) origHeight * scaleFactor);
        int newWidth = (int) ((float) origWidth * scaleFactor);

        //positionToSpriteTopLeft();

        if (newHeight > size.second || newWidth > size.first) {
            bitmap.recycle();
            bitmap = BitmapFactory.decodeFile(this.imagePath);

        }

        bitmap = ImageEditing.scaleBitmap(bitmap, newWidth, newHeight, true);
        size = new Pair<Integer, Integer>(newWidth, newHeight);

        //setPositionToSpriteCenter();
        //mToDraw = true;

    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public Bitmap getBitmap() {
        this.scaleBitmap(sprite.getScale());
        return bitmap;
    }

}
