/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.robotium.solo.Condition;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class PauseResumeInStageTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public PauseResumeInStageTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoStageFromMainMenu(solo);
		waitForStage();
	}

	public void testIgnoreTouchEventsWhenStagePaused() {
		InputListenerMock touchListener = new InputListenerMock();
		StageActivity.stageListener.getStage().addListener(touchListener);

		assertFalse("Already clicked on the stage", touchListener.called);

		clickOnStage();

		assertTrue("Touch event not fired", touchListener.called);

		UiTestUtils.pauseStage(solo);
		touchListener.called = false;

		assertFalse("Already clicked on the stage", touchListener.called);

		clickOnStage();

		assertFalse("Touch events shouldn't be fired when stage is paused", touchListener.called);
	}

	private void waitForStage() {
		solo.waitForCondition(new Condition() {
			@Override
			public boolean isSatisfied() {
				return StageActivity.stageListener != null && StageActivity.stageListener.getStage() != null;
			}
		}, 2000);
	}

	private void clickOnStage() {
		solo.clickOnScreen(ScreenValues.SCREEN_WIDTH / 2f, ScreenValues.SCREEN_HEIGHT / 2f);
		solo.sleep(500);
	}

	private static class InputListenerMock extends InputListener {
		public boolean called;

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			called = true;
			return super.touchDown(event, x, y, pointer, button);
		}
	}
}
