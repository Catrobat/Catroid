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
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.LegoNxtPlayToneBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.adapter.BrickAdapter;
import at.tugraz.ist.catroid.ui.fragment.ScriptFragment;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class LegoNxtPlayToneBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {

	private static final int MIN_FREQ = 200;
	private static final int MAX_FREQ = 14000;
	private static final double SET_DURATION = 3.0;
	private static final int SET_FREQUENCY = 70;
	private static final int SET_FREQUENCY_INITIALLY = 20;

	private Solo solo;
	private Project project;
	private LegoNxtPlayToneBrick playToneBrick;

	public LegoNxtPlayToneBrickTest() {
		super(ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	@Smoke
	public void testNXTPlayToneBrick() {
		ScriptTabActivity activity = (ScriptTabActivity) solo.getCurrentActivity();
		ScriptFragment fragment = (ScriptFragment) activity.getTabFragment(ScriptTabActivity.INDEX_TAB_SCRIPTS);
		BrickAdapter adapter = fragment.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2 + 1, solo.getCurrentListViews().get(0).getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.nxt_play_tone)));

		String buttonPositiveText = solo.getString(R.string.ok);

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, SET_DURATION + "");
		solo.clickOnButton(buttonPositiveText);

		double duration = (Integer) UiTestUtils.getPrivateField("durationInMilliSeconds", playToneBrick);
		assertEquals("Wrong text in field.", SET_DURATION, duration / 1000);
		assertEquals("Value in Brick is not updated.", SET_DURATION + "", solo.getEditText(0).getText().toString());

		assertEquals("SeekBar is at wrong position", SET_FREQUENCY_INITIALLY, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnEditText(1);
		solo.clearEditText(0);
		solo.enterText(0, SET_FREQUENCY + "");
		solo.clickOnButton(buttonPositiveText);

		int hertz = (Integer) UiTestUtils.getPrivateField("hertz", playToneBrick);
		assertEquals("Wrong text in field.", SET_FREQUENCY * 100, hertz);
		assertEquals("Value in Brick is not updated.", SET_FREQUENCY + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", SET_FREQUENCY, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, SET_FREQUENCY_INITIALLY);
		solo.sleep(200);

		hertz = (Integer) UiTestUtils.getPrivateField("hertz", playToneBrick);
		assertEquals("Wrong text in field.", SET_FREQUENCY_INITIALLY * 100, hertz);
		assertEquals("Value in Brick is not updated.", SET_FREQUENCY_INITIALLY + "", solo.getEditText(1).getText()
				.toString());
		assertEquals("SeekBar is at wrong position", SET_FREQUENCY_INITIALLY, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(0);

		int freq_btn = (Integer) UiTestUtils.getPrivateField("hertz", playToneBrick);
		assertEquals("Wrong text in field.", freq_btn, hertz - 100);
		assertEquals("Value in Brick is not updated.", (hertz - 1) / 100 + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", (hertz - 1) / 100, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(1);

		freq_btn = (Integer) UiTestUtils.getPrivateField("hertz", playToneBrick);
		assertEquals("Wrong text in field.", freq_btn, hertz);
		assertEquals("Value in Brick is not updated.", hertz / 100 + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", hertz / 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MIN_FREQ / 100);
		solo.sleep(200);
		solo.clickOnButton(0);
		solo.clickOnButton(0);

		hertz = (Integer) UiTestUtils.getPrivateField("hertz", playToneBrick);
		assertEquals("Wrong text in field.", hertz, MIN_FREQ);
		assertEquals("Value in Brick is not updated.", hertz / 100 + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", hertz / 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MAX_FREQ / 100);
		solo.sleep(200);
		solo.clickOnButton(1);
		solo.clickOnButton(1);

		hertz = (Integer) UiTestUtils.getPrivateField("hertz", playToneBrick);
		assertEquals("Wrong text in field.", hertz, MAX_FREQ);
		assertEquals("Value in Brick is not updated.", hertz / 100 + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", hertz / 100, solo.getCurrentProgressBars().get(0).getProgress());
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript(sprite);

		int setDurationInitially = 1000;
		playToneBrick = new LegoNxtPlayToneBrick(sprite, SET_FREQUENCY_INITIALLY * 100, setDurationInitially);

		script.addBrick(playToneBrick);
		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}
