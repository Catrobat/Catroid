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
import java.util.Iterator;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.actions.BroadcastNotifyAction;
import org.catrobat.catroid.content.actions.ExtendedActions;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class Look extends Image {
	private static final float DEGREE_UI_OFFSET = 90.0f;
	protected boolean imageChanged = false;
	protected boolean brightnessChanged = false;
	protected LookData lookData;
	protected Sprite sprite;
	protected float alpha = 1f;
	protected float brightness = 1f;
	public boolean visible = true;
	protected Pixmap pixmap;
	private HashMap<String, ArrayList<SequenceAction>> broadcastSequenceMap = new HashMap<String, ArrayList<SequenceAction>>();
	private HashMap<String, ArrayList<SequenceAction>> broadcastWaitSequenceMap = new HashMap<String, ArrayList<SequenceAction>>();
	private ParallelAction whenParallelAction;
	private ArrayList<Action> actionsToRestart = new ArrayList<Action>();
	private boolean allActionAreFinished = false;

	public Look(Sprite sprite) {
		this.sprite = sprite;
		setBounds(0f, 0f, 0f, 0f);
		setOrigin(0f, 0f);
		setScale(1f, 1f);
		setRotation(0f);
		setTouchable(Touchable.enabled);
		this.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (doTouchDown(x, y, pointer)) {
					return true;
				}
				setTouchable(Touchable.disabled);
				Actor target = getParent().hit(event.getStageX(), event.getStageY(), true);
				if (target != null) {
					target.fire(event);
				}
				setTouchable(Touchable.enabled);
				return false;
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

	public Look copyLookForSprite(final Sprite cloneSprite) {
		Look cloneLook = cloneSprite.look;

		cloneLook.alpha = this.alpha;
		cloneLook.brightness = this.brightness;
		cloneLook.visible = this.visible;
		cloneLook.broadcastSequenceMap = new HashMap<String, ArrayList<SequenceAction>>(this.broadcastSequenceMap);
		cloneLook.broadcastWaitSequenceMap = new HashMap<String, ArrayList<SequenceAction>>(
				this.broadcastWaitSequenceMap);
		cloneLook.whenParallelAction = null;
		cloneLook.allActionAreFinished = this.allActionAreFinished;
		cloneLook.actionsToRestart = new ArrayList<Action>();

		return cloneLook;
	}

	public boolean doTouchDown(float x, float y, int pointer) {
		if (sprite.isPaused) {
			return true;
		}
		if (!visible) {
			return false;
		}

		// We use Y-down, libgdx Y-up. This is the fix for accurate y-axis detection
		y = (getHeight() - 1) - y;

		if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
			if (pixmap != null && ((pixmap.getPixel((int) x, (int) y) & 0x000000FF) > 10)) {
				if (whenParallelAction == null) {
					sprite.createWhenScriptActionSequence("Tapped");
				} else {
					whenParallelAction.restart();
				}
				return true;
			}
		}
		return false;
	}

	public void putBroadcastSequenceAction(String broadcastMessage, SequenceAction action) {
		if (broadcastSequenceMap.containsKey(broadcastMessage)) {
			broadcastSequenceMap.get(broadcastMessage).add(action);
		} else {
			ArrayList<SequenceAction> actionList = new ArrayList<SequenceAction>();
			actionList.add(action);
			broadcastSequenceMap.put(broadcastMessage, actionList);
		}
	}

	public void doHandleBroadcastEvent(String broadcastMessage) {
		if (broadcastSequenceMap.containsKey(broadcastMessage)) {
			for (SequenceAction action : broadcastSequenceMap.get(broadcastMessage)) {
				if (action.getActor() == null) {
					addAction(action);
				} else {
					actionsToRestart.add(action);
				}
			}
		}
	}

	public void doHandleBroadcastFromWaiterEvent(BroadcastEvent event, String broadcastMessage) {
		if (broadcastSequenceMap.containsKey(broadcastMessage)) {
			if (!broadcastWaitSequenceMap.containsKey(broadcastMessage)) {
				ArrayList<SequenceAction> actionList = new ArrayList<SequenceAction>();
				for (SequenceAction broadcastAction : broadcastSequenceMap.get(broadcastMessage)) {
					event.raiseNumberOfReceivers();
					SequenceAction broadcastWaitAction = ExtendedActions.sequence(broadcastAction,
							ExtendedActions.broadcastNotify(event));
					actionList.add(broadcastWaitAction);
					addAction(broadcastWaitAction);
				}
				broadcastWaitSequenceMap.put(broadcastMessage, actionList);
			} else {
				ArrayList<SequenceAction> actionList = broadcastWaitSequenceMap.get(broadcastMessage);
				for (SequenceAction action : actionList) {
					event.raiseNumberOfReceivers();
					Array<Action> actions = action.getActions();
					BroadcastNotifyAction notifyAction = (BroadcastNotifyAction) actions.get(actions.size - 1);
					notifyAction.setEvent(event);
					actionsToRestart.add(action);
				}
			}
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		checkImageChanged();
		if (alpha == 0.0f) {
			setVisible(false);
		} else {
			setVisible(true);
		}
		if (this.visible && this.getDrawable() != null) {
			super.draw(batch, this.alpha);
		}
	}

	@Override
	public void act(float delta) {
		Array<Action> actions = getActions();
		allActionAreFinished = false;
		int finishedCount = 0;
		for (int i = 0, n = actions.size; i < n; i++) {
			for (Iterator<Action> iterator = actionsToRestart.iterator(); iterator.hasNext();) {
				Action actionToRestart = iterator.next();
				actionToRestart.restart();
				iterator.remove();
			}
			Action action = actions.get(i);
			if (action.act(delta)) {
				finishedCount++;
			}
		}
		if (finishedCount == actions.size) {
			allActionAreFinished = true;
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
			float newX = getX() - (pixmap.getWidth() - getWidth()) / 2f;
			float newY = getY() - (pixmap.getHeight() - getHeight()) / 2f;

			setPosition(newX, newY);
			setSize(pixmap.getWidth(), pixmap.getHeight());
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
				int r = (int) (((pixel >> 24) & 0xff) + (255 * (brightness - 1)));
				int g = (int) (((pixel >> 16) & 0xff) + (255 * (brightness - 1)));
				int b = (int) (((pixel >> 8) & 0xff) + (255 * (brightness - 1)));
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

	public void setLookData(LookData lookData) {
		this.lookData = lookData;
		imageChanged = true;
	}

	public LookData getLookData() {
		return lookData;
	}

	public boolean getAllActionsAreFinished() {
		return allActionAreFinished;
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

	public void setWhenParallelAction(ParallelAction action) {
		whenParallelAction = action;
	}

	public float getXInUserInterfaceDimensionUnit() {
		return getX() + getWidth() / 2f;
	}

	public float getYInUserInterfaceDimensionUnit() {
		return getY() + getHeight() / 2f;
	}

	public void setXInUserInterfaceDimensionUnit(float x) {
		setX(x - getWidth() / 2f);
	}

	public void setYInUserInterfaceDimensionUnit(float y) {
		setY(y - getHeight() / 2f);
	}

	public void setPositionInUserInterfaceDimensionUnit(float x, float y) {
		setXInUserInterfaceDimensionUnit(x);
		setYInUserInterfaceDimensionUnit(y);
	}

	public void changeXInUserInterfaceDimensionUnit(float changeX) {
		setX(getX() + changeX);
	}

	public void changeYInUserInterfaceDimensionUnit(float changeY) {
		setY(getY() + changeY);
	}

	public float getWidthInUserInterfaceDimensionUnit() {
		return getWidth() * getSizeInUserInterfaceDimensionUnit() / 100f;
	}

	public float getHeightInUserInterfaceDimensionUnit() {
		return getHeight() * getSizeInUserInterfaceDimensionUnit() / 100f;
	}

	public float getDirectionInUserInterfaceDimensionUnit() {
		float direction = (getRotation() + DEGREE_UI_OFFSET) % 360;
		if (direction < 0) {
			direction += 360f;
		}
		direction = 180f - direction;

		return direction;
	}

	public void setDirectionInUserInterfaceDimensionUnit(float degrees) {
		setRotation((-degrees + DEGREE_UI_OFFSET) % 360);
	}

	public void changeDirectionInUserInterfaceDimensionUnit(float changeDegrees) {
		setRotation((getRotation() - changeDegrees) % 360);
	}

	public float getSizeInUserInterfaceDimensionUnit() {
		return getScaleX() * 100f;
	}

	public void setSizeInUserInterfaceDimensionUnit(float percent) {
		if (percent < 0) {
			percent = 0;
		}

		setScale(percent / 100f, percent / 100f);
	}

	public void changeSizeInUserInterfaceDimensionUnit(float changePercent) {
		setSizeInUserInterfaceDimensionUnit(getSizeInUserInterfaceDimensionUnit() + changePercent);
	}

	public float getTransparencyInUserInterfaceDimensionUnit() {
		return (1f - alpha) * 100f;
	}

	public void setTransparencyInUserInterfaceDimensionUnit(float percent) {
		if (percent < 0f) {
			percent = 0f;
		} else if (percent >= 100f) {
			percent = 100f;
			setVisible(false);
		}

		if (percent < 100.0f) {
			setVisible(true);
		}

		alpha = (100f - percent) / 100f;
	}

	public void changeTransparencyInUserInterfaceDimensionUnit(float changePercent) {
		setTransparencyInUserInterfaceDimensionUnit(getTransparencyInUserInterfaceDimensionUnit() + changePercent);
	}

	public float getBrightnessInUserInterfaceDimensionUnit() {
		return brightness * 100f;
	}

	public void setBrightnessInUserInterfaceDimensionUnit(float percent) {
		if (percent < 0f) {
			percent = 0f;
		} else if (percent > 200f) {
			percent = 200f;
		}

		brightness = percent / 100f;
		brightnessChanged = true;
		imageChanged = true;
	}

	public void changeBrightnessInUserInterfaceDimensionUnit(float changePercent) {
		setBrightnessInUserInterfaceDimensionUnit(getBrightnessInUserInterfaceDimensionUnit() + changePercent);
	}
}
