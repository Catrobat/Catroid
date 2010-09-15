package at.tugraz.ist.catroid.utils;


import android.graphics.Bitmap;
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
	
	public static Bitmap scaleBitmap(Bitmap bm, float scalingFactor){
		return ImageEditing.scaleBitmap(bm, (int)(bm.getWidth()*scalingFactor), (int)(bm.getHeight()*scalingFactor));
	}
}
