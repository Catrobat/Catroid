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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class BroadCastReceiverRegressionTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public BroadCastReceiverRegressionTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
	}

	/*
	 * Regression test for https://github.com/Catrobat/Catroid/pull/105
	 */
	public void testReceiversWorkMoreThanOnce() {
		UiTestUtils.createEmptyProject();
		Sprite sprite = ProjectManager.INSTANCE.getCurrentProject().getSpriteList().get(0);
		Script script = sprite.getScript(0);

		final String testMessage = "RegressionTest#105";
		BroadcastBrick broadcastBrick = new BroadcastBrick(sprite, testMessage);
		script.addBrick(broadcastBrick);

		BroadcastScript broadcastScript = new BroadcastScript(sprite, testMessage);
		final int xMovement = 100;
		ChangeXByNBrick changeXByNBrick = new ChangeXByNBrick(sprite, xMovement);
		broadcastScript.addBrick(changeXByNBrick);
		sprite.addScript(broadcastScript);

		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);

		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);

		assertEquals("Broadcast was not executed!", xMovement, (int) sprite.look.getXInUserInterfaceDimensionUnit());

		solo.goBack();
		solo.goBack();
		solo.waitForActivity(ScriptActivity.class.getSimpleName());

		// This is where the magic happens. We try to execute the program again, Broadcasts should still work.
		UiTestUtils.clickOnBottomBar(solo, R.id.button_play);
		solo.waitForActivity(StageActivity.class.getSimpleName());
		solo.sleep(2000);

		assertEquals("Broadcast didn't work a second time!", xMovement,
				(int) sprite.look.getXInUserInterfaceDimensionUnit());
	}
}
