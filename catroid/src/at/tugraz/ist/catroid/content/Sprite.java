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
package at.tugraz.ist.catroid.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.util.Pair;
import at.tugraz.ist.catroid.common.Consts;

public class Sprite implements Serializable, Comparable<Sprite> {
	private static final long serialVersionUID = 1L;
	private String name;
	private transient int xPosition;
	private transient int yPosition;
	private transient int zPosition;
	private transient double scale;
	private transient boolean isVisible;
	private transient boolean toDraw;
	private List<Script> scriptList;
	private transient List<Thread> threadList;
	private transient Costume costume;

	private Object readResolve() {
		init();
		return this;
	}

	private void init() {
		zPosition = 0;
		scale = 100.0;
		isVisible = true;
		threadList = new ArrayList<Thread>();
		costume = new Costume(this, null);
		xPosition = 0;
		yPosition = 0;
		toDraw = false;
	}

	public Sprite(String name) {
		this.name = name;
		scriptList = new ArrayList<Script>();
		init();
	}

	public void startStartScripts() {
		for (Script s : scriptList) {
			if (s instanceof StartScript) {
				if (!s.isFinished()) {
					startScript(s);
				}
			}
		}
	}

	public void startTapScripts() {
		for (Script s : scriptList) {
			if (s instanceof TapScript) {
				startScript(s);
			}
		}
	}

	private void startScript(Script s) {
		final Script script = s;
		Thread t = new Thread(new Runnable() {
			public void run() {
				script.run();
			}
		});
		threadList.add(t);
		t.start();
	}

	public void pause() {
		for (Script s : scriptList) {
			s.setPaused(true);
		}
		for (Thread t : threadList) {
			t.interrupt();
		}
		threadList.clear();
	}

	public void resume() {
		for (Script s : scriptList) {
			s.setPaused(false);
			if (s.isFinished()) {
				continue;
			}
			startScript(s);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getXPosition() {
		return xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}

	public int getZPosition() {
		return zPosition;
	}

	public double getScale() {
		return scale;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public synchronized void setXYPosition(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		costume.setDrawPosition();
		toDraw = true;
	}

	public synchronized void setZPosition(int zPosition) {
		this.zPosition = zPosition;
		toDraw = true;
	}

	public synchronized void setScale(double scale) {
		if (scale <= 0.0) {
			throw new IllegalArgumentException("Sprite scale must be greater than zero!");
		}

		int width = costume.getImageWidthHeight().first;
		int height = costume.getImageWidthHeight().second;

		if (width == 0 || height == 0) {
			this.scale = scale;
			return;
		}

		this.scale = scale;

		if (width * this.scale / 100. < 1) {
			this.scale = 1. / width * 100.;
		}
		if (height * this.scale / 100. < 1) {
			this.scale = 1. / height * 100.;
		}

		if (width * this.scale / 100. > Consts.MAX_COSTUME_WIDTH) {
			this.scale = (double) Consts.MAX_COSTUME_WIDTH / width * 100.;
		}

		if (height * this.scale / 100. > Consts.MAX_COSTUME_HEIGHT) {
			this.scale = (double) Consts.MAX_COSTUME_HEIGHT / height * 100.;
		}

		costume.scale(this.scale);
		toDraw = true;
	}

	public synchronized void show() {
		isVisible = true;
		toDraw = true;
	}

	public synchronized void hide() {
		isVisible = false;
		toDraw = true;
	}

	public synchronized Costume getCostume() {
		return costume;
	}

	public List<Script> getScriptList() {
		return scriptList;
	}

	public boolean getToDraw() {
		return toDraw;
	}

	public void setToDraw(boolean value) {
		toDraw = value;
	}

	public int compareTo(Sprite sprite) {
		long thisZValue = getZPosition();
		long otherZValue = sprite.getZPosition();
		long difference = thisZValue - otherZValue;
		if (difference > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int) difference;
	}

	public boolean processOnTouch(int coordX, int coordY) {
		if (costume.getBitmap() == null || isVisible == false) {
			return false;
		}

		int inSpriteCoordX = coordX - costume.getDrawPositionX();
		int inSpriteCoordY = coordY - costume.getDrawPositionY();

		Pair<Integer, Integer> tempPair = costume.getImageWidthHeight();
		int width = tempPair.first;
		int height = tempPair.second;

		if (inSpriteCoordX < 0 || inSpriteCoordX > width) {
			return false;
		}
		if (inSpriteCoordY < 0 || inSpriteCoordY > height) {
			return false;
		}

		try {
			if (Color.alpha(costume.getBitmap().getPixel(inSpriteCoordX, inSpriteCoordY)) <= 10) {
				return false;
			}
		} catch (Exception ex) {
			return false;
		}

		return true;

	}

	@Override
	public String toString() {
		return name;
	}
}
