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

package org.catrobat.catroid.uiespresso.content.brick.utils;

import org.catrobat.catroid.uiespresso.util.wrappers.DataInteractionWrapper;

import androidx.test.espresso.DataInteraction;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class BrickFormulaEditTextDataInteractionWrapper extends DataInteractionWrapper {
	public BrickFormulaEditTextDataInteractionWrapper(DataInteraction dataInteraction) {
		super(dataInteraction);
	}

	public BrickFormulaEditTextDataInteractionWrapper checkShowsText(String text) {
		dataInteraction.check(matches(withText("'" + text + "' ")));
		return new BrickFormulaEditTextDataInteractionWrapper(dataInteraction);
	}

	public <V extends Number> BrickFormulaEditTextDataInteractionWrapper checkShowsNumber(V value) {
		dataInteraction.check(matches(withText((value + " ").replace("-", "- "))));
		return new BrickFormulaEditTextDataInteractionWrapper(dataInteraction);
	}

	public <V extends Number> BrickFormulaEditTextDataInteractionWrapper performEnterNumber(V valueToBeEntered) {
		dataInteraction.perform(click());

		onFormulaEditor()
				.performEnterNumber(valueToBeEntered);

		pressBack();

		return new BrickFormulaEditTextDataInteractionWrapper(dataInteraction);
	}

	public BrickFormulaEditTextDataInteractionWrapper performEnterString(String stringToBeEntered) {
		dataInteraction.perform(click());

		onFormulaEditor()
				.performEnterString(stringToBeEntered);

		pressBack();

		return new BrickFormulaEditTextDataInteractionWrapper(dataInteraction);
	}

	public BrickFormulaEditTextDataInteractionWrapper performEnterString(int stringResourceId) {
		dataInteraction.perform(click());

		onFormulaEditor()
				.performEnterString(stringResourceId);

		pressBack();

		return new BrickFormulaEditTextDataInteractionWrapper(dataInteraction);
	}
}
