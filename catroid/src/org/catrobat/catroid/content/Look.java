/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.actions.ExtendedActions;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Look extends Image {
	protected Semaphore xYWidthHeightLock = new Semaphore(1);
	protected Semaphore imageLock = new Semaphore(1);
	protected Semaphore scaleLock = new Semaphore(1);
	protected Semaphore alphaValueLock = new Semaphore(1);
	protected Semaphore brightnessLock = new Semaphore(1);
	protected boolean imageChanged = false;
	protected boolean brightnessChanged = false;
	protected LookData lookData;
	protected Sprite sprite;
	protected float alphaValue;
	protected float brightnessValue;
	public boolean show;
	public int zPosition;
	protected Pixmap pixmap;
	private ArrayList<SequenceAction> touchDownSequenceList;
	private HashMap<String, BroadcastScript> broadcastMap;
	private HashMap<String, BroadcastScript> broadcastWaitMap;

	public Look(Sprite sprite) {
		this.sprite = sprite;
		setBounds(0f, 0f, 0f, 0f);
		setOrigin(0f, 0f);
		setScale(1f, 1f);
		setRotation(0f);
		setTouchable(Touchable.enabled);
		this.alphaValue = 1f;
		this.brightnessValue = 1f;
		this.show = true;
		this.zPosition = 0;
		this.touchDownSequenceList = new ArrayList<SequenceAction>();
		this.broadcastMap = new HashMap<String, BroadcastScript>();
		this.broadcastWaitMap = new HashMap<String, BroadcastScript>();
		this.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return doTouchDown(x, y, pointer);
			}
		});
		this.addListener(new BroadcastListener() {
			@Override
			public void handleBroadcastEvent(BroadcastEvent event, String broadcastMessage) {
				doHandleBroadcastEvent(broadcastMessage);
			}

			@Override
			public void handleBroadcastFromWaiterEvent(BroadcastEvent event, String broadcastMessage) {
				doHandleBroadcastFromWaiterEvent(event, broadcastMessage);
			}

			@Override
			public void handleBroadcastToWaiterEvent(BroadcastEvent event, String broadcastMessage) {
				doHandleBroadcastToWaiterEvent(broadcastMessage);
			}

		});
	}

	public boolean doTouchDown(float x, float y, int pointer) {
		if (sprite.isPaused) {
			return true;
		}
		if (!show) {
			return false;
		}
		xYWidthHeightLock.acquireUninterruptibly();
		float width = getWidth();
		float height = getHeight();
		xYWidthHeightLock.release();

		// We use Y-down, libgdx Y-up. This is the fix for accurate y-axis detection
		y = height - y;

		if (x >= 0 && x <= width && y >= 0 && y <= height) {
			if (pixmap != null && ((pixmap.getPixel((int) x, (int) y) & 0x000000FF) > 10)) {
				sprite.createWhenScriptActionSequence("Tapped");
				return true;
			}
		}
		return false;
	}

	public void doHandleBroadcastEvent(String broadcastMessage) {
		if (broadcastMap.containsKey(broadcastMessage)) {
			sprite.createBroadcastScriptActionSequence(broadcastMap.get(broadcastMessage));
		}
	}

	public void doHandleBroadcastFromWaiterEvent(BroadcastEvent event, String broadcastMessage) {
		if (broadcastMap.containsKey(broadcastMessage)) {
			Action action = ExtendedActions.broadcastToWaiter(event.getSenderSprite(), broadcastMessage);
			sprite.createBroadcastScriptActionSequence(broadcastMap.get(broadcastMessage), action);
		}
	}

	public void doHandleBroadcastToWaiterEvent(String broadcastMessage) {
		if (broadcastWaitMap.containsKey(broadcastMessage)) {
			sprite.createBroadcastScriptActionSequence(broadcastWaitMap.get(broadcastMessage));
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		checkImageChanged();
		if (this.show && this.getDrawable() != null) {
			super.draw(batch, this.alphaValue);
		}
	}

	protected void checkImageChanged() {
		imageLock.acquireUninterruptibly();
		if (imageChanged) {
			if (lookData == null) {
				xYWidthHeightLock.acquireUninterruptibly();
				setX(getX() + getWidth() / 2f);
				setY(getY() + getHeight() / 2f);
				setWidth(0f);
				setHeight(0f);
				xYWidthHeightLock.release();
				setDrawable(null);
				imageChanged = false;
				imageLock.release();
				return;
			}

			pixmap = lookData.getPixmap();

			xYWidthHeightLock.acquireUninterruptibly();
			setX(getX() + getWidth() / 2f);
			setY(getY() + getHeight() / 2f);
			setWidth(pixmap.getWidth());
			setHeight(pixmap.getHeight());
			setX(getX() - getWidth() / 2f);
			setY(getY() - getHeight() / 2f);
			setOrigin(getWidth() / 2f, getHeight() / 2f);
			xYWidthHeightLock.release();

			brightnessLock.acquireUninterruptibly();
			if (brightnessChanged) {
				lookData.setPixmap(adjustBrightness(lookData.getOriginalPixmap()));
				lookData.setTextureRegion();
				brightnessChanged = false;
			}
			brightnessLock.release();

			TextureRegion region = lookData.getTextureRegion();
			TextureRegionDrawable drawable = new TextureRegionDrawable(region);
			setDrawable(drawable);

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
		return newPixmap;
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
		setX(x - getWidth() / 2f);
	}

	public void setYPosition(float y) {
		setY(y - getHeight() / 2f);
	}

	public void setXYPosition(float x, float y) {
		setX(x - getWidth() / 2f);
		setY(y - getHeight() / 2f);
	}

	public float getXPosition() {
		float xPosition = getX();
		xPosition += getWidth() / 2f;
		return xPosition;
	}

	public float getYPosition() {
		float yPosition = getY();
		yPosition += getHeight() / 2f;
		return yPosition;
	}

	public void releaseXYWidthHeightLock() {
		xYWidthHeightLock.release();
	}

	public void setLookData(LookData lookData) {
		imageLock.acquireUninterruptibly();
		this.lookData = lookData;
		imageChanged = true;
		imageLock.release();
	}

	public String getImagePath() {
		imageLock.acquireUninterruptibly();
		String path;
		if (this.lookData == null) {
			path = "";
		} else {
			path = this.lookData.getAbsolutePath();
		}
		imageLock.release();
		return path;
	}

	public void setSize(float size) {
		scaleLock.acquireUninterruptibly();
		setScale(size, size);
		scaleLock.release();
	}

	public float getSize() {
		scaleLock.acquireUninterruptibly();
		float size = (getScaleX() + getScaleY()) / 2f;
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
		brightnessChanged = true;
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
		brightnessChanged = true;
		imageChanged = true;
		imageLock.release();
	}

	public float getBrightnessValue() {
		brightnessLock.acquireUninterruptibly();
		float brightness = brightnessValue;
		brightnessLock.release();
		return brightness;
	}

	public LookData getLookData() {
		return lookData;
	}

	public void putBroadcast(String broadcastMessage, BroadcastScript script) {
		broadcastMap.put(broadcastMessage, script);
	}

	public void removeBroadcast(String broadcastMessage) {
		broadcastMap.remove(broadcastMessage);
	}

	public void addTouchDownSequenceAction(SequenceAction action) {
		touchDownSequenceList.add(action);
	}

	public void removeTouchDownSequenceAction(SequenceAction action) {
		touchDownSequenceList.remove(action);
	}

	public void putBroadcastWait(String broadcastMessage, BroadcastScript script) {
		broadcastWaitMap.put(broadcastMessage, script);
	}

	public void removeBroadcastWait(String broadcastMessage) {
		broadcastWaitMap.remove(broadcastMessage);
	}

}
