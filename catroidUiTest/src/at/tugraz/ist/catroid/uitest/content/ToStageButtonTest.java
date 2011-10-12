/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 * 
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.uitest.content;

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class ToStageButtonTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	private final String projectNameThree = UiTestUtils.PROJECTNAME1;
	private final String spriteNameTwo = "Balmung";

	public ToStageButtonTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();

		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testToStageButton() {
		//What does this test exactly it's quite a big test...

		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.sleep(300);
		solo.enterText(0, projectNameThree);
		solo.goBack();
		//		solo.setActivityOrientation(Solo.LANDSCAPE);
		//		solo.sleep(600);
		//		solo.setActivityOrientation(Solo.PORTRAIT);
		solo.clickOnButton(0);

		solo.sleep(500);
		solo.clickOnText(getActivity().getString(R.string.background));
		solo.sleep(500);

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.assertCurrentActivity("Not in stage", StageActivity.class);
		List<Sprite> sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		Sprite background = sprite_list.get(0);
		assertEquals("Background sprite has too many scripts", 1, background.getNumberOfScripts());

		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.back_to_construction_site));
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_when);
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_set_size_to);
		solo.sleep(100);

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.sleep(100);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		solo.assertCurrentActivity("Not in stage", StageActivity.class);
		assertEquals("Script list has wrong number of elements", 2, sprite_list.get(0).getNumberOfScripts());

		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.back_to_construction_site));
		solo.clickLongOnText(getActivity().getString(R.string.brick_when));
		solo.clickOnText(getActivity().getString(R.string.delete));

		solo.sleep(100);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.assertCurrentActivity("Not in stage", StageActivity.class);
		assertEquals("Script list has wrong number of elements", 1, sprite_list.get(0).getNumberOfScripts());

		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.back_to_construction_site));
		solo.goBack();
		solo.sleep(100);

		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_add_sprite);

		solo.enterText(0, spriteNameTwo);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.ok));
		solo.clickOnText(spriteNameTwo);

		solo.sleep(100);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.assertCurrentActivity("Not in stage", StageActivity.class);
		assertEquals("Script list has wrong number of elements", 1, sprite_list.get(0).getNumberOfScripts());

		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.back_to_construction_site));
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_when);
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_set_size_to);
		solo.sleep(100);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_play);

		solo.sleep(100);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		solo.assertCurrentActivity("Not in stage", StageActivity.class);
		assertEquals("Script list has wrong number of elements", 2, sprite_list.get(1).getNumberOfScripts());
		assertTrue("Is not correct Block",
				sprite_list.get(2).getScript(0).getBrickList().get(0) instanceof SetSizeToBrick);

		solo.goBack();
		solo.clickOnText(getActivity().getString(R.string.back_to_construction_site));
		solo.sleep(100);
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_home);

		solo.clickOnButton(getActivity().getString(R.string.my_projects));
		solo.clickOnText(projectNameThree);

		solo.clickOnText(spriteNameTwo);
		solo.sleep(100);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertEquals("Script list has wrong number of elements", 2, sprite_list.get(1).getNumberOfScripts());
		assertTrue("Is not correct Block",
				sprite_list.get(2).getScript(0).getBrickList().get(0) instanceof SetSizeToBrick);
	}
}
