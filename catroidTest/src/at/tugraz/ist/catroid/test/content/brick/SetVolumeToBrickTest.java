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

import java.io.File;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetVolumeToBrick;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class SetVolumeToBrickTest extends InstrumentationTestCase {

	private static final int SOUND_FILE_ID = R.raw.testsound;
	private static final String TEST_PROJECT_NAME = TestUtils.TEST_PROJECT_NAME1;
	private static final float VOLUME = 50.6f;

	private File soundFile;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Project project = new Project(getInstrumentation().getTargetContext(), TEST_PROJECT_NAME);
		assertTrue("cannot save project", TestUtils.saveProjectAndWait(this, project));
		ProjectManager.getInstance().setProject(project);

		soundFile = TestUtils.saveFileToProject(TEST_PROJECT_NAME, "soundTest.mp3", SOUND_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_SOUND_FILE);
	}

	@Override
	protected void tearDown() throws Exception {
		if (soundFile != null && soundFile.exists()) {
			soundFile.delete();
		}
		SoundManager.getInstance().clear();
		TestUtils.deleteTestProjects();
		super.tearDown();
	}

	public void testVolume() {
		Sprite sprite = new Sprite("testSprite");
		SetVolumeToBrick setVolumeToBrick = new SetVolumeToBrick(sprite, VOLUME);
		setVolumeToBrick.execute();
		assertEquals("Incorrect sprite volume value after SetVolumeToBrick executed", VOLUME, SoundManager
				.getInstance().getVolume());
	}
}
