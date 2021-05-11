/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.io.XStreamFieldKeyOrder;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.ui.controller.BackpackListManager;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;

@XStreamAlias("scene")
@XStreamFieldKeyOrder({
		"name",
		"objectList"
})
public class Scene implements Nameable, Serializable {

	private static final long serialVersionUID = 1L;

	@XStreamAlias("name")
	private String name;
	@XStreamAlias("objectList")
	private List<Sprite> spriteList = new ArrayList<>();

	private transient PhysicsWorld physicsWorld;
	private transient Project project;

	public transient boolean firstStart = true;

	public Scene() {
	}

	public Scene(String name, @NonNull Project project) {
		this.name = name;
		this.project = project;
	}

	public String getName() {
		return name;
	}

	public synchronized void setName(String name) {
		this.name = name;
	}

	public Project getProject() {
		return project;
	}

	public synchronized void setProject(Project project) {
		this.project = project;
	}

	public List<Sprite> getSpriteList() {
		return spriteList;
	}

	public Sprite getSprite(String spriteName) {
		for (Sprite sprite : spriteList) {
			if (spriteName.equals(sprite.getName())) {
				return sprite;
			}
		}
		return null;
	}

	public Sprite getBackgroundSprite() {
		if (spriteList.size() > 0) {
			return spriteList.get(0);
		}
		return null;
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

	public PhysicsWorld getPhysicsWorld() {
		if (physicsWorld == null) {
			resetPhysicsWorld();
		}
		return physicsWorld;
	}

	public synchronized PhysicsWorld resetPhysicsWorld() {
		return (physicsWorld = new PhysicsWorld(
				project.getXmlHeader().virtualScreenWidth,
				project.getXmlHeader().virtualScreenHeight));
	}

	public synchronized void setPhysicsWorld(PhysicsWorld world) {
		physicsWorld = world;
	}

	public File getDirectory() {
		if (project == null) {
			return new File(BackpackListManager.getInstance().backpackSceneDirectory,
					FileMetaDataExtractor.encodeSpecialCharsForFileSystem(name));
		} else {
			return new File(project.getDirectory(), FileMetaDataExtractor.encodeSpecialCharsForFileSystem(name));
		}
	}

	public void removeClonedSprites() {
		for (Iterator<Sprite> iterator = spriteList.iterator(); iterator.hasNext(); ) {
			Sprite sprite = iterator.next();
			if (sprite.isClone) {
				iterator.remove();
			}
		}
	}

	public void removeSpriteFromCloneBricks(Sprite spriteToDelete) {
		for (Sprite currentSprite : spriteList) {
			if (!currentSprite.equals(spriteToDelete)) {
				for (Script currentScript : currentSprite.getScriptList()) {
					List<Brick> flatList = new ArrayList();
					currentScript.addToFlatList(flatList);
					for (Brick currentBrick : flatList) {
						if (currentBrick instanceof CloneBrick) {
							CloneBrick cloneBrick = (CloneBrick) currentBrick;
							if (cloneBrick.getSelectedItem() != null
									&& cloneBrick.getSelectedItem().equals(spriteToDelete)) {
								cloneBrick.resetSpinner();
							}
						}
					}
				}
			}
		}
	}

	public Set<String> getBroadcastMessagesInUse() {
		Set<String> messagesInUse = new LinkedHashSet<>();
		for (Sprite currentSprite : spriteList) {
			for (Script currentScript : currentSprite.getScriptList()) {
				if (currentScript instanceof BroadcastScript) {
					messagesInUse.add(((BroadcastScript) currentScript).getBroadcastMessage());
				}
				List<Brick> flatList = new ArrayList();
				currentScript.addToFlatList(flatList);
				for (Brick currentBrick : flatList) {
					if (currentBrick instanceof BroadcastMessageBrick) {
						messagesInUse.add(((BroadcastMessageBrick) currentBrick).getBroadcastMessage());
					}
				}
			}
		}
		return messagesInUse;
	}

	public void editBroadcastMessagesInUse(String oldMessage, String newMessage) {
		for (Sprite currentSprite : spriteList) {
			for (Script currentScript : currentSprite.getScriptList()) {
				if (currentScript instanceof BroadcastScript) {
					BroadcastScript broadcastScript = (BroadcastScript) currentScript;
					if (broadcastScript.getBroadcastMessage().equals(oldMessage)) {
						broadcastScript.setBroadcastMessage(newMessage);
					}
				}
				List<Brick> flatList = new ArrayList();
				currentScript.addToFlatList(flatList);
				for (Brick currentBrick : flatList) {
					if (currentBrick instanceof BroadcastMessageBrick) {
						BroadcastMessageBrick broadcastMessageBrick = (BroadcastMessageBrick) currentBrick;
						if (broadcastMessageBrick.getBroadcastMessage().equals(oldMessage)) {
							broadcastMessageBrick.setBroadcastMessage(newMessage);
						}
					}
				}
			}
		}
	}

	public void updateUserDataReferences(String oldName, String newName, UserData<?> item) {
		if (ProjectManager.getInstance().getCurrentProject().isGlobalVariable(item)) {
			for (Sprite sprite : spriteList) {
				sprite.updateUserDataReferences(oldName, newName, item);
			}
		} else {
			ProjectManager.getInstance().getCurrentSprite().updateUserDataReferences(oldName, newName, item);
		}
	}

	public void deselectElements(List<UserData<?>> elements) {
		for (Sprite sprite : spriteList) {
			sprite.deselectElements(elements);
		}
	}

	public void checkForInvisibleSprites() {
		for (Sprite sprite : spriteList) {
			if (sprite instanceof GroupItemSprite && isInvisibleSprite(spriteList.indexOf(sprite))) {
				sprite.setConvertToSprite(true);
				Sprite convertedSprite = sprite.convert();
				spriteList.set(spriteList.indexOf(sprite), convertedSprite);
			}
		}
	}

	private boolean isInvisibleSprite(int index) {
		for (int spriteIndex = index - 1; spriteIndex > 0; spriteIndex--) {
			Sprite currentSprite = spriteList.get(spriteIndex);
			if (!(currentSprite instanceof GroupItemSprite)) {
				return !(currentSprite instanceof GroupSprite);
			}
		}
		return true;
	}
}
