/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ThreadScheduler;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.actions.ScriptSequenceActionWithWaiter;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.physics.ParticleConstants;
import org.catrobat.catroid.sensing.CollisionInformation;
import org.catrobat.catroid.utils.TouchUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.VisibleForTesting;

import static org.catrobat.catroid.physics.ParticleConstants.LIFE_HIGH_MAX_ACTIVE;
import static org.catrobat.catroid.physics.ParticleConstants.LIFE_HIGH_MAX_DEAD;
import static org.catrobat.catroid.physics.ParticleConstants.PARTICLE_SCALE;

public class Look extends Image {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({ROTATION_STYLE_LEFT_RIGHT_ONLY, ROTATION_STYLE_ALL_AROUND, ROTATION_STYLE_NONE})
	public @interface RotationStyle {}
	public static final int ROTATION_STYLE_LEFT_RIGHT_ONLY = 0;
	public static final int ROTATION_STYLE_ALL_AROUND = 1;
	public static final int ROTATION_STYLE_NONE = 2;

	public static final float DEGREE_UI_OFFSET = 90.0f;
	private static final float COLOR_SCALE = 200.0f;
	private boolean lookVisible = true;
	private boolean simultaneousMovementXY = false;
	private int lookListIndexBeforeLookRequest = -1;
	protected LookData lookData;
	protected Sprite sprite;
	protected float alpha = 1f;
	protected float brightness = 1f;
	protected float hue = 0f;
	protected Pixmap pixmap;
	private BrightnessContrastHueShader shader;
	private int rotationMode = ROTATION_STYLE_ALL_AROUND;
	private float rotation = 90f;
	private float realRotation = rotation;
	private ThreadScheduler scheduler;
	private ParticleEffect particleEffect;

	public boolean hasParticleEffect = false;
	public boolean isAdditive = true;

	private boolean isParticleEffectPaused = false;

	public Look(final Sprite sprite) {
		this.sprite = sprite;
		scheduler = new ThreadScheduler(this);
		setBounds(0f, 0f, 0f, 0f);
		setOrigin(0f, 0f);
		setScale(1f, 1f);
		setRotation(0f);
		setTouchable(Touchable.enabled);
		addListeners();
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
		this.addListener(new EventWrapperListener(this));
	}

	public synchronized boolean isLookVisible() {
		return lookVisible;
	}

	public synchronized void setLookVisible(boolean lookVisible) {
		this.lookVisible = lookVisible;
	}

	public synchronized int getLookListIndexBeforeLookRequest() {
		return lookListIndexBeforeLookRequest;
	}

	public synchronized void setLookListIndexBeforeLookRequest(int lookListIndexBeforeLookRequest) {
		this.lookListIndexBeforeLookRequest = lookListIndexBeforeLookRequest;
	}

	@Override
	public boolean remove() {
		notifyAllWaiters();
		setLookVisible(false);
		boolean returnValue = super.remove();
		for (EventListener listener : this.getListeners()) {
			this.removeListener(listener);
		}
		getActions().clear();
		scheduler = null;
		this.sprite = null;
		this.lookData = null;
		return returnValue;
	}

	public void copyTo(final Look destination) {
		destination.setLookVisible(this.isLookVisible());
		destination.setPositionInUserInterfaceDimensionUnit(this.getXInUserInterfaceDimensionUnit(),
				this.getYInUserInterfaceDimensionUnit());
		destination.setSizeInUserInterfaceDimensionUnit(this.getSizeInUserInterfaceDimensionUnit());
		destination.setTransparencyInUserInterfaceDimensionUnit(this.getTransparencyInUserInterfaceDimensionUnit());
		destination.setColorInUserInterfaceDimensionUnit(this.getColorInUserInterfaceDimensionUnit());

		destination.setRotationMode(this.getRotationMode());
		destination.setMotionDirectionInUserInterfaceDimensionUnit(this.getMotionDirectionInUserInterfaceDimensionUnit());
		destination.setBrightnessInUserInterfaceDimensionUnit(this.getBrightnessInUserInterfaceDimensionUnit());
		destination.hasParticleEffect = hasParticleEffect;
		destination.isAdditive = isAdditive;
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
			EventWrapper event = new EventWrapper(new EventId(EventId.TAP), false);
			sprite.look.fire(event);
			return true;
		}

