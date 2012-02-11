/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content;

import java.util.concurrent.Semaphore;

import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.utils.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actors.Image;

public class Costume extends Image {
	protected Semaphore xYWidthHeightLock = new Semaphore(1);
	protected Semaphore imageLock = new Semaphore(1);
	protected Semaphore scaleLock = new Semaphore(1);
	protected Semaphore alphaValueLock = new Semaphore(1);
	protected Semaphore brightnessLock = new Semaphore(1);
	protected Semaphore disposeTexturesLock = new Semaphore(1);
	protected boolean imageChanged = false;
	protected CostumeData costumeData;
	protected Pixmap currentAlphaPixmap;
	protected Sprite sprite;
	protected float alphaValue;
	protected float brightnessValue;
	protected boolean internalPath;
	public boolean show;
	public int zPosition;

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
		this.currentAlphaPixmap = null;
		this.zPosition = 0;
		this.internalPath = false;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer) {
		if (sprite.isPaused) {
			return true;
		}
		if (!show) {
			return false;
		}
		xYWidthHeightLock.acquireUninterruptibly();
		float width = this.width;
		float height = this.height;
		xYWidthHeightLock.release();

		if (x >= 0 && x <= width && y >= 0 && y <= height) {
			if (currentAlphaPixmap != null && ((currentAlphaPixmap.getPixel((int) x, (int) y) & 0x000000FF) > 10)) {
				sprite.startWhenScripts("Tapped");
				return true;
			}
		}
		return false;
	}

	@Override
	public void touchUp(float x, float y, int pointer) {

	}

	@Override
	public void touchDragged(float x, float y, int pointer) {

	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		checkImageChanged();
		if (this.show && this.region != null) {
			super.draw(batch, this.alphaValue);
		}
	}

	protected void checkImageChanged() {
		imageLock.acquireUninterruptibly();
		if (imageChanged) {
			this.disposeTextures();
			currentAlphaPixmap = null;
			if (costumeData == null) {
				xYWidthHeightLock.acquireUninterruptibly();
				this.x += this.width / 2f;
				this.y += this.height / 2f;
				this.width = 0f;
				this.height = 0f;
				xYWidthHeightLock.release();
				this.region = new TextureRegion();
				imageChanged = false;
				imageLock.release();
				return;
			}

			Pixmap pixmap;
			if (internalPath) {
				pixmap = new Pixmap(Gdx.files.internal(costumeData.getAbsolutePath()));
			} else {
				pixmap = new Pixmap(Gdx.files.absolute(costumeData.getAbsolutePath()));
			}

			xYWidthHeightLock.acquireUninterruptibly();
			this.x += this.width / 2f;
			this.y += this.height / 2f;
			this.width = pixmap.getWidth();
			this.height = pixmap.getHeight();
			this.x -= this.width / 2f;
			this.y -= this.height / 2f;
			this.originX = this.width / 2f;
			this.originY = this.height / 2f;
			xYWidthHeightLock.release();

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

			imageChanged = false;
		}
		imageLock.release();
	}

	protected Pixmap adjustBrightness(Pixmap currentPixmap) {
		Pixmap newPixmap = new Pixmap(currentPixmap.getWidth(), currentPixmap.getHeight(), currentPixmap.getFormat());
		for (int y = 0; y < currentPixmap.getHeight(); y++) {
			for (int x = 0; x < currentPixmap.getWidth(); x++) {
				int pixel = currentPixmap.getPixel(x, y);
				int r = (int) (((pixel >> 24) & 0xff) + (255 * (brightnessValue - 1)));
				int g = (int) (((pixel >> 16) & 0xff) + (255 * (brightnessValue - 1)));
				int b = (int) (((pixel >> 8) & 0xff) + (255 * (brightnessValue - 1)));
				int a = pixel & 0xff;

				if (r > 255) {
					r = 255;
				} else if (r < 0) {
					r = 0;
				}
				if (g > 255) {
					g = 255;
				} else if (g < 0) {
					g = 0;
				}
				if (b > 255) {
					b = 255;
				} else if (b < 0) {
					b = 0;
				}

				newPixmap.setColor(r / 255f, g / 255f, b / 255f, a / 255f);
				newPixmap.drawPixel(x, y);
			}
		}
		currentPixmap.dispose();
		return newPixmap;
	}

	public void disposeTextures() {
		disposeTexturesLock.acquireUninterruptibly();
		if (this.region != null && this.region.getTexture() != null) {
			this.region.getTexture().dispose();
		}
		if (currentAlphaPixmap != null) {
			currentAlphaPixmap.dispose();
		}
		disposeTexturesLock.release();
	}

	public void refreshTextures() {
		imageLock.acquireUninterruptibly();
		this.imageChanged = true;
		imageLock.release();
	}

	// Always use this method for the following methods
	public void aquireXYWidthHeightLock() {
		xYWidthHeightLock.acquireUninterruptibly();
	}

	public void setXPosition(float x) {
		this.x = x - (this.width / 2f);
	}

	public void setYPosition(float y) {
		this.y = y - (this.height / 2f);
	}

	public void setXYPosition(float x, float y) {
		this.x = x - (this.width / 2f);
		this.y = y - (this.height / 2f);
	}

	public float getXPosition() {
		float xPos = this.x;
		xPos += this.width / 2f;
		return xPos;
	}

	public float getYPosition() {
		float yPos = this.y;
		yPos += this.height / 2f;
		return yPos;
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}

	public void releaseXYWidthHeightLock() {
		xYWidthHeightLock.release();
	}

	public void setCostumeData(CostumeData costumeData) {
		imageLock.acquireUninterruptibly();
		this.costumeData = costumeData;
		imageChanged = true;
		imageLock.release();
	}

	public void setCostumeDataInternal(CostumeData costumeData) {
		imageLock.acquireUninterruptibly();
		internalPath = true;
		this.costumeData = costumeData;
		imageChanged = true;
		imageLock.release();
	}

	public String getImagePath() {
		imageLock.acquireUninterruptibly();
		String path;
		if (this.costumeData == null) {
			path = "";
		} else if (internalPath) {
			path = this.costumeData.getInternalPath();
		} else {
			path = this.costumeData.getAbsolutePath();
		}
		imageLock.release();
		return path;
	}

	public void setSize(float size) {
		scaleLock.acquireUninterruptibly();
		this.scaleX = size;
		this.scaleY = size;
		scaleLock.release();
	}

	public float getSize() {
		scaleLock.acquireUninterruptibly();
		float size = (this.scaleX + this.scaleY) / 2f;
		scaleLock.release();
		return size;
	}

	public void setAlphaValue(float alphaValue) {
		if (alphaValue < 0f) {
			alphaValue = 0f;
		} else if (alphaValue > 1f) {
			alphaValue = 1f;
		}
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

	public void setBrightnessValue(float percent) {
		if (percent < 0f) {
			percent = 0f;
		}
		brightnessLock.acquireUninterruptibly();
		brightnessValue = percent;
		brightnessLock.release();
		imageLock.acquireUninterruptibly();
		imageChanged = true;
		imageLock.release();
	}

	public void changeBrightnessValueBy(float percent) {
		brightnessLock.acquireUninterruptibly();
		brightnessValue += percent;
		if (brightnessValue < 0f) {
			brightnessValue = 0f;
		}
		brightnessLock.release();
		imageLock.acquireUninterruptibly();
		imageChanged = true;
		imageLock.release();
	}

	public float getBrightnessValue() {
		brightnessLock.acquireUninterruptibly();
		float brightness = brightnessValue;
		brightnessLock.release();
		return brightness;
	}

	public CostumeData getCostumeData() {
		return costumeData;
	}

}
