package at.tugraz.ist.catroid.test.utils;

import junit.framework.TestCase;
import android.graphics.Bitmap;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class ImageEditingTest extends TestCase {

	public void testScaleImage() {
		// create a 100x100 bitmap
		Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
		
		Bitmap scaledBitmap = ImageEditing.scaleBitmap(bitmap, 0.5f);
		
		assertEquals(50, scaledBitmap.getWidth());
		assertEquals(50, scaledBitmap.getHeight());
		
		scaledBitmap = ImageEditing.scaleBitmap(bitmap, 60, 70);
		
		assertEquals(60, scaledBitmap.getWidth());
		assertEquals(70, scaledBitmap.getHeight());
	}
	
}
