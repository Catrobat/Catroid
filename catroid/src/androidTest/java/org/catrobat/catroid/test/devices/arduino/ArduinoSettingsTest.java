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

package org.catrobat.catroid.test.devices.arduino;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.exceptions.ProjectException;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.asynctask.ProjectSaveTask;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ArduinoSettingsTest {

	private boolean sharedPreferenceBuffer;
	private String projectName = ArduinoSettingsTest.class.getSimpleName();
	private Project project;

	@Before
	public void setUp() throws Exception {
		Context context = InstrumentationRegistry.getTargetContext();
		sharedPreferenceBuffer = SettingsFragment.isArduinoSharedPreferenceEnabled(context);
		SettingsFragment.setArduinoSharedPreferenceEnabled(context, false);
		createProjectArduino();
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(projectName);
		SettingsFragment
				.setArduinoSharedPreferenceEnabled(InstrumentationRegistry.getTargetContext(), sharedPreferenceBuffer);
	}

	@Test
	public void testIfArduinoBricksAreEnabledIfItItUsedInAProgram() throws IOException, ProjectException {
		Context context = InstrumentationRegistry.getTargetContext();

		assertFalse(SettingsFragment.isArduinoSharedPreferenceEnabled(context));

		ProjectManager.getInstance().loadProject(project.getDirectory(), context);

		assertTrue(SettingsFragment.isArduinoSharedPreferenceEnabled(context));

		StorageOperations.deleteDir(new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, projectName));
	}

	private void createProjectArduino() {
		project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new SingleSprite("Arduino");

		StartScript startScript = new StartScript();
		ArduinoSendPWMValueBrick brick = new ArduinoSendPWMValueBrick(3, 255);
		startScript.addBrick(brick);
		sprite.addScript(startScript);

		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectSaveTask
				.task(project, InstrumentationRegistry.getTargetContext());
	}
}
