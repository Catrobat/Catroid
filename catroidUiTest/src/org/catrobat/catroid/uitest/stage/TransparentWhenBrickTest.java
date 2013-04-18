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
package org.catrobat.catroid.uitest.stage;

import java.io.File;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.Values;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class TransparentWhenBrickTest extends ActivityInstrumentationTestCase2<StageActivity> {

	private final int screenWidth = 480;
	private final int screenHeight = 800;
	private final String catFilename = "catroid_sunglasses.png";
	private final String fishFilename = "fish.jpg";
	private int catX = 60;
	private int catY = 150;
	private int fishX = -60;
	private int fishY = -150;
	private Solo solo;
	private Sprite cat;
	private Sprite fish;

	public TransparentWhenBrickTest() {
		super(StageActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
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

	public void testTapOnSideAreaOfForegroundSprite() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		Reflection.setPrivateField(StageActivity.stageListener, "makeAutomaticScreenshot", false);
		solo.sleep(2000);
		assertTrue("Sprite cat is not at x=0 and y=0", cat.look.getXPosition() == 0 && cat.look.getYPosition() == 0);
		assertTrue("Sprite fish is not at x=0 and y=0", fish.look.getXPosition() == 0 && fish.look.getYPosition() == 0);
		assertTrue("Sprite fish is not the foreground sprite", fish.look.getZIndex() > cat.look.getZIndex());
		UiTestUtils.clickOnStageCoordinates(solo, 22, 45, screenWidth, screenHeight);
		solo.sleep(1000);
		assertTrue("Sprite cat is at false position", cat.look.getXPosition() == catX
				&& cat.look.getYPosition() == catY);
		assertTrue("Sprite fish has moved", fish.look.getXPosition() == 0 && fish.look.getYPosition() == 0);
		UiTestUtils.clickOnStageCoordinates(solo, 0, 0, screenWidth, screenHeight);
		solo.sleep(1000);
		assertTrue("Sprite cat is at false position", cat.look.getXPosition() == catX
				&& cat.look.getYPosition() == catY);
		assertTrue("Sprite fish has moved", fish.look.getXPosition() == fishX && fish.look.getYPosition() == fishY);
	}

	public void testTapOnHalfTransparentAreaOfForegroundSprite() {
		fish.look.setAlphaValue(0.5f);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		Reflection.setPrivateField(StageActivity.stageListener, "makeAutomaticScreenshot", false);
		solo.sleep(2000);
		assertTrue("Sprite cat is not at x=0 and y=0", cat.look.getXPosition() == 0 && cat.look.getYPosition() == 0);
		assertTrue("Sprite fish is not at x=0 and y=0", fish.look.getXPosition() == 0 && fish.look.getYPosition() == 0);
		assertTrue("Sprite fish is not the foreground sprite", fish.look.getZIndex() > cat.look.getZIndex());
		UiTestUtils.clickOnStageCoordinates(solo, 22, 45, screenWidth, screenHeight);
		solo.sleep(1000);
		assertTrue("Sprite cat is at false position", cat.look.getXPosition() == catX
				&& cat.look.getYPosition() == catY);
		assertTrue("Sprite fish has moved", fish.look.getXPosition() == 0 && fish.look.getYPosition() == 0);
		UiTestUtils.clickOnStageCoordinates(solo, 0, 0, screenWidth, screenHeight);
		solo.sleep(1000);
		assertTrue("Sprite cat is at false position", cat.look.getXPosition() == catX
				&& cat.look.getYPosition() == catY);
		assertTrue("Sprite fish has moved", fish.look.getXPosition() == fishX && fish.look.getYPosition() == fishY);
	}

	public void testTapOnFullTransparentAreaOfForegroundSprite() {
		fish.look.setAlphaValue(0.0f);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		Reflection.setPrivateField(StageActivity.stageListener, "makeAutomaticScreenshot", false);
		solo.sleep(2000);
		assertTrue("Sprite cat is not at x=0 and y=0", cat.look.getXPosition() == 0 && cat.look.getYPosition() == 0);
		assertTrue("Sprite fish is not at x=0 and y=0", fish.look.getXPosition() == 0 && fish.look.getYPosition() == 0);
		assertTrue("Sprite fish is not the foreground sprite", fish.look.getZIndex() > cat.look.getZIndex());
		UiTestUtils.clickOnStageCoordinates(solo, 0, 0, screenWidth, screenHeight);
		solo.sleep(1000);
		assertTrue("Sprite cat is at false position", cat.look.getXPosition() == catX
				&& cat.look.getYPosition() == catY);
		assertTrue("Sprite fish has moved", fish.look.getXPosition() == 0 && fish.look.getYPosition() == 0);
	}

	private void createProject() {
		Values.SCREEN_WIDTH = screenWidth;
		Values.SCREEN_HEIGHT = screenHeight;

		Project project = new Project(null, UiTestUtils.PROJECTNAME1);
		cat = new Sprite("cat");
		StartScript startScriptCat = new StartScript(cat);
		SetLookBrick setLookCat = new SetLookBrick(cat);

		LookData lookDataCat = new LookData();
		lookDataCat.setLookName(catFilename);

		cat.getLookDataList().add(lookDataCat);
		setLookCat.setLook(lookDataCat);
		startScriptCat.addBrick(setLookCat);
		cat.addScript(startScriptCat);

		WhenScript whenScriptCat = new WhenScript(cat);
		PlaceAtBrick placeAtCat = new PlaceAtBrick(cat, catX, catY);
		whenScriptCat.addBrick(placeAtCat);
		cat.addScript(whenScriptCat);

		project.addSprite(cat);

		fish = new Sprite("fish");
		StartScript startScriptFish = new StartScript(fish);
		SetLookBrick setLookFish = new SetLookBrick(fish);

		LookData lookDataFish = new LookData();
		lookDataFish.setLookName(fishFilename);

		fish.getLookDataList().add(lookDataFish);
		setLookFish.setLook(lookDataFish);
		startScriptFish.addBrick(setLookFish);
		fish.addScript(startScriptFish);

		WhenScript whenScriptFish = new WhenScript(fish);
		PlaceAtBrick placeAtFish = new PlaceAtBrick(fish, fishX, fishY);
		whenScriptFish.addBrick(placeAtFish);
		fish.addScript(whenScriptFish);

		project.addSprite(fish);

		StorageHandler.getInstance().saveProject(project);

		File catImageFile = UiTestUtils.saveFileToProject(project.getName(), catFilename,
				org.catrobat.catroid.uitest.R.drawable.catroid_sunglasses, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File fishImageFile = UiTestUtils.saveFileToProject(project.getName(), fishFilename,
				org.catrobat.catroid.uitest.R.drawable.fish, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		lookDataCat.setLookFilename(catImageFile.getName());
		lookDataFish.setLookFilename(fishImageFile.getName());

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().saveProject();
	}
}
