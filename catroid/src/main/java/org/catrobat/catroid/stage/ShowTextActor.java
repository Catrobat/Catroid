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

package org.catrobat.catroid.stage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;

import java.util.List;
import java.util.Map;

public class ShowTextActor extends Actor {

	private int xPosition;
	private int yPosition;
	private UserVariable variableToShow;
	private String variableNameToCompare;
	private String variableValue;
	private String variableValueWithoutDecimal;

	private Sprite sprite;
	private UserBrick userBrick;
	private float scale = 3f;

	public ShowTextActor(UserVariable userVariable, int xPosition, int yPosition, Sprite sprite, UserBrick userBrick) {
		this.variableToShow = userVariable;
		this.variableNameToCompare = variableToShow.getName();
		this.variableValueWithoutDecimal = null;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.sprite = sprite;
		this.userBrick = userBrick;
	}

	public static String convertToEnglishDigits(String value) {
		return value
				// Eastern-Arabic ٠
				.replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5")
				.replace("٦", "6").replace("٧", "7").replace("٨", "8").replace("٩", "9").replace("٠", "0")
				// Farsi
				.replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4").replace("۵", "5")
				.replace("۶", "6").replace("۷", "7").replace("۸", "8").replace("۹", "9").replace("۰", "0")
				// Hindi
				.replace("१", "1").replace("२", "2").replace("३", "3").replace("४", "4").replace("५", "5")
				.replace("६", "6").replace("७", "7").replace("८", "8").replace("९", "9").replace("०", "0")
				// Assamese and Bengali
				.replace("১", "1").replace("২", "2").replace("৩", "3").replace("৪", "4").replace("৫", "5")
				.replace("৬", "6").replace("৭", "7").replace("৮", "8").replace("৯", "9").replace("০", "0")
				// Tamil
				.replace("௧", "1").replace("௦", "0").replace("௨", "2").replace("௩", "3").replace("௪", "4")
				.replace("௫", "5").replace("௬", "6").replace("௭", "7").replace("௮", "8").replace("௯", "9")
				// Gujarati
				.replace("૧", "1").replace("૨", "2").replace("૩", "3").replace("૪", "4").replace("૫", "5")
				.replace("૬", "6").replace("૭", "7").replace("૮", "8").replace("૯", "9").replace("૦", "0");
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		DataContainer dataContainer = ProjectManager.getInstance().getSceneToPlay().getDataContainer();

		List<UserVariable> projectVariableList = dataContainer.getProjectVariables();
		Map<Sprite, List<UserVariable>> spriteVariableMap = dataContainer.getSpriteVariableMap();
		List<UserVariable> spriteVariableList = spriteVariableMap.get(sprite);
		List<UserVariable> userBrickVariableList = dataContainer.getOrCreateVariableListForUserBrick(userBrick);

		drawVariables(projectVariableList, batch);
		drawVariables(spriteVariableList, batch);
		drawVariables(userBrickVariableList, batch);
	}

	private void drawVariables(List<UserVariable> variableList, Batch batch) {
		if (variableList == null) {
			return;
		}

		if (variableToShow.isDummy()) {
			drawText(batch, Constants.NO_VARIABLE_SELECTED, xPosition, yPosition);
		} else {
			for (UserVariable variable : variableList) {
				if (variable.getName().equals(variableToShow.getName())) {
					variableValue = variable.getValue().toString();
					if (variable.getVisible()) {
						if (isNumberAndInteger(variableValue)) {
							drawText(batch, variableValueWithoutDecimal, xPosition, yPosition);
						} else {
							drawText(batch, variableValue, xPosition, yPosition);
						}
					}
					break;
				}
			}
		}
	}

	private boolean isNumberAndInteger(String variableValue) {
		double variableValueIsNumber = 0;

		if (variableValue.matches("-?\\d+(\\.\\d+)?")) {
			variableValueIsNumber = Double.parseDouble(convertToEnglishDigits(variableValue));
		} else {
			return false;
		}

		if (((int) variableValueIsNumber) - variableValueIsNumber == 0) {
			variableValueWithoutDecimal = Integer.toString((int) variableValueIsNumber);
			return true;
		} else {
			return false;
		}
	}

	private void drawText(Batch batch, String text, float posX, float posY) {
		// Convert to bitmap
		Paint paint = new Paint();
		paint.setTextSize(17 * scale);
		paint.setColor(android.graphics.Color.BLACK);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.LEFT);
		float baseline = -paint.ascent();
		int width = (int) (paint.measureText(text) + 0.6f);
		int height = (int) (baseline + paint.descent() + 0.6f);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawText(text, 0, baseline, paint);
		// Convert to texture
		Texture tex = new Texture(bitmap.getWidth(), bitmap.getHeight(),
				Pixmap.Format.RGBA8888);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
				tex.getTextureObjectHandle());
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		bitmap.recycle();
		// Draw and dispose
		batch.draw(tex, posX, posY);
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

	public UserBrick getUserBrick() {
		return userBrick;
	}
}
