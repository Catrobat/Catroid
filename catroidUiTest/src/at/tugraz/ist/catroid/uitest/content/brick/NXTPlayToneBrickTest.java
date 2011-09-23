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
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.NXTPlayToneBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class NXTPlayToneBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private static final int MIN_FREQ = 2;
	private static final int MAX_FREQ = 140;
	private Solo solo;
	private Project project;
	private NXTPlayToneBrick playToneBrick;

	private double setDurationInitially;
	private double setDuration;
	private int setFrequency;
	private int setFrequencyInitially;

	public NXTPlayToneBrickTest() {
		super("at.tugraz.ist.catroid", ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
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

	@Smoke
	public void testNXTPlayToneBrick() {

		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		assertEquals("Wrong Brick instance.", projectBrickList.get(0),
				getActivity().getAdapter().getChild(groupCount - 1, 0));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.nxt_play_tone)));

		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, setDuration + "");
		solo.goBack();
		solo.clickOnButton(0);

		solo.sleep(300);
		double duration = (Double) UiTestUtils.getPrivateField("duration", playToneBrick);
		assertEquals("Wrong text in field.", setDuration, duration);
		assertEquals("Value in Brick is not updated.", setDuration + "", solo.getEditText(0).getText().toString());

		assertEquals("SeekBar is at wrong position", setFrequencyInitially, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnEditText(1);
		solo.clearEditText(0);
		solo.enterText(0, setFrequency + "");
		solo.goBack();
		solo.clickOnButton(0);

		solo.sleep(300);
		int frequency = (Integer) UiTestUtils.getPrivateField("frequency", playToneBrick);
		assertEquals("Wrong text in field.", setFrequency, frequency);
		assertEquals("Value in Brick is not updated.", setFrequency + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", setFrequency, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, setFrequencyInitially);
		solo.sleep(300);

		frequency = (Integer) UiTestUtils.getPrivateField("frequency", playToneBrick);
		assertEquals("Wrong text in field.", setFrequencyInitially, frequency);
		assertEquals("Value in Brick is not updated.", setFrequencyInitially + "", solo.getEditText(1).getText()
				.toString());
		assertEquals("SeekBar is at wrong position", setFrequencyInitially, solo.getCurrentProgressBars().get(0)
				.getProgress());

		solo.clickOnButton(0);
		solo.sleep(300);

		int freq_btn = (Integer) UiTestUtils.getPrivateField("frequency", playToneBrick);
		assertEquals("Wrong text in field.", freq_btn, frequency - 1);
		assertEquals("Value in Brick is not updated.", frequency - 1 + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", frequency - 1, solo.getCurrentProgressBars().get(0).getProgress());

		solo.clickOnButton(1);
		solo.sleep(300);

		freq_btn = (Integer) UiTestUtils.getPrivateField("frequency", playToneBrick);
		assertEquals("Wrong text in field.", freq_btn, frequency);
		assertEquals("Value in Brick is not updated.", frequency + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", frequency, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MIN_FREQ);
		solo.clickOnButton(0);
		solo.clickOnButton(0);
		solo.sleep(300);

		frequency = (Integer) UiTestUtils.getPrivateField("frequency", playToneBrick);
		assertEquals("Wrong text in field.", frequency, MIN_FREQ);
		assertEquals("Value in Brick is not updated.", frequency + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", frequency, solo.getCurrentProgressBars().get(0).getProgress());

		solo.setProgressBar(0, MAX_FREQ);
		solo.clickOnButton(1);
		solo.clickOnButton(1);
		solo.sleep(300);

		frequency = (Integer) UiTestUtils.getPrivateField("frequency", playToneBrick);
		assertEquals("Wrong text in field.", frequency, MAX_FREQ);
		assertEquals("Value in Brick is not updated.", frequency + "", solo.getEditText(1).getText().toString());
		assertEquals("SeekBar is at wrong position", frequency, solo.getCurrentProgressBars().get(0).getProgress());

	}

	private void createProject() {
		//		setX = 17;
		project = new Project(null, "testProject");
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript("script", sprite);

		setFrequencyInitially = 20;
		setDurationInitially = 1.0;

		playToneBrick = new NXTPlayToneBrick(sprite, setFrequencyInitially, setDurationInitially);

		setDuration = 3.0;
		setFrequency = 70;

		script.addBrick(playToneBrick);

		sprite.addScript(script);
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

}
