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

import at.tugraz.ist.catroid.content.script.Script;

public class Sprite implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private int xPosition;
	private int yPosition;
	private int zPosition;
	private double scale;
	private boolean isVisible;
	private List<Costume> costumeList;
	private List<Script> scriptList;
	private Costume currentCostume;


	private void init() {
		this.zPosition = 0;
		this.scale = 1.0;
		this.isVisible = true;
		this.costumeList = new ArrayList<Costume>();
		this.scriptList = new ArrayList<Script>();
		this.currentCostume = null;
	}
	
	public Sprite(String name) {
		this.name = name;
		this.xPosition = 0;
		this.yPosition = 0;
		
		init();
	}

	public Sprite(String name, int xPosition, int yPosition) {
		this.name = name;
		this.xPosition = xPosition;
		this.yPosition = yPosition;

		init();
	}

	public void startScripts() {
		for (Script s : scriptList) {
			final Script script = s;
			new Thread(
		            new Runnable() {
		                public void run() {
		                   script.run();
		                }
		            }).start();
		}
	}
	
	public void pause() {
		
	}
	
	public void unpause() {
		
	}
	
	public String getName() {
		return name;
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
	}

	public synchronized void setZPosition(int zPosition) {
		this.zPosition = zPosition;
	}

	public void setScale(double scale) {
		if (scale <= 0.0)
			throw new IllegalArgumentException("Sprite scale must be greater than zero!");
		this.scale = scale;
	}

	public void show() {
		isVisible = true;
	}

	public void hide() {
		isVisible = false;
	}

	public Costume getCurrentCostume() {
		return currentCostume;
	}

	public List<Costume> getCostumeList() {
		return costumeList;
	}
	
	public List<Script> getScriptList() {
		return scriptList;
	}

	public void setCurrentCostume(Costume costume) throws IllegalArgumentException {
		if(!costumeList.contains(costume))
			throw new IllegalArgumentException("Selected costume is not contained in Costume list of this sprite.");
		currentCostume = costume;
	}
}
