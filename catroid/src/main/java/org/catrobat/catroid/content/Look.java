/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.DroneVideoLookData;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorldConverter;
import org.catrobat.catroid.utils.TouchUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Look extends Image {
	private static final float DEGREE_UI_OFFSET = 90.0f;
	private static final float COLOR_SCALE = 200.0f;
	private static ArrayList<Action> actionsToRestart = new ArrayList<>();
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
				doHandleBroadcastEvent(event.getSenderSprite(), broadcastMessage);
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
		physicsObjectStateHandler.update(true);
	}

	public static boolean actionsToRestartContains(Action action) {
		return Look.actionsToRestart.contains(action);
	}

	public static void actionsToRestartAdd(Action action) {
		Look.actionsToRestart.add(action);
	}

	public void copyTo(final Look destination) {
		destination.setLookVisible(this.isLookVisible());
		destination.setPositionInUserInterfaceDimensionUnit(this.getXInUserInterfaceDimensionUnit(),
				this.getYInUserInterfaceDimensionUnit());
		destination.setSizeInUserInterfaceDimensionUnit(this.getSizeInUserInterfaceDimensionUnit());
		destination.setTransparencyInUserInterfaceDimensionUnit(this.getTransparencyInUserInterfaceDimensionUnit());
		destination.setColorInUserInterfaceDimensionUnit(this.getColorInUserInterfaceDimensionUnit());

		destination.setRotationMode(this.getRotationMode());
		destination.setDirectionInUserInterfaceDimensionUnit(this.getDirectionInUserInterfaceDimensionUnit());
		destination.setBrightnessInUserInterfaceDimensionUnit(this.getBrightnessInUserInterfaceDimensionUnit());
	}

	public boolean doTouchDown(float x, float y, int pointer) {
		if (!isLookVisible()) {
			return false;
		}
		if (isFlipped()) {
			x = (getWidth() - 1) - x;
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
		physicsObjectStateHandler.checkHangup(true);
		checkImageChanged();
		batch.setShader(shader);
		if (alpha == 0.0f) {
			super.setVisible(false);
		} else {
			super.setVisible(true);
		}

		if (lookData instanceof DroneVideoLookData) {
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

			flipLookDataIfNeeded(getRotationMode());
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

		boolean isBackgroundLook = getZIndex() == Constants.Z_INDEX_BACKGROUND;
		if (isBackgroundLook) {
			BackgroundWaitHandler.fireBackgroundChangedEvent(lookData);
		}
		PhysicsWorld physicsWorld = ProjectManager.getInstance().getSceneToPlay().getPhysicsWorld();
		physicsWorld.changeLook(sprite.getPhysicsProperties(), this);
		updatePhysicsObjectState(true);
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
		flipLookDataIfNeeded(mode);
	}

	private void flipLookDataIfNeeded(int mode) {
		boolean orientedLeft = getDirectionInUserInterfaceDimensionUnit() < 0;
		boolean differentModeButFlipped = mode != Look.ROTATION_STYLE_LEFT_RIGHT_ONLY && isFlipped();
		boolean facingLeftButNotFlipped = mode == Look.ROTATION_STYLE_LEFT_RIGHT_ONLY && orientedLeft;
		if (differentModeButFlipped || facingLeftButNotFlipped) {
			getLookData().getTextureRegion().flip(true, false);
		}
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
				setRotation(rotation);
				boolean orientedRight = realRotation >= 0;
				boolean orientedLeft = realRotation < 0;
				boolean needsFlipping = (isFlipped() && orientedRight) || (!isFlipped() && orientedLeft);
				if (needsFlipping && lookData != null) {
					lookData.getTextureRegion().flip(true, false);
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
		return (lookData != null && lookData.getTextureRegion().isFlipX());
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
		updatePhysicsObjectState(true);
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
		hue = val / COLOR_SCALE;
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

	protected void doHandleBroadcastEvent(Sprite senderSprite, String broadcastMessage) {
		BroadcastHandler.doHandleBroadcastEvent(this, senderSprite, broadcastMessage);
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

		BrightnessContrastHueShader() {
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

	public Map<String, List<String>> createScriptActions() {
		this.setWhenParallelAction(null);
		Map<String, List<String>> scriptActions = new HashMap<>();
		sprite.createStartScriptActionSequenceAndPutToMap(scriptActions, false);
		return scriptActions;
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

	// ----------------- PHYSICS  -----------------
	public static final float SCALE_FACTOR_ACCURACY = 10000.0f;

	private final Look.PhysicsObjectStateHandler physicsObjectStateHandler = new Look.PhysicsObjectStateHandler();

	public void setXInUserInterfaceDimensionUnit(float x) {
		setX(x - getWidth() / 2f);
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		if (null != sprite.getPhysicsProperties()) {
			sprite.getPhysicsProperties().setX(x + getWidth() / 2.0f);
			sprite.getPhysicsProperties().setY(y + getHeight() / 2.0f);
		}
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		if (null != sprite.getPhysicsProperties()) {
			sprite.getPhysicsProperties().setX(x + getWidth() / 2.0f);
		}
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		if (null != sprite.getPhysicsProperties()) {
			sprite.getPhysicsProperties().setY(y + getHeight() / 2.0f);
		}
	}

	public float getAngularVelocityInUserInterfaceDimensionUnit() {
		return sprite.getPhysicsProperties().getRotationSpeed();
	}

	public float getXVelocityInUserInterfaceDimensionUnit() {
		return sprite.getPhysicsProperties().getVelocity().x;
	}

	public float getYVelocityInUserInterfaceDimensionUnit() {
		return sprite.getPhysicsProperties().getVelocity().y;
	}

	public float getX() {
		float x = sprite.getPhysicsProperties().getX() - getWidth() / 2.0f;
		super.setX(x);
		return x;
	}

	@Override
	public float getY() {
		float y = sprite.getPhysicsProperties().getY() - getHeight() / 2.0f;
		super.setY(y);
		return y;
	}

	@Override
	public float getRotation() {
		super.setRotation((sprite.getPhysicsProperties().getDirection() % 360));

		float rotation = super.getRotation();
		float realRotation = convertStageAngleToCatroidAngle(sprite.getPhysicsProperties().getDirection() % 360);
		if (realRotation < 0) {
			realRotation += 360;
		}

		switch (getRotationMode()) {
			case ROTATION_STYLE_LEFT_RIGHT_ONLY:
				super.setRotation(0f);
				boolean orientedRight = realRotation > 180 || realRotation == 0;
				boolean orientedLeft = realRotation <= 180 && realRotation != 0;
				if (((isFlipped() && orientedRight) || (!isFlipped() && orientedLeft)) && lookData != null) {
					lookData.getTextureRegion().flip(true, false);
				}
				break;
			case ROTATION_STYLE_ALL_AROUND:
				super.setRotation(rotation);
				break;
			case ROTATION_STYLE_NONE:
				super.setRotation(0f);
				break;
		}

		return super.getRotation();
	}

	@Override
	public void setRotation(float degrees) {
		super.setRotation(degrees);
		if (null != sprite.getPhysicsProperties()) {
			sprite.getPhysicsProperties().setDirection(super.getRotation() % 360);
		}
	}

	@Override
	public void setScale(float scaleX, float scaleY) {
		Vector2 oldScales = new Vector2(getScaleX(), getScaleY());
		if (scaleX < 0.0f || scaleY < 0.0f) {
			scaleX = 0.0f;
			scaleY = 0.0f;
		}

		int scaleXComp = Math.round(scaleX * SCALE_FACTOR_ACCURACY);
		int scaleYComp = Math.round(scaleY * SCALE_FACTOR_ACCURACY);
		if (scaleXComp == Math.round(oldScales.x * SCALE_FACTOR_ACCURACY) && scaleYComp == Math.round(oldScales.y * SCALE_FACTOR_ACCURACY)) {
			return;
		}

		super.setScale(scaleX, scaleY);

		if (sprite.getPhysicsProperties() != null) {
			PhysicsWorld physicsWorld = ProjectManager.getInstance().getSceneToPlay().getPhysicsWorld();
			physicsWorld.changeLook(sprite.getPhysicsProperties(), this);
			updatePhysicsObjectState(true);
		}
	}

	public void updatePhysicsObjectState(boolean record) {
		physicsObjectStateHandler.update(record);
	}

	public boolean isHangedUp() {
		return physicsObjectStateHandler.isHangedUp();
	}

	public void setNonColliding(boolean nonColliding) {
		physicsObjectStateHandler.setNonColliding(nonColliding);
	}

	public void startGlide() {
		physicsObjectStateHandler.activateGlideTo();
	}

	public void stopGlide() {
		physicsObjectStateHandler.deactivateGlideTo();
	}

	private interface PhysicsObjectStateCondition {
		boolean isTrue();
	}

	private class PhysicsObjectStateHandler {

		private LinkedList<Look.PhysicsObjectStateCondition> hangupConditions = new LinkedList<>();
		private LinkedList<Look.PhysicsObjectStateCondition> nonCollidingConditions = new LinkedList<>();
		private LinkedList<Look.PhysicsObjectStateCondition> fixConditions = new LinkedList<>();

		private Look.PhysicsObjectStateCondition positionCondition;
		private Look.PhysicsObjectStateCondition visibleCondition;
		private Look.PhysicsObjectStateCondition transparencyCondition;
		private Look.PhysicsObjectStateCondition glideToCondition;

		private boolean glideToIsActive = false;
		private boolean hangedUp = false;
		private boolean fixed = false;
		private boolean nonColliding = false;

		PhysicsObjectStateHandler() {

			positionCondition = new Look.PhysicsObjectStateCondition() {
				@Override
				public boolean isTrue() {
					return isOutsideActiveArea();
				}

				private boolean isOutsideActiveArea() {
					return isXOutsideActiveArea() || isYOutsideActiveArea();
				}

				private boolean isXOutsideActiveArea() {
					return Math.abs(PhysicsWorldConverter.convertBox2dToNormalCoordinate(sprite.getPhysicsProperties().getMassCenter().x))
							- sprite.getPhysicsProperties().getCircumference() > PhysicsWorld.activeArea.x / 2.0f;
				}

				private boolean isYOutsideActiveArea() {
					return Math.abs(PhysicsWorldConverter.convertBox2dToNormalCoordinate(sprite.getPhysicsProperties().getMassCenter().y))
							- sprite.getPhysicsProperties().getCircumference() > PhysicsWorld.activeArea.y / 2.0f;
				}
			};

			visibleCondition = new Look.PhysicsObjectStateCondition() {
				@Override
				public boolean isTrue() {
					return !isLookVisible();
				}
			};

			transparencyCondition = new Look.PhysicsObjectStateCondition() {
				@Override
				public boolean isTrue() {
					return alpha == 0.0;
				}
			};

			glideToCondition = new Look.PhysicsObjectStateCondition() {
				@Override
				public boolean isTrue() {
					return glideToIsActive;
				}
			};

			hangupConditions.add(transparencyCondition);
			hangupConditions.add(positionCondition);
			hangupConditions.add(visibleCondition);
			hangupConditions.add(glideToCondition);

			nonCollidingConditions.add(transparencyCondition);
			nonCollidingConditions.add(positionCondition);
			nonCollidingConditions.add(visibleCondition);

			fixConditions.add(glideToCondition);
		}

		private boolean checkHangup(boolean record) {
			boolean shouldBeHangedUp = false;
			for (Look.PhysicsObjectStateCondition hangupCondition : hangupConditions) {
				if (hangupCondition.isTrue()) {
					shouldBeHangedUp = true;
					break;
				}
			}
			boolean deactivateHangup = hangedUp && !shouldBeHangedUp;
			boolean activateHangup = !hangedUp && shouldBeHangedUp;
			if (deactivateHangup) {
				sprite.getPhysicsProperties().deactivateHangup(record);
			} else if (activateHangup) {
				sprite.getPhysicsProperties().activateHangup();
			}
			hangedUp = shouldBeHangedUp;
			return hangedUp;
		}

		private boolean checkNonColliding(boolean record) {
			boolean shouldBeNonColliding = false;
			for (Look.PhysicsObjectStateCondition nonCollideCondition : nonCollidingConditions) {
				if (nonCollideCondition.isTrue()) {
					shouldBeNonColliding = true;
					break;
				}
			}
			boolean deactivateNonColliding = nonColliding && !shouldBeNonColliding;
			boolean activateNonColliding = !nonColliding && shouldBeNonColliding;
			if (deactivateNonColliding) {
				sprite.getPhysicsProperties().deactivateNonColliding(record, false);
			} else if (activateNonColliding) {
				sprite.getPhysicsProperties().activateNonColliding(false);
			}
			nonColliding = shouldBeNonColliding;
			return nonColliding;
		}

		private boolean checkFixed(boolean record) {
			boolean shouldBeFixed = false;
			for (Look.PhysicsObjectStateCondition fixedCondition : fixConditions) {
				if (fixedCondition.isTrue()) {
					shouldBeFixed = true;
					break;
				}
			}
			boolean deactivateFix = fixed && !shouldBeFixed;
			boolean activateFix = !fixed && shouldBeFixed;
			if (deactivateFix) {
				sprite.getPhysicsProperties().deactivateFixed(record);
			} else if (activateFix) {
				sprite.getPhysicsProperties().activateFixed();
			}
			fixed = shouldBeFixed;
			return fixed;
		}

		public void update(boolean record) {
			checkHangup(record);
			checkNonColliding(record);
			checkFixed(record);
		}

		public void activateGlideTo() {
			if (!glideToIsActive) {
				glideToIsActive = true;
				updatePhysicsObjectState(true);
			}
		}

		public void deactivateGlideTo() {
			glideToIsActive = false;
			updatePhysicsObjectState(true);
		}

		public boolean isHangedUp() {
			return hangedUp;
		}

		public void setNonColliding(boolean nonColliding) {
			if (this.nonColliding != nonColliding) {
				this.nonColliding = nonColliding;
				update(true);
			}
		}
	}
}
