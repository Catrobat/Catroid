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

package org.catrobat.catroid.uiespresso.util.wrappers;

import android.view.View;

import org.hamcrest.Matcher;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.AdapterViewProtocol;

public abstract class DataInteractionWrapper {

	protected DataInteraction dataInteraction;

	public DataInteractionWrapper(DataInteraction dataInteraction) {
		this.dataInteraction = dataInteraction;
	}

	public final DataInteraction onChildView(Matcher<View> childMatcher) {
		return dataInteraction.onChildView(childMatcher);
	}

	public final DataInteraction inRoot(Matcher<Root> rootMatcher) {
		return dataInteraction.inRoot(rootMatcher);
	}

	public final DataInteraction inAdapterView(Matcher<View> adapterMatcher) {
		return dataInteraction.inAdapterView(adapterMatcher);
	}

	public final DataInteraction atPosition(Integer atPosition) {
		return dataInteraction.atPosition(atPosition);
	}

	public final DataInteraction usingAdapterViewProtocol(AdapterViewProtocol adapterViewProtocol) {
		return dataInteraction.usingAdapterViewProtocol(adapterViewProtocol);
	}

	public final ViewInteraction perform(ViewAction... actions) {
		return dataInteraction.perform(actions);
	}

	public final ViewInteraction check(ViewAssertion assertion) {
		return dataInteraction.check(assertion);
	}
}
