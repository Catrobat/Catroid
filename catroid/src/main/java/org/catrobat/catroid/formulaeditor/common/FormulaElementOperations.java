/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor.common;

import android.content.res.Resources;

import com.badlogic.gdx.math.Rectangle;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.nfc.NfcHandler;
import org.catrobat.catroid.sensing.CollisionDetection;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.catrobat.catroid.utils.TouchUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import static org.catrobat.catroid.formulaeditor.common.Conversions.FALSE;
import static org.catrobat.catroid.formulaeditor.common.Conversions.TRUE;
import static org.catrobat.catroid.utils.NumberFormats.trimTrailingCharacters;

public final class FormulaElementOperations {
	private FormulaElementOperations() {
	}

	public static double getLookLayerIndex(Sprite sprite, Look look, List<Sprite> spriteList) {
		int lookZIndex = look.getZIndex();
		if (lookZIndex == 0) {
			return 0;
		} else if (lookZIndex < 0) {
			return spriteList.indexOf(sprite);
		} else {
			return (double) lookZIndex - Constants.Z_INDEX_NUMBER_VIRTUAL_LAYERS;
		}
	}

	public static boolean equalsDoubleIEEE754(double left, double right) {
		return Double.isNaN(left) && Double.isNaN(right)
				|| !Double.isNaN(left) && !Double.isNaN(right) && left >= right && left <= right;
	}

	public static boolean interpretOperatorEqual(Object left, Object right) {
		String leftString = String.valueOf(left);
		String rightString = String.valueOf(right);
		try {
			double tempLeft = Double.parseDouble(leftString);
			double tempRight = Double.parseDouble(rightString);
			return equalsDoubleIEEE754(tempLeft, tempRight);
		} catch (NumberFormatException numberFormatException) {
			return leftString.equals(rightString);
		}
	}

	public static double tryInterpretDoubleValue(Object object) {
		if (object instanceof String) {
			try {
				return Double.valueOf((String) object);
			} catch (NumberFormatException numberFormatException) {
				return Double.NaN;
			}
		} else {
			return (double) object;
		}
	}

	public static Object normalizeDegeneratedDoubleValues(Object value) {

		if (value instanceof String || value instanceof Character) {
			return value;
		}

		if (value == null) {
			return 0.0;
		}

		if ((Double) value == Double.NEGATIVE_INFINITY) {
			return -Double.MAX_VALUE;
		}
		if ((Double) value == Double.POSITIVE_INFINITY) {
			return Double.MAX_VALUE;
		}

		return value;
	}

	public static boolean isInteger(double value) {
		return !Double.isInfinite(value) && !Double.isNaN(value) && value == Math.rint(value);
	}

	public static double tryGetLookBackgroundNumber(LookData lookData, List<LookData> lookDataList) {
		if (lookData == null) {
			return 1;
		}
		return lookDataList.indexOf(lookData) + 1d;
	}

	public static String getLookBackgroundName(LookData lookData) {
		if (lookData == null) {
			return "";
		}
		return lookData.getName();
	}

	public static double tryCalculateCollidesWithEdge(Look look, StageListener stageListener,
			Rectangle screen) {
		if (stageListener == null || !stageListener.firstFrameDrawn) {
			return FALSE;
		}
		return CollisionDetection.collidesWithEdge(look, screen);
	}

	public static double calculateCollidesWithFinger(Look look) {
		return CollisionDetection.collidesWithFinger(look.getCurrentCollisionPolygon(),
				TouchUtil.getCurrentTouchingPoints());
	}

	public static Object interpretObjectSensor(Sensors sensor, Sprite sprite,
			Scene currentlyEditedScene, Project currentProject) {
		Look look = sprite.look;
		LookData lookData = look.getLookData();
		List<LookData> lookDataList = sprite.getLookList();
		if (lookData == null && !lookDataList.isEmpty()) {
			lookData = lookDataList.get(0);
		}
		switch (sensor) {
			case OBJECT_BRIGHTNESS:
				return (double) look.getBrightnessInUserInterfaceDimensionUnit();
			case OBJECT_COLOR:
				return (double) look.getColorInUserInterfaceDimensionUnit();
			case OBJECT_TRANSPARENCY:
				return (double) look.getTransparencyInUserInterfaceDimensionUnit();
			case OBJECT_LAYER:
				return getLookLayerIndex(sprite, look, currentlyEditedScene.getSpriteList());
			case OBJECT_ROTATION:
				return (double) look.getDirectionInUserInterfaceDimensionUnit();
			case OBJECT_SIZE:
				return (double) look.getSizeInUserInterfaceDimensionUnit();
			case OBJECT_X:
				return (double) look.getXInUserInterfaceDimensionUnit();
			case OBJECT_Y:
				return (double) look.getYInUserInterfaceDimensionUnit();
			case OBJECT_ANGULAR_VELOCITY:
				return (double) look.getAngularVelocityInUserInterfaceDimensionUnit();
			case OBJECT_X_VELOCITY:
				return (double) look.getXVelocityInUserInterfaceDimensionUnit();
			case OBJECT_Y_VELOCITY:
				return (double) look.getYVelocityInUserInterfaceDimensionUnit();
			case OBJECT_DISTANCE_TO:
				return (double) look.getDistanceToTouchPositionInUserInterfaceDimensions();
			case OBJECT_LOOK_NUMBER:
			case OBJECT_BACKGROUND_NUMBER:
				return tryGetLookBackgroundNumber(lookData, lookDataList);
			case OBJECT_LOOK_NAME:
			case OBJECT_BACKGROUND_NAME:
				return getLookBackgroundName(lookData);
			case NFC_TAG_MESSAGE:
				return NfcHandler.getLastNfcTagMessage();
			case NFC_TAG_ID:
				return NfcHandler.getLastNfcTagId();
			case COLLIDES_WITH_EDGE:
				Rectangle screen = currentProject.getScreenRectangle();
				return tryCalculateCollidesWithEdge(look, StageActivity.stageListener, screen);
			case COLLIDES_WITH_FINGER:
				return calculateCollidesWithFinger(look);
			default:
				return FALSE;
		}
	}

