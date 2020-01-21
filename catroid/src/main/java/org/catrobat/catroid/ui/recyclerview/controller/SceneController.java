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

package org.catrobat.catroid.ui.recyclerview.controller;

import android.content.res.Resources;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SOUND_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.Z_INDEX_BACKGROUND;

public class SceneController {

	private static final String TAG = SceneController.class.getSimpleName();

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();
	private SpriteController spriteController = new SpriteController();

	public static String getUniqueDefaultSceneName(Resources resources, List<Scene> scope) {

		for (int i = 1; i < Integer.MAX_VALUE; i++) {
			String name = resources.getString(R.string.default_scene_name, i);

			boolean isNameUnique = true;
			for (Scene scene : scope) {
				if (scene.getName().equals(name)) {
					isNameUnique = false;
				}
			}

			if (isNameUnique) {
				return name;
			}
		}

		throw new IllegalStateException("Could not find new Scene name.");
	}

	public static Scene newSceneWithBackgroundSprite(String sceneName, String backgroundName, Project dstProject) {
		Scene scene = new Scene(sceneName, dstProject);
		Sprite backgroundSprite = new Sprite(backgroundName);
		backgroundSprite.look.setZIndex(Z_INDEX_BACKGROUND);
		scene.addSprite(backgroundSprite);
		return scene;
	}

	public boolean rename(Scene sceneToRename, String name) {
		String previousName = sceneToRename.getName();
		String encodedName = FileMetaDataExtractor.encodeSpecialCharsForFileSystem(name);
		File newDir = new File(sceneToRename.getProject().getDirectory(), encodedName);
		boolean renamed = sceneToRename.getDirectory().renameTo(newDir);

		if (renamed) {
			sceneToRename.setName(name);
			for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
				for (Sprite sprite : scene.getSpriteList()) {
					for (Brick brick : sprite.getAllBricks()) {
						if (brick instanceof SceneStartBrick
								&& ((SceneStartBrick) brick).getSceneToStart().equals(previousName)) {
							((SceneStartBrick) brick).setSceneToStart(name);
						}
						if (brick instanceof SceneTransitionBrick
								&& ((SceneTransitionBrick) brick).getSceneForTransition().equals(previousName)) {
							((SceneTransitionBrick) brick).setSceneForTransition(name);
						}
					}
				}
			}
		}

		return renamed;
	}

	public Scene copy(Scene sceneToCopy, Project dstProject) throws IOException {
		String name = uniqueNameProvider.getUniqueNameInNameables(sceneToCopy.getName(), dstProject.getSceneList());

		Scene scene = new Scene();
		scene.setName(name);
		scene.setProject(dstProject);

		if (!createDirectory(scene.getDirectory())) {
			throw new IOException("Directory for Scene " + name + " could not be created.");
		}

		scene.setPhysicsWorld(new PhysicsWorld());

		for (Sprite sprite : sceneToCopy.getSpriteList()) {
			scene.getSpriteList().add(spriteController.copy(sprite, dstProject, scene));
		}

		return scene;
	}

	public void delete(Scene sceneToDelete) throws IOException {
		StorageOperations.deleteDir(sceneToDelete.getDirectory());
	}

	public Scene pack(Scene sceneToPack) throws IOException {
		String name = uniqueNameProvider
				.getUniqueNameInNameables(sceneToPack.getName(), BackpackListManager.getInstance().getScenes());

		Scene scene = new Scene();
		scene.setName(name);
		scene.setProject(null);

		if (!createDirectory(scene.getDirectory())) {
			throw new IOException("Directory for Scene " + name + " could not be created.");
		}

		for (Sprite sprite : sceneToPack.getSpriteList()) {
			scene.getSpriteList().add(spriteController.copy(sprite, null, scene));
		}

		return scene;
	}

	public Scene unpack(Scene sceneToUnpack, Project dstProject) throws IOException {
		return copy(sceneToUnpack, dstProject);
	}

	private boolean createDirectory(File dir) {
		File imageDir = new File(dir, IMAGE_DIRECTORY_NAME);
		File soundDir = new File(dir, SOUND_DIRECTORY_NAME);

		dir.mkdir();
		imageDir.mkdir();
		soundDir.mkdir();

		if (!imageDir.isDirectory() || !soundDir.isDirectory()) {
			if (dir.isDirectory()) {
				try {
					StorageOperations.deleteDir(dir);
				} catch (IOException e) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
			}
			return false;
		}
		return true;
	}
}
