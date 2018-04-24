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
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.common.Constants.BACKPACK_IMAGE_DIRECTORY;
import static org.catrobat.catroid.common.Constants.BACKPACK_SOUND_DIRECTORY;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileDoesNotExist;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileExists;
import static org.catrobat.catroid.utils.PathBuilder.buildProjectPath;

@RunWith(AndroidJUnit4.class)
public class SpriteControllerTest {

	private Project project;
	private Scene scene;
	private Sprite sprite;

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
	public void testCopySprite() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite copy = controller.copy(sprite, scene, scene);

		assertEquals(2, scene.getSpriteList().size());

		assertEquals(sprite.getLookList().size(), copy.getLookList().size());
		assertEquals(sprite.getSoundList().size(), copy.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), copy.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), copy.getNumberOfBricks());

		assertLookFileExists(copy.getLookList().get(0).getFileName());
		assertFileExists(copy.getSoundList().get(0).getFile());
	}

	@Test
	public void testDeleteSprite() {
		SpriteController controller = new SpriteController();
		String deletedLookFileName = sprite.getLookList().get(0).getFileName();
		File deletedSoundFile = sprite.getSoundList().get(0).getFile();

		controller.delete(sprite, scene);

		assertEquals(2, scene.getSpriteList().size());

		assertLookFileDoesNotExist(deletedLookFileName);
		assertFileDoesNotExist(deletedSoundFile);
	}

	@Test
	public void testPackSprite() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite packedSprite = controller.pack(sprite);

		assertEquals(0, BackpackListManager.getInstance().getBackPackedSprites().size());

		assertEquals(sprite.getLookList().size(), packedSprite.getLookList().size());
		assertEquals(sprite.getSoundList().size(), packedSprite.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), packedSprite.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), packedSprite.getNumberOfBricks());

		assertFileExists(new File(BACKPACK_IMAGE_DIRECTORY, packedSprite.getLookList().get(0).getFileName()));
		assertFileExists(new File(BACKPACK_SOUND_DIRECTORY, packedSprite.getSoundList().get(0).getFile().getName()));
	}

	@Test
	public void testDeleteSpriteFromBackPack() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite packedSprite = controller.pack(sprite);

		controller.deleteFromBackpack(packedSprite);

		assertEquals(0, BackpackListManager.getInstance().getBackPackedSprites().size());
		assertFileDoesNotExist(new File(BACKPACK_IMAGE_DIRECTORY, packedSprite.getLookList().get(0).getFileName()));
		assertFileDoesNotExist(new File(BACKPACK_SOUND_DIRECTORY,
				packedSprite.getSoundList().get(0).getFile().getName()));
	}

	@Test
	public void testUnpackSprite() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite packedSprite = controller.pack(sprite);
		Sprite unpackedSprite = controller.unpack(packedSprite, scene);

		assertEquals(0, BackpackListManager.getInstance().getBackPackedSprites().size());

		assertFileExists(new File(BACKPACK_IMAGE_DIRECTORY, packedSprite.getLookList().get(0).getFileName()));
		assertFileExists(new File(BACKPACK_SOUND_DIRECTORY, packedSprite.getSoundList().get(0).getFile().getName()));

		assertEquals(2, scene.getSpriteList().size());

		assertEquals(sprite.getLookList().size(), unpackedSprite.getLookList().size());
		assertEquals(sprite.getSoundList().size(), unpackedSprite.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), unpackedSprite.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), unpackedSprite.getNumberOfBricks());

		assertLookFileExists(unpackedSprite.getLookList().get(0).getFileName());
		assertFileExists(unpackedSprite.getSoundList().get(0).getFile());
	}

	@Test
	public void testDeepCopySprite() throws IOException {
		SpriteController controller = new SpriteController();
		Sprite copy = controller.copy(sprite, scene, scene);

		assertEquals(2, scene.getSpriteList().size());

		assertEquals(sprite.getLookList().size(), copy.getLookList().size());
		assertEquals(sprite.getSoundList().size(), copy.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), copy.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), copy.getNumberOfBricks());

		assertLookFileExists(copy.getLookList().get(0).getFileName());
		assertFileExists(copy.getSoundList().get(0).getFile());

		controller.delete(sprite, scene);

		assertEquals(sprite.getLookList().size(), copy.getLookList().size());
		assertEquals(sprite.getSoundList().size(), copy.getSoundList().size());
		assertEquals(sprite.getNumberOfScripts(), copy.getNumberOfScripts());
		assertEquals(sprite.getNumberOfBricks(), copy.getNumberOfBricks());

		assertLookFileExists(copy.getLookList().get(0).getFileName());
		assertFileExists(copy.getSoundList().get(0).getFile());

		assertLookFileDoesNotExist(sprite.getLookList().get(0).getFileName());
		assertFileDoesNotExist(sprite.getSoundList().get(0).getFile());
	}

	private void assertLookFileExists(String fileName) {
		assertFileExists(new File(new File(scene.getDirectory(), Constants.IMAGE_DIRECTORY_NAME), fileName));
	}

	private void assertLookFileDoesNotExist(String fileName) {
		assertFileDoesNotExist(new File(new File(scene.getDirectory(), Constants.IMAGE_DIRECTORY_NAME), fileName));
	}

	private void clearBackPack() throws IOException {
		if (BACKPACK_IMAGE_DIRECTORY.exists()) {
			StorageHandler.deleteDir(BACKPACK_IMAGE_DIRECTORY);
		}
		if (BACKPACK_SOUND_DIRECTORY.exists()) {
			StorageHandler.deleteDir(BACKPACK_SOUND_DIRECTORY);
		}
		BACKPACK_IMAGE_DIRECTORY.mkdirs();
		BACKPACK_SOUND_DIRECTORY.mkdirs();
	}

	private void createProject() throws IOException {
		project = new Project(InstrumentationRegistry.getTargetContext(), "SpriteControllerTest");
		scene = project.getDefaultScene();
		ProjectManager.getInstance().setCurrentProject(project);

		sprite = new Sprite("testSprite");
		scene.addSprite(sprite);

		StartScript script = new StartScript();
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(0, 0);
		script.addBrick(placeAtBrick);
		sprite.addScript(script);

		File imageFile = FileTestUtils.copyResourceFileToProject(
				project.getName(), scene.getName(),
				"red_image.bmp",
				org.catrobat.catroid.test.R.raw.red_image,
				InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.IMAGE);

		sprite.getLookList().add(new LookData("testLook", imageFile.getName()));

		File soundFile = FileTestUtils.copyResourceFileToProject(
				project.getName(), ProjectManager.getInstance().getCurrentScene().getName(), "longsound.mp3",
				org.catrobat.catroid.test.R.raw.longsound, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.SOUND
		);

		sprite.getSoundList().add(new SoundInfo("testSound", soundFile));

		StorageHandler.getInstance().saveProject(project);
	}

	private void deleteProject() throws IOException {
		File projectDir = new File(buildProjectPath(project.getName()));
		if (projectDir.exists()) {
			StorageHandler.deleteDir(projectDir);
		}
	}
}
