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
package org.catrobat.catroid.test.stage;

import android.app.Instrumentation;
import android.content.Intent;

import org.catrobat.catroid.io.StageAudioFocus;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.test.utils.BaseActivityUnitTestCase;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class StageActivityAudioFocusTest extends BaseActivityUnitTestCase<StageActivity> {

	public StageActivityAudioFocusTest() {
		super(StageActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
	}

	public void testAudioFocusAcquisitionInStageActivityLifecycle() throws InterruptedException {

		StageActivity activity = startActivity(new Intent(Intent.ACTION_MAIN), null, null);
		Instrumentation instrumentation = getInstrumentation();
		StageAudioFocus stageAudioFocus = (StageAudioFocus) Reflection.getPrivateField(StageActivity.class, activity, "stageAudioFocus");

		Thread.sleep(400);
		instrumentation.callActivityOnStart(activity);
		assertFalse("AudioFocus granted after Stage is started, but not resumed", stageAudioFocus.isAudioFocusGranted());

		Thread.sleep(400);
		instrumentation.callActivityOnResume(activity);
		assertTrue("AudioFocus not granted after Stage has resumed", stageAudioFocus.isAudioFocusGranted());

		Thread.sleep(400);
		instrumentation.callActivityOnPause(activity);
		assertFalse("AudioFocus still granted, although the stage is paused", stageAudioFocus.isAudioFocusGranted());

		Thread.sleep(400);
		instrumentation.callActivityOnStop(activity);
		assertFalse("AudioFocus granted, although the stage already should be paused and abandoned the audio focus", stageAudioFocus.isAudioFocusGranted());
	}
}
