package at.tugraz.ist.catroid.stage;

import android.graphics.Bitmap;

/**
 * 
 * Everyone who implements this can DrawObjects.
 * 
 * @author Thomas Holzmann
 *
 */
public interface IDraw {
	/**
	 * Processes all sprites which should be drawn on the stage.
	 * 
	 */
	public void draw();
	
	/**
	 * Processes a Bitmap and display it on a grey Screen.
	 * 
	 */
	public void drawPauseScreen(Bitmap pauseBitmap);
}
