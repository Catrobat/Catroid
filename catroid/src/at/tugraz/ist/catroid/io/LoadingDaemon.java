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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;

/**
 * @author Markus
 * 
 */
public class LoadingDaemon implements ApplicationListener, AssetErrorListener {

	private static LoadingDaemon instance;
	AssetManager manager;
	private Thread daemon;

	public static LoadingDaemon getInstance() {
		if (instance == null) {
			instance = new LoadingDaemon();
		}
		return instance;
	}

	private LoadingDaemon() {
		manager = new AssetManager(new AbsoluteFileHandleResolver());
		manager.setErrorListener(this);

		daemon = new Thread(new Runnable() {

			public void run() {
				while (true) {
					try {
						boolean test = update();
						if (test) {
							Thread.yield();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			public void finalize() {
				manager.clear();

			}
		});
		daemon.setDaemon(true);

	}

	public boolean update() {

		try {
			boolean result = manager.update();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void load(String fileName, Class<?> type) {
		manager.load(fileName, type);

	}

	public void unload(String fileName) {
		manager.unload(fileName);

	}

	public void clear() {
		manager.clear();

	}

	public Object get(String fileName, Class<?> type) {
		return manager.get(fileName, type);
	}

	public void startDaemon() {
		if (!daemon.isAlive()) {
			daemon.start();
		}
	}

	public void stopDaemon() {
		if (daemon.isAlive()) {
			daemon.stop();
		}
	}

	@SuppressWarnings("rawtypes")
	public void error(String fileName, Class arg1, Throwable t) {
		Gdx.app.error("AssetManagerTest", "couldn't load asset '" + fileName + "'", t);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#create()
	 */
	public void create() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
	 */
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#render()
	 */
	public void render() {
		// TODO Auto-generated method stub
		int i = 0;
		i = i + 1;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#pause()
	 */
	public void pause() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#resume()
	 */
	public void resume() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.ApplicationListener#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