		return false;
	}

	public synchronized void createBrightnessContrastHueShader() {
		shader = new BrightnessContrastHueShader();
		shader.setBrightness(brightness);
		shader.setHue(hue);
	}

	public ParticleEffect getParticleEffect() {
		if (particleEffect == null) {
			initialiseParticleEffect();
		}
		return particleEffect;
	}

	private void initialiseParticleEffect() {
		particleEffect = new ParticleEffect();
		particleEffect.load(Gdx.files.internal("particles"), Gdx.files.internal(""));
		particleEffect.start();
	}

	public void pauseParticleEffect() {
		isParticleEffectPaused = true;
	}

	public void resumeParticleEffect() {
		isParticleEffectPaused = false;
	}

	@VisibleForTesting
	public boolean isParticleEffectPaused() {
		return isParticleEffectPaused;
	}

	public void clearParticleEffect() {
		if (particleEffect != null) {
			particleEffect.dispose();
			particleEffect = null;
		}
	}

	public ParticleEmitter getParticleEmitter() {
		return getParticleEffect().getEmitters().first();
	}

	private void setupParticleEffects(ParticleEmitter particleEmitter) {
		particleEmitter.setPosition(
				sprite.look.getX() + sprite.look.getWidth() / 2f,
				sprite.look.getY() + sprite.look.getHeight() / 2f);

		float spriteSize = sprite.look.getSizeInUserInterfaceDimensionUnit() / 2;

		float pScale = 1;
		if (sprite.getLookList().size() == 0) {
			pScale = spriteSize / PARTICLE_SCALE;
		}

		particleEmitter.getXScale().setHigh(spriteSize);
		particleEmitter.getVelocity().setHighMin(ParticleConstants.VELOCITY_HIGH_MIN * pScale);
		particleEmitter.getVelocity().setHighMax(ParticleConstants.VELOCITY_HIGH_MAX * pScale);
		particleEmitter.getGravity().setHigh(ProjectManager.getInstance().getCurrentlyPlayingScene().getPhysicsWorld().getGravity().y);
		particleEmitter.setAdditive(isAdditive);
	}

	private void fadeInParticles() {
		ParticleEmitter particleEmitter = getParticleEmitter();
		setupParticleEffects(particleEmitter);
		particleEmitter.setContinuous(true);
		particleEmitter.getLife().setHighMax(LIFE_HIGH_MAX_ACTIVE);

		particleEffect.update(Gdx.graphics.getDeltaTime());
	}

	private void fadeOutParticles() {
		ParticleEmitter particleEmitter = getParticleEmitter();
		setupParticleEffects(particleEmitter);
		particleEmitter.setContinuous(false);
		particleEmitter.getLife().setHighMax(LIFE_HIGH_MAX_DEAD);

		particleEffect.update(Gdx.graphics.getDeltaTime());
	}

	@Override
	public synchronized void draw(Batch batch, float parentAlpha) {
		if (!isParticleEffectPaused) {
			if (hasParticleEffect) {
				fadeInParticles();
			} else {
				if (particleEffect != null) {
					fadeOutParticles();
				}
			}
		}

		if (particleEffect != null) {
			particleEffect.draw(batch);
		}

		batch.setShader(shader);
		super.setVisible(alpha != 0.0f);

		if (isLookVisible() && this.getDrawable() != null) {
			super.draw(batch, this.alpha);
		}
		batch.setShader(null);
	}

	@Override
	public void act(float delta) {
		scheduler.tick(delta);
		if (sprite != null) {
			sprite.runningStitch.update();
			sprite.evaluateConditionScriptTriggers();
		}
	}

	@Override
	protected void positionChanged() {
		if (sprite != null && sprite.penConfiguration != null && sprite.penConfiguration.isPenDown()
				&& !simultaneousMovementXY) {
			float x = getXInUserInterfaceDimensionUnit();
			float y = getYInUserInterfaceDimensionUnit();
			sprite.penConfiguration.addPosition(new PointF(x, y));
		}
	}

	public void startThread(ScriptSequenceAction sequenceAction) {
		if (scheduler != null) {
			scheduler.startThread(sequenceAction);
		}
	}

	public void stopThreads(Array<Action> threads) {
		if (scheduler != null) {
			scheduler.stopThreads(threads);
		}
	}

	public void stopThreadWithScript(Script script) {
		if (scheduler != null) {
			scheduler.stopThreadsWithScript(script);
		}
	}

	public void setSchedulerState(@ThreadScheduler.SchedulerState int state) {
		scheduler.setState(state);
	}

	public synchronized void refreshTextures(boolean refreshShader) {
		if (lookData == null) {
			setBounds(getX() + getWidth() / 2f, getY() + getHeight() / 2f, 0f, 0f);
			setDrawable(null);
			return;
		}
		pixmap = lookData.getPixmap();
		if (pixmap != null) {
			float newX = getX() - (pixmap.getWidth() - getWidth()) / 2f;
			float newY = getY() - (pixmap.getHeight() - getHeight()) / 2f;
			setSize(pixmap.getWidth(), pixmap.getHeight());
			setPosition(newX, newY);
			setOrigin(getWidth() / 2f, getHeight() / 2f);
			TextureRegion region = lookData.getTextureRegion();
			TextureRegionDrawable drawable = new TextureRegionDrawable(region);
			setDrawable(drawable);
			flipLookDataIfNeeded(getRotationMode());
			if (refreshShader) {
				refreshShader();
			}
		}
	}

	private void refreshShader() {
		createShaderIfNotExisting();
		shader.setBrightness(brightness);
		shader.setHue(hue);
	}

	public synchronized LookData getLookData() {
		return lookData;
	}

	public synchronized void setLookData(LookData lookData) {
		this.lookData = lookData;
		refreshTextures(false);
	}

	public boolean haveAllThreadsFinished() {
		return scheduler.haveAllThreadsFinished();
	}

	public synchronized String getImagePath() {
		String path;
		if (this.lookData == null) {
			path = "";
		} else {
			path = this.lookData.getFile().getAbsolutePath();
		}
		return path;
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

		float dx = TouchUtil.getX(touchIndex) - getXInUserInterfaceDimensionUnit();
		float dy = TouchUtil.getY(touchIndex) - getYInUserInterfaceDimensionUnit();

		return (float) Math.hypot(dx, dy);
	}

	public float getAngularVelocityInUserInterfaceDimensionUnit() {
		// only available in physicsLook
		return 0;
	}

	public float getXVelocityInUserInterfaceDimensionUnit() {
		if (sprite.isGliding()) {
			return sprite.getGlidingVelocityX();
		}
		return 0;
	}

	public float getYVelocityInUserInterfaceDimensionUnit() {
		if (sprite.isGliding()) {
			return sprite.getGlidingVelocityY();
		}
		return 0;
	}

	public void setPositionInUserInterfaceDimensionUnit(float x, float y) {
		adjustSimultaneousMovementXY(x, y);
		setXInUserInterfaceDimensionUnit(x);
		adjustSimultaneousMovementXY(getXInUserInterfaceDimensionUnit(), y);
		setYInUserInterfaceDimensionUnit(y);
	}

	private void adjustSimultaneousMovementXY(float x, float y) {
		simultaneousMovementXY = x != getXInUserInterfaceDimensionUnit() && y != getYInUserInterfaceDimensionUnit();
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

	public float getMotionDirectionInUserInterfaceDimensionUnit() {
		return realRotation;
	}

	public float getLookDirectionInUserInterfaceDimensionUnit() {
		float direction = 0f;
		switch (rotationMode) {
			case ROTATION_STYLE_NONE : direction = DEGREE_UI_OFFSET;
			break;
			case ROTATION_STYLE_ALL_AROUND : direction = realRotation;
			break;
			case ROTATION_STYLE_LEFT_RIGHT_ONLY : direction =
					isFlipped() ? -DEGREE_UI_OFFSET : DEGREE_UI_OFFSET;
		}
		return direction;
	}

	public void setRotationMode(int mode) {
		rotationMode = mode;
		flipLookDataIfNeeded(mode);
	}

	private void flipLookDataIfNeeded(int mode) {
		boolean orientedLeft = getMotionDirectionInUserInterfaceDimensionUnit() < 0;
		boolean differentModeButFlipped = mode != ROTATION_STYLE_LEFT_RIGHT_ONLY && isFlipped();
		boolean facingWrongDirection = mode == ROTATION_STYLE_LEFT_RIGHT_ONLY && (orientedLeft ^ isFlipped());
		if (differentModeButFlipped || facingWrongDirection) {
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

	public void setMotionDirectionInUserInterfaceDimensionUnit(float degrees) {
		rotation = (-degrees + DEGREE_UI_OFFSET) % 360;
		realRotation = convertStageAngleToCatroidAngle(rotation);

		switch (rotationMode) {
			case ROTATION_STYLE_LEFT_RIGHT_ONLY:
				setRotation(0f);
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

	public boolean isFlipped() {
		return (lookData != null && lookData.getTextureRegion().isFlipX());
	}

	public void changeDirectionInUserInterfaceDimensionUnit(float changeDegrees) {
		setMotionDirectionInUserInterfaceDimensionUnit(
				(getMotionDirectionInUserInterfaceDimensionUnit() + changeDegrees) % 360);
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

	public synchronized void setBrightnessInUserInterfaceDimensionUnit(float percent) {
		if (percent < 0f) {
			percent = 0f;
		} else if (percent > 200f) {
			percent = 200f;
		}

		brightness = percent / 100f;
		refreshTextures(true);
	}

	public void changeBrightnessInUserInterfaceDimensionUnit(float changePercent) {
		setBrightnessInUserInterfaceDimensionUnit(getBrightnessInUserInterfaceDimensionUnit() + changePercent);
	}

	public float getColorInUserInterfaceDimensionUnit() {
		return hue * COLOR_SCALE;
	}

	public synchronized void setColorInUserInterfaceDimensionUnit(float val) {
		val = val % COLOR_SCALE;
		if (val < 0) {
			val = COLOR_SCALE + val;
		}
		hue = val / COLOR_SCALE;
		refreshTextures(true);
	}

	private void createShaderIfNotExisting() {
		if (shader == null) {
			createBrightnessContrastHueShader();
		}
	}

	public void changeColorInUserInterfaceDimensionUnit(float val) {
		setColorInUserInterfaceDimensionUnit(getColorInUserInterfaceDimensionUnit() + val);
	}

	private boolean isAngleInCatroidInterval(float catroidAngle) {
		return (catroidAngle > -180 && catroidAngle <= 180);
	}

	public float breakDownCatroidAngle(float catroidAngle) {
		catroidAngle = catroidAngle % 360;
		if (catroidAngle >= 0 && !isAngleInCatroidInterval(catroidAngle)) {
			return catroidAngle - 360;
		} else if (catroidAngle < 0 && !isAngleInCatroidInterval(catroidAngle)) {
			return catroidAngle + 360;
		}
		return catroidAngle;
	}

	public float convertCatroidAngleToStageAngle(float catroidAngle) {
		catroidAngle = breakDownCatroidAngle(catroidAngle);
		return -catroidAngle + DEGREE_UI_OFFSET;
	}

	public float convertStageAngleToCatroidAngle(float stageAngle) {
		float catroidAngle = -stageAngle + DEGREE_UI_OFFSET;
		return breakDownCatroidAngle(catroidAngle);
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

	public Polygon[] getCurrentCollisionPolygon() {
		Polygon[] originalPolygons;
		if (getLookData() == null) {
			originalPolygons = new Polygon[0];
		} else {
			CollisionInformation collisionInformation = getLookData().getCollisionInformation();
			if (collisionInformation.collisionPolygons == null) {
				collisionInformation.loadCollisionPolygon();
			}
			originalPolygons = collisionInformation.collisionPolygons;
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

	void notifyAllWaiters() {
		for (Action action : getActions()) {
			if (action instanceof ScriptSequenceActionWithWaiter) {
				((ScriptSequenceActionWithWaiter) action).notifyWaiter();
			}
		}
	}

	public float getAlpha() {
		return alpha;
	}

	@VisibleForTesting
	public float getBrightness() {
		return brightness;
	}
}
