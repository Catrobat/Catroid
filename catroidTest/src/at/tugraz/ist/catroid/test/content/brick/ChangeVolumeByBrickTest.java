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
package at.tugraz.ist.catroid.test.content.brick;

import android.content.Context;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.ChangeVolumeByBrick;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.Utils;

public class ChangeVolumeByBrickTest extends InstrumentationTestCase {

	private static final float LOUDER = 10.6f;
	private static final float SOFTER = -20.3f;
	private static final int SOUND_FILE_ID = R.raw.testsound;
	private static final String TEST_PROJECT_NAME = TestUtils.TEST_PROJECT_NAME1;

	private Context context;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getInstrumentation().getTargetContext();

		Project project = new Project(context, TEST_PROJECT_NAME);
		assertTrue("cannot save project", StorageHandler.getInstance().saveProjectSynchronously(project));
		ProjectManager.getInstance().setProject(project);

		TestUtils.saveFileToProject(TEST_PROJECT_NAME, "soundTest.mp3", SOUND_FILE_ID, context,
				TestUtils.TYPE_SOUND_FILE);

		Utils.updateScreenWidthAndHeight(context);
	}

	@Override
	protected void tearDown() throws Exception {
		SoundManager.getInstance().clear();
		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testVolume() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite size value", 70.0f, SoundManager.getInstance().getVolume());

		float volume = SoundManager.getInstance().getVolume();
		volume += LOUDER;
		ChangeVolumeByBrick changeVolumeByBrick1 = new ChangeVolumeByBrick(sprite, LOUDER);
		changeVolumeByBrick1.execute();
		assertEquals("Incorrect sprite volume after ChangeVolumeByBrick executed", volume, SoundManager.getInstance()
				.getVolume());

		volume = SoundManager.getInstance().getVolume();
		volume += SOFTER;
		ChangeVolumeByBrick changeVolumeByBrick2 = new ChangeVolumeByBrick(sprite, SOFTER);
		changeVolumeByBrick2.execute();
		assertEquals("Incorrect sprite size value after SetSizeToBrick executed", volume, SoundManager.getInstance()
				.getVolume());
	}
}
