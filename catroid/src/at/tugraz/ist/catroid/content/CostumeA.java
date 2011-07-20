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

import java.util.concurrent.Semaphore;

import at.tugraz.ist.catroid.utils.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actors.Image;

/**
 * @author jib218
 * 
 */
public class CostumeA extends Image {
	private Semaphore xyLock = new Semaphore(1);
	private Semaphore setImageLock = new Semaphore(1);
	private Sprite sprite;

	public CostumeA(Sprite sprite) {
		super(Utils.getUniqueName());
		this.sprite = sprite;
		this.x = 0;
		this.y = 0;
	}

	public void setX(float x) {
		xyLock.acquireUninterruptibly();
		this.x = x;
		xyLock.release();
	}

	public void setY(float y) {
		xyLock.acquireUninterruptibly();
		this.y = y;
		xyLock.release();
	}

	public void setXY(float x, float y) {
		xyLock.acquireUninterruptibly();
		this.x = x;
		this.y = y;
		xyLock.release();
	}

	public void setImagePath(String path) {
		setImageLock.acquireUninterruptibly();
		Texture tex = new Texture(Gdx.files.absolute(path));
		this.region = new TextureRegion(tex);
		System.out.println("++++++++++ Path: " + path);
		setImageLock.release();
	}
}
