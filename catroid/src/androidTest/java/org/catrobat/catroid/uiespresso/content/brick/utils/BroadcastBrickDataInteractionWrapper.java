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

package org.catrobat.catroid.uiespresso.content.brick.utils;

import android.support.test.espresso.DataInteraction;
import android.widget.Spinner;

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.uiespresso.util.matchers.ScriptListMatchers;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.instanceOf;

public class BroadcastBrickDataInteractionWrapper extends BrickDataInteractionWrapper {

	public BroadcastBrickDataInteractionWrapper(DataInteraction dataInteraction) {
		super(dataInteraction);
	}

	public static BroadcastBrickDataInteractionWrapper onBroadcastBrickAtPosition(int position) {
		return new BroadcastBrickDataInteractionWrapper(onData(instanceOf(Brick.class))
				.inAdapterView(ScriptListMatchers.isScriptListView())
				.atPosition(position));
	}

	public BroadcastBrickSpinnerDataInteractionWrapper onSpinner(int spinnerResourceId) {
		dataInteraction.onChildView(withId(spinnerResourceId)).check(matches(instanceOf(Spinner.class)));
		return new BroadcastBrickSpinnerDataInteractionWrapper(
				dataInteraction.onChildView(withId(spinnerResourceId)));
	}
}
