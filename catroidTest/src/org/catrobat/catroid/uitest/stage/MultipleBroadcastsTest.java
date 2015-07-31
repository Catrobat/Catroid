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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.util.ArrayList;

public class MultipleBroadcastsTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Sprite sprite1;
	private int sprite1PosX = 30;
	private Sprite sprite2;
	private int sprite2PosX = 60;
	private Sprite sprite3;
	private int sprite3PosX = -30;
	private Sprite sprite4;
	private int sprite4PosX = -60;
	private final String broadcastMessage = "run";

	public MultipleBroadcastsTest() {
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

	public void testSendMultipleBroadcastsWhenProjectStart() {
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);
		assertEquals("Sprite1 is at the false x position", sprite1PosX,
				(int) sprite1.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Sprite2 is at the false x position", sprite2PosX,
				(int) sprite2.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Sprite3 is at the false x position", sprite3PosX,
				(int) sprite3.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Sprite4 is at the false x position", sprite4PosX,
				(int) sprite4.look.getXInUserInterfaceDimensionUnit());
	}

	private void createProject() {
		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();

		sprite1 = new Sprite("sprite1");
		StartScript startScript1 = new StartScript();
		BroadcastBrick broadcastBrick1 = new BroadcastBrick(broadcastMessage);
		startScript1.addBrick(broadcastBrick1);
		BroadcastScript broadcastScript1 = new BroadcastScript("run");
		SetXBrick setXBrick1 = new SetXBrick(sprite1PosX);
		broadcastScript1.addBrick(setXBrick1);
		sprite1.addScript(startScript1);
		sprite1.addScript(broadcastScript1);
		spriteList.add(sprite1);

		sprite2 = new Sprite("sprite2");
		StartScript startScript2 = new StartScript();
		BroadcastBrick broadcastBrick2 = new BroadcastBrick(broadcastMessage);
		startScript2.addBrick(broadcastBrick2);
		BroadcastScript broadcastScript2 = new BroadcastScript("run");
		SetXBrick setXBrick2 = new SetXBrick(sprite2PosX);
		broadcastScript2.addBrick(setXBrick2);
		sprite2.addScript(startScript2);
		sprite2.addScript(broadcastScript2);
		spriteList.add(sprite2);

		sprite3 = new Sprite("sprite3");
		StartScript startScript3 = new StartScript();
		BroadcastBrick broadcastBrick3 = new BroadcastBrick(broadcastMessage);
		startScript3.addBrick(broadcastBrick3);
		BroadcastScript broadcastScript3 = new BroadcastScript("run");
		SetXBrick setXBrick3 = new SetXBrick(sprite3PosX);
		broadcastScript3.addBrick(setXBrick3);
		sprite3.addScript(startScript3);
		sprite3.addScript(broadcastScript3);
		spriteList.add(sprite3);

		sprite4 = new Sprite("sprite4");
		StartScript startScript4 = new StartScript();
		BroadcastBrick broadcastBrick4 = new BroadcastBrick(broadcastMessage);
		startScript4.addBrick(broadcastBrick4);
		BroadcastScript broadcastScript4 = new BroadcastScript("run");
		SetXBrick setXBrick4 = new SetXBrick(sprite4PosX);
		broadcastScript4.addBrick(setXBrick4);
		sprite4.addScript(startScript4);
		sprite4.addScript(broadcastScript4);
		spriteList.add(sprite4);

		UiTestUtils.createProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, spriteList, null);
	}
}
