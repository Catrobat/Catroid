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
import java.util.Set;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.actions.BroadcastNotifyAction;
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
import com.badlogic.gdx.utils.Array;

public class Look extends Image {
	protected boolean imageChanged = false;
	protected boolean brightnessChanged = false;
	protected LookData lookData;
	protected Sprite sprite;
	protected float alphaValue;
	protected float brightnessValue;
	public boolean show;
	public int zPosition;
	protected Pixmap pixmap;
	private HashMap<String, ArrayList<BroadcastScript>> broadcastMap;
	protected HashMap<String, SequenceAction> broadcastSequenceList;
	protected HashMap<String, SequenceAction> broadcastWaitSequenceList;
	protected ArrayList<SequenceAction> whenSequenceList;
	private boolean broadcastFirst = true;

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
		this.broadcastMap = new HashMap<String, ArrayList<BroadcastScript>>();
		this.whenSequenceList = new ArrayList<SequenceAction>();
		this.broadcastSequenceList = new HashMap<String, SequenceAction>();
		this.broadcastWaitSequenceList = new HashMap<String, SequenceAction>();
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
		});
	}

	public boolean doTouchDown(float x, float y, int pointer) {
		if (sprite.isPaused) {
			return true;
		}
		if (!show) {
			return false;
		}

		// We use Y-down, libgdx Y-up. This is the fix for accurate y-axis detection
		y = getHeight() - y;

		if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight()) {
			if (pixmap != null && ((pixmap.getPixel((int) x, (int) y) & 0x000000FF) > 10)) {
				if (whenSequenceList.isEmpty()) {
					sprite.createWhenScriptActionSequence("Tapped");
				}
				for (SequenceAction action : whenSequenceList) {
					action.restart();
				}
				return true;
			}
		}
		return false;
	}

	public void doHandleBroadcastEvent(String broadcastMessage) {
		//		if (broadcastMap.containsKey(broadcastMessage)) {
		//			ArrayList<BroadcastScript> broadcastList = broadcastMap.get(broadcastMessage);
		//			ParallelAction parallelAction = ExtendedActions.parallel();
		//			for (BroadcastScript script : broadcastList) {
		//				parallelAction.addAction(sprite.createBroadcastScriptActionSequence(script));
		//			}
		//			addAction(parallelAction);
		//		}
		if (broadcastSequenceList.containsKey(broadcastMessage)) {
			Set<String> keys = broadcastSequenceList.keySet();
			if (broadcastFirst) {
				for (String key : keys) {
					addAction(broadcastSequenceList.get(key));
				}
				broadcastFirst = false;
			}
			broadcastSequenceList.get(broadcastMessage).restart();
		}
	}

	public void doHandleBroadcastFromWaiterEvent(BroadcastEvent event, String broadcastMessage) {
		//		if (broadcastMap.containsKey(broadcastMessage)) {
		//			Action broadcastNotifyAction = ExtendedActions.broadcastNotify(event);
		//			ArrayList<BroadcastScript> broadcastList = broadcastMap.get(broadcastMessage);
		//			ParallelAction parallelAction = ExtendedActions.parallel();
		//			for (BroadcastScript script : broadcastList) {
		//				parallelAction.addAction(sprite.createBroadcastScriptActionSequence(script));
		//			}
		//			addAction(ExtendedActions.sequence(parallelAction, broadcastNotifyAction));
		//		}
		if (broadcastSequenceList.containsKey(broadcastMessage)) {
			if (broadcastWaitSequenceList.isEmpty()) {
				Set<String> keys = broadcastSequenceList.keySet();
				for (String key : keys) {
					SequenceAction action = ExtendedActions.sequence(broadcastSequenceList.get(key),
							ExtendedActions.broadcastNotify(event));
					broadcastWaitSequenceList.put(key, action);
					addAction(action);
				}
			}
			SequenceAction action = broadcastWaitSequenceList.get(broadcastMessage);
			Array<Action> actions = action.getActions();
			BroadcastNotifyAction notifyAction = (BroadcastNotifyAction) actions.get(actions.size - 1);
			notifyAction.setEvent(event);
			action.restart();
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		checkImageChanged();
		if (this.show && this.getDrawable() != null) {
			super.draw(batch, this.alphaValue);
		}
	}

	@Override
	public void act(float delta) {
		Array<Action> actions = getActions();
		for (int i = 0, n = actions.size; i < n; i++) {
			Action action = actions.get(i);
			action.act(delta);
		}
	}

	protected void checkImageChanged() {
		if (imageChanged) {
			if (lookData == null) {
				setBounds(getX() + getWidth() / 2f, getY() + getHeight() / 2f, 0f, 0f);
				setDrawable(null);
				imageChanged = false;
				return;
			}

			pixmap = lookData.getPixmap();
			setX(getX() + getWidth() / 2f);
			setY(getY() + getHeight() / 2f);
			setWidth(pixmap.getWidth());
			setHeight(pixmap.getHeight());
			setX(getX() - getWidth() / 2f);
			setY(getY() - getHeight() / 2f);
			setOrigin(getWidth() / 2f, getHeight() / 2f);

			if (brightnessChanged) {
				lookData.setPixmap(adjustBrightness(lookData.getOriginalPixmap()));
				lookData.setTextureRegion();
				brightnessChanged = false;
			}

			TextureRegion region = lookData.getTextureRegion();
			TextureRegionDrawable drawable = new TextureRegionDrawable(region);
			setDrawable(drawable);

			imageChanged = false;
		}
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
		this.imageChanged = true;
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

	public void setLookData(LookData lookData) {
		this.lookData = lookData;
		imageChanged = true;
	}

	public String getImagePath() {
		String path;
		if (this.lookData == null) {
			path = "";
		} else {
			path = this.lookData.getAbsolutePath();
		}
		return path;
	}

	public void setSize(float size) {
		setScale(size, size);
	}

	public float getSize() {
		float size = (getScaleX() + getScaleY()) / 2f;
		return size;
	}

	public void setAlphaValue(float alphaValue) {
		if (alphaValue < 0f) {
			alphaValue = 0f;
		} else if (alphaValue > 1f) {
			alphaValue = 1f;
		}
		this.alphaValue = alphaValue;
	}

	public void changeAlphaValueBy(float value) {
		float newAlphaValue = this.alphaValue + value;
		if (newAlphaValue < 0f) {
			this.alphaValue = 0f;
		} else if (newAlphaValue > 1f) {
			this.alphaValue = 1f;
		} else {
			this.alphaValue = newAlphaValue;
		}
	}

	public float getAlphaValue() {
		return alphaValue;
	}

	public void setBrightnessValue(float percent) {
		if (percent < 0f) {
			percent = 0f;
		}
		brightnessValue = percent;
		brightnessChanged = true;
		imageChanged = true;
	}

	public void changeBrightnessValueBy(float percent) {
		brightnessValue += percent;
		if (brightnessValue < 0f) {
			brightnessValue = 0f;
		}
		brightnessChanged = true;
		imageChanged = true;
	}

	public float getBrightnessValue() {
		return brightnessValue;
	}

	public LookData getLookData() {
		return lookData;
	}

	public void putBroadcast(String broadcastMessage, BroadcastScript script) {
		ArrayList<BroadcastScript> broadcastList;
		if (broadcastMap.containsKey(broadcastMessage)) {
			broadcastList = broadcastMap.get(broadcastMessage);
			broadcastList.add(script);
		} else {
			broadcastList = new ArrayList<BroadcastScript>();
			broadcastList.add(script);
			broadcastMap.put(broadcastMessage, broadcastList);
		}
	}

	public void removeBroadcast(String broadcastMessage) {
		broadcastMap.remove(broadcastMessage);
	}
}
