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
import java.util.concurrent.CountDownLatch;

import android.graphics.Color;
import at.tugraz.ist.catroid.common.Consts;

public class Sprite implements Serializable, Comparable<Sprite> {
	private static final long serialVersionUID = 1L;
	private static final int MIN_ALPHA = 10;
	private String name;
	private transient int xPosition;
	private transient int yPosition;
	private transient int zPosition;
	private transient double size;
	private transient boolean isVisible;
	private transient boolean toDraw;
	private List<Script> scriptList;
	private transient Costume costume;

	public transient volatile boolean isPaused;
	public transient volatile boolean isFinished;

	private Object readResolve() {
		init();
		return this;
	}

	private void init() {
		zPosition = 0;
		size = 100.0;
		isVisible = true;
		costume = new Costume(this, null);
		xPosition = 0;
		yPosition = 0;
		toDraw = false;
		isPaused = false;
		isFinished = false;
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
		t.start();
	}

	public void startScriptBroadcast(Script s, final CountDownLatch simultaneousStart) {
		final Script script = s;
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					simultaneousStart.await();
				} catch (InterruptedException e) {
				}
				script.run();
			}
		});
		t.start();
	}

	public void startScriptBroadcastWait(Script s, final CountDownLatch simultaneousStart, final CountDownLatch wait) {
		final Script script = s;
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					simultaneousStart.await();
				} catch (InterruptedException e) {
				}
				script.run();
				wait.countDown();
			}
		});
		t.start();
	}

	public void pause() {
		for (Script s : scriptList) {
			s.setPaused(true);
		}
		this.isPaused = true;
	}

	public void resume() {
		for (Script s : scriptList) {
			s.setPaused(false);
		}
		this.isPaused = false;
	}

	public void finish() {
		for (Script s : scriptList) {
			s.setFinish(true);
		}
		this.isFinished = true;
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

	public double getSize() {
		return size;
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

	public synchronized void setSize(double size) {
		if (size <= 0.0) {
			throw new IllegalArgumentException("Sprite size must be greater than zero!");
		}

		int costumeWidth = costume.getImageWidthHeight().first;
		int costumeHeight = costume.getImageWidthHeight().second;

		this.size = size;

		if (costumeWidth > 0 && costumeHeight > 0) {
			if (costumeWidth * this.size / 100. < 1) {
				this.size = 1. / costumeWidth * 100.;
			}
			if (costumeHeight * this.size / 100. < 1) {
				this.size = 1. / costumeHeight * 100.;
			}

			if (costumeWidth * this.size / 100. > Consts.MAX_COSTUME_WIDTH) {
				this.size = (double) Consts.MAX_COSTUME_WIDTH / costumeWidth * 100.;
			}

			if (costumeHeight * this.size / 100. > Consts.MAX_COSTUME_HEIGHT) {
				this.size = (double) Consts.MAX_COSTUME_HEIGHT / costumeHeight * 100.;
			}

			costume.setSizeTo(this.size);
			toDraw = true;
		}
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

	public void addScript(Script script) {
		if (script != null && !scriptList.contains(script)) {
			scriptList.add(script);
		}
	}

	public void addScript(int index, Script script) {
		if (script != null && !scriptList.contains(script)) {
			scriptList.add(index, script);
		}
	}

	public Script getScript(int index) {
		return scriptList.get(index);
	}

	public int getNumberOfScripts() {
		return scriptList.size();
	}

	public int getScriptIndex(Script script) {
		return scriptList.indexOf(script);
	}

	public void removeAllScripts() {
		scriptList.clear();
	}

	public boolean removeScript(Script script) {
		return scriptList.remove(script);
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

	public boolean processOnTouch(int xCoordinate, int yCoordinate) {
		if (costume.getBitmap() == null || isVisible == false) {
			return false;
		}

		int inSpriteXCoordinate = xCoordinate - costume.getDrawPositionX();
		int inSpriteYCoordinate = yCoordinate - costume.getDrawPositionY();

		int width = costume.getImageWidthHeight().first;
		int height = costume.getImageWidthHeight().second;

		if (inSpriteXCoordinate < 0 || inSpriteXCoordinate > width) {
			return false;
		}
		if (inSpriteYCoordinate < 0 || inSpriteYCoordinate > height) {
			return false;
		}
		if (Color.alpha(costume.getBitmap().getPixel(inSpriteXCoordinate, inSpriteYCoordinate)) <= MIN_ALPHA) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
