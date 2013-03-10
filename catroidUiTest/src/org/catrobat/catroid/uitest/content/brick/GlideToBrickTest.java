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
package org.catrobat.catroid.uitest.content.brick;

import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.Reflection;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

public class GlideToBrickTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {

	private Solo solo;

	public GlideToBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createEmptyProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	@Override
	public void tearDown() throws Exception {
		UiTestUtils.goBackToHome(getInstrumentation());
		solo.finishOpenedActivities();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
		solo = null;
	}

	public void testNumberInput() {
		String whenStartedText = solo.getString(R.string.brick_when_started);

		UiTestUtils.addNewBrick(solo, R.string.brick_glide);
		solo.clickOnText(whenStartedText);

		double duration = 1.5;
		int xPosition = 123;
		int yPosition = 567;

		// This is a hack. On my device and on Jenkins, the click on the first EditText could not be completed.
		// Doing it manually here
		// #2 Improved hack to have it working on newer devices too, still no solution or anything
		if (Build.VERSION.SDK_INT < 15) {
			solo.clickOnView(solo.getView(R.id.brick_glide_to_edit_text_duration));
		}

		UiTestUtils.clickEnterClose(solo, 0, String.valueOf(duration));
		UiTestUtils.clickEnterClose(solo, 1, String.valueOf(xPosition));
		UiTestUtils.clickEnterClose(solo, 2, String.valueOf(yPosition));

		ProjectManager manager = ProjectManager.getInstance();
		List<Brick> brickList = manager.getCurrentSprite().getScript(0).getBrickList();
		GlideToBrick glideToBrick = (GlideToBrick) brickList.get(0);

		assertEquals("Wrong duration input in Glide to brick", Math.round(duration * 1000),
				glideToBrick.getDurationInMilliSeconds());
		assertEquals("Wrong x input in Glide to brick", xPosition,
				Reflection.getPrivateField(glideToBrick, "xDestination"));
		assertEquals("Wrong y input in Glide to brick", yPosition,
				Reflection.getPrivateField(glideToBrick, "yDestination"));

		UiTestUtils.clickEnterClose(solo, 0, "1");
		TextView secondsTextView = (TextView) solo.getView(R.id.brick_glide_to_seconds_text_view);
		assertTrue(
				"Specifier hasn't changed from plural to singular",
				secondsTextView.getText().equals(
						secondsTextView.getResources().getQuantityString(R.plurals.second_plural, 1)));
		UiTestUtils.clickEnterClose(solo, 0, "5.0");
		secondsTextView = (TextView) solo.getView(R.id.brick_glide_to_seconds_text_view);
		assertTrue(
				"Specifier hasn't changed from singular to plural",
				secondsTextView.getText().equals(
						secondsTextView.getResources().getQuantityString(R.plurals.second_plural, 5)));
	}
}
