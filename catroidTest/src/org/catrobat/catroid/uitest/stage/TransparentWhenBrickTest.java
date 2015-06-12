/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.uitest.stage;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetTransparencyBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;

public class TransparentWhenBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private final int screenWidth = 480;
	private final int screenHeight = 800;
	private final String catFilename = "catroid_sunglasses.png";
	private final String fishFilename = "fish.jpg";
	SetTransparencyBrick setTransparencyBrick;
	private int catXPosition = 60;
	private int catYPosition = 150;
	private int fishXPosition = -60;
	private int fishYPosition = -150;
	private Sprite cat;
	private Sprite fish;

	public TransparentWhenBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoSpritesFromMainMenu(solo);
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
	}

	public void testTapOnSideAreaOfForegroundSprite() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		assertTrue("Sprite cat is not at x=0 and y=0",
				cat.look.getXInUserInterfaceDimensionUnit() == 0 && cat.look.getYInUserInterfaceDimensionUnit() == 0);
		assertTrue("Sprite fish is not at x=0 and y=0",
				fish.look.getXInUserInterfaceDimensionUnit() == 0 && fish.look.getYInUserInterfaceDimensionUnit() == 0);
		assertTrue("Sprite fish is not the foreground sprite", fish.look.getZIndex() > cat.look.getZIndex());
		UiTestUtils.clickOnStageCoordinates(solo, 22, 45, screenWidth, screenHeight);
		solo.sleep(1000);
		assertTrue("Sprite cat is at false position", cat.look.getXInUserInterfaceDimensionUnit() == catXPosition
				&& cat.look.getYInUserInterfaceDimensionUnit() == catYPosition);
		assertTrue("Sprite fish has moved",
				fish.look.getXInUserInterfaceDimensionUnit() == 0 && fish.look.getYInUserInterfaceDimensionUnit() == 0);
		UiTestUtils.clickOnStageCoordinates(solo, 0, 0, screenWidth, screenHeight);
		solo.sleep(1000);
		assertTrue("Sprite cat is at false position", cat.look.getXInUserInterfaceDimensionUnit() == catXPosition
				&& cat.look.getYInUserInterfaceDimensionUnit() == catYPosition);
		assertTrue(
				"Sprite fish has moved",
				fish.look.getXInUserInterfaceDimensionUnit() == fishXPosition
						&& fish.look.getYInUserInterfaceDimensionUnit() == fishYPosition
		);
	}

	public void testTapOnHalfTransparentAreaOfForegroundSprite() {
		setTransparencyBrick.setFormulaWithBrickField(Brick.BrickField.TRANSPARENCY, new Formula(50.0));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		assertTrue("Sprite cat is not at x=0 and y=0",
				cat.look.getXInUserInterfaceDimensionUnit() == 0 && cat.look.getYInUserInterfaceDimensionUnit() == 0);
		assertTrue("Sprite fish is not at x=0 and y=0",
				fish.look.getXInUserInterfaceDimensionUnit() == 0 && fish.look.getYInUserInterfaceDimensionUnit() == 0);
		assertTrue("Sprite fish is not the foreground sprite", fish.look.getZIndex() > cat.look.getZIndex());
		UiTestUtils.clickOnStageCoordinates(solo, 22, 45, screenWidth, screenHeight);
		solo.sleep(1000);
		assertTrue("Sprite cat is at false position", cat.look.getXInUserInterfaceDimensionUnit() == catXPosition
				&& cat.look.getYInUserInterfaceDimensionUnit() == catYPosition);
		assertTrue("Sprite fish has moved",
				fish.look.getXInUserInterfaceDimensionUnit() == 0 && fish.look.getYInUserInterfaceDimensionUnit() == 0);
		UiTestUtils.clickOnStageCoordinates(solo, 0, 0, screenWidth, screenHeight);
		solo.sleep(1000);
		assertTrue("Sprite cat is at false position", cat.look.getXInUserInterfaceDimensionUnit() == catXPosition
				&& cat.look.getYInUserInterfaceDimensionUnit() == catYPosition);
		assertTrue(
				"Sprite fish has moved",
				fish.look.getXInUserInterfaceDimensionUnit() == fishXPosition
						&& fish.look.getYInUserInterfaceDimensionUnit() == fishYPosition
		);
	}

	public void testTapOnFullTransparentAreaOfForegroundSprite() {
		setTransparencyBrick.setFormulaWithBrickField(Brick.BrickField.TRANSPARENCY, new Formula(100.0));
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		assertTrue("Sprite cat is not at x=0 and y=0",
				cat.look.getXInUserInterfaceDimensionUnit() == 0 && cat.look.getYInUserInterfaceDimensionUnit() == 0);
		assertTrue("Sprite fish is not at x=0 and y=0",
				fish.look.getXInUserInterfaceDimensionUnit() == 0 && fish.look.getYInUserInterfaceDimensionUnit() == 0);
		assertTrue("Sprite fish is not the foreground sprite", fish.look.getZIndex() > cat.look.getZIndex());
		UiTestUtils.clickOnStageCoordinates(solo, 0, 0, screenWidth, screenHeight);
		solo.sleep(1000);
		assertEquals("Sprite cat is at false position", catXPosition, (int) cat.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Sprite cat is at false position", catYPosition, (int) cat.look.getYInUserInterfaceDimensionUnit());
		assertEquals("Sprite fish has moved", 0, (int) fish.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Sprite fish has moved", 0, (int) fish.look.getYInUserInterfaceDimensionUnit());
	}

	private void createProject() {
		ScreenValues.SCREEN_WIDTH = screenWidth;
		ScreenValues.SCREEN_HEIGHT = screenHeight;

		Project project = new Project(null, UiTestUtils.PROJECTNAME1);
		cat = new Sprite("cat");
		StartScript startScriptCat = new StartScript();
		SetLookBrick setLookCat = new SetLookBrick();

		LookData lookDataCat = new LookData();
		lookDataCat.setLookName(catFilename);

		cat.getLookDataList().add(lookDataCat);
		setLookCat.setLook(lookDataCat);
		startScriptCat.addBrick(setLookCat);
		cat.addScript(startScriptCat);

		WhenScript whenScriptCat = new WhenScript();
		PlaceAtBrick placeAtCat = new PlaceAtBrick(catXPosition, catYPosition);
		whenScriptCat.addBrick(placeAtCat);
		cat.addScript(whenScriptCat);

		project.addSprite(cat);

		fish = new Sprite("fish");
		StartScript startScriptFish = new StartScript();
		SetLookBrick setLookFish = new SetLookBrick();
		setTransparencyBrick = new SetTransparencyBrick(0.0);

		LookData lookDataFish = new LookData();
		lookDataFish.setLookName(fishFilename);

		fish.getLookDataList().add(lookDataFish);
		setLookFish.setLook(lookDataFish);
		startScriptFish.addBrick(setLookFish);
		startScriptFish.addBrick(setTransparencyBrick);
		fish.addScript(startScriptFish);

		WhenScript whenScriptFish = new WhenScript();
		PlaceAtBrick placeAtFish = new PlaceAtBrick(fishXPosition, fishYPosition);
		whenScriptFish.addBrick(placeAtFish);
		fish.addScript(whenScriptFish);

		project.addSprite(fish);

		StorageHandler.getInstance().saveProject(project);

		File catImageFile = UiTestUtils.saveFileToProject(project.getName(), catFilename,
				org.catrobat.catroid.test.R.drawable.catroid_sunglasses, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		File fishImageFile = UiTestUtils.saveFileToProject(project.getName(), fishFilename,
				org.catrobat.catroid.test.R.drawable.fish, getInstrumentation().getContext(),
				UiTestUtils.FileTypes.IMAGE);
		lookDataCat.setLookFilename(catImageFile.getName());
		lookDataFish.setLookFilename(fishImageFile.getName());

		ProjectManager.getInstance().setProject(project);
		StorageHandler.getInstance().saveProject(project);
	}
}
