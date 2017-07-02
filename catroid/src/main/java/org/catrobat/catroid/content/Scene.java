/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.content;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickWithSpriteReference;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserListBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.XStreamFieldKeyOrder;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("scene")
// Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
// CHECKSTYLE DISABLE IndentationCheck FOR 7 LINES
@XStreamFieldKeyOrder({
		"name",
		"objectList",
		"data",
		"originalWidth",
		"originalHeight"
})
public class Scene implements Serializable {

	private static final long serialVersionUID = 1L;

	@XStreamAlias("name")
	private String sceneName;
	@XStreamAlias("objectList")
	private List<Sprite> spriteList = new ArrayList<>();
	@XStreamAlias("data")
	private DataContainer dataContainer = null;
	@XStreamAlias("originalWidth")
	private int originalWidth = 0;
	@XStreamAlias("originalHeight")
	private int originalHeight = 0;

	private transient PhysicsWorld physicsWorld;
	private transient Project project;
	public transient boolean firstStart = true;
	public transient boolean isBackPackScene = false;

	public Scene(Context context, String name, Project project) {
		sceneName = name;
		dataContainer = new DataContainer(project);
		this.project = project;

		if (project != null) {
			originalWidth = project.getXmlHeader().virtualScreenWidth;
			originalHeight = project.getXmlHeader().virtualScreenHeight;
		}

		if (context == null) {
			return;
		}

		Sprite background;
		try {
			background = new SingleSprite(context.getString(R.string.background));
		} catch (Resources.NotFoundException e) {
			//Because in test project we can't find the string
			background = new SingleSprite("Background");
		}
		background.look.setZIndex(0);
		addSprite(background);
	}

	public String getName() {
		return sceneName;
	}

	public synchronized void addSprite(Sprite sprite) {
		if (spriteList.contains(sprite)) {
			return;
		}
		spriteList.add(sprite);
	}

	public synchronized boolean removeSprite(Sprite sprite) {
		return spriteList.remove(sprite);
	}

	@Override
	public Scene clone() {
		Scene clonedScene = new Scene();
		clonedScene.sceneName = sceneName;
		clonedScene.originalWidth = originalWidth;
		clonedScene.originalHeight = originalHeight;
		clonedScene.physicsWorld = new PhysicsWorld(originalWidth, originalHeight);
		clonedScene.project = project;
		clonedScene.firstStart = firstStart;
		clonedScene.isBackPackScene = isBackPackScene;

		clonedScene.dataContainer = new DataContainer(project);
		ProjectManager.getInstance().setCurrentScene(this);
		for (Sprite sprite : spriteList) {
			Sprite cloneSprite = sprite.cloneForScene();
			for (UserBrick userBrick : cloneSprite.getUserBrickList()) {
				ProjectManager.getInstance().setCurrentScene(clonedScene);
				userBrick.updateUserBrickParametersAndVariables();
				ProjectManager.getInstance().setCurrentScene(this);
			}
			clonedScene.spriteList.add(cloneSprite);
		}

		clonedScene.dataContainer.cloneSpriteListsForScene(clonedScene, dataContainer);
		clonedScene.dataContainer.cloneSpriteVariablesForScene(clonedScene, dataContainer);
		if (!isBackPackScene) {
			clonedScene.correctUserVariableAndListReferences();
		}

		return clonedScene;
	}

	public Scene cloneForBackPack() {
		Scene clonedScene;
		try {
			ProjectManager.getInstance().checkNestingBrickReferences(false, true, true);
			clonedScene = clone();
			clonedScene.isBackPackScene = false;
			if (!clonedScene.mergeProjectVariables()) {
				return null;
			}
			clonedScene.correctUserVariableAndListReferences();
		} catch (Exception e) {
			Log.e("Scene.cloneForBackpack", e.getMessage());
			return null;
		}

		return clonedScene;
	}

	public boolean mergeProjectVariables() {
		List<String> variables = new ArrayList<>();
		List<String> lists = new ArrayList<>();

		for (Sprite sprite : spriteList) {
			for (Brick brick : sprite.getListWithAllBricks()) {
				if (brick instanceof UserVariableBrick && !variables.contains(((UserVariableBrick) brick)
						.getUserVariable().getName())) {
					variables.add(((UserVariableBrick) brick).getUserVariable().getName());
				}
				if (brick instanceof UserListBrick
						&& !lists.contains(((UserListBrick) brick).getUserList().getName())) {
					lists.add(((UserListBrick) brick).getUserList().getName());
				}
				if (brick instanceof FormulaBrick) {
					for (Formula formula : ((FormulaBrick) brick).getFormulas()) {
						formula.getVariableAndListNames(variables, lists);
					}
				}
			}
		}

		for (String variable : variables) {
			if (dataContainer.existVariableInAnySprite(variable, spriteList)) {
				return false;
			}
			if (!dataContainer.existProjectVariableWithName(variable) && !dataContainer.existUserVariableWithName(variable)) {
				dataContainer.addProjectUserVariable(variable);
			}
		}

		for (String list : lists) {
			if (dataContainer.existListInAnySprite(list, spriteList)) {
				return false;
			}
			if (!dataContainer.existProjectListWithName(list)) {
				dataContainer.addProjectUserList(list);
			}
		}

		return true;
	}

