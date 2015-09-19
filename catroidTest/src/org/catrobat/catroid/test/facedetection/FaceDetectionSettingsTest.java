/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.test.facedetection;

import android.content.Context;
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.IOException;

public class FaceDetectionSettingsTest extends InstrumentationTestCase {

	Context context = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		context = this.getInstrumentation().getTargetContext();
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(context, false);
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsActivity.setFaceDetectionSharedPreferenceEnabled(context, false);
		super.tearDown();
	}

	public void testIfFaceDetectionIsEnabledIfItIsUsedInAProgram() throws IOException, CompatibilityProjectException, OutdatedVersionProjectException, LoadingProjectException {

		createProjectFaceDetection();

		assertFalse("By default facedetection should be disabled",
				SettingsActivity.isFaceDetectionPreferenceEnabled(context));

		ProjectManager.getInstance().loadProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, context);

		assertTrue("After loading a project which needs facedetection it should be enabled",
				SettingsActivity.isFaceDetectionPreferenceEnabled(context));

		ProjectManager.getInstance().deleteProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, context);
	}

	private void createProjectFaceDetection() {
		Project projectFaceDetection = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("Facedetection!");
		StartScript startScript = new StartScript();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(new Formula(new FormulaElement(FormulaElement.ElementType.SENSOR,
				Sensors.FACE_SIZE.name(), null)));
		startScript.addBrick(setSizeToBrick);
		sprite.addScript(startScript);
		projectFaceDetection.addSprite(sprite);

		Reflection.setPrivateField(ProjectManager.getInstance(), "asynchronTask", false);
		ProjectManager.getInstance().setProject(projectFaceDetection);
		ProjectManager.getInstance().saveProject(context);
		ProjectManager.getInstance().setProject(null);
	}
}
