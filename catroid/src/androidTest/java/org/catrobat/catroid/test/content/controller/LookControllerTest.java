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
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.controller.LookController;
import org.catrobat.catroid.uiespresso.util.FileTestUtils;
import org.catrobat.catroid.utils.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileDoesNotExist;
import static org.catrobat.catroid.uiespresso.util.FileTestUtils.assertFileExists;
import static org.catrobat.catroid.utils.Utils.buildProjectPath;

@RunWith(AndroidJUnit4.class)
public class LookControllerTest {

	private LookData redLookData;
	private Project project;
	private Sprite sprite;

	private static final String BACK_PACK_LOOKS_PATH = Utils.buildPath(
			Constants.DEFAULT_ROOT,
			Constants.BACKPACK_DIRECTORY,
			Constants.BACKPACK_IMAGE_DIRECTORY);

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
	public void testCopyLook() throws IOException {
		LookController controller = new LookController();
		LookData copy = controller.copy(redLookData, project.getDefaultScene(), project.getDefaultScene(), sprite);

		assertEquals(1, sprite.getLookList().size());
		assertLookFileExists(copy.getFileName());
	}

	@Test
	public void testDeleteLook() throws IOException {
		LookController controller = new LookController();
		String deletedLookFileName = redLookData.getFileName();
		controller.delete(redLookData, project.getDefaultScene());

		assertEquals(1, sprite.getLookList().size());
		assertLookFileDoesNotExist(deletedLookFileName);
	}

	@Test
	public void testPackLook() throws IOException {
		LookController controller = new LookController();
		LookData packedLook = controller.pack(redLookData, project.getDefaultScene());

		assertEquals(0, BackPackListManager.getInstance().getBackPackedLooks().size());
		assertFileExists(BACK_PACK_LOOKS_PATH, packedLook.getFileName());
	}

	@Test
	public void testDeleteLookFromBackPack() throws IOException {
		LookController controller = new LookController();
		LookData packedLook = controller.pack(redLookData, project.getDefaultScene());
		controller.deleteFromBackpack(packedLook);

		assertEquals(0, BackPackListManager.getInstance().getBackPackedLooks().size());
		assertFileDoesNotExist(BACK_PACK_LOOKS_PATH, packedLook.getFileName());

		assertEquals(1, sprite.getLookList().size());
		assertLookFileExists(redLookData.getFileName());
	}

	@Test
	public void testUnPackLook() throws IOException {
		LookController controller = new LookController();
		LookData packedLook = controller.pack(redLookData, project.getDefaultScene());
		LookData unpackedLook = controller.unpack(packedLook, project.getDefaultScene(), sprite);

		assertEquals(0, BackPackListManager.getInstance().getBackPackedLooks().size());
		assertFileExists(BACK_PACK_LOOKS_PATH, packedLook.getFileName());

		assertEquals(1, sprite.getLookList().size());
		assertLookFileExists(unpackedLook.getFileName());
	}

	@Test
	public void testDeepCopyLook() throws IOException {
		LookController controller = new LookController();
		LookData copy = controller.copy(redLookData, project.getDefaultScene(), project.getDefaultScene(), sprite);

		assertLookFileExists(copy.getFileName());

		controller.delete(copy, project.getDefaultScene());

		assertLookFileDoesNotExist(copy.getFileName());
		assertLookFileExists(redLookData.getFileName());
	}

	private void assertLookFileExists(String fileName) {
		assertFileExists(project.getDefaultScene().getPath(), Constants.IMAGE_DIRECTORY, fileName);
	}

	private void assertLookFileDoesNotExist(String fileName) {
		assertFileDoesNotExist(project.getDefaultScene().getPath(), Constants.IMAGE_DIRECTORY, fileName);
	}

	private void clearBackPack() throws IOException {
		File backPackDir = new File(BACK_PACK_LOOKS_PATH);
		if (backPackDir.exists()) {
			StorageHandler.deleteDir(BACK_PACK_LOOKS_PATH);
		}
		backPackDir.mkdirs();
	}

	private void createProject() {
		project = new Project(InstrumentationRegistry.getTargetContext(), "lookControllerTest");

		sprite = new Sprite("testSprite");

		redLookData = new LookData();
		String redImageName = "red_image.bmp";
		redLookData.setName(redImageName);
		sprite.getLookList().add(redLookData);

		project.getDefaultScene().addSprite(sprite);
		StorageHandler.getInstance().saveProject(project);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentScene(project.getDefaultScene());

		File redImageFile = FileTestUtils.saveFileToProject(project.getName(), project.getDefaultScene().getName(),
				redImageName,
				org.catrobat.catroid.test.R.raw.red_image, InstrumentationRegistry.getContext(),
				FileTestUtils.FileTypes.IMAGE);

		redLookData.setFileName(redImageFile.getName());
		StorageHandler.getInstance().saveProject(project);
	}

	private void deleteProject() throws IOException {
		File projectDir = new File(buildProjectPath(project.getName()));
		if (projectDir.exists()) {
			StorageHandler.deleteDir(buildProjectPath(project.getName()));
		}
	}
}
