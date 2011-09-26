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

package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.BroadcastBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastWaitBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeBrightnessBrick;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class BrickExceptionOnDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;
	private Project project;
	private Sprite sprite;
	private Script script;
	private final String spriteName = "cat";

	public BrickExceptionOnDialogTest() {
		super("at.tugraz.ist.catroid", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		createProject();
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		super.tearDown();
	}

	public void testBroadCastBrick() {
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite);
		script.addBrick(broadcastBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.new_broadcast_message));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.new_broadcast_message));
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testBroadcastWaitBrick() {
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite);
		script.addBrick(broadcastWaitBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.new_broadcast_message));
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnText(getActivity().getString(R.string.new_broadcast_message));
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void testChangeBrightnessBrick() {
		ChangeBrightnessBrick brightnessBrick = new ChangeBrightnessBrick(sprite, 40);
		script.addBrick(brightnessBrick);

		solo.clickOnText(getActivity().getString(R.string.current_project_button));
		solo.clickOnText(spriteName);
		solo.clickOnEditText(0);
		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.assertCurrentActivity("not in ProjectActivity", ProjectActivity.class);
		solo.clickOnText(spriteName);
		solo.clickOnEditText(0);
		solo.assertCurrentActivity("not in scripttabactivity", ScriptTabActivity.class);
	}

	public void createProject() {
		sprite = new Sprite(spriteName);
		script = new StartScript("script", sprite);
		sprite.addScript(script);
		ProjectManager manager = ProjectManager.getInstance();
		manager.fileChecksumContainer = new FileChecksumContainer();
		manager.setProject(project);
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(sprite);
		project = UiTestUtils.createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, spriteList, getActivity());
	}
}
