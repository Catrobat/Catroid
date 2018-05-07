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

package org.catrobat.catroid.test.devices.phiro;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.io.File;
import java.io.IOException;

public class PhiroSettingsTest extends InstrumentationTestCase {

	private String projectName = "testProject";
	Context context = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		context = this.getInstrumentation().getTargetContext();
		SettingsFragment.setPhiroSharedPreferenceEnabled(context, false);
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsFragment.setPhiroSharedPreferenceEnabled(context, false);
		super.tearDown();
	}

	public void testIfPhiroBricksAreEnabledIfItItUsedInAProgram() throws IOException, CompatibilityProjectException,
			OutdatedVersionProjectException, LoadingProjectException, InterruptedException {

		createProjectPhiro();

		assertFalse("By default phiro should be disabled",
				SettingsFragment.isPhiroSharedPreferenceEnabled(context));

		ProjectManager.getInstance().loadProject(projectName, context);

		assertTrue("After loading a project which needs phiro it should be enabled",
				SettingsFragment.isPhiroSharedPreferenceEnabled(context));

		StorageOperations.deleteDir(new File(Constants.DEFAULT_ROOT_DIRECTORY, projectName));
	}

	private void createProjectPhiro() throws InterruptedException {
		Project projectPhiro = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new SingleSprite("Phiro");
		StartScript startScript = new StartScript();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(new Formula(new FormulaElement(FormulaElement.ElementType.SENSOR,
				Sensors.PHIRO_BOTTOM_LEFT.name(), null)));
		startScript.addBrick(setSizeToBrick);
		sprite.addScript(startScript);
		projectPhiro.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(projectPhiro);
		ProjectManager.getInstance().saveProject(context);
		Thread.sleep(100);
		ProjectManager.getInstance().setProject(null);
	}
}
