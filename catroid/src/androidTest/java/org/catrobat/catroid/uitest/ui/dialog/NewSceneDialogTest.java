/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.uitest.ui.dialog;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class NewSceneDialogTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Project testingproject;

	public NewSceneDialogTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		String testingprojectName = UiTestUtils.PROJECTNAME1;
		UiTestUtils.createTestProject(testingprojectName);
		testingproject = ProjectManager.getInstance().getCurrentProject();
		String testingsceneName = "testingscene";
		Scene testingscene = new Scene(null, testingsceneName, testingproject);
		testingproject.addScene(testingscene);
		StorageHandler.getInstance().saveProject(testingproject);
		ProjectManager.getInstance().loadProject(testingprojectName, getActivity());
		UiTestUtils.getIntoScenesFromMainMenu(solo);
	}

	public void testNewSceneDialogEmpty() {
		testingproject = ProjectManager.getInstance().getCurrentProject();
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		String defaultSceneName = String.format(solo.getString(R.string.default_scene_name), testingproject
				.getSceneList().size());
		assertTrue("Default name should be " + defaultSceneName, solo.waitForText(defaultSceneName));

		solo.clickOnText(solo.getString(R.string.ok));

		solo.sleep(1000);

		Scene newScene = testingproject.getSceneByName(defaultSceneName);
		assertNotNull("New scene was not added", newScene);
		assertTrue("New Scene is not empty", newScene.getSpriteList().size() == 1 && newScene.getSpriteList().get(0)
				.getListWithAllBricks().size() == 0 && newScene.getSpriteList().get(0).getSoundList().size() == 0
				&& newScene.getSpriteList().get(0).getLookDataList().size() == 0);
	}
}
