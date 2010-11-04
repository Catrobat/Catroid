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
	 * @param bm the bitmap to resize
	 * @param xSize desired x size
	 * @param ySize desired y size
	 * @param recycleOldBm if true, the assigned bitmap at parameter bm will be recycled after scaling
	 * @return a new, scaled bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap bm, int xSize, int ySize, boolean recycleOldBm){
		Matrix matrix = new Matrix();
		float scaleWidth = (((float)xSize)/bm.getWidth());
		float scaleHeight = (((float)ySize)/bm.getHeight());
	    matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0,bm.getWidth() ,bm.getHeight() , matrix, true);
		if (recycleOldBm)
			bm.recycle();
		return newbm;
	}
	
	public static Bitmap scaleBitmap(Bitmap bm, int xSize, int ySize){
		return ImageEditing.scaleBitmap(bm, xSize, ySize, false);
	}
	
	public static Bitmap scaleBitmap(Bitmap bm, double scalingFactor, boolean recycleOldBm){
		return ImageEditing.scaleBitmap(bm, (int)(bm.getWidth()*scalingFactor), (int)(bm.getHeight()*scalingFactor), recycleOldBm);
	}
	
	public static Bitmap getScaledBitmap(String imagePath, int maxOutWidth, int maxOutHeight) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		// return null, but we can query the bitmap size
		BitmapFactory.decodeFile(imagePath, options);
		int width = options.outWidth;
		int height = options.outHeight;
		
		double scaleWidth = (double)width / maxOutWidth;
		double scaleHeight = (double)height / maxOutHeight;
		double scaleMax = Math.max(scaleWidth, scaleHeight);
		
		options.inJustDecodeBounds = false;
		if (scaleMax > 1) {
			options.inSampleSize = (int)scaleMax;
		}
		
		return BitmapFactory.decodeFile(imagePath, options);
	}
}