	public boolean isBackgroundObject(Sprite sprite) {
		if (spriteList.indexOf(sprite) == 0) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return sceneName;
	}

	public int getOriginalWidth() {
		return originalWidth;
	}

	public int getOriginalHeight() {
		return originalHeight;
	}

	public List<Sprite> getSpriteList() {
		return spriteList;
	}

	public void removeAllClones() {
		ProjectManager.getInstance().getCurrentProject().removeInvalidVariablesAndLists(dataContainer);
		dataContainer.removeVariablesOfClones();
		for (Sprite s : new ArrayList<>(spriteList)) {
			if (s.isClone) {
				spriteList.remove(s);
			}
		}
	}

	public synchronized void setSpriteList(List<Sprite> spriteList) {
		this.spriteList = spriteList;
	}

	public synchronized void setDataContainer(DataContainer container) {
		dataContainer = container;
	}

	public synchronized void setPhysicsWorld(PhysicsWorld world) {
		physicsWorld = world;
	}

	public synchronized void resetDataContainerForDefaultScene() {
		dataContainer = new DataContainer(project);
	}

	public synchronized boolean rename(String name, Context context, boolean showError) {
		if (name.equals(getName())) {
			return true;
		}
		File oldSceneDirectory = new File(Utils.buildScenePath(project.getName(), sceneName));
		File newSceneDirectory = new File(Utils.buildScenePath(project.getName(), name));
		String oldName = sceneName;

		boolean directoryRenamed = true;

		if (!oldSceneDirectory.getAbsolutePath().equalsIgnoreCase(newSceneDirectory.getAbsolutePath())) {
			directoryRenamed = oldSceneDirectory.renameTo(newSceneDirectory);
		}

		if (directoryRenamed) {
			sceneName = name;
			ProjectManager.getInstance().saveProject(context);
		}

		if (!directoryRenamed) {
			if (showError) {
				ToastUtil.showError(context, R.string.error_rename_scene);
			}
			Log.e("Scene", "rename: could not rename " + oldSceneDirectory.getAbsolutePath() + " to " + newSceneDirectory.getAbsolutePath());
			return false;
		}

		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			for (Sprite sprite : scene.spriteList) {
				for (Brick brick : sprite.getListWithAllBricks()) {
					if (brick instanceof SceneStartBrick && ((SceneStartBrick) brick).getSceneToStart().equals(oldName)) {
						((SceneStartBrick) brick).setSceneToStart(name);
					}
					if (brick instanceof SceneTransitionBrick && ((SceneTransitionBrick) brick).getSceneForTransition().equals(oldName)) {
						((SceneTransitionBrick) brick).setSceneForTransition(name);
					}
				}
			}
		}

