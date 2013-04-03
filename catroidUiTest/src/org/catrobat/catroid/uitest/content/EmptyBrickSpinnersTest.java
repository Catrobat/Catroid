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
package org.catrobat.catroid.uitest.content;

import java.io.IOException;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.BroadcastWaitBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.json.JSONException;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class EmptyBrickSpinnersTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private final static String TEST_PROJECT_NAME = UiTestUtils.PROJECTNAME1;
	private final static String LOOK_DATA_NAME = "lookData";
	private final static String TEST_SOUND_TITLE = "soundTitle";
	private final static String TEST_BROADCAST_MESSAGE = "broadcastMessage";
	private final static String TEST_BROADCAST_WAIT_MESSAGE = "broadcastWaitMessage";

	public EmptyBrickSpinnersTest() {
		super(ScriptActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.clearAllUtilTestProjects();
		createSpinnerProject();
		solo = new Solo(getInstrumentation(), getActivity());
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testBricksWithEmptySpinner() throws IOException, JSONException {
		final String spinnerNothingSelectedText = solo.getString(R.string.broadcast_nothing_selected);

		assertTrue("look " + LOOK_DATA_NAME + " is not selected", solo.searchText(LOOK_DATA_NAME));
		solo.clickOnText(LOOK_DATA_NAME);
		solo.clickOnText(spinnerNothingSelectedText);

		assertTrue(TEST_SOUND_TITLE + " Sound is not selected", solo.searchText(TEST_SOUND_TITLE));
		solo.clickOnText(TEST_SOUND_TITLE);
		solo.clickOnText(spinnerNothingSelectedText);

		assertTrue(TEST_BROADCAST_WAIT_MESSAGE + " Message is not selected",
				solo.searchText(TEST_BROADCAST_WAIT_MESSAGE));
		solo.clickOnText(TEST_BROADCAST_WAIT_MESSAGE);
		solo.clickOnText(spinnerNothingSelectedText);

		assertTrue(TEST_BROADCAST_MESSAGE + " Message is not selected", solo.searchText(TEST_BROADCAST_MESSAGE));
		solo.clickOnText(TEST_BROADCAST_MESSAGE);
		solo.clickOnText(spinnerNothingSelectedText);

		// go back that the project xml is saved
		solo.goBack();
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		// TODO: add XML validation based on xsd
		//	String projectXMLPath = Utils.buildPath(Utils.buildProjectPath(TEST_PROJECT_NAME), Constants.PROJECTCODE_NAME);
		//	XMLValidationUtil.sendProjectXMLToServerForValidating(projectXMLPath);
	}

	private void createSpinnerProject() {
		Project project = new Project(null, TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("testSprite");
		project.addSprite(sprite);

		Script startScript = new StartScript(sprite);
		sprite.addScript(startScript);

		addSetLookBrick(sprite, startScript);
		addPlaySoundBrick(sprite, startScript);
		addBroadcastWaitBrick(sprite, startScript);
		addBroadcastBrick(sprite, startScript);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(startScript);
	}

	private void addBroadcastBrick(Sprite sprite, Script startScript) {
		MessageContainer.addMessage(TEST_BROADCAST_MESSAGE);
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite);
		broadcastBrick.setSelectedMessage(TEST_BROADCAST_MESSAGE);
		startScript.addBrick(broadcastBrick);
	}

	private void addBroadcastWaitBrick(Sprite sprite, Script startScript) {
		MessageContainer.addMessage(TEST_BROADCAST_WAIT_MESSAGE);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite);
		broadcastWaitBrick.setSelectedMessage(TEST_BROADCAST_WAIT_MESSAGE);
		startScript.addBrick(broadcastWaitBrick);
	}

	private void addPlaySoundBrick(Sprite sprite, Script startScript) {
		SoundInfo dummySoundInfo = new SoundInfo();
		dummySoundInfo.setTitle(TEST_SOUND_TITLE);
		sprite.getSoundList().add(dummySoundInfo);

		PlaySoundBrick playSoundBrick = new PlaySoundBrick(sprite);
		playSoundBrick.setSoundInfo(dummySoundInfo);
		startScript.addBrick(playSoundBrick);
	}

	private void addSetLookBrick(Sprite sprite, Script startScript) {
		LookData dummyLookData = new LookData();
		dummyLookData.setLookName(LOOK_DATA_NAME);
		sprite.getLookDataList().add(dummyLookData);

		SetLookBrick setLookBrick = new SetLookBrick(sprite);
		setLookBrick.setLook(dummyLookData);
		startScript.addBrick(setLookBrick);
	}
}