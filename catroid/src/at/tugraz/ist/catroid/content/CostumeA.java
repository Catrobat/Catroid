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

import at.tugraz.ist.catroid.common.TextureContainer;
import at.tugraz.ist.catroid.utils.Utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.actors.Image;

/**
 * @author Johannes Iber
 * 
 */
public class CostumeA extends Image {
	private Semaphore xyLock = new Semaphore(1);
	private Semaphore imageLock = new Semaphore(1);
	private Semaphore scaleLock = new Semaphore(1);
	private boolean imageChanged = false;
	private boolean scaleChanged = false;
	private String imagePath;
	private String currentImagePath = "";
	private Sprite sprite;
	public float alphaValue;
	private float size;

	public CostumeA(Sprite sprite) {
		super(Utils.getUniqueName());
		this.sprite = sprite;
		this.x = 0;
		this.y = 0;
		this.alphaValue = 1f;
		this.size = 1f;
		this.width = 0f;
		this.height = 0f;
		this.touchable = true;
	}

	@Override
	protected boolean touchDown(float x, float y, int pointer) {
		xyLock.acquireUninterruptibly();
		if (x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height) {
			sprite.startTapScripts();
		}
		xyLock.release();
		return true;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		checkImageChanged();
		checkScaleChanged();
		if (this.region != null) {
			super.draw(batch, this.alphaValue);
		}
	}

	private void checkImageChanged() {
		imageLock.acquireUninterruptibly();
		if (imageChanged) {
			if (imagePath.equals("")) {
				xyLock.acquireUninterruptibly();
				this.x += this.width / 2;
				this.y += this.height / 2;
				xyLock.release();
				TextureContainer.getInstance().getTextureRegion(currentImagePath, imagePath);
				this.width = 0f;
				this.height = 0f;
				imageChanged = false;
				imageLock.release();
				return;
			}
			xyLock.acquireUninterruptibly();
			this.x += this.width / 2;
			this.y += this.height / 2;
			xyLock.release();
			this.region = TextureContainer.getInstance().getTextureRegion(currentImagePath, imagePath);
			currentImagePath = imagePath;
			this.width = this.region.getTexture().getWidth();
			this.height = this.region.getTexture().getHeight();
			xyLock.acquireUninterruptibly();
			this.x -= this.width / 2;
			this.y -= this.height / 2;
			xyLock.release();

			imageChanged = false;
			scaleChanged = true;
		}
		imageLock.release();
	}

	private void checkScaleChanged() {
		scaleLock.acquireUninterruptibly();
		if (scaleChanged) {
			xyLock.acquireUninterruptibly();
			this.x += this.width / 2;
			this.y += this.height / 2;
			xyLock.release();
			this.width = this.region.getTexture().getWidth() * size;
			this.height = this.region.getTexture().getHeight() * size;
			xyLock.acquireUninterruptibly();
			this.x -= this.width / 2;
			this.y -= this.height / 2;
			xyLock.release();
			scaleChanged = false;
		}
		scaleLock.release();
	}

	public void setXPosition(float x) {
		xyLock.acquireUninterruptibly();
		if (this.region != null && this.region.getTexture() != null) {
			this.x = x - this.width / 2;
		} else {
			this.x = x;
		}
		xyLock.release();
	}

	public void setYPosition(float y) {
		xyLock.acquireUninterruptibly();
		if (this.region != null && this.region.getTexture() != null) {
			this.y = y - this.height / 2;
		} else {
			this.y = y;
		}
		xyLock.release();
	}

	public void setXYPosition(float x, float y) {
		xyLock.acquireUninterruptibly();
		if (this.region != null && this.region.getTexture() != null) {
			this.x = x - this.width / 2;
			this.y = y - this.height / 2;
		} else {
			this.x = x;
			this.y = y;
		}
		xyLock.release();
	}

	public float getXPosition() {
		float xPos = this.x;
		if (this.region != null && this.region.getTexture() != null) {
			xPos += this.width / 2;
		}
		return xPos;
	}

	public float getYPosition() {
		float yPos = this.y;
		if (this.region != null && this.region.getTexture() != null) {
			yPos += this.height / 2;
		}
		return yPos;
	}

	public void setImagePath(String path) {
		if (path == null) {
			path = "";
		}
		imageLock.acquireUninterruptibly();
		imagePath = path;
		imageChanged = true;
		imageLock.release();
	}

	public void setSize(float size) {
		scaleLock.acquireUninterruptibly();
		this.size = size;
		scaleChanged = true;
		scaleLock.release();
	}

}
