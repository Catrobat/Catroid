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

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.Utils;

import com.jayway.android.robotium.solo.Solo;

public class ToStageButtonTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

	private final String projectNameThree = Utils.PROJECTNAME1;
	private final String spriteNameTwo = "Balmung";

	public ToStageButtonTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		Utils.clearAllUtilTestProjects();

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
		Utils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testToStageButton() {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.clickOnEditText(0);
		solo.enterText(0, projectNameThree);
		solo.goBack();

		solo.clickOnButton(getActivity().getString(R.string.new_project_dialog_button));
		solo.sleep(500);
		solo.clickOnText(getActivity().getString(R.string.stage));
		solo.sleep(500);
		List<ImageButton> btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_play) {
				solo.clickOnImageButton(i);
			}
		}

		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
		List<Sprite> sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertEquals("Script list has to much elements", 0, sprite_list.get(0).getScriptList().size());

		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.brick_if_touched));
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.brick_scale_costume));

		btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_play) {
				solo.clickOnImageButton(i);
			}
		}

		solo.sleep(500);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
		assertEquals("Script list has wrong number of elements", 1, sprite_list.get(0).getScriptList().size());
		assertTrue("Is not correct Block",
				sprite_list.get(0).getScriptList().get(0).getBrickList().get(0) instanceof ScaleCostumeBrick);

		solo.goBack();
		solo.clickLongOnText(getActivity().getString(R.string.brick_if_touched));
		solo.clickOnText(getActivity().getString(R.string.delete_script_button));

		solo.sleep(500);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_play) {
				solo.clickOnImageButton(i);
			}
		}

		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
		assertEquals("Script list has wrong number of elements", 0, sprite_list.get(0).getScriptList().size());

		solo.goBack();
		solo.goBack();
		solo.sleep(500);

		btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_add_sprite) {
				solo.clickOnImageButton(i);
			}
		}
		solo.clickOnEditText(0);
		solo.enterText(0, spriteNameTwo);

		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_sprite_dialog_button));
		solo.clickOnText(spriteNameTwo);

		solo.sleep(500);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_play) {
				solo.clickOnImageButton(i);
			}
		}

		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
		assertEquals("Script list has wrong number of elements", 0, sprite_list.get(0).getScriptList().size());

		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.brick_if_touched));
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(getActivity().getString(R.string.brick_scale_costume));
		btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_play) {
				solo.clickOnImageButton(i);
			}
		}

		solo.sleep(500);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertTrue("Not in stage", solo.getCurrentActivity() instanceof StageActivity);
		assertEquals("Script list has wrong number of elements", 1, sprite_list.get(1).getScriptList().size());
		assertTrue("Is not correct Block",
				sprite_list.get(1).getScriptList().get(0).getBrickList().get(0) instanceof ScaleCostumeBrick);

		solo.goBack();
		btnList = solo.getCurrentImageButtons();
		for (int i = 0; i < btnList.size(); i++) {
			ImageButton btn = btnList.get(i);
			if (btn.getId() == R.id.btn_action_home) {
				solo.clickOnImageButton(i);
			}
		}

		solo.clickOnButton(getActivity().getString(R.string.projects_on_phone));
		solo.clickOnText(projectNameThree);

		solo.clickOnText(spriteNameTwo);
		solo.sleep(500);
		sprite_list = ProjectManager.getInstance().getCurrentProject().getSpriteList();
		assertEquals("Script list has wrong number of elements", 1, sprite_list.get(1).getScriptList().size());
		assertTrue("Is not correct Block",
				sprite_list.get(1).getScriptList().get(0).getBrickList().get(0) instanceof ScaleCostumeBrick);
	}
}