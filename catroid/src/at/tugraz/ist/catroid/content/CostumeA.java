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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actors.Image;

/**
 * @author jib218
 * 
 */
public class CostumeA extends Image {
	private Semaphore xyLock = new Semaphore(1);
	private Semaphore setImageLock = new Semaphore(1);
	private boolean imageChanged = false;
	private String imagePath;
	private Sprite sprite;
	public float alphaValue;

	public CostumeA(Sprite sprite) {
		super(Utils.getUniqueName());
		this.x = 0;
		this.y = 0;
		this.sprite = sprite;
		this.alphaValue = 1f;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		checkImageChanged();
		super.draw(batch, this.alphaValue);
	}

	private void checkImageChanged() {
		if (imageChanged) {
			if (this.region != null && this.region.getTexture() != null) {
				this.region.getTexture().dispose();
			}
			Texture tex = new Texture(Gdx.files.absolute(imagePath));
			//			this.width = tex.getWidth();
			//			this.height = tex.getHeight();
			this.width = 256;
			this.height = 256;
			this.x = this.width / -2;
			this.y = this.height / -2;
			this.region = new TextureRegion(tex);
			imageChanged = false;
		}
	}

	public void setXPosition(float x) {
		xyLock.acquireUninterruptibly();
		this.x = x;
		xyLock.release();
	}

	public void setYPosition(float y) {
		xyLock.acquireUninterruptibly();
		this.y = y;
		xyLock.release();
	}

	public void setXYPosition(float x, float y) {
		xyLock.acquireUninterruptibly();
		this.x = x;
		this.y = y;
		xyLock.release();
	}

	public void setImagePath(String path) {
		setImageLock.acquireUninterruptibly();
		imageChanged = true;
		imagePath = path;
		setImageLock.release();
	}
}
