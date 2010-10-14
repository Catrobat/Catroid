package at.tugraz.ist.catroid.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageEditing {
	

	public ImageEditing() {

	}
	public static Bitmap scaleBitmap(Bitmap bm, int xSize, int ySize){
		Matrix matrix = new Matrix();
		float scaleWidth = (((float)xSize)/bm.getWidth());
		float scaleHeight = (((float)ySize)/bm.getHeight());
	    matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0,bm.getWidth() ,bm.getHeight() , matrix, true);
		return newbm;
	}
	
	public static Bitmap scaleBitmap(Bitmap bm, double scalingFactor){
		return ImageEditing.scaleBitmap(bm, (int)(bm.getWidth()*scalingFactor), (int)(bm.getHeight()*scalingFactor));
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
