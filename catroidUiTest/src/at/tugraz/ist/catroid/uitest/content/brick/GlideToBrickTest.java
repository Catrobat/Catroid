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
package at.tugraz.ist.catroid.uitest.content.brick;

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class GlideToBrickTest extends ActivityInstrumentationTestCase2<ScriptTabActivity> {
	private Solo solo;

	public GlideToBrickTest() {
		super("at.tugraz.ist.catroid", ScriptTabActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		getActivity().finish();
		super.tearDown();
	}

	public void testNumberInput() {
		UiTestUtils.addNewBrickAndScrollDown(solo, R.string.brick_glide);

		double duration = 1.5;
		int xPosition = 123;
		int yPosition = 567;

		int numberOfEditTexts = solo.getCurrentEditTexts().size();
		UiTestUtils.clickEnterClose(solo, numberOfEditTexts - 3, String.valueOf(duration));
		UiTestUtils.clickEnterClose(solo, numberOfEditTexts - 2, String.valueOf(xPosition));
		UiTestUtils.clickEnterClose(solo, numberOfEditTexts - 1, String.valueOf(yPosition));

		solo.sleep(1000);
		ProjectManager manager = ProjectManager.getInstance();
		List<Brick> brickList = manager.getCurrentScript().getBrickList();
		GlideToBrick glideToBrick = (GlideToBrick) brickList.get(brickList.size() - 1);
		assertEquals("Wrong duration input in Glide to brick", Math.round(duration * 1000),
				glideToBrick.getDurationInMilliSeconds());

		assertEquals("Wrong x input in Glide to brick", xPosition,
				UiTestUtils.getPrivateField("xDestination", glideToBrick));
		assertEquals("Wrong y input in Glide to brick", yPosition,
				UiTestUtils.getPrivateField("yDestination", glideToBrick));
	}
}
