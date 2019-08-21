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
package org.catrobat.catroid.uiespresso.facedetection;

import android.Manifest;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.NoteBrick;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

@RunWith(AndroidJUnit4.class)
public class FaceDetectionFormulaEditorComputeDialogTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Rule
	public GrantPermissionRule runtimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA);

	@Before
	public void setUp() throws Exception {
		createProject("FaceDetectionFormulaEditorComputeDialogTest");
		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void computeDialogFacedetectionResourceTest() {
		onView(withId(R.id.brick_note_edit_text))
				.perform(click());
		CameraManager.makeInstance();

		onFormulaEditor()
				.performClickOn(FormulaEditorWrapper.Control.COMPUTE);

		assertTrue(FaceDetectionHandler.isFaceDetectionRunning());

		pressBack();

		assertFalse(FaceDetectionHandler.isFaceDetectionRunning());

		onFormulaEditor()
				.performClickOn(FormulaEditorWrapper.NumPad.NUM0);

		onFormulaEditor()
				.performClickOn(FormulaEditorWrapper.Control.COMPUTE);

		assertFalse(FaceDetectionHandler.isFaceDetectionRunning());
	}

	public Project createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		Formula formula = new Formula(new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.FACE_SIZE.name(), null));
		script.addBrick(new NoteBrick(formula));

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		return project;
	}
}
