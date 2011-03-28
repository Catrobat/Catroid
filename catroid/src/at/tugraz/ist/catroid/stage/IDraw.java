package at.tugraz.ist.catroid.stage;

import android.graphics.Bitmap;

/**
 * 
 * Everyone who implements this can DrawObjects.
 *
 */
public interface IDraw {
	/**
	 * Processes all sprites which should be drawn on the stage.
	 * @return true, if the sprites could be drawn
	 * 
	 */
	public boolean draw();
	
	/**
	 * Processes a Bitmap and display it on a grey Screen.
	 * 
	 */
	public void drawPauseScreen(Bitmap pauseBitmap);
}
