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

package org.catrobat.catroid.test.content.controller;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.controller.SoundController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.common.Constants.BACKPACK_SOUND_DIRECTORY;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileDoesNotExist;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileDoesNotExistInDirectory;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileExists;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileExistsInDirectory;
import static org.catrobat.catroid.utils.PathBuilder.buildProjectPath;
import static org.catrobat.catroid.utils.PathBuilder.buildScenePath;

@RunWith(AndroidJUnit4.class)
public class SoundControllerTest {

	private Project project;
	private Scene scene;
	private Sprite sprite;
	private SoundInfo soundInfo;

	@Before
	public void setUp() throws IOException {
		clearBackPack();
		createProject();
	}

	@After
	public void tearDown() throws IOException {
		deleteProject();
		clearBackPack();
	}

	@Test
	public void testCopySound() throws IOException {
		SoundController controller = new SoundController();
		SoundInfo copy = controller.copy(soundInfo, scene, sprite);

		assertEquals(1, sprite.getSoundList().size());
		assertFileExists(copy.getFile());
	}

	@Test
	public void testDeleteSound() throws IOException {
		SoundController controller = new SoundController();
		File deletedSoundFile = soundInfo.getFile();
		controller.delete(soundInfo);

		assertEquals(1, sprite.getSoundList().size());
		assertFileDoesNotExist(deletedSoundFile);
	}

	@Test
	public void testPackSound() throws IOException {
		SoundController controller = new SoundController();
		SoundInfo packedSound = controller.pack(soundInfo);

		assertEquals(0, BackpackListManager.getInstance().getBackpackedSounds().size());

		assertFileExists(packedSound.getFile());
		assertFileExistsInDirectory(packedSound.getFile(), BACKPACK_SOUND_DIRECTORY);
	}

	@Test
	public void testDeleteSoundFromBackPack() throws IOException {
		SoundController controller = new SoundController();
		SoundInfo packedSound = controller.pack(soundInfo);
		controller.delete(packedSound);

		assertEquals(0, BackpackListManager.getInstance().getBackpackedSounds().size());

		assertFileDoesNotExist(packedSound.getFile());
		assertFileDoesNotExistInDirectory(packedSound.getFile(), BACKPACK_SOUND_DIRECTORY);

		assertEquals(1, sprite.getSoundList().size());
		assertFileExists(soundInfo.getFile());
	}

	@Test
	public void testUnpackSound() throws IOException {
		SoundController controller = new SoundController();
		SoundInfo packedSound = controller.pack(soundInfo);
		SoundInfo unpackedSound = controller.unpack(packedSound, scene, sprite);

		assertEquals(0, BackpackListManager.getInstance().getBackpackedSounds().size());

		assertFileExists(packedSound.getFile());
		assertFileExistsInDirectory(packedSound.getFile(), BACKPACK_SOUND_DIRECTORY);

		assertEquals(1, sprite.getSoundList().size());
		assertFileExists(unpackedSound.getFile());
	}

	@Test
	public void testDeepCopySound() throws IOException {
		SoundController controller = new SoundController();
		SoundInfo copy = controller.copy(soundInfo, scene, sprite);

		assertFileExists(copy.getFile());

		controller.delete(copy);

		assertFileDoesNotExist(copy.getFile());
		assertFileExists(soundInfo.getFile());
	}

	private void clearBackPack() throws IOException {
		if (BACKPACK_SOUND_DIRECTORY.exists()) {
			StorageOperations.deleteDir(BACKPACK_SOUND_DIRECTORY);
		}
		BACKPACK_SOUND_DIRECTORY.mkdirs();
	}

	private void createProject() throws IOException {
		project = new Project(InstrumentationRegistry.getTargetContext(), "SoundControllerTest");
		scene = project.getDefaultScene();
		ProjectManager.getInstance().setCurrentProject(project);

		sprite = new Sprite("testSprite");
		scene.addSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);

		File soundFile = ResourceImporter.createSoundFileFromResourcesInDirectory(
				InstrumentationRegistry.getContext().getResources(),
				org.catrobat.catroid.test.R.raw.longsound,
				new File(buildScenePath(project.getName(), project.getDefaultScene().getName()), SOUND_DIRECTORY_NAME),
				"longsound.mp3");

		soundInfo = new SoundInfo("testSound", soundFile);
		sprite.getSoundList().add(soundInfo);

		XstreamSerializer.getInstance().saveProject(project);
	}

	private void deleteProject() throws IOException {
		File projectDir = new File(buildProjectPath(project.getName()));
		if (projectDir.exists()) {
			StorageOperations.deleteDir(projectDir);
		}
	}
}
