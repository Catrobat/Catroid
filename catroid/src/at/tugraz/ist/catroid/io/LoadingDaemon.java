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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;

/**
 * @author Markus
 * 
 */
public class LoadingDaemon implements AssetErrorListener {

	private static LoadingDaemon instance;
	private AssetManager manager;
	private Thread daemon;

	public static LoadingDaemon getInstance() {
		if (instance == null) {
			instance = new LoadingDaemon();
		}
		return instance;
	}

	private LoadingDaemon() {
		manager = new AssetManager();
		manager.setErrorListener(this);
		daemon = new Thread(new Runnable() {

			public void run() {
				while (true) {
					try {
						if (manager.update()) {
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
		if (manager.isLoaded(fileName, type)) {
			return type.cast(manager.get(fileName, type));
		}
		return null;
	}

	public void startDaemon() {
		if (!daemon.isAlive()) {
			daemon.start();
		}
	}

	@SuppressWarnings("rawtypes")
	public void error(String fileName, Class type, Throwable t) {
		Gdx.app.error("LoadingDaemon", "couldn't load asset '" + fileName + "'" + type.toString(), t);

	}

}
