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
package org.catrobat.catroid.uiespresso.util.rules;

import android.app.Activity;

import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.io.XstreamSerializer;

import androidx.test.core.app.ApplicationProvider;

public class DontGenerateDefaultProjectActivityTestRule<T extends Activity> extends
		BaseActivityTestRule<T> {

	public DontGenerateDefaultProjectActivityTestRule(Class<T> activityClass, boolean initialTouchMode, boolean launchActivity) {
		super(activityClass, initialTouchMode, launchActivity);
	}

	public DontGenerateDefaultProjectActivityTestRule(Class<T> activityClass, boolean initialTouchMode) {
		super(activityClass, initialTouchMode);
	}

	public DontGenerateDefaultProjectActivityTestRule(Class<T> activityClass) {
		super(activityClass);
	}

	@Override
	protected void beforeActivityLaunched() {
		super.beforeActivityLaunched();
		setUpDummyProject();
	}

	void setUpDummyProject() {
		FlavoredConstants.DEFAULT_ROOT_DIRECTORY.mkdir();
		Project project = new Project(ApplicationProvider.getApplicationContext(),
				"DummyToPreventDefaultProjectCreation");
		XstreamSerializer.getInstance().saveProject(project);
	}
}
