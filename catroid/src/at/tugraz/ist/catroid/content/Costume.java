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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actors.Image;

/**
 * @author Johannes Iber
 * 
 */
public class Costume extends Image {
	private Semaphore xyLock = new Semaphore(1);
	private Semaphore imageLock = new Semaphore(1);
	private Semaphore scaleLock = new Semaphore(1);
	private Semaphore alphaValueLock = new Semaphore(1);
	private Semaphore brightnessLock = new Semaphore(1);
	private boolean imageChanged = false;
	private String imagePath;
	private String currentImagePath = "";
	private Pixmap currentAlphaPixmap = null;
	private Sprite sprite;
	private float alphaValue;
	private float brightnessValue;
	public boolean show;
	public int zPosition = 0;

	public Costume(Sprite sprite) {
		super(Utils.getUniqueName());
		this.sprite = sprite;
		this.x = 0f;
		this.y = 0f;
		this.originX = 0f;
		this.originY = 0f;
		this.alphaValue = 1f;
		this.brightnessValue = 1f;
		this.scaleX = 1f;
		this.scaleY = 1f;
		this.rotation = 0f;
		this.width = 0f;
		this.height = 0f;
		this.touchable = true;
		this.show = true;
	}

	@Override
	protected boolean touchDown(float x, float y, int pointer) {
		if (sprite.isPaused) {
			return true;
		}
		if (!show) {
			return false;
		}
		xyLock.acquireUninterruptibly();
		if (x >= 0 && x <= this.width && y >= 0 && y <= this.height) {
			if (currentAlphaPixmap != null && ((currentAlphaPixmap.getPixel((int) x, (int) y) & 0x000000FF) > 10)) {
				sprite.startTapScripts();
				xyLock.release();
				return true;
			}
		}
		xyLock.release();
		return false;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		checkImageChanged();
		if (this.show && this.region != null) {
			super.draw(batch, this.alphaValue);
		}
	}

	private void checkImageChanged() {
		imageLock.acquireUninterruptibly();
		if (imageChanged) {
			if (this.region != null && this.region.getTexture() != null) {
				this.region.getTexture().dispose();
			}
			if (currentAlphaPixmap != null) {
				currentAlphaPixmap.dispose();
			}
			currentImagePath = imagePath;
			currentAlphaPixmap = null;
			if (currentImagePath.equals("")) {
				xyLock.acquireUninterruptibly();
				this.x += this.width / 2;
				this.y += this.height / 2;
				this.width = 0f;
				this.height = 0f;
				xyLock.release();
				this.region = new TextureRegion();
				imageChanged = false;
				imageLock.release();
				return;
			}
			xyLock.acquireUninterruptibly();
			this.x += this.width / 2;
			this.y += this.height / 2;
			this.width = 0f;
			this.height = 0f;
			xyLock.release();

			Pixmap pixmap = new Pixmap(Gdx.files.absolute(currentImagePath));
			currentAlphaPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.Alpha);
			currentAlphaPixmap.drawPixmap(pixmap, 0, 0, 0, 0, pixmap.getWidth(), pixmap.getHeight());

			brightnessLock.acquireUninterruptibly();
			if (brightnessValue != 1f) {
				pixmap = this.adjustBrightness(pixmap);
			}
			brightnessLock.release();

			Texture texture = new Texture(pixmap);
			pixmap.dispose();

			this.region = new TextureRegion(texture);
			xyLock.acquireUninterruptibly();
			this.width = this.region.getTexture().getWidth();
			this.height = this.region.getTexture().getHeight();
			this.x -= this.width / 2;
			this.y -= this.height / 2;
			xyLock.release();
			this.originX = this.width / 2f;
			this.originY = this.height / 2f;

			imageChanged = false;
		}
		imageLock.release();
	}

	private Pixmap adjustBrightness(Pixmap currentPixmap) {
		Pixmap newPixmap = new Pixmap(currentPixmap.getWidth(), currentPixmap.getHeight(), currentPixmap.getFormat());
		for (int y = 0; y < currentPixmap.getHeight(); y++) {
			for (int x = 0; x < currentPixmap.getWidth(); x++) {
				int pixel = currentPixmap.getPixel(x, y);
				int rr = (int) (((pixel >> 24) & 0xff) * brightnessValue);
				int gg = (int) (((pixel >> 16) & 0xff) * brightnessValue);
				int bb = (int) (((pixel >> 8) & 0xff) * brightnessValue);
				int aa = pixel & 0xff;

				if (rr > 255) {
					rr = 255;
				} else if (rr < 0) {
					rr = 0;
				}
				if (gg > 255) {
					gg = 255;
				} else if (rr < 0) {
					gg = 0;
				}
				if (bb > 255) {
					bb = 255;
				} else if (rr < 0) {
					bb = 0;
				}

				newPixmap.setColor(rr / 255f, gg / 255f, bb / 255f, aa / 255f);
				newPixmap.drawPixel(x, y);
			}
		}
		currentPixmap.dispose();
		return newPixmap;
	}

	public void disposeTextures() {
		if (this.region != null && this.region.getTexture() != null) {
			this.region.getTexture().dispose();
		}
		if (currentAlphaPixmap != null) {
			currentAlphaPixmap.dispose();
		}
	}

	public void resume() {
		imageLock.acquireUninterruptibly();
		this.imageChanged = true;
		imageLock.release();
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
		xyLock.acquireUninterruptibly();
		float xPos = this.x;
		if (this.region != null && this.region.getTexture() != null) {
			xPos += this.width / 2;
		}
		xyLock.release();
		return xPos;
	}

	public float getYPosition() {
		xyLock.acquireUninterruptibly();
		float yPos = this.y;
		if (this.region != null && this.region.getTexture() != null) {
			yPos += this.height / 2;
		}
		xyLock.release();
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
		this.scaleX = size;
		this.scaleY = size;
		scaleLock.release();
	}

	public void setAlphaValue(float alphaValue) {
		alphaValueLock.acquireUninterruptibly();
		this.alphaValue = alphaValue;
		alphaValueLock.release();
	}

	public void changeAlphaValueBy(float value) {
		alphaValueLock.acquireUninterruptibly();
		float newAlphaValue = this.alphaValue + value;
		if (newAlphaValue < 0f) {
			this.alphaValue = 0f;
		} else if (newAlphaValue > 1f) {
			this.alphaValue = 1f;
		} else {
			this.alphaValue = newAlphaValue;
		}
		alphaValueLock.release();
	}

	public float getAlphaValue() {
		alphaValueLock.acquireUninterruptibly();
		float alphaValue = this.alphaValue;
		alphaValueLock.release();
		return alphaValue;
	}

	public void setBrightness(float percent) {
		brightnessLock.acquireUninterruptibly();
		brightnessValue = percent;
		brightnessLock.release();
		imageLock.acquireUninterruptibly();
		imageChanged = true;
		imageLock.release();
	}

	public void changeBrightnessBy(float percent) {
		brightnessLock.acquireUninterruptibly();
		brightnessValue += percent;
		brightnessLock.release();
		imageLock.acquireUninterruptibly();
		imageChanged = true;
		imageLock.release();
	}
}
