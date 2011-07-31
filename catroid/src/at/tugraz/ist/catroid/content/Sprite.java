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

public class Sprite implements Serializable, Comparable<Sprite> {
	private static final long serialVersionUID = 1L;
	private String name;
	private transient int zPosition;
	private List<Script> scriptList;
	public transient Costume costume;

	public transient volatile boolean isPaused;
	public transient volatile boolean isFinished;

	private Object readResolve() {
		init();
		return this;
	}

	private void init() {
		zPosition = 0;
		costume = new Costume(this);
		isPaused = false;
		isFinished = false;
	}

	public Sprite(String name) {
		this.name = name;
		scriptList = new ArrayList<Script>();
		init();
	}

	public void startWhenScripts(String action) {
		for (Script s : scriptList) {
			if (s instanceof WhenScript) {
				if (((WhenScript) s).getAction().equalsIgnoreCase(action)) {
					startScript(s);
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

	public int getZPosition() {
		return zPosition;
	}

	public synchronized void setZPosition(int zPosition) {
		this.zPosition = zPosition;
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

	public int compareTo(Sprite sprite) {
		long thisZValue = getZPosition();
		long otherZValue = sprite.getZPosition();
		long difference = thisZValue - otherZValue;
		if (difference > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return (int) difference;
	}

	@Override
	public String toString() {
		return name;
	}
}
