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
import android.os.Handler;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;

public class StageManager {
	protected ArrayList<Sprite> spriteList;
	private Boolean spritesChanged;
	private IDraw draw;
	private boolean isPaused;
	private Handler handler = new Handler();
	//	private boolean ttsNeeded = false;
	//	private boolean bluetoothNeeded;
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

	public StageManager(Activity activity) {

		spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();

		spritesChanged = true;
		draw = new CanvasDraw(activity);
	}

	public void startScripts() {
		for (Sprite sprite : spriteList) {
			sprite.startStartScripts();
		}
	}

	public int getRequiredRessources() {
		int ressources = Brick.NO_RESSOURCES;

		for (Sprite sprite : spriteList) {
			ressources |= sprite.getRequiredRessources();

		}
		return ressources;
	}

	public boolean drawSprites() {
		return draw.draw();
	}

	public boolean saveScreenshot() {
		draw.draw();
		return draw.saveScreenshot();
	}

	public void processOnTouch(int xCoordinate, int yCoordinate, String action) {
		draw.processOnTouch(xCoordinate, yCoordinate);
		ArrayList<Sprite> touchedSpriteList = new ArrayList<Sprite>();
		for (Sprite sprite : spriteList) {
			if (sprite.processOnTouch(xCoordinate, yCoordinate)) {
				touchedSpriteList.add(sprite);
			}
		}

		Collections.sort(touchedSpriteList);
		if (!touchedSpriteList.isEmpty()) {
			touchedSpriteList.get(touchedSpriteList.size() - 1).startWhenScripts(action);
		}
	}

	public void pause(boolean drawScreen) {
		for (Sprite sprite : spriteList) {
			sprite.pause();
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

	public void finish() {
		for (Sprite sprite : spriteList) {
			sprite.finish();
		}
	}
}
