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
import android.util.Pair;
import at.tugraz.ist.catroid.common.Consts;

public class Sprite implements Serializable, Comparable<Sprite> {
	private static final long serialVersionUID = 1L;
	private String name;
	private transient int xPosition;
	private transient int yPosition;
	private transient int zPosition;
	private transient double size;
	private transient boolean isVisible;
	private transient boolean toDraw;
	private List<Script> scriptList;
	private transient Costume costume;
	private transient double ghostEffectValue;
	private transient double brightnessValue;
	private transient double volume;

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
		ghostEffectValue = 0.0;
		brightnessValue = 0.0;
		isPaused = false;
		isFinished = false;
		volume = 70.0;
	}

	public Sprite(String name) {
		this.name = name;
		scriptList = new ArrayList<Script>();
		init();
	}

	public void startWhenScripts(String action) {
		for (Script s : scriptList) {
			if (s instanceof WhenScript) {
				if (((WhenScript) s).getAction().equalsIgnoreCase(WhenScript.TOUCHINGSTOPS)) {
					s.setPaused(true);
					s.isFinished = true;
				} else {
					if (((WhenScript) s).getAction().equalsIgnoreCase(action)) {
						startScript(s);
					}
				}
			}
		}
	}

	public void startStartScripts() {
		for (Script s : scriptList) {
			if (s instanceof StartScript) {
				if (!s.isFinished()) {
					startScript(s);
				}
			} else if (s instanceof WhenScript) {
				if (((WhenScript) s).getAction().equalsIgnoreCase(WhenScript.TOUCHINGSTOPS)) {
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
			s.setPaused(true);
			s.setFinish(true);
			s.isFinished = true;
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

	public double getGhostEffectValue() {
		return ghostEffectValue;
	}

	public double getBrightnessValue() {
		return this.brightnessValue;
	}

	public double getVolume() {
		return this.volume;
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

	public synchronized void setVolume(double volume) {

		this.volume = volume;
		toDraw = true;
	}

	public synchronized void clearGraphicEffect() {
		costume.clearGraphicEffect();
		ghostEffectValue = 0;
		brightnessValue = 0;
		toDraw = true;
	}

	public synchronized void setBrightnessValue(double brightnessValue) {
		this.brightnessValue = brightnessValue;
		double brightness;

		if (brightnessValue > 100) {
			brightness = 170;
		} else if (brightnessValue < 0 && brightnessValue > -100) {
			brightness = brightnessValue - 70;
		} else if (brightnessValue <= -100) {
			brightness = -255;
		} else {
			brightness = brightnessValue + 70;
		}

		costume.setBrightness(brightness);
		toDraw = true;
	}

	public synchronized void setGhostEffectValue(double ghostEffectValue) {
		this.ghostEffectValue = Math.abs(ghostEffectValue);
		double calculation = 0;
		int opacityValue = 0;

		calculation = Math.floor(255 - (this.ghostEffectValue * 2.55));
		//		 calculation: a value between 0 (completely transparent) and 255 (completely opaque).
		if (calculation > 0) {
			if (calculation >= 12) {
				opacityValue = (int) calculation; // Effect value from 0% to 95%
			} else {
				opacityValue = 12; // Effect value from 96% to 99%. Opacity Value more than 12 sprite would be untouchable.
			}
		} else if (calculation <= 0) {
			opacityValue = 0; //  Effect value 100%. Sprite untouchable.
		}
		costume.setGhostEffect(opacityValue);
		toDraw = true;
	}

	public synchronized void setSize(double size) {
		if (size <= 0.0) {
			throw new IllegalArgumentException("Sprite size must be greater than zero!");
		}

		int width = costume.getImageWidthHeight().first;
		int height = costume.getImageWidthHeight().second;

		if (width == 0 || height == 0) {
			this.size = size;
			return;
		}

		this.size = size;

		if (width * this.size / 100. < 1) {
			this.size = 1. / width * 100.;
		}
		if (height * this.size / 100. < 1) {
			this.size = 1. / height * 100.;
		}

		if (width * this.size / 100. > Consts.MAX_COSTUME_WIDTH) {
			this.size = (double) Consts.MAX_COSTUME_WIDTH / width * 100.;
		}

		if (height * this.size / 100. > Consts.MAX_COSTUME_HEIGHT) {
			this.size = (double) Consts.MAX_COSTUME_HEIGHT / height * 100.;
		}

		costume.setSizeTo(this.size);
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

	public void addScript(Script script) {
		if (script != null && !scriptList.contains(script)) {
			scriptList.add(script);
		}
	}

	public void addScript(int location, Script script) {
		if (script != null && !scriptList.contains(script)) {
			scriptList.add(location, script);
		}
	}

	public Script getScript(int location) {
		return scriptList.get(location);
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