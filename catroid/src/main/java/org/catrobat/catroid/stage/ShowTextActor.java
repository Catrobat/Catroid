/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.stage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;
import java.util.Locale;

import static org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_CENTERED;
import static org.catrobat.catroid.utils.ShowTextUtils.DEFAULT_TEXT_SIZE;
import static org.catrobat.catroid.utils.ShowTextUtils.DEFAULT_X_OFFSET;
import static org.catrobat.catroid.utils.ShowTextUtils.calculateAlignmentValuesForText;
import static org.catrobat.catroid.utils.ShowTextUtils.calculateColorRGBs;
import static org.catrobat.catroid.utils.ShowTextUtils.getStringAsInteger;
import static org.catrobat.catroid.utils.ShowTextUtils.isNumberAndInteger;
import static org.catrobat.catroid.utils.ShowTextUtils.isValidColorString;
import static org.catrobat.catroid.utils.ShowTextUtils.sanitizeTextSize;

public class ShowTextActor extends Actor {

	private static final int DEFAULT_ALIGNMENT = ALIGNMENT_STYLE_CENTERED;
	private float textSize;
	private int xPosition;
	private int yPosition;
	private String color;
	private UserVariable variableToShow;
	private String variableNameToCompare;
	private int alignment;
	private Sprite sprite;

	public ShowTextActor(UserVariable userVariable, int xPosition, int yPosition, float relativeSize, String color,
			Sprite sprite, int alignment) {
		this.variableToShow = userVariable;
		this.variableNameToCompare = variableToShow.getName();
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.textSize = DEFAULT_TEXT_SIZE * relativeSize;
		this.color = color;
		this.sprite = sprite;
		this.alignment = alignment;
	}

	public ShowTextActor(UserVariable userVariable, int xPosition, int yPosition, float relativeSize, String color,
			Sprite sprite) {
		this.variableToShow = userVariable;
		this.variableNameToCompare = variableToShow.getName();
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.textSize = DEFAULT_TEXT_SIZE * relativeSize;
		this.color = color;
		this.sprite = sprite;
		this.alignment = DEFAULT_ALIGNMENT;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		drawVariables(ProjectManager.getInstance().getCurrentProject().getUserVariables(), batch);
		drawVariables(ProjectManager.getInstance().getCurrentProject().getMultiplayerVariables(), batch);
		drawVariables(sprite.getUserVariables(), batch);
	}

	private void drawVariables(List<UserVariable> variableList, Batch batch) {
		if (variableList == null) {
			return;
		}

		if (variableToShow.isDummy()) {
			drawText(batch, Constants.NO_VARIABLE_SELECTED, xPosition, yPosition, color);
		} else {
			for (UserVariable variable : variableList) {
				if (variable.getName().equals(variableToShow.getName())) {
					String variableValue = variable.getValue().toString();
					if (variableValue.isEmpty()) {
						continue;
					}
					if (variable.getVisible()) {
						if (isNumberAndInteger(variableValue)) {
							drawText(batch, getStringAsInteger(variableValue), xPosition, yPosition, color);
						} else {
							drawText(batch, variableValue, xPosition, yPosition, color);
						}
					}
					break;
				}
			}
		}
	}

	private void drawText(Batch batch, String text, float posX, float posY, String color) {
		// Convert to bitmap
		Paint paint = new Paint();
		float textSizeInPx = sanitizeTextSize(textSize);
		paint.setTextSize(textSizeInPx);

		if (isValidColorString(color)) {
			color = color.toUpperCase(Locale.getDefault());
			int[] rgb;
			rgb = calculateColorRGBs(color);
			paint.setColor((0xFF000000) | (rgb[0] << 16) | (rgb[1] << 8) | (rgb[2]));
			batch.setColor((float) rgb[0] / 255, (float) rgb[1] / 255, (float) rgb[2] / 255, 1);
		} else {
			paint.setColor(Color.BLACK);
		}

		float baseline = -paint.ascent();
		paint.setAntiAlias(true);

		int availableWidth = (int) Math.ceil(ScreenValues.SCREEN_WIDTH + 2 * Math.abs(posX));
		int bitmapWidth = Math.min(availableWidth, (int) paint.measureText(text));
		int canvasWidth = calculateAlignmentValuesForText(paint, bitmapWidth, alignment);
		int height = (int) (baseline + paint.descent());

		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawText(text, canvasWidth, baseline, paint);
		// Convert to texture
		Texture tex = new Texture(bitmap.getWidth(), bitmap.getHeight(),
				Pixmap.Format.RGBA8888);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
				tex.getTextureObjectHandle());
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		bitmap.recycle();
		// Draw and dispose
		float xOffset = DEFAULT_X_OFFSET + canvasWidth;
		batch.draw(tex, posX - xOffset, posY - textSizeInPx);
		batch.flush();
		tex.dispose();
	}

	public void setPositionX(int xPosition) {
		this.xPosition = xPosition;
	}

	public void setPositionY(int yPosition) {
		this.yPosition = yPosition;
	}

	public String getVariableNameToCompare() {
		return variableNameToCompare;
	}

	public Sprite getSprite() {
		return sprite;
	}
}
