/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content;

import java.io.Serializable;
import java.util.ArrayList;

import at.tugraz.ist.catroid.content.bricks.Brick;

public abstract class Script implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<Brick> brickList;
	protected transient boolean isFinished;
	private transient volatile boolean paused;
	private transient volatile boolean finish;
	private transient int executingBrickIndex;
	protected Sprite sprite;

	protected Object readResolve() {
		init();
		return this;
	}

	public Script(Sprite sprite) {
		brickList = new ArrayList<Brick>();
		this.sprite = sprite;
		init();
	}

	private void init() {
		paused = false;
		finish = false;
	}

	public void run() {
		isFinished = false;
		for (int i = 0; i < brickList.size(); i++) {
			if (!sprite.isAlive(Thread.currentThread())) {
				break;
			}
			while (paused) {
				if (finish) {
					isFinished = true;
					return;
				}
				Thread.yield();
			}
			executingBrickIndex = i;
			brickList.get(i).execute();
			i = executingBrickIndex;
		}
		isFinished = true;
	}

	public void addBrick(Brick brick) {
		if (brick != null) {
			brickList.add(brick);
		}
	}

	public void addBrick(int position, Brick brick) {
		if (brick != null) {
			brickList.add(position, brick);
		}
	}

	public void removeBrick(Brick brick) {
		brickList.remove(brick);
	}

	public ArrayList<Brick> getBrickList() {
		return brickList;
	}

	public synchronized void setPaused(boolean paused) {
		this.paused = paused;
	}

	public synchronized void setFinish(boolean finish) {
		this.finish = finish;
	}

	public boolean isPaused() {
		return paused;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public int getExecutingBrickIndex() {
		return executingBrickIndex;
	}

	public void setExecutingBrickIndex(int executingBrickIndex) {
		this.executingBrickIndex = executingBrickIndex;
	}

	public int getRequiredResources() {
		int ressources = Brick.NO_RESOURCES;

		for (Brick brick : brickList) {
			ressources |= brick.getRequiredResources();
		}
		return ressources;
	}

	public boolean containsBrickOfType(Class<?> type) {
		for (Brick brick : brickList) {
			//Log.i("bt", brick.REQUIRED_RESSOURCES + "");
			if (brick.getClass() == type) {
				return true;
			}
		}
		return false;
	}

	public int containsBrickOfTypeReturnsFirstIndex(Class<?> type) {
		int i = 0;
		for (Brick brick : brickList) {

			if (brick.getClass() == type) {
				return i;
			}
			i++;
		}
		return -1;
	}

	//
	//	public boolean containsBluetoothBrick() {
	//		for (Brick brick : brickList) {
	//			if ((brick instanceof NXTMotorActionBrick) || (brick instanceof NXTMotorTurnAngleBrick)
	//					|| (brick instanceof NXTMotorStopBrick) || (brick instanceof NXTPlayToneBrick)) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}

	public Brick getBrick(int index) {
		return brickList.get(index);
	}
}
