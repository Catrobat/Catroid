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

import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * @author MH
 * 
 */
public class LoadingDaemon extends Thread implements ApplicationListener {

	private static LoadingDaemon instance;
	private Project currentProject = null;
	private List<Sprite> spriteList;
	private ArrayList<CostumeData> costumeDataList;
	private boolean daemonRunning = false;
	private boolean projectLoading = false;
	private Semaphore costumeDataListLock = new Semaphore(1);

	@Override
	public void run() {
		try {
			if (costumeDataList.isEmpty()) {
				Thread.yield();
			} else {
				costumeDataListLock.acquireUninterruptibly();
				for (int j = 0; j < costumeDataList.size(); j++) {
					if (projectLoading) {
						costumeDataListLock.release();
						return;
					}
					CostumeData data = costumeDataList.get(j);
					String path = data.getAbsolutePath();
					FileHandle file = Gdx.files.absolute(path);
					Pixmap pixmap = new Pixmap(file);
					data.setPixmap(pixmap);
					//Texture texture = new Texture(file);
					//TextureRegion region = new TextureRegion(texture);
					//data.setTextureRegion(region);
				}
				costumeDataListLock.release();
				if (!costumeDataList.isEmpty()) {
					costumeDataList.clear();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			start();
			daemonRunning = true;
		}
	}

	private LoadingDaemon() {
		setDaemon(true);
	}

	public static LoadingDaemon getInstance() {
		if (instance == null) {
			instance = new LoadingDaemon();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	public void create() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
	 */
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#render()
	 */
	public void render() {

	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#pause()
	 */
	public void pause() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.badlogic.gdx.ApplicationListener#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
