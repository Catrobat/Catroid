/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content;

import android.graphics.PointF;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.common.DroneVideoLookData;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.utils.TouchUtil;

import java.util.ArrayList;
import java.util.Iterator;

public class Look extends Image {
	private static final float DEGREE_UI_OFFSET = 90.0f;
	private static final float COLOR_SCALE = 200.0f;
	private static ArrayList<Action> actionsToRestart = new ArrayList<Action>();
	private boolean lookVisible = true;
	protected boolean imageChanged = false;
	protected boolean brightnessChanged = false;
	protected boolean colorChanged = false;
	protected LookData lookData;
	protected Sprite sprite;
	protected float alpha = 1f;
	protected float brightness = 1f;
	protected float hue = 0f;
	protected Pixmap pixmap;
	private ParallelAction whenParallelAction;
	private boolean allActionsAreFinished = false;
	private BrightnessContrastHueShader shader;
	public static final int ROTATION_STYLE_ALL_AROUND = 1;
	public static final int ROTATION_STYLE_LEFT_RIGHT_ONLY = 0;
	public static final int ROTATION_STYLE_NONE = 2;
	private int rotationMode = ROTATION_STYLE_ALL_AROUND;
	private float rotation = 90f;
	private float realRotation = rotation;
	public boolean isFlipped = false;

	public Look(final Sprite sprite) {
		this.sprite = sprite;
		setBounds(0f, 0f, 0f, 0f);
		setOrigin(0f, 0f);
		setScale(1f, 1f);
		setRotation(0f);
		setTouchable(Touchable.enabled);
		addListeners();
		rotation = getDirectionInUserInterfaceDimensionUnit();
	}

	protected void addListeners() {
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

	public boolean isLookVisible() {
		return lookVisible;
	}

	public void setLookVisible(boolean lookVisible) {
		this.lookVisible = lookVisible;
	}

	public static boolean actionsToRestartContains(Action action) {
		return Look.actionsToRestart.contains(action);
	}

	public static void actionsToRestartAdd(Action action) {
		Look.actionsToRestart.add(action);
	}

	public Look copyLookForSprite(final Sprite cloneSprite) {
		Look cloneLook = cloneSprite.look;

		cloneLook.alpha = this.alpha;
		cloneLook.brightness = this.brightness;
		cloneLook.setLookVisible(isLookVisible());
		cloneLook.whenParallelAction = null;
		cloneLook.allActionsAreFinished = this.allActionsAreFinished;

		return cloneLook;
	}

	public boolean doTouchDown(float x, float y, int pointer) {
		if (sprite.isPaused) {
			return true;
		}
		if (!isLookVisible()) {
			return false;
		}
		if (isFlipped) {
			x = -x + getWidth();
		}

		// We use Y-down, libgdx Y-up. This is the fix for accurate y-axis detection
		y = (getHeight() - 1) - y;

		if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()
				&& ((pixmap != null && ((pixmap.getPixel((int) x, (int) y) & 0x000000FF) > 10)))) {
			if (whenParallelAction == null) {
				sprite.createWhenScriptActionSequence("Tapped");
			} else {
				whenParallelAction.restart();
			}
			return true;
		}

		return false;
	}

	public void createBrightnessContrastHueShader() {
		shader = new BrightnessContrastHueShader();
		shader.setBrightness(brightness);
		shader.setHue(hue);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		checkImageChanged();
		batch.setShader(shader);
		if (alpha == 0.0f) {
			super.setVisible(false);
		} else {
			super.setVisible(true);
		}

		if (lookData instanceof DroneVideoLookData && lookData != null) {
			lookData.draw(batch, alpha);
		}

		if (isLookVisible() && this.getDrawable() != null) {
			super.draw(batch, this.alpha);
		}
		batch.setShader(null);
	}

