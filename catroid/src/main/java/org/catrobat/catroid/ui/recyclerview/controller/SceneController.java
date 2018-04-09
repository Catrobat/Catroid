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
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SceneController {

	private static final String TAG = SceneController.class.getSimpleName();

	private UniqueNameProvider uniqueNameProvider = new UniqueNameProvider();
	private SpriteController spriteController = new SpriteController();

	public Scene copy(Scene sceneToCopy, Project dstProject) throws IOException {
		String name = uniqueNameProvider.getUniqueName(
				sceneToCopy.getName(),
				getScope(dstProject.getSceneList()));

		String dir = Utils.buildScenePath(dstProject.getName(), name);

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
		StorageHandler.deleteDir(sceneToDelete.getPath());
	}

	public Scene pack(Scene sceneToPack) throws IOException {
		String name = uniqueNameProvider.getUniqueName(
				sceneToPack.getName(),
				getScope(BackPackListManager.getInstance().getBackPackedScenes()));

		String dir = Utils.buildBackpackScenePath(name);

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
		Scene scene = copy(sceneToUnpack, dstProject);
		return scene;
	}

	private boolean createDirectory(String dirPath) {
		File dir = new File(dirPath);
		File imgDir = new File(Utils.buildPath(dirPath, Constants.IMAGE_DIRECTORY));
		File sndDir = new File(Utils.buildPath(dirPath, Constants.SOUND_DIRECTORY));

		dir.mkdir();
		imgDir.mkdir();
		sndDir.mkdir();

		if (!imgDir.isDirectory() || !sndDir.isDirectory()) {
			if (dir.isDirectory()) {
				try {
					StorageHandler.deleteDir(dirPath);
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
