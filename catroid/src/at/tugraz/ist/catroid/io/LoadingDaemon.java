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
package at.tugraz.ist.catroid.io;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

public class LoadingDaemon extends Thread implements ApplicationListener {

	private static LoadingDaemon instance;
	private Project currentProject = null;
	private List<Sprite> spriteList;
	private ArrayList<CostumeData> costumeDataList;
	private boolean daemonRunning = false;
	private boolean projectLoading = false;
	private Semaphore costumeDataListLock = new Semaphore(1);

	private LoadingDaemon() {
		setDaemon(true);
		setName("CatroidLoadingDaemon");
	}

	public static LoadingDaemon getInstance() {
		if (instance == null) {
			instance = new LoadingDaemon();
		}
		return instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (costumeDataList.isEmpty()) {
					Thread.yield();
				} else {

					for (int j = 0; j < costumeDataList.size(); j++) {
						costumeDataListLock.acquireUninterruptibly();
						if (projectLoading) {
							costumeDataListLock.release();
							break;
						}
						CostumeData data = costumeDataList.get(j);
						data.setPixmap(new Pixmap(Gdx.files.absolute(data.getAbsolutePath())));
						costumeDataListLock.release();
					}
					costumeDataListLock.acquireUninterruptibly();
					if (!costumeDataList.isEmpty()) {
						costumeDataList.clear();
					}
					costumeDataListLock.release();
				}
			}
		} catch (Exception e) {
			Log.e("CATROID", "LoadingDaemon: Failed to load pixmap!", e);
			return;
		}

	}

	public void initAndStart() {
		if ((currentProject == null) || (currentProject != ProjectManager.getInstance().getCurrentProject())) {
			currentProject = ProjectManager.getInstance().getCurrentProject();
			spriteList = new ArrayList<Sprite>();
			spriteList = currentProject.getSpriteList();
			projectLoading = true;
			costumeDataListLock.acquireUninterruptibly();
			costumeDataList = new ArrayList<CostumeData>();
			for (int i = 0; i < spriteList.size(); i++) {
				costumeDataList.addAll(spriteList.get(i).getCostumeDataList());
			}
			projectLoading = false;
			costumeDataListLock.release();
		}
		if (!daemonRunning) {
			//start();
			daemonRunning = true;
		}
	}

	public void addCostumeDataToList(CostumeData data) {
		CostumeData costumeData = data;
		costumeDataListLock.acquireUninterruptibly();
		costumeDataList.add(costumeData);
		costumeDataListLock.release();
	}

	public void removeCostumeDataFromList(int position) {
		int pos = position;
		costumeDataListLock.acquireUninterruptibly();
		if (pos < costumeDataList.size()) {
			costumeDataList.remove(pos);
		}
		costumeDataListLock.release();
	}

	public void create() {

	}

	public void resize(int width, int height) {

	}

	public void render() {

	}

	public void pause() {

	}

	public void dispose() {

	}

}
