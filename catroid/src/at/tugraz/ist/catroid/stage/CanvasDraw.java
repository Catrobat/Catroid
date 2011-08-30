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

package at.tugraz.ist.catroid.stage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.SpeechBubble;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.Utils;

/**
 * 
 * Draws DrawObjects into a canvas.
 */

public class CanvasDraw implements IDraw {
	private Canvas canvas = null;
	private SurfaceView surfaceView;
	private Paint whitePaint;
	private SurfaceHolder holder;
	private Bitmap canvasBitmap;
	private Canvas bufferCanvas;
	private boolean firstRun;
	private Rect flushRectangle;
	private Bitmap screenshotIcon;
	private int screenshotIconXPosition;
	private Activity activity;
	ArrayList<Sprite> sprites;

	public CanvasDraw(Activity activity) {
		super();
		this.activity = activity;
		surfaceView = StageActivity.stage;
		holder = surfaceView.getHolder();
		whitePaint = new Paint();
		whitePaint.setStyle(Paint.Style.FILL);
		whitePaint.setColor(Color.WHITE);
		firstRun = true;
		canvasBitmap = Bitmap.createBitmap(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT, Bitmap.Config.RGB_565);
		bufferCanvas = new Canvas(canvasBitmap);
		flushRectangle = new Rect(0, 0, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
		screenshotIcon = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_screenshot);
		screenshotIconXPosition = Values.SCREEN_WIDTH - screenshotIcon.getWidth()
				- Consts.SCREENSHOT_ICON_PADDING_RIGHT;
		sprites = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
	}

	public synchronized boolean draw() {
		canvas = holder.lockCanvas();
		if (canvas != null) {

			// draw white rectangle:
			bufferCanvas.drawRect(flushRectangle, whitePaint);
			java.util.Collections.sort(sprites);

			for (Sprite sprite : sprites) {
				if (!sprite.isVisible()) {
					continue; //don't need to draw
				}
				if (sprite.getCostume().getBitmap() != null) {
					//try {
					Costume tempCostume = sprite.getCostume();
					SpeechBubble tempBubble = sprite.getBubble();

					bufferCanvas.drawBitmap(tempCostume.getBitmap(), tempCostume.getDrawPositionX(),
							tempCostume.getDrawPositionY(), null);

					if (!sprite.getName().equals(activity.getString(R.string.background))) {

						tempBubble.draw(bufferCanvas, tempCostume, activity);

					}
					sprite.setToDraw(false);
					//} catch (Exception e) {
					//e.printStackTrace();
					//}
				}
			}
			if (!NativeAppActivity.isRunning()) {
				bufferCanvas.drawBitmap(screenshotIcon, screenshotIconXPosition, Consts.SCREENSHOT_ICON_PADDING_TOP,
						null);
			}
			canvas.drawBitmap(canvasBitmap, 0, 0, null);

			if (firstRun && !NativeAppActivity.isRunning()) {
				saveThumbnail(false);
				firstRun = false;
			}

		} else {
			return false;
		}

		holder.unlockCanvasAndPost(canvas);
		return true;
	}

	public synchronized void drawPauseScreen(Bitmap pauseBitmap) {
		Paint greyPaint = new Paint();
		greyPaint.setStyle(Paint.Style.FILL);
		greyPaint.setColor(Color.DKGRAY);
		canvas = holder.lockCanvas();
		if (canvas != null) {
			canvas.drawRect(new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), greyPaint);
			if (pauseBitmap != null) {
				Bitmap scaledPauseBitmap = ImageEditing.scaleBitmap(pauseBitmap,
						(canvas.getWidth() / 2f) / pauseBitmap.getWidth());
				int posX = canvas.getWidth() / 2 - scaledPauseBitmap.getWidth() / 2;
				int posY = canvas.getHeight() / 2 - scaledPauseBitmap.getHeight() / 2;
				canvas.drawBitmap(scaledPauseBitmap, posX, posY, null);
			}
		}
		holder.unlockCanvasAndPost(canvas);
	}

	public void processOnTouch(int xCoordinate, int yCoordinate) {
		String toastText;
		if (xCoordinate >= screenshotIconXPosition
				&& yCoordinate <= Consts.SCREENSHOT_ICON_PADDING_TOP + screenshotIcon.getHeight()
				&& !NativeAppActivity.isRunning()) {
			Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(100);
			if (saveThumbnail(true)) {
				toastText = activity.getString(R.string.notification_screenshot_ok);
			} else {
				toastText = activity.getString(R.string.error_screenshot_failed);
			}

			Toast.makeText(activity, toastText, Toast.LENGTH_SHORT).show();
		}
	}

	public boolean saveThumbnail(boolean overwrite) {
		try {
			String path = Utils.buildPath(Consts.DEFAULT_ROOT, ProjectManager.getInstance().getCurrentProject()
					.getName());
			File file = new File(Utils.buildPath(path, Consts.SCREENSHOT_FILE_NAME));
			File noMediaFile = new File(Utils.buildPath(path, Consts.NO_MEDIA_FILE));
			if (!noMediaFile.exists()) {
				noMediaFile.createNewFile();
			}
			if (file.exists() && !overwrite) {
				return false;
			}

			FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Consts.BUFFER_8K);
			canvasBitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
