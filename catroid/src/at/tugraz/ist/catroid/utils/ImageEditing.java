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

package at.tugraz.ist.catroid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageEditing {

    public ImageEditing() {

    }

    /**
     * Scales the bitmap to the specified size.
     * 
     * @param bm
     *            the bitmap to resize
     * @param xSize
     *            desired x size
     * @param ySize
     *            desired y size
     * @param recycleOldBm
     *            if true, the assigned bitmap at parameter bm will be recycled
     *            after scaling
     * @return a new, scaled bitmap
     */
    public static Bitmap scaleBitmap(Bitmap bm, int xSize, int ySize, boolean recycleOldBm) {
        Matrix matrix = new Matrix();
        float scaleWidth = (((float) xSize) / bm.getWidth());
        float scaleHeight = (((float) ySize) / bm.getHeight());
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        if (recycleOldBm)
            bm.recycle();
        return newbm;
    }

    public static Bitmap scaleBitmap(Bitmap bm, int xSize, int ySize) {
        return ImageEditing.scaleBitmap(bm, xSize, ySize, false);
    }

    public static Bitmap scaleBitmap(Bitmap bm, double scalingFactor, boolean recycleOldBm) {
        return scaleBitmap(bm, (int) Math.round(bm.getWidth() * scalingFactor), (int) Math.round(bm.getHeight() * scalingFactor),
                recycleOldBm);
    }

    public static Bitmap getScaledBitmap(String imagePath, int outWidth, int outHeight) {
        if (imagePath == null) {
            return null;
        }
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, o);

        int origWidth = o.outWidth;
        int origHeight = o.outHeight;

        double sampleSizeWidth = (origWidth / (double) outWidth);
        double sampleSizeHeight = origHeight / (double) outHeight;
        double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);
        int sampleSizeRounded = (int) Math.floor(sampleSize);

        int newHeight = (int) Math.ceil(origHeight / sampleSize);
        int newWidth = (int) Math.ceil(origWidth / sampleSize);

        o.inJustDecodeBounds = false;
        o.inSampleSize = sampleSizeRounded;

        Bitmap tmpBitmap = BitmapFactory.decodeFile(imagePath, o);
        return scaleBitmap(tmpBitmap, newWidth, newHeight, true);
    }
    public static Bitmap getBitmap(String imagePath, int maxOutWidth, int maxOutHeight) {
        if (imagePath == null) {
            return null;
        }
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, o);

        int origWidth = o.outWidth;
        int origHeight = o.outHeight;

        double sampleSizeWidth = (origWidth / (double) maxOutWidth);
        double sampleSizeHeight = origHeight / (double) maxOutHeight;
        double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);
        
        if(sampleSize < 1){
            return  BitmapFactory.decodeFile(imagePath);
        }
        
        int sampleSizeRounded = (int) Math.floor(sampleSize);

        int newHeight = (int) Math.ceil(origHeight / sampleSize);
        int newWidth = (int) Math.ceil(origWidth / sampleSize);

        o.inJustDecodeBounds = false;
        o.inSampleSize = sampleSizeRounded;

        Bitmap tmpBitmap = BitmapFactory.decodeFile(imagePath, o);
        return scaleBitmap(tmpBitmap, newWidth, newHeight, true);
    }
}
