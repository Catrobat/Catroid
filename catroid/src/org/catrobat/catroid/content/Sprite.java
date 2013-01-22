/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.bricks.Brick;

public class Sprite implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private List<Script> scriptList;
	private ArrayList<CostumeData> costumeList;
	private ArrayList<SoundInfo> soundList;
	public transient Costume costume;

	public transient boolean isPaused;
	public transient boolean isFinished;

	private transient Map<Thread, Boolean> activeThreads;
	private transient Map<Script, List<Thread>> activeScripts;

	private Object readResolve() {
		//filling FileChecksumContainer:
		if (soundList != null && costumeList != null && ProjectManager.getInstance().getCurrentProject() != null) {
			FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();
			if (container == null) {
				ProjectManager.getInstance().setFileChecksumContainer(new FileChecksumContainer());
			}
			for (SoundInfo soundInfo : soundList) {
				container.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
			}
			for (CostumeData costumeData : costumeList) {
				container.addChecksum(costumeData.getChecksum(), costumeData.getAbsolutePath());
			}
		}
		init();
		return this;
	}

	private void init() {
		costume = new Costume(this);
		isPaused = false;
		isFinished = false;
		if (soundList == null) {
			soundList = new ArrayList<SoundInfo>();
		}
		if (costumeList == null) {
			costumeList = new ArrayList<CostumeData>();
		}
		activeThreads = new HashMap<Thread, Boolean>();
		activeScripts = new HashMap<Script, List<Thread>>();
	}

	public Sprite(String name) {
		this.name = name;
		scriptList = new ArrayList<Script>();
		costumeList = new ArrayList<CostumeData>();
		soundList = new ArrayList<SoundInfo>();
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

	public Sprite() {

	}

	private synchronized void startScript(Script s) {
		final Script script = s;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				script.run();
			}
		});
		if (script instanceof WhenScript) {
			if (!activeScripts.containsKey(script)) {
				activeScripts.put(script, new LinkedList<Thread>());
				activeScripts.get(script).add(t);
				activeThreads.put(t, true);
			} else {
				ListIterator<Thread> currentScriptThreads = activeScripts.get(script).listIterator();
				while (currentScriptThreads.hasNext()) {
					Thread currentThread = currentScriptThreads.next();
					activeThreads.put(currentThread, false);
				}
				activeScripts.get(script).clear();
				activeScripts.get(script).add(t);
				activeThreads.put(t, true);
			}
		}
		t.start();
	}

	public synchronized void startScriptBroadcast(Script s, final CountDownLatch simultaneousStart) {
		final Script script = s;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					simultaneousStart.await();
				} catch (InterruptedException e) {
				}
				script.run();
			}
		});
		if (script instanceof BroadcastScript) {
			if (!activeScripts.containsKey(script)) {
				activeScripts.put(script, new LinkedList<Thread>());
				activeScripts.get(script).add(t);
				activeThreads.put(t, true);
			} else {
				ListIterator<Thread> currentScriptThreads = activeScripts.get(script).listIterator();
				while (currentScriptThreads.hasNext()) {
					Thread currentThread = currentScriptThreads.next();
					activeThreads.put(currentThread, false);
				}
				activeScripts.get(script).clear();
				activeScripts.get(script).add(t);
				activeThreads.put(t, true);
			}
		}
		t.start();
	}

	public synchronized void startScriptBroadcastWait(Script s, final CountDownLatch simultaneousStart,
			final CountDownLatch wait) {
		final Script script = s;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					simultaneousStart.await();
				} catch (InterruptedException e) {
				}
				script.run();
				wait.countDown();
			}
		});
		if (script instanceof BroadcastScript) {

			if (!activeScripts.containsKey(script)) {
				activeScripts.put(script, new LinkedList<Thread>());
				activeScripts.get(script).add(t);
				activeThreads.put(t, true);
			} else {
				ListIterator<Thread> currentScriptThreads = activeScripts.get(script).listIterator();
				while (currentScriptThreads.hasNext()) {
					Thread currentThread = currentScriptThreads.next();
					activeThreads.put(currentThread, false);
				}
				activeScripts.get(script).clear();
				activeScripts.get(script).add(t);
				activeThreads.put(t, true);
			}
		}
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
		if (index < 0 || index >= scriptList.size()) {
			return null;
		}
		return scriptList.get(index);
	}

	public int getNumberOfScripts() {
		return scriptList.size();
	}

	public int getNumberOfBricks() {
		int brickCount = 0;
		for (Script s : scriptList) {
			brickCount += s.getBrickList().size();
		}
		return brickCount;
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

	public ArrayList<CostumeData> getCostumeDataList() {
		return costumeList;
	}

	public ArrayList<SoundInfo> getSoundList() {
		return soundList;
	}

	public void setSoundList(ArrayList<SoundInfo> list) {
		soundList = list;
	}

	public int getRequiredResources() {
		int ressources = Brick.NO_RESOURCES;

		for (Script script : scriptList) {
			ressources |= script.getRequiredResources();
		}
		return ressources;
	}

	@Override
	public String toString() {
		return name;
	}

	public synchronized boolean isAlive(Thread thread) {
		if (activeThreads.containsKey(thread)) {
			return activeThreads.get(thread);
		} else {
			return true;
		}

	}

	public synchronized void setInactive(Thread thread) {
		if (activeThreads.containsKey(thread)) {
			activeThreads.remove(thread);
		}
	}
}
