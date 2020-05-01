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

package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.TestResult;

import static org.catrobat.catroid.stage.TestResult.STAGE_ACTIVITY_TEST_FAIL;

public abstract class AssertAction extends Action {

	protected String position;
	protected Sprite sprite;
	protected String assertTitle = "\nAssertError\n";

	protected void failWith(String message) {
		StageActivity.finishTestWithResult(
				new TestResult(formattedPosition()
						+ assertTitle + message, STAGE_ACTIVITY_TEST_FAIL));
	}

	protected boolean equalValues(String actual, String expected) {
		try {
			return actual.equals(expected) || Double.parseDouble(actual) == Double.parseDouble(expected);
		} catch (NumberFormatException numberFormatException) {
			return false;
		}
	}

	protected int indexOfDifference(CharSequence actual, CharSequence expected) {
		if (actual == null || expected == null) {
			return 0;
		}
		int position;
		for (position = 0; position < actual.length() && position < expected.length(); ++position) {
			if (actual.charAt(position) != expected.charAt(position)) {
				break;
			}
		}

		return position;
	}

	protected String generateIndicator(Object actual, Object expected) {
		int errorPosition = indexOfDifference(actual.toString(), expected.toString());

		return String.valueOf(new char[errorPosition]).replace('\0', '-')
				+ "^";
	}

	private String formattedPosition() {
		return "on sprite \"" + sprite.getName() + "\"\n" + position;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setPosition(String position) {
		this.position = position;
	}
}
