/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.test.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.controller.BackpackListManager;

import java.io.File;
import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;

public final class TestUtils {

	public static final String DEFAULT_TEST_PROJECT_NAME = "testProject";
	public static final String DEFAULT_TEST_SPRITE_NAME = "testSprite";

	public static final double DELTA = 0.00001;

	private TestUtils() {
		throw new AssertionError();
	}

	public static void deleteProjects(String... projectNames) throws IOException {
		for (String projectName : projectNames) {
			File projectDir = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName);
			if (projectDir.exists() && projectDir.isDirectory()) {
				StorageOperations.deleteDir(projectDir);
			}
		}
	}

	public static Project createProjectWithLanguageVersion(double catrobatLanguageVersion,
			String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		project.setCatrobatLanguageVersion(catrobatLanguageVersion);

		Sprite firstSprite = new Sprite("cat");
		Script testScript = new StartScript();
		Brick testBrick = new HideBrick();
		testScript.addBrick(testBrick);

		firstSprite.addScript(testScript);
		project.getDefaultScene().addSprite(firstSprite);

		XstreamSerializer.getInstance().saveProject(project);
		return project;
	}

	public static void removeFromPreferences(Context context, String key) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = preferences.edit();
		edit.remove(key);
		edit.commit();
	}

	public static Pixmap createRectanglePixmap(int width, int height, Color color) {
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixmap.setColor(color);
		pixmap.fillRectangle(0, 0, width, height);
		return pixmap;
	}

	public static File createSoundFile(Project project, int source, String soundName) throws IOException {
		return ResourceImporter
				.createSoundFileFromResourcesInDirectory(InstrumentationRegistry.getInstrumentation().getContext().getResources(),
						source, new File(project.getDefaultScene().getDirectory(),
								SOUND_DIRECTORY_NAME), soundName);
	}

	public static boolean clearBackPack(BackpackListManager backpackListManager) throws IOException {
		if (backpackListManager.backpackImageDirectory.exists()) {
			StorageOperations.deleteDir(backpackListManager.backpackImageDirectory);
		}
		if (backpackListManager.backpackSoundDirectory.exists()) {
			StorageOperations.deleteDir(backpackListManager.backpackSoundDirectory);
		}
		if (backpackListManager.backpackSceneDirectory.exists()) {
			StorageOperations.deleteDir(backpackListManager.backpackSceneDirectory);
		}
		return backpackListManager.backpackSceneDirectory.mkdirs()
				&& backpackListManager.backpackImageDirectory.mkdirs()
				&& backpackListManager.backpackSoundDirectory.mkdirs();
	}
}
