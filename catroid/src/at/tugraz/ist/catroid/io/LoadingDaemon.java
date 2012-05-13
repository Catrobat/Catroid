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

import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * @author Markus
 * 
 */
public class LoadingDaemon {

	private static LoadingDaemon instance;
	final Thread daemon;
	final ObjectMap<Class<?>, ObjectMap<String, Object>> objectMap;
	final Array<ObjectDescriptor> preloadQueue;
	final Stack<LoadingTask> tasks;
	final ExecutorService threadPool;

	public static LoadingDaemon getInstance() {
		if (instance == null) {
			instance = new LoadingDaemon();
		}
		return instance;
	}

	private LoadingDaemon() {
		objectMap = new ObjectMap<Class<?>, ObjectMap<String, Object>>();
		preloadQueue = new Array<LoadingDaemon.ObjectDescriptor>();
		tasks = new Stack<LoadingTask>();
		threadPool = Executors.newFixedThreadPool(1, new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
		daemon = new Thread(new Runnable() {

			public void run() {
				while (true) {
					try {
						if (instance.update()) {
							Thread.yield();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

			@Override
			public void finalize() {
				instance.clear();

			}
		}, "Loading Daemon");
		daemon.setDaemon(true);
	}

	public synchronized boolean update() {
		return false;
	}

	public synchronized void load(String fileName, Class<?> type) {

	}

	public synchronized void unload(String fileName) {

	}

	public synchronized void clear() {

	}

	public synchronized Object get(String fileName, Class<?> type) {

		return null;
	}

	public synchronized void startDaemon() {
		if (!daemon.isAlive()) {
			daemon.start();
		}
	}

	public class ObjectDescriptor {
		final String fileName;
		final Class<?> type;

		public ObjectDescriptor(String fileName, Class<?> type) {
			this.fileName = fileName;
			this.type = type;

		}

		@Override
		public String toString() {
			return fileName;
		}
	}

}