	@Override
	public void act(float delta) {
		Array<Action> actions = getActions();
		allActionsAreFinished = false;
		int finishedCount = 0;

		for (Iterator<Action> iterator = Look.actionsToRestart.iterator(); iterator.hasNext(); ) {
			Action actionToRestart = iterator.next();
			actionToRestart.restart();
			iterator.remove();
		}

		int n = actions.size;
		for (int i = 0; i < n; i++) {
			Action action = actions.get(i);
			if (action.act(delta)) {
				finishedCount++;
			}
		}
		if (finishedCount == actions.size) {
			allActionsAreFinished = true;
		}
	}

	@Override
	public void addAction(Action action) {
		super.addAction(action);
		allActionsAreFinished = false;
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

			setSize(pixmap.getWidth(), pixmap.getHeight());
			setPosition(newX, newY);
			setOrigin(getWidth() / 2f, getHeight() / 2f);

			if (brightnessChanged) {
				shader.setBrightness(brightness);
				brightnessChanged = false;
			}

			if (colorChanged) {
				shader.setHue(hue);
				colorChanged = false;
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

	public LookData getLookData() {
		return lookData;
	}

	public void setLookData(LookData lookData) {
		this.lookData = lookData;
		imageChanged = true;

		boolean isBackgroundLook = getZIndex() == 0;
		if (isBackgroundLook) {
			BackgroundWaitHandler.fireBackgroundChangedEvent(lookData);
		}
	}

	public boolean getAllActionsAreFinished() {
		return allActionsAreFinished;
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

	public void setXInUserInterfaceDimensionUnit(float x) {
		setX(x - getWidth() / 2f);
	}

	public float getYInUserInterfaceDimensionUnit() {
		return getY() + getHeight() / 2f;
	}

	public void setYInUserInterfaceDimensionUnit(float y) {
		setY(y - getHeight() / 2f);
	}

	public float getDistanceToTouchPositionInUserInterfaceDimensions() {
		int touchIndex = TouchUtil.getLastTouchIndex();

		return (float)
				Math.sqrt(Math.pow((TouchUtil.getX(touchIndex) - getXInUserInterfaceDimensionUnit()), 2)
						+ Math.pow((TouchUtil.getY(touchIndex) - getYInUserInterfaceDimensionUnit()), 2));
	}

	public float getAngularVelocityInUserInterfaceDimensionUnit() {
		// only available in physicsLook
		return 0;
	}

	public float getXVelocityInUserInterfaceDimensionUnit() {
		// only available in physicsLook
		return 0;
	}

	public float getYVelocityInUserInterfaceDimensionUnit() {
		// only available in physicsLook
		return 0;
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
		return realRotation;
	}

	public void setRotationMode(int mode) {
		rotationMode = mode;
	}

	public int getRotationMode() {
		return rotationMode;
	}

	private PointF rotatePointAroundPoint(PointF center, PointF point, float rotation) {
		float sin = (float) Math.sin(rotation);
		float cos = (float) Math.cos(rotation);
		point.x -= center.x;
		point.y -= center.y;
		float xNew = point.x * cos - point.y * sin;
		float yNew = point.x * sin + point.y * cos;
		point.x = xNew + center.x;
		point.y = yNew + center.y;
		return point;
	}

	public Rectangle getHitbox() {
		float x = getXInUserInterfaceDimensionUnit() - getWidthInUserInterfaceDimensionUnit() / 2;
		float y = getYInUserInterfaceDimensionUnit() - getHeightInUserInterfaceDimensionUnit() / 2;
		float width = getWidthInUserInterfaceDimensionUnit();
		float height = getHeightInUserInterfaceDimensionUnit();
		float[] vertices;
		if (getRotation() == 0) {
			vertices = new float[] {
					x, y,
					x, y + height,
					x + width, y + height,
					x + width, y
			};
		} else {
			PointF center = new PointF(x + width / 2f, y + height / 2f);
			PointF upperLeft = rotatePointAroundPoint(center, new PointF(x, y), getRotation());
			PointF upperRight = rotatePointAroundPoint(center, new PointF(x, y + height), getRotation());
			PointF lowerRight = rotatePointAroundPoint(center, new PointF(x + width, y + height), getRotation());
			PointF lowerLeft = rotatePointAroundPoint(center, new PointF(x + width, y), getRotation());
			vertices = new float[] {
					upperLeft.x, upperLeft.y,
					upperRight.x, upperRight.y,
					lowerRight.x, lowerRight.y,
					lowerLeft.x, lowerLeft.y
			};
		}

		Polygon p = new Polygon(vertices);

		return p.getBoundingRectangle();
	}

	public void setDirectionInUserInterfaceDimensionUnit(float degrees) {
		rotation = (-degrees + DEGREE_UI_OFFSET) % 360;
		realRotation = convertStageAngleToCatroidAngle(rotation);

		switch (rotationMode) {
			case ROTATION_STYLE_LEFT_RIGHT_ONLY:
				setRotation(0f);
				boolean orientedRight = realRotation >= 0;
				boolean orientedLeft = realRotation < 0;
				if (isFlipped && orientedRight || !isFlipped && orientedLeft) {
					if (lookData != null) {
						lookData.getTextureRegion().flip(true, false);
					}
					isFlipped = !isFlipped;
				}
				break;
			case ROTATION_STYLE_ALL_AROUND:
				setRotation(rotation);
				break;
			case ROTATION_STYLE_NONE:
				setRotation(0f);
				break;
		}
	}

	public float getRealRotation() {
		return realRotation;
	}

	public boolean isFlipped() {
		return isFlipped;
	}

	public void setFlipped(boolean status) {
		isFlipped = status;
	}

	public void changeDirectionInUserInterfaceDimensionUnit(float changeDegrees) {
		setDirectionInUserInterfaceDimensionUnit(
				(getDirectionInUserInterfaceDimensionUnit() + changeDegrees) % 360);
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
		if (percent < 100.0f) {
			if (percent < 0f) {
				percent = 0f;
			}
			setVisible(true);
		} else {
			percent = 100f;
			setVisible(false);
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

	public float getColorInUserInterfaceDimensionUnit() {
		return hue * COLOR_SCALE;
	}

	public void setColorInUserInterfaceDimensionUnit(float val) {
		val = val % COLOR_SCALE;
		if (val < 0) {
			val = COLOR_SCALE + val;
		}
		hue = ((float) val) / COLOR_SCALE;
		colorChanged = true;
		imageChanged = true;
	}

	public void changeColorInUserInterfaceDimensionUnit(float val) {
		setColorInUserInterfaceDimensionUnit(getColorInUserInterfaceDimensionUnit() + val);
	}

	private boolean isAngleInCatroidInterval(float catroidAngle) {
		return (catroidAngle > -180 && catroidAngle <= 180);
	}

	private float breakDownCatroidAngle(float catroidAngle) {
		catroidAngle = catroidAngle % 360;
		if (catroidAngle >= 0 && !isAngleInCatroidInterval(catroidAngle)) {
			return catroidAngle - 360;
		} else if (catroidAngle < 0 && !isAngleInCatroidInterval(catroidAngle)) {
			return catroidAngle + 360;
		}
		return catroidAngle;
	}

	protected float convertCatroidAngleToStageAngle(float catroidAngle) {
		catroidAngle = breakDownCatroidAngle(catroidAngle);
		return -catroidAngle + DEGREE_UI_OFFSET;
	}

	protected float convertStageAngleToCatroidAngle(float stageAngle) {
		float catroidAngle = -stageAngle + DEGREE_UI_OFFSET;
		return breakDownCatroidAngle(catroidAngle);
	}

	protected void doHandleBroadcastEvent(String broadcastMessage) {
		BroadcastHandler.doHandleBroadcastEvent(this, broadcastMessage);
	}

	protected void doHandleBroadcastFromWaiterEvent(BroadcastEvent event, String broadcastMessage) {
		BroadcastHandler.doHandleBroadcastFromWaiterEvent(this, event, broadcastMessage);
	}

	private class BrightnessContrastHueShader extends ShaderProgram {

		private static final String VERTEX_SHADER = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
				+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" + "attribute vec2 "
				+ ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" + "uniform mat4 u_projTrans;\n" + "varying vec4 v_color;\n"
				+ "varying vec2 v_texCoords;\n" + "\n" + "void main()\n" + "{\n" + " v_color = "
				+ ShaderProgram.COLOR_ATTRIBUTE + ";\n" + " v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
				+ " gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" + "}\n";
		private static final String FRAGMENT_SHADER = "#ifdef GL_ES\n"
				+ "    #define LOWP lowp\n"
				+ "    precision mediump float;\n"
				+ "#else\n"
				+ "    #define LOWP\n"
				+ "#endif\n"
				+ "varying LOWP vec4 v_color;\n"
				+ "varying vec2 v_texCoords;\n"
				+ "uniform sampler2D u_texture;\n"
				+ "uniform float brightness;\n"
				+ "uniform float contrast;\n"
				+ "uniform float hue;\n"
				+ "vec3 rgb2hsv(vec3 c)\n"
				+ "{\n"
				+ "    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);\n"
				+ "    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));\n"
				+ "    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));\n"
				+ "    float d = q.x - min(q.w, q.y);\n"
				+ "    float e = 1.0e-10;\n"
				+ "    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);\n"
				+ "}\n"
				+ "vec3 hsv2rgb(vec3 c)\n"
				+ "{\n"
				+ "    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);\n"
				+ "    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);\n"
				+ "    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);\n"
				+ "}\n"
				+ "void main()\n"
				+ "{\n"
				+ "    vec4 color = v_color * texture2D(u_texture, v_texCoords);\n"
				+ "    color.rgb /= color.a;\n"
				+ "    color.rgb = ((color.rgb - 0.5) * max(contrast, 0.0)) + 0.5;\n"
				+ "    color.rgb += brightness;\n"
				+ "    color.rgb *= color.a;\n"
				+ "    vec3 hsv = rgb2hsv(color.rgb);\n"
				+ "    hsv.x += hue;\n"
				+ "    vec3 rgb = hsv2rgb(hsv);\n"
				+ "    gl_FragColor = vec4(rgb.r, rgb.g, rgb.b, color.a);\n"
				+ " }";

		private static final String BRIGHTNESS_STRING_IN_SHADER = "brightness";
		private static final String CONTRAST_STRING_IN_SHADER = "contrast";
		private static final String HUE_STRING_IN_SHADER = "hue";

		public BrightnessContrastHueShader() {
			super(VERTEX_SHADER, FRAGMENT_SHADER);
			ShaderProgram.pedantic = false;
			if (isCompiled()) {
				begin();
				setUniformf(BRIGHTNESS_STRING_IN_SHADER, 0.0f);
				setUniformf(CONTRAST_STRING_IN_SHADER, 1.0f);
				setUniformf(HUE_STRING_IN_SHADER, 0.0f);
				end();
			}
		}

		public void setBrightness(float brightness) {
			begin();
			setUniformf(BRIGHTNESS_STRING_IN_SHADER, brightness - 1f);
			end();
		}

		public void setHue(float hue) {
			begin();
			setUniformf(HUE_STRING_IN_SHADER, hue);
			end();
		}
	}

	public Polygon[] getCurrentCollisionPolygon() {
		Polygon[] originalPolygons;
		if (getLookData() == null) {
			originalPolygons = new Polygon[0];
		} else {
			if (getLookData().getCollisionInformation().collisionPolygons == null) {
				getLookData().getCollisionInformation().loadOrCreateCollisionPolygon();
			}
			originalPolygons = getLookData().getCollisionInformation().collisionPolygons;
		}

		Polygon[] transformedPolygons = new Polygon[originalPolygons.length];

		for (int p = 0; p < transformedPolygons.length; p++) {
			Polygon poly = new Polygon(originalPolygons[p].getTransformedVertices());
			poly.translate(getX(), getY());
			poly.setRotation(getRotation());
			poly.setScale(getScaleX(), getScaleY());
			poly.setOrigin(getOriginX(), getOriginY());
			transformedPolygons[p] = poly;
		}
		return transformedPolygons;
	}
}
