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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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
	private BrightnessContrastShader shader;

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

	public void createBrightnessContrastShader() {
		shader = new BrightnessContrastShader();
		shader.setBrightness(brightness);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		checkImageChanged();
		batch.setShader(shader);
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
				shader.setBrightness(brightness);
				brightnessChanged = false;
			}

			TextureRegion region = lookData.getTextureRegion();
			TextureRegionDrawable drawable = new TextureRegionDrawable(region);
			setDrawable(drawable);

			imageChanged = false;
		}
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

	private class BrightnessContrastShader extends ShaderProgram {

		private static final String VERTEX_SHADER = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
				+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" + "attribute vec2 "
				+ ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" + "uniform mat4 u_projTrans;\n" + "varying vec4 v_color;\n"
				+ "varying vec2 v_texCoords;\n" + "\n" + "void main()\n" + "{\n" + " v_color = "
				+ ShaderProgram.COLOR_ATTRIBUTE + ";\n" + " v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
				+ " gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" + "}\n";
		private static final String FRAGMENT_SHADER = "#ifdef GL_ES\n" + "#define LOWP lowp\n"
				+ "precision mediump float;\n" + "#else\n" + "#define LOWP \n" + "#endif\n"
				+ "varying LOWP vec4 v_color;\n" + "varying vec2 v_texCoords;\n" + "uniform sampler2D u_texture;\n"
				+ "uniform float brightness;\n" + "uniform float contrast;\n" + "void main()\n" + "{\n"
				+ " vec4 color = v_color * texture2D(u_texture, v_texCoords);\n" + " color.rgb /= color.a;\n"
				+ " color.rgb = ((color.rgb - 0.5) * max(contrast, 0.0)) + 0.5;\n" //apply contrast
				+ " color.rgb += brightness;\n" //apply brightness
				+ " color.rgb *= color.a;\n" + " gl_FragColor = color;\n" + "}";

		private static final String BRIGHTNESS_STRING_IN_SHADER = "brightness";
		private static final String CONTRAST_STRING_IN_SHADER = "contrast";

		public BrightnessContrastShader() {
			super(VERTEX_SHADER, FRAGMENT_SHADER);
			ShaderProgram.pedantic = false;
			if (isCompiled()) {
				begin();
				setUniformf(BRIGHTNESS_STRING_IN_SHADER, 0.0f);
				setUniformf(CONTRAST_STRING_IN_SHADER, 1.0f);
				end();
			}
		}

		public void setBrightness(float brightness) {
			begin();
			setUniformf(BRIGHTNESS_STRING_IN_SHADER, brightness - 1f);
			end();
		}
	}
}
