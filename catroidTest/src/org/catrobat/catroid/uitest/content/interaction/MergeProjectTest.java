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
package org.catrobat.catroid.uitest.content.interaction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.SpeakBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.content.bricks.WhenStartedBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

public class MergeProjectTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private String projectNameTo = "mergeTo";
	private String projectNameFrom = "mergeFrom";

	public MergeProjectTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Project projectTo = StandardProjectHandler.createAndSaveEmptyProject(projectNameTo, getActivity());
		Project projectFrom = StandardProjectHandler.createAndSaveEmptyProject(projectNameFrom, getActivity());
		ProjectManager.getInstance().setProject(projectFrom);
		ProjectManager.getInstance().setProject(projectTo);
	}

	public void testMergeInSimpleProject() {
		ProjectManager manager = ProjectManager.getInstance();
		String firstProjectName = solo.getString(R.string.default_project_name);
		Project firstProject = StorageHandler.getInstance().loadProject(firstProjectName);

		assertEquals("mergeTo have to be the current project", projectNameTo, manager.getCurrentProject().getName());

		int spriteSize = firstProject.getSpriteList().size() - 1;
		spriteSize += manager.getCurrentProject().getSpriteList().size();
		solo.sleep(500);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.clickLongOnText(solo.getString(R.string.default_project_name));
		solo.clickOnText(solo.getString(R.string.merge_button));

		assertTrue("There have to be Bricks in the Background of the first project.", solo.waitForText(solo.getString(R.string.error_bricks_in_background), 1, 1000));

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.sleep(1000);

		assertEquals("Wrong number of Sprites.", spriteSize, manager.getCurrentProject().getSpriteList().size());
	}

	public void testMergeWithSelf() {
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.clickLongOnText(projectNameTo);
		solo.clickOnText(solo.getString(R.string.merge_button));

		assertTrue("Can't merge with self.", solo.waitForText(solo.getString(R.string.error_merge_with_self), 1, 1000));
	}

	public void testDifferentResolution() {
		initDifferentResolutionTest();

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.clickLongOnText(projectNameFrom);
		solo.clickOnText(solo.getString(R.string.merge_button));

		assertTrue("Different resolution dialog should be shown.", solo.waitForText(solo.getString(R.string.error_different_resolutions1), 1, 1000));
		assertTrue("Different resolution dialog should be shown.", solo.waitForText(solo.getString(R.string.error_different_resolutions2), 1, 1000));
		assertTrue("Different resolution dialog should be shown.", solo.waitForText(solo.getString(R.string.error_different_resolutions3), 1, 1000));

		solo.clickOnButton(solo.getString(R.string.main_menu_continue));
		solo.sleep(1500);
		solo.clickOnText(projectNameTo);

		assertTrue("Can't find merged Brick", solo.waitForText("TestSprite1", 1, 1000));
	}

	public void testBroadcastMessages() {
		initBroadcastTest();

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.clickLongOnText(projectNameFrom);
		solo.clickOnText(solo.getString(R.string.merge_button));
		solo.sleep(1200);

		solo.clickOnText(projectNameTo);
		solo.clickOnText("testSprite2");
		solo.clickOnText(solo.getString(R.string.scripts));
		solo.clickOnText("Broadcast From");

		assertTrue("There have to be 2 Broadcast-Messages.", solo.waitForText("Broadcast To", 1, 1000));
	}

	public void testMergeConflict() {
		initTestMergeConflict();

		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.clickLongOnText(projectNameFrom);
		solo.clickOnText(solo.getString(R.string.merge_button));

		assertTrue("Mergingerror dialog should be shown.", solo.waitForText(solo.getString(R.string.merge_conflict), 1, 1000));
		assertTrue("Mergingerror dialog should be shown with Objects.", solo.waitForText(solo.getString(R.string.sprite), 1, 1000));
		assertTrue("Mergingerror dialog should be shown with Broadcasts.", solo.waitForText(solo.getString(R.string.broadcast), 1, 1000));
		assertTrue("Mergingerror dialog should be shown with Variables.", solo.waitForText(solo.getString(R.string.variable), 1, 1000));
		assertTrue("Mergingerror dialog should be shown with List Items.", solo.waitForText(solo.getString(R.string.project_list), 1, 1000));
	}

	public void testMergeInComplexProject() {
		initComplexTest();
		ProjectManager manager = ProjectManager.getInstance();
		String defaultProjectName = solo.getString(R.string.default_project_name);
		Project projectFrom = StorageHandler.getInstance().loadProject(defaultProjectName);

		assertEquals("mergeTo have to be the current project", projectNameTo, manager.getCurrentProject().getName());

		int spriteSize = projectFrom.getSpriteList().size() - 1;
		spriteSize += manager.getCurrentProject().getSpriteList().size();

		solo.sleep(500);
		solo.clickOnButton(solo.getString(R.string.main_menu_programs));
		solo.clickLongOnText(solo.getString(R.string.default_project_name));
		solo.clickOnText(solo.getString(R.string.merge_button));

		assertTrue("There have to be Bricks in the mergeFrom project.", solo.waitForText(solo.getString(R.string.error_bricks_in_background), 1, 1000));

		solo.clickOnText(solo.getString(R.string.main_menu_continue));
		solo.sleep(1000);

		assertEquals("Wrong number of Sprites.", spriteSize, manager.getCurrentProject().getSpriteList().size());
	}

	private void initDifferentResolutionTest() {
		Project projectTo = StorageHandler.getInstance().loadProject(projectNameTo);

		XmlHeader header = projectTo.getXmlHeader();
		header.setVirtualScreenWidth(100);
		header.setVirtualScreenHeight(100);
		projectTo.setXmlHeader(header);

		projectTo.addSprite(new Sprite("TestSprite1"));

		StorageHandler.getInstance().saveProject(projectTo);
		ProjectManager.getInstance().setProject(projectTo);
	}

	private void initBroadcastTest() {
		Project projectTo = StorageHandler.getInstance().loadProject(projectNameTo);
		Project projectFrom = StorageHandler.getInstance().loadProject(projectNameFrom);

		BroadcastBrick brickTo = new BroadcastBrick("Broadcast To");
		StartScript scriptTo = new StartScript();
		scriptTo.addBrick(brickTo);
		Sprite spriteTo = new Sprite("testSprite1");
		spriteTo.addScript(scriptTo);
		projectTo.addSprite(spriteTo);

		BroadcastBrick brickFrom = new BroadcastBrick("Broadcast From");
		StartScript scriptFrom = new StartScript();
		scriptFrom.addBrick(brickFrom);
		Sprite spriteFrom = new Sprite("testSprite2");
		spriteFrom.addScript(scriptFrom);
		projectFrom.addSprite(spriteFrom);

		StorageHandler.getInstance().saveProject(projectFrom);
		ProjectManager.getInstance().setProject(projectFrom);
		StorageHandler.getInstance().saveProject(projectTo);
		ProjectManager.getInstance().setProject(projectTo);
	}

	private void initTestMergeConflict() {
		Project projectTo = StorageHandler.getInstance().loadProject(projectNameTo);
		Project projectFrom = StorageHandler.getInstance().loadProject(projectNameFrom);

		BroadcastBrick brickFrom = new BroadcastBrick("conflict Broadcast");
		StartScript scriptFrom = new StartScript();
		scriptFrom.addBrick(brickFrom);

		Sprite sprite = new Sprite("conflict Sprite");
		sprite.addScript(scriptFrom);

		projectTo.addSprite(sprite);
		projectFrom.addSprite(sprite);

		projectFrom.getDataContainer().addProjectUserList("conflict List");
		projectTo.getDataContainer().addProjectUserList("conflict List");

		projectFrom.getDataContainer().addProjectUserVariable("conflict variable");
		projectTo.getDataContainer().addProjectUserVariable("conflict variable");

		StorageHandler.getInstance().saveProject(projectFrom);
		ProjectManager.getInstance().setProject(projectFrom);
		StorageHandler.getInstance().saveProject(projectTo);
		ProjectManager.getInstance().setProject(projectTo);
	}

	private void initComplexTest() {
		Project projectTo = StorageHandler.getInstance().loadProject(projectNameTo);

		WhenStartedBrick whenStarted = new WhenStartedBrick();
		WaitBrick wait = new WaitBrick(1);
		SpeakBrick speak = new SpeakBrick("Hello");
		StartScript speakScriptTo = new StartScript();
		speakScriptTo.addBrick(whenStarted);
		speakScriptTo.addBrick(wait);
		speakScriptTo.addBrick(speak);
		Sprite speakSpriteTo = new Sprite("testSprite1");
		speakSpriteTo.addScript(speakScriptTo);
		projectTo.addSprite(speakSpriteTo);

		BroadcastBrick broadcastTo = new BroadcastBrick("Broadcast To");
		StartScript broadcastScriptTo = new StartScript();
		broadcastScriptTo.addBrick(broadcastTo);
		Sprite broadcastSpriteTo = new Sprite("testSprite2");
		broadcastSpriteTo.addScript(broadcastScriptTo);
		projectTo.addSprite(broadcastSpriteTo);

		StorageHandler.getInstance().saveProject(projectTo);
		ProjectManager.getInstance().setProject(projectTo);
	}
}
