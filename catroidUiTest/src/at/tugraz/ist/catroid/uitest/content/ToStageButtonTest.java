/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.uitest.content;

import java.io.File;
import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class ToStageButtonTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	private final String projectNameThree = "Ulumulu3";
	private final String spriteNameTwo = "Balmung";

	public ToStageButtonTest() {
		super("at.tugraz.ist.catroid.ui", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		File directory = new File("/sdcard/catroid/" + projectNameThree);
		UtilFile.deleteDirectory(directory);

		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		File directory = new File("/sdcard/catroid/" + projectNameThree);
		UtilFile.deleteDirectory(directory);
		assertFalse(projectNameThree + " was not deleted!", directory.exists());

		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();

		super.tearDown();
	}

	public void testToStageButton() throws InterruptedException {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.clickOnEditText(0);
		solo.enterText(0, projectNameThree);
		solo.goBack();

		solo.clickOnButton(getActivity().getString(R.string.new_project_dialog_button));
		Thread.sleep(500);
		solo.clickOnText(getActivity().getString(R.string.stage));
		solo.clickOnButton(getActivity().getString(R.string.construction_site_play));

		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
		List<Sprite> sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertEquals("Script list has to much elements", 0, sprite_list.get(0).getScriptList().size());

		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.touched_main_adapter));
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.brick_scale_costume));
		solo.clickOnButton(getActivity().getString(R.string.construction_site_play));

		Thread.sleep(500);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
		assertEquals("Script list has wrong number of elements", 1, sprite_list.get(0).getScriptList().size());
		assertTrue("Is not correct Block",
				sprite_list.get(0).getScriptList().get(0).getBrickList().get(0) instanceof ScaleCostumeBrick);

		solo.goBack();
		solo.clickLongOnText(getActivity().getString(R.string.touched_main_adapter));
		solo.clickOnText(getActivity().getString(R.string.delete_script_button));

		Thread.sleep(500);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		solo.clickOnButton(getActivity().getString(R.string.construction_site_play));

		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
		assertEquals("Script list has wrong number of elements", 0, sprite_list.get(0).getScriptList().size());

		solo.goBack();
		solo.goBack();

		solo.clickOnButton(getActivity().getString(R.string.add_sprite));
		solo.clickOnEditText(0);
		solo.enterText(0, spriteNameTwo);

		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_sprite_dialog_button));
		solo.clickOnText(spriteNameTwo);

		Thread.sleep(500);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		solo.clickOnButton(getActivity().getString(R.string.construction_site_play));

		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
		assertEquals("Script list has wrong number of elements", 0, sprite_list.get(0).getScriptList().size());

		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.touched_main_adapter));
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.brick_scale_costume));
		solo.clickOnButton(getActivity().getString(R.string.construction_site_play));

		Thread.sleep(500);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
		assertEquals("Script list has wrong number of elements", 1, sprite_list.get(1).getScriptList().size());
		assertTrue("Is not correct Block",
				sprite_list.get(1).getScriptList().get(0).getBrickList().get(0) instanceof ScaleCostumeBrick);

		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.main_menu));

		solo.clickOnButton(getActivity().getString(R.string.load_project));
		solo.clickOnText(projectNameThree);

		solo.clickOnText(spriteNameTwo);
		Thread.sleep(500);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertEquals("Script list has wrong number of elements", 1, sprite_list.get(1).getScriptList().size());
		assertTrue("Is not correct Block",
				sprite_list.get(1).getScriptList().get(0).getBrickList().get(0) instanceof ScaleCostumeBrick);

	}
}
