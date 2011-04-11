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
package at.tugraz.ist.catroid.content.sprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.util.Pair;
import at.tugraz.ist.catroid.content.script.Script;

public class Sprite implements Serializable, Comparable<Sprite> {
	private static final long serialVersionUID = 1L;
	private String name;
	private int xPosition;
	private int yPosition;
	private int zPosition;
	private double scale;
	private boolean isVisible;
	private boolean toDraw;
	private List<Script> scriptList;
	private List<Thread> threadList;
	private Costume costume;

	private void init() {
		zPosition = 0;
		scale = 100.0;
		isVisible = true;
		scriptList = new ArrayList<Script>();
		threadList = new ArrayList<Thread>();
		costume = new Costume(this,null);
	}

	public Sprite(String name) {
		this.name = name;
		xPosition = 0;
		yPosition = 0;
		toDraw = false;
		init();
	}

	public Sprite(String name, int xPosition, int yPosition) {
		this.name = name;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		toDraw = false;
		init();
	}

	public void startScripts() {
		for (Script s : scriptList) {
			if (!s.isTouchScript()) {
				startScript(s);
			}
		}
	}

	public void startTouchScripts() {
		for (Script s : scriptList) {
			if (s.isTouchScript()) {
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
			if (s.isTouchScript() && s.isFinished()) {
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
		this.scale = scale;
		costume.scale(scale);
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
		if(costume.getBitmap() == null) {
			return false;
		}

		int inSpriteCoordX = coordX - costume.getDrawPositionX();
		int inSpriteCoordY = coordY - costume.getDrawPositionY();

		Pair<Integer,Integer> tempPair = costume.getImageWidthHeight();
		int width = tempPair.first;
		int height = tempPair.second;

		if (inSpriteCoordX < 0 || inSpriteCoordX > width) {
			return false;
		}
		if (inSpriteCoordY < 0 || inSpriteCoordY > height) {
			return false;
		}

		try{
			if (Color.alpha(costume.getBitmap().getPixel(inSpriteCoordX, inSpriteCoordY)) <= 10) {
				return false;
			}
		}catch(Exception ex){
			return false;
		}

		return true;

	}

	@Override
	public String toString() {
		return name;
	}
}
