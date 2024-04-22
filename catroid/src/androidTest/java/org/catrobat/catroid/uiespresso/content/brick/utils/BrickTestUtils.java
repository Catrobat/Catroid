/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import static org.koin.java.KoinJavaComponent.inject;

import androidx.test.core.app.ApplicationProvider;

import static org.koin.java.KoinJavaComponent.inject;

public final class BrickTestUtils {
	private BrickTestUtils() {
		throw new AssertionError();
	}

	public static Script createEmptyCastProjectAndGetStartScript(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName, false, true);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
		projectManager.setCurrentSprite(sprite);
		projectManager.setCurrentlyEditedScene(project.getDefaultScene());
		return script;
	}
}
