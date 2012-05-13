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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import at.tugraz.ist.catroid.io.LoadingDaemon.ObjectDescriptor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * @author Markus
 * 
 */
public class LoadingTask implements Runnable {

	final String fileName;
	final Class<?> type;
	final ExecutorService threadPool;
	Future<?> loadFuture = null;
	Object object = null;

	public LoadingTask(ObjectDescriptor desc, ExecutorService threadPool) {
		this.fileName = desc.fileName;
		this.type = desc.type;
		this.threadPool = threadPool;

	}

	public void run() {
		if (type == Pixmap.class) {
			object = new Pixmap(Gdx.files.absolute(fileName));
		} else if (type == Texture.class) {
			object = new Texture(Gdx.files.absolute(fileName));
		}

	}

	public boolean update() {
		if (loadFuture == null) {
			loadFuture = threadPool.submit(this);
		} else if (loadFuture.isDone()) {
			try {
				loadFuture.get();
			} catch (Exception e) {
				throw new GdxRuntimeException("Could not load object " + fileName + "," + e);
			}
		}
		return object != null;
	}

	public Object get() {
		return object;
	}

}