	@NotNull
	public static List<Sprite> getAllClones(Sprite sprite, StageListener stageListener) {
		List<Sprite> spriteAndClones = new ArrayList<>();
		spriteAndClones.add(sprite);
		if (stageListener != null) {
			spriteAndClones.addAll(stageListener.getAllClonesOfSprite(sprite));
		}
		return spriteAndClones;
	}

	public static Object interpretUserList(UserList userList) {
		if (userList == null) {
			return FALSE;
		}

		return interpretUserListValues(userList.getValue());
	}

	private static Object interpretUserListValues(List<Object> userListValues) {
		if (userListValues.isEmpty()) {
			return "";
		} else if (userListValues.size() == 1) {
			return userListValues.get(0);
		} else {
			return interpretMultipleItemsUserList(userListValues);
		}
	}

	private static Object interpretMultipleItemsUserList(List<Object> userListValues) {
		List<String> userListStringValues = new ArrayList<>();

		for (Object listValue : userListValues) {
			if (listValue instanceof Double) {
				Double doubleValueOfListItem = (Double) listValue;
				userListStringValues.add(trimTrailingCharacters(String.valueOf(doubleValueOfListItem.intValue())));
			} else if (listValue instanceof String) {
				String stringValueOfListItem = (String) listValue;
				userListStringValues.add(trimTrailingCharacters(stringValueOfListItem));
			}
		}

		StringBuilder stringBuilder = new StringBuilder(userListStringValues.size());
		String separator = listConsistsOfSingleCharacters(userListStringValues) ? "" : " ";
		for (String userListStringValue : userListStringValues) {
			stringBuilder.append(trimTrailingCharacters(userListStringValue));
			stringBuilder.append(separator);
		}

		return stringBuilder.toString().trim();
	}

	private static boolean listConsistsOfSingleCharacters(List<String> userListStringValues) {
		for (String userListStringValue : userListStringValues) {
			if (userListStringValue.length() > 1) {
				return false;
			}
		}
		return true;
	}

	public static Object interpretUserVariable(UserVariable userVariable) {
		if (userVariable == null) {
			return FALSE;
		}
		return userVariable.getValue();
	}

	public static double interpretLookCollision(Look look, List<Look> looks) {
		for (Look secondLook : looks) {
			if (look.equals(secondLook)) {
				continue;
			}

			if (CollisionDetection.checkCollisionBetweenLooks(look, secondLook) == TRUE) {
				return TRUE;
			}
		}

		return FALSE;
	}

	public static List<Look> toLooks(List<Sprite> sprites) {
		List<Look> looks = new ArrayList<>(sprites.size());
		for (Sprite sprite : sprites) {
			looks.add(sprite.look);
		}
		return looks;
	}

	public static Object interpretSensor(Sprite sprite, Scene currentlyEditedScene,
			Project currentProject, String value) {
		Sensors sensor = Sensors.getSensorByValue(value);
		if (sensor.isObjectSensor) {
			return interpretObjectSensor(sensor, sprite, currentlyEditedScene, currentProject);
		} else {
			return SensorHandler.getSensorValue(sensor);
		}
	}

	@Nullable
	public static Sprite tryFindSprite(Scene scene, String spriteName) {
		try {
			return scene.getSprite(spriteName);
		} catch (Resources.NotFoundException exception) {
			return null;
		}
	}

	public static double interpretCollision(Look firstLook, String secondSpriteName,
			Scene currentlyPlayingScene, StageListener stageListener) {
		Sprite secondSprite = tryFindSprite(currentlyPlayingScene, secondSpriteName);
		if (secondSprite == null) {
			return FALSE;
		} else if (secondSprite instanceof GroupSprite) {
			List<Sprite> spritesFromGroupWithGroupName = GroupSprite.getSpritesFromGroupWithGroupName(secondSpriteName, currentlyPlayingScene.getSpriteList());
			return interpretLookCollision(firstLook, toLooks(spritesFromGroupWithGroupName));
		} else {
			return interpretLookCollision(firstLook, toLooks(getAllClones(secondSprite, stageListener)));
		}
	}

	public static double tryInterpretCollision(Look firstLook, String secondSpriteName,
			Scene currentlyPlayingScene, StageListener stageListener) {
		try {
			return interpretCollision(firstLook, secondSpriteName, currentlyPlayingScene, stageListener);
		} catch (Exception e) {
			return FALSE;
		}
	}

	public static Object tryInterpretElementRecursive(FormulaElement element, Sprite sprite) {
		try {
			return element.interpretRecursive(sprite);
		} catch (NumberFormatException numberFormatException) {
			return Double.NaN;
		}
	}

	public static int tryParseIntFromObject(Object value) {
		if (value instanceof String) {
			return tryParseIntFromString((String) value);
		} else {
			return ((Double) value).intValue();
		}
	}

	private static int tryParseIntFromString(String value) {
		try {
			return Double.valueOf(value).intValue();
		} catch (NumberFormatException numberFormatexception) {
			return 0;
		}
	}
}