		return true;
	}

	public synchronized void setSceneName(String name) {
		sceneName = name;
	}

	public PhysicsWorld getPhysicsWorld() {
		if (physicsWorld == null) {
			resetPhysicsWorld();
		}
		return physicsWorld;
	}

	public synchronized PhysicsWorld resetPhysicsWorld() {
		return (physicsWorld = new PhysicsWorld(project.getXmlHeader().virtualScreenWidth, project.getXmlHeader()
				.virtualScreenHeight));
	}

	// default constructor for XMLParser
	public Scene() {
	}

	public synchronized void setProject(Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

	public DataContainer getDataContainer() {
		return dataContainer;
	}

	public synchronized void addUsedMessagesToList(List<String> usedMessages) {
		for (Sprite currentSprite : spriteList) {
			for (int scriptIndex = 0; scriptIndex < currentSprite.getNumberOfScripts(); scriptIndex++) {
				Script currentScript = currentSprite.getScript(scriptIndex);
				if (currentScript instanceof BroadcastMessage) {
					addBroadcastMessage(((BroadcastMessage) currentScript).getBroadcastMessage(), usedMessages);
				}

				for (int brickIndex = 0; brickIndex < currentScript.getBrickList().size(); brickIndex++) {
					Brick currentBrick = currentScript.getBrick(brickIndex);
					if (currentBrick instanceof BroadcastMessage) {
						addBroadcastMessage(((BroadcastMessage) currentBrick).getBroadcastMessage(), usedMessages);
					}
				}
			}
			for (UserBrick userBrick : currentSprite.getUserBrickList()) {
				Script userScript = userBrick.getDefinitionBrick().getUserScript();
				for (Brick currentBrick : userScript.getBrickList()) {
					if (currentBrick instanceof BroadcastMessage) {
						addBroadcastMessage(((BroadcastMessage) currentBrick).getBroadcastMessage(), usedMessages);
					}
				}
			}
		}
	}

	private void addBroadcastMessage(String broadcastMessageToAdd, List<String> broadcastMessages) {
		if (broadcastMessageToAdd != null && !broadcastMessageToAdd.isEmpty()
				&& !broadcastMessages.contains(broadcastMessageToAdd)) {
			broadcastMessages.add(broadcastMessageToAdd);
		}
	}

	public boolean screenshotExists(String screenshotName) {
		File screenShot = new File(Utils.buildScenePath(project.getName(), getName()), screenshotName);
		if (screenShot.exists()) {
			return false;
		}
		return true;
	}

	public boolean containsSpriteBySpriteName(String searchedSprite) {
		return getSpriteBySpriteName(searchedSprite) != null;
	}

	public UserVariable getProjectVariableWithName(String name) {
		for (UserVariable variable : dataContainer.getProjectVariables()) {
			if (name.equals(variable.getName())) {
				return variable;
			}
		}
		return null;
	}

	public UserList getProjectListWithName(String name) {
		for (UserList list : dataContainer.getProjectLists()) {
			if (name.equals(list.getName())) {
				return list;
			}
		}
		return null;
	}

	public boolean existProjectVariable(UserVariable variable) {
		return dataContainer.existProjectVariable(variable);
	}

	public boolean existSpriteVariable(UserVariable variable, Sprite sprite) {
		if (!spriteList.contains(sprite)) {
			return false;
		}
		return dataContainer.existSpriteVariable(variable, sprite);
	}

	public boolean existProjectList(UserList list) {
		return dataContainer.existProjectList(list);
	}

	public boolean existSpriteList(UserList list, Sprite sprite) {
		if (!spriteList.contains(sprite)) {
			return false;
		}
		return dataContainer.existSpriteList(list, sprite);
	}

	public Sprite getSpriteByUserVariable(UserVariable variable) {
		Sprite spriteByUserVariable = null;
		for (Sprite sprite : spriteList) {
			if (dataContainer.existSpriteVariable(variable, sprite)) {
				spriteByUserVariable = sprite;
				break;
			}
		}
		return spriteByUserVariable;
	}

	public Sprite getSpriteByUserList(UserList list) {
		Sprite spriteByUserList = null;
		for (Sprite sprite : spriteList) {
			if (dataContainer.existSpriteList(list, sprite)) {
				spriteByUserList = sprite;
				break;
			}
		}
		return spriteByUserList;
	}

	public Sprite getSpriteBySpriteName(String searchedSprite) {
		Sprite spriteBySpriteName = null;
		for (Sprite sprite : spriteList) {
			if (searchedSprite.equals(sprite.getName())) {
				spriteBySpriteName = sprite;
				break;
			}
		}
		return spriteBySpriteName;
	}

	public synchronized void replaceBackgroundSprite(Sprite unpackedSprite) {
		unpackedSprite.setName(spriteList.get(0).getName());
		spriteList.set(0, unpackedSprite);
	}

	public boolean containsSprite(Sprite selectedSprite) {
		for (Sprite sprite : spriteList) {
			if (sprite.equals(selectedSprite)) {
				return true;
			}
		}
		return false;
	}

	public synchronized void correctUserVariableAndListReferences() {
		for (Sprite sprite : spriteList) {
			for (Brick brick : sprite.getListWithAllBricks()) {
				if (brick instanceof UserVariableBrick) {
					((UserVariableBrick) brick).setUserVariable(dataContainer.getUserVariable(((UserVariableBrick)
							brick).getUserVariable().getName(), sprite));
				}

				if (brick instanceof UserListBrick) {
					((UserListBrick) brick).setUserList(dataContainer.getUserList(((UserListBrick) brick).getUserList().getName(), sprite));
				}
			}
		}
	}

	public void refreshSpriteReferences() {
		for (Brick brick : getAllBricks()) {
			if (!(brick instanceof BrickWithSpriteReference)) {
				continue;
			}

			BrickWithSpriteReference referenceBrick = ((BrickWithSpriteReference) brick);
			Sprite referencedSprite = referenceBrick.getSprite();
			if (referencedSprite == null) {
				continue;
			}

			Sprite newSprite = getSpriteBySpriteName(referencedSprite.getName());
			referenceBrick.setSprite(newSprite);
		}
	}

	public List<Brick> getAllBricks() {
		List<Brick> result = new ArrayList<>();
		for (Sprite sprite : spriteList) {
			result.addAll(sprite.getAllBricks());
		}
		return result;
	}
}
