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
import org.catrobat.catroid.content.bricks.LegoNxtPlayToneBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class LegoNxtPlayToneBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {

	private static final int MIN_FREQ = 200;
	private static final int MAX_FREQ = 14000;
	private static final double SET_DURATION = 3.0;
	private static final int SET_FREQUENCY = 70;
	private static final int SET_FREQUENCY_INITIALLY = 20;

	private Solo solo;
	private Project project;
	private LegoNxtPlayToneBrick playToneBrick;

	public LegoNxtPlayToneBrickTest() {
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
	public void testNXTPlayToneBrick() {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();
		int groupCount = adapter.getScriptCount();

		assertEquals("Incorrect number of bricks.", 2 + 1, dragDropListView.getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0), adapter.getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(solo.getString(R.string.nxt_play_tone)));

		UiTestUtils.clickEnterClose(solo, 0, SET_DURATION + "");

		double duration = (Integer) UiTestUtils.getPrivateField(playToneBrick, "durationInMilliSeconds");
		assertEquals("Wrong text in field.", SET_DURATION, duration / 1000);
		assertEquals("Value in Brick is not updated.", SET_DURATION + "", solo.getEditText(0).getText().toString());

		assertEquals("SeekBar is at wrong position", SET_FREQUENCY_INITIALLY, solo.getCurrentProgressBars().get(0)
				.getProgress());

		UiTestUtils.clickEnterClose(solo, 1, SET_FREQUENCY + "");

		int hertz = (Integer) UiTestUtils.getPrivateField(playToneBrick, "hertz");
		assertEquals("Wrong text in field.", SET_FREQUENCY * 100, hertz);
		assertEquals("Value in Brick is not updated.", SET_FREQUENCY + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", SET_FREQUENCY, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, SET_FREQUENCY_INITIALLY);
		solo.sleep(200);

		hertz = (Integer) UiTestUtils.getPrivateField(playToneBrick, "hertz");
		assertEquals("Wrong text in field.", SET_FREQUENCY_INITIALLY * 100, hertz);
		assertEquals("Value in Brick is not updated.", SET_FREQUENCY_INITIALLY + "", solo.getEditText(1).getText()
				.toString());
		assertEquals("SeekBar is at wrong position", SET_FREQUENCY_INITIALLY, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(0);

		int freq_btn = (Integer) UiTestUtils.getPrivateField(playToneBrick, "hertz");
		assertEquals("Wrong text in field.", freq_btn, hertz - 100);
		assertEquals("Value in Brick is not updated.", (hertz - 1) / 100 + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", (hertz - 1) / 100, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(1);

		freq_btn = (Integer) UiTestUtils.getPrivateField(playToneBrick, "hertz");
		assertEquals("Wrong text in field.", freq_btn, hertz);
		assertEquals("Value in Brick is not updated.", hertz / 100 + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", hertz / 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MIN_FREQ / 100);
		solo.sleep(200);
		solo.clickOnButton(0);
		solo.clickOnButton(0);

		hertz = (Integer) UiTestUtils.getPrivateField(playToneBrick, "hertz");
		assertEquals("Wrong text in field.", hertz, MIN_FREQ);
		assertEquals("Value in Brick is not updated.", hertz / 100 + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", hertz / 100, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MAX_FREQ / 100);
		solo.sleep(200);
		solo.clickOnButton(1);
		solo.clickOnButton(1);

		hertz = (Integer) UiTestUtils.getPrivateField(playToneBrick, "hertz");
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
