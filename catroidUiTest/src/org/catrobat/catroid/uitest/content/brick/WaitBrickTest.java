/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.content.brick;

import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class WaitBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private Solo solo;
	private Project project;
	private WaitBrick waitBrick;

	public WaitBrickTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testWaitBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = ProjectManager.getInstance().getCurrentSprite().getScript(adapter.getScriptCount() - 1)
				.getBrickList().size();
		assertEquals("Incorrect number of bricks.", 2, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getItem(1));
		assertNotNull("TextView does not exist", solo.getText(solo.getString(R.string.brick_wait)));

		double waitTime = 2.25;

		UiTestUtils.insertValueViaFormulaEditor(solo, 0, waitTime);

		Formula actualWaitTime = (Formula) Reflection.getPrivateField(waitBrick, "timeToWaitInSeconds");
		assertEquals("Wrong text in field", waitTime, (double) actualWaitTime.interpretDouble(null));
		assertEquals("Text not updated", waitTime, Double.parseDouble(solo.getEditText(0).getText().toString()));

		UiTestUtils.insertValueViaFormulaEditor(solo, 0, 1);
		TextView secondsTextView = (TextView) solo.getView(R.id.brick_wait_second_text_view);
		assertTrue(
				"Specifier hasn't changed from plural to singular",
				secondsTextView.getText().equals(
						secondsTextView.getResources().getQuantityString(R.plurals.second_plural, 1)));

		UiTestUtils.insertValueViaFormulaEditor(solo, 0, 1.4);
		secondsTextView = (TextView) solo.getView(R.id.brick_wait_second_text_view);
		assertTrue(
				"Specifier hasn't changed from singular to plural",
				secondsTextView.getText().equals(
						secondsTextView.getResources().getQuantityString(R.plurals.second_plural,
								Utils.convertDoubleToPluralInteger(1.4))));

	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		waitBrick = new WaitBrick(sprite, 1000);
		script.addBrick(waitBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
