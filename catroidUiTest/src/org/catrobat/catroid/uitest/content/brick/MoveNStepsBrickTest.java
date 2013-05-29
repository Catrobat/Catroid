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
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.catrobat.catroid.utils.Utils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class MoveNStepsBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private static final double STEPS_TO_MOVE = 23.0;

	private Solo solo;
	private Project project;
	private MoveNStepsBrick moveNStepsBrick;

	public MoveNStepsBrickTest() {
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

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);
		moveNStepsBrick = new MoveNStepsBrick(sprite, 0);
		script.addBrick(moveNStepsBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);

	}

	@Smoke
	public void testGoNStepsBackBrick() {
		ScriptActivity activity = (ScriptActivity) solo.getCurrentActivity();
		ScriptFragment fragment = (ScriptFragment) activity.getFragment(ScriptActivity.FRAGMENT_SCRIPTS);
		BrickAdapter adapter = fragment.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentViews(ListView.class).get(1).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.brick_move)));

		UiTestUtils.testBrickWithFormulaEditor(solo, 0, 1, STEPS_TO_MOVE, "steps", moveNStepsBrick);

		UiTestUtils.insertValueViaFormulaEditor(solo, 0, STEPS_TO_MOVE);

		assertEquals("Wrong text in field.", STEPS_TO_MOVE,
				((Formula) Reflection.getPrivateField(moveNStepsBrick, "steps")).interpretDouble(null));
		assertEquals("Value in Brick is not updated.", STEPS_TO_MOVE,
				Double.valueOf(solo.getEditText(0).getText().toString()));

		UiTestUtils.insertValueViaFormulaEditor(solo, 0, 1);
		TextView stepTextView = (TextView) solo.getView(R.id.brick_move_n_steps_step_text_view);
		assertTrue(
				"Specifier hasn't changed from plural to singular",
				stepTextView.getText().equals(
						stepTextView.getResources().getQuantityString(R.plurals.brick_move_n_step_plural, 1)));

		UiTestUtils.insertValueViaFormulaEditor(solo, 0, 1.4);
		stepTextView = (TextView) solo.getView(R.id.brick_move_n_steps_step_text_view);
		assertTrue(
				"Specifier hasn't changed from singular to plural",
				stepTextView.getText().equals(
						stepTextView.getResources().getQuantityString(R.plurals.brick_move_n_step_plural,
								Utils.convertDoubleToPluralInteger(1.4))));
	}
}
