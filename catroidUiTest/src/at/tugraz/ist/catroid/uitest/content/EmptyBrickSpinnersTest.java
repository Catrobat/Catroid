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
package at.tugraz.ist.catroid.uitest.content;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.json.JSONException;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.BroadcastBrick;
import at.tugraz.ist.catroid.content.bricks.BroadcastWaitBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.PointToBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
import at.tugraz.ist.catroid.uitest.util.XMLValidationUtil;
import at.tugraz.ist.catroid.utils.Utils;

import com.jayway.android.robotium.solo.Solo;

public class EmptyBrickSpinnersTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;
	private String testProjectName = UiTestUtils.PROJECTNAME1;
	private String costumeDataName = "blubb";
	private String pointToSpriteName = "pointSprite";
	private String testSoundTitle = "soundTitle";
	private String testBroadcastMessage = "broadcastMessage";
	private String testBroadcastWaitMessage = "broadcastWaitMessage";

	public EmptyBrickSpinnersTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
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
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testBricksWithEmptySpinner() throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, IOException, JSONException {

		String spinnerNothingSelectedText = solo.getString(R.string.broadcast_nothing_selected);

		assertTrue("costume " + costumeDataName + " is not selected", solo.searchText(costumeDataName));
		solo.clickOnText(costumeDataName);
		solo.clickOnText(spinnerNothingSelectedText);

		assertTrue(pointToSpriteName + " Sprite is not selected", solo.searchText(pointToSpriteName));
		solo.clickOnText(pointToSpriteName);
		solo.clickOnText(spinnerNothingSelectedText);

		assertTrue(testSoundTitle + " Sound is not selected", solo.searchText(testSoundTitle));
		solo.clickOnText(testSoundTitle);
		solo.clickOnText(spinnerNothingSelectedText);

		assertTrue(testBroadcastWaitMessage + " Message is not selected", solo.searchText(testBroadcastWaitMessage));
		solo.clickOnText(testBroadcastWaitMessage);
		solo.clickOnText(spinnerNothingSelectedText);

		assertTrue(testBroadcastMessage + " Mesage is not selected", solo.searchText(testBroadcastMessage));
		solo.clickOnText(testBroadcastMessage);
		solo.clickOnText(spinnerNothingSelectedText);

		// go back that the project xml is saved
		solo.goBack();
		solo.waitForActivity(ProjectActivity.class.getSimpleName());

		String projectXMLPath = Utils.buildPath(Utils.buildProjectPath(testProjectName), Constants.PROJECTCODE_NAME);
		XMLValidationUtil.sendProjectXMLToServerForValidating(projectXMLPath);
	}

	private void createSpinnerProject() {
		Project project = new Project(null, testProjectName);
		Sprite sprite = new Sprite("testSprite");
		Sprite pointToSprite = new Sprite(pointToSpriteName);
		project.addSprite(pointToSprite);
		project.addSprite(sprite);

		Script startScript = new StartScript(sprite);
		sprite.addScript(startScript);

		addSetCostumeBrick(sprite, startScript);
		addPointToBrick(sprite, pointToSprite, startScript);
		addPlaySoundBrick(sprite, startScript);
		addBroadcastWaitBrick(sprite, startScript);
		addBroadcastBrick(sprite, startScript);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(startScript);
	}

	private void addBroadcastBrick(Sprite sprite, Script startScript) {
		ProjectManager.getInstance().getMessageContainer().addMessage(testBroadcastMessage);
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite);
		broadcastBrick.setSelectedMessage(testBroadcastMessage);
		startScript.addBrick(broadcastBrick);
	}

	private void addBroadcastWaitBrick(Sprite sprite, Script startScript) {
		ProjectManager.getInstance().getMessageContainer().addMessage(testBroadcastWaitMessage);
		BroadcastWaitBrick broadcastWaitBrick = new BroadcastWaitBrick(sprite);
		broadcastWaitBrick.setSelectedMessage(testBroadcastWaitMessage);
		startScript.addBrick(broadcastWaitBrick);
	}

	private void addPlaySoundBrick(Sprite sprite, Script startScript) {
		SoundInfo dummySoundInfo = new SoundInfo();
		dummySoundInfo.setTitle(testSoundTitle);
		sprite.getSoundList().add(dummySoundInfo);

		PlaySoundBrick playSoundBrick = new PlaySoundBrick(sprite);
		playSoundBrick.setSoundInfo(dummySoundInfo);
		startScript.addBrick(playSoundBrick);
	}

	private void addPointToBrick(Sprite sprite, Sprite pointToSprite, Script startScript) {
		PointToBrick pointToBrick = new PointToBrick(sprite, pointToSprite);
		startScript.addBrick(pointToBrick);
	}

	private void addSetCostumeBrick(Sprite sprite, Script startScript) {
		CostumeData dummyCostumeData = new CostumeData();
		dummyCostumeData.setCostumeName(costumeDataName);
		sprite.getCostumeDataList().add(dummyCostumeData);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		setCostumeBrick.setCostume(dummyCostumeData);
		startScript.addBrick(setCostumeBrick);
	}
}
