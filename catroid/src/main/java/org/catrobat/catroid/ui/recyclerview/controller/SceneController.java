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

import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.utils.PathBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.catrobat.catroid.common.Constants.BACKPACK_SCENE_DIRECTORY;

public class SceneController {

	private static final String TAG = SceneController.class.getSimpleName();

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();
	private SpriteController spriteController = new SpriteController();

	public Scene copy(Scene sceneToCopy, Project dstProject) throws IOException {
		String name = uniqueNameProvider.getUniqueName(
				sceneToCopy.getName(),
				getScope(dstProject.getSceneList()));

		File dir = new File(PathBuilder.buildScenePath(dstProject.getName(), name));

		if (!createDirectory(dir)) {
			throw new IOException("DstDir for Scene" + name + "could not be created.");
		}

		Scene scene = new Scene();
		scene.setName(name);

		scene.setProject(dstProject);

		scene.setPhysicsWorld(new PhysicsWorld());
		scene.setDataContainer(new DataContainer(dstProject));

		for (Sprite sprite : sceneToCopy.getSpriteList()) {
			scene.getSpriteList().add(spriteController.copy(sprite, sceneToCopy, scene));
		}

		scene.mergeProjectVariables();
		scene.correctUserVariableAndListReferences();

		return scene;
	}

	public void delete(Scene sceneToDelete) throws IOException {
		StorageOperations.deleteDir(sceneToDelete.getDirectory());
	}

	public Scene pack(Scene sceneToPack) throws IOException {
		String name = uniqueNameProvider.getUniqueName(
				sceneToPack.getName(),
				getScope(BackpackListManager.getInstance().getBackpackedScenes()));

		File dir = new File(BACKPACK_SCENE_DIRECTORY, name);

		if (!createDirectory(dir)) {
			throw new IOException("DstDir for Scene" + name + "could not be created.");
		}

		Scene scene = new Scene();
		scene.setName(name);

		scene.setProject(null);
		scene.isBackPackScene = true;

		for (Sprite sprite : sceneToPack.getSpriteList()) {
			scene.getSpriteList().add(spriteController.copy(sprite, sceneToPack, scene));
		}

		return scene;
	}

	public Scene unpack(Scene sceneToUnpack, Project dstProject) throws IOException {
		return copy(sceneToUnpack, dstProject);
	}

	private boolean createDirectory(File dir) {
		File imageDir = new File(dir, Constants.IMAGE_DIRECTORY_NAME);
		File soundDir = new File(dir, Constants.SOUND_DIRECTORY_NAME);

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

	private Set<String> getScope(List<Scene> items) {
		Set<String> scope = new HashSet<>();
		for (Scene item : items) {
			scope.add(item.getName());
		}
		return scope;
	}
}
