/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.android.AndroidFiles;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.stage.ScreenshotSaver;
import org.catrobat.catroid.stage.StageActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.test.rule.ActivityTestRule;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ScreenshotSaverTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"VALID", "valid.png", true},
				{"NULL", null, false},
				{"WHITESPACES", "    ", false},
				{"ILLEGAL_CHARACTERS", "|\\?*<\":>+[]/'", false},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public String fileName;

	@Parameterized.Parameter(2)
	public boolean expectedResult;

	private static final int NUMBER_OF_COLORS = 4;

	private ScreenshotSaver screenshotSaver;
	private byte[] dummyScreenshotData;
	private Boolean result;
	private Files gdxFileHandler;

	@Rule
	public ActivityTestRule<StageActivity> activityTestRule =
			new ActivityTestRule<>(StageActivity.class, false, true);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	@Before
	public void setUp() throws Exception {
		ScreenValues.setToDefaultScreenSize();
		int height = ScreenValues.SCREEN_HEIGHT;
		int width = ScreenValues.SCREEN_WIDTH;
		dummyScreenshotData = new byte[NUMBER_OF_COLORS * width * height];

		StageActivity stageActivity = activityTestRule.getActivity();
		String folder = stageActivity.getCacheDir().getAbsolutePath() + "/";

		gdxFileHandler = new AndroidFiles(stageActivity.getAssets(),
				stageActivity.getFilesDir().getAbsolutePath());

		screenshotSaver = new ScreenshotSaver(gdxFileHandler, folder, width, height);
	}

	@After
	public void tearDown() throws IOException {
		result = null;
		File dir = new File(gdxFileHandler.getLocalStoragePath());
		StorageOperations.deleteDir(dir);
	}

	@Flaky
	@Test
	public void testRequestScreenshotWithValidNameShouldBeSuccessful() throws InterruptedException {
		CountDownLatch expectation = new CountDownLatch(1);
		screenshotSaver.saveScreenshotAndNotify(dummyScreenshotData, fileName,
				success -> {
					result = success;
					expectation.countDown();
				});

		expectation.await(1, TimeUnit.SECONDS);

		assertEquals("Callback wasn't called",
				0,
				expectation.getCount());
		assertEquals("Save screenshot hasn't the expected outcome",
				result,
				expectedResult);
	}
}
