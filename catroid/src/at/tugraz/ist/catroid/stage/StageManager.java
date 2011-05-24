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

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

public class StageManager {
	private Activity activity;
	protected ArrayList<Sprite> spriteList;
	private Boolean spritesChanged;
	private IDraw draw;
	private boolean isPaused;
	private Handler handler = new Handler();

	private Runnable runnable = new Runnable() {
		public void run() {
			for (Sprite sprite : spriteList) {
				if (sprite.getToDraw() == true) {
					spritesChanged = true;
					sprite.setToDraw(false);
				}
			}
			if (spritesChanged) {
				spritesChanged = !drawSprites();
			}

			if (!isPaused) {
				handler.postDelayed(this, 33);
			}
		}
	};

	public int getMaxZValue() {
		return ProjectManager.getInstance().getCurrentProject().getMaxZValue();
	}

	public StageManager(Activity activity) {

		spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
		this.activity = activity;

		spritesChanged = true;
		draw = new CanvasDraw(activity);

		for (Sprite sprite : spriteList) {
			sprite.startStartScripts();
		}
	}

	public boolean drawSprites() {
		return draw.draw();
	}

	public void processOnTouch(int coordX, int coordY) {
		draw.processOnTouch(coordX, coordY);
		ArrayList<Sprite> touchedSpriteList = new ArrayList<Sprite>();
		for (Sprite sprite : spriteList) {
			if (sprite.processOnTouch(coordX, coordY)) {
				touchedSpriteList.add(sprite);
			}
		}

		Collections.sort(touchedSpriteList);
		if (!touchedSpriteList.isEmpty()) {
			touchedSpriteList.get(touchedSpriteList.size() - 1).startTapScripts();
		}
	}

	public void pause(boolean drawScreen) {
		for (Sprite sprite : spriteList) {
			sprite.pause();
		}

		if (drawScreen) {
			Bitmap pauseBitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.paused_cat);
			draw.drawPauseScreen(pauseBitmap);
			handler.removeCallbacks(runnable);
			spritesChanged = true;
		}

		isPaused = true;
	}

	public void resume() {
		for (Sprite sprite : spriteList) {
			sprite.resume();
		}
		isPaused = false;
		spritesChanged = true;
		runnable.run();
	}

	public void start() {
		isPaused = false;
		runnable.run();
	}
}
