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

import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BroadcastSequenceMap;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sprite implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = Sprite.class.getSimpleName();

	public transient Look look = new Look(this);
	public transient boolean isPaused;
	public transient boolean isBackpackObject = false;
	@XStreamAsAttribute
	private String name;
	private List<Script> scriptList = new ArrayList<>();
	private List<LookData> lookList = new ArrayList<>();
	private List<SoundInfo> soundList = new ArrayList<>();
	private List<UserBrick> userBricks = new ArrayList<>();
	private List<NfcTagData> nfcTagList = new ArrayList<>();
	private transient ActionFactory actionFactory = new ActionFactory();

	public Sprite(String name) {
		this.name = name;
	}

	public Sprite() {
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Sprite)) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		Sprite sprite = (Sprite) obj;
		return sprite.name.equals(this.name);
	}

	@Override
	public int hashCode() {
		return super.hashCode() * TAG.hashCode();
	}

	public List<Script> getScriptList() {
		return scriptList;
	}

	public List<Brick> getListWithAllBricks() {
		List<Brick> allBricks = new ArrayList<>();
		for (Script script : scriptList) {
			allBricks.add(script.getScriptBrick());
			allBricks.addAll(script.getBrickList());
		}
		for (UserBrick userBrick : userBricks) {
			allBricks.add(userBrick);
			Script userScript = userBrick.getDefinitionBrick().getUserScript();
			if (userScript != null) {
				allBricks.addAll(userScript.getBrickList());
			}
		}
		return allBricks;
	}

	public List<Brick> getAllBricks() {
		List<Brick> result = new ArrayList<>();
		for (Script script : scriptList) {
			for (Brick brick : script.getBrickList()) {
				result.add(brick);
			}
		}
		for (UserBrick userBrick : userBricks) {
			result.add(userBrick);
			Script userScript = userBrick.getDefinitionBrick().getUserScript();
			for (Brick brick : userScript.getBrickList()) {
				result.add(brick);
			}
		}
		return result;
	}

	public List<PlaySoundBrick> getPlaySoundBricks() {
		List<PlaySoundBrick> result = new ArrayList<>();
		for (Brick brick : getAllBricks()) {
			if (brick instanceof PlaySoundBrick) {
				result.add((PlaySoundBrick) brick);
			}
		}
		return result;
	}

	public void resetSprite() {
		if ((getRequiredResources() & Brick.PHYSICS) > 0) {
			PhysicsWorld physicsWorld = ProjectManager.getInstance().getCurrentProject().getPhysicsWorld();
			look = new PhysicsLook(this, physicsWorld);
		} else {
			look = new Look(this);
		}
		for (LookData lookData : lookList) {
			lookData.resetLookData();
		}
	}

	public void removeUserBrick(UserBrick brickToRemove) {
		for (UserBrick userBrick : userBricks) {
			userBrick.getDefinitionBrick().getUserScript().removeInstancesOfUserBrick(brickToRemove);
		}

		for (Script script : scriptList) {
			script.removeInstancesOfUserBrick(brickToRemove);
		}

		userBricks.remove(brickToRemove);
	}

	public UserBrick addUserBrick(UserBrick brick) {
		if (userBricks == null) {
			userBricks = new ArrayList<>();
		}
		userBricks.add(brick);
		return brick;
	}

	public List<UserBrick> getUserBrickList() {
		if (userBricks == null) {
			userBricks = new ArrayList<>();
		}
		return userBricks;
	}

	public List<UserBrick> getUserBricksByDefinitionBrick(UserScriptDefinitionBrick definitionBrick, boolean scriptBricks, boolean prototypeBricks) {
		List<UserBrick> matchingUserBricks = new ArrayList<>();
		if (scriptBricks) {
			for (Brick brick : getAllBricks()) {
				if (brick instanceof UserBrick) {
					UserBrick userBrick = (UserBrick) brick;
					if (userBrick.getDefinitionBrick().equals(definitionBrick)) {
						matchingUserBricks.add(userBrick);
					}
				}
			}
		}

		if (prototypeBricks) {
			for (UserBrick userBrick : userBricks) {
				if (userBrick.getDefinitionBrick().equals(definitionBrick)) {
					matchingUserBricks.add(userBrick);
				}
			}
		}

		return matchingUserBricks;
	}

	public void createStartScriptActionSequenceAndPutToMap(Map<String, List<String>> scriptActions) {
		for (int scriptCounter = 0; scriptCounter < scriptList.size(); scriptCounter++) {
			Script script = scriptList.get(scriptCounter);
			if (script instanceof StartScript) {
				Action sequenceAction = createActionSequence(script);
				look.addAction(sequenceAction);
				BroadcastHandler.getActionScriptMap().put(sequenceAction, script);
				BroadcastHandler.getScriptSpriteMapMap().put(script, this);
				String actionName = sequenceAction.toString() + Constants.ACTION_SPRITE_SEPARATOR + name + scriptCounter;
				if (scriptActions.containsKey(Constants.START_SCRIPT)) {
					scriptActions.get(Constants.START_SCRIPT).add(actionName);
					BroadcastHandler.getStringActionMap().put(actionName, sequenceAction);
				} else {
					List<String> startScriptList = new ArrayList<>();
					startScriptList.add(actionName);
					scriptActions.put(Constants.START_SCRIPT, startScriptList);
					BroadcastHandler.getStringActionMap().put(actionName, sequenceAction);
				}
			} else if (script instanceof BroadcastScript) {
				BroadcastScript broadcastScript = (BroadcastScript) script;
				SequenceAction action = createActionSequence(broadcastScript);
				BroadcastHandler.getActionScriptMap().put(action, script);
				BroadcastHandler.getScriptSpriteMapMap().put(script, this);
				putBroadcastSequenceAction(broadcastScript.getBroadcastMessage(), action);
				String actionName = action.toString() + Constants.ACTION_SPRITE_SEPARATOR + name + scriptCounter;
				if (scriptActions.containsKey(Constants.BROADCAST_SCRIPT)) {
					scriptActions.get(Constants.BROADCAST_SCRIPT).add(actionName);
					BroadcastHandler.getStringActionMap().put(actionName, action);
				} else {
					List<String> broadcastScriptList = new ArrayList<>();
					broadcastScriptList.add(actionName);
					scriptActions.put(Constants.BROADCAST_SCRIPT, broadcastScriptList);
					BroadcastHandler.getStringActionMap().put(actionName, action);
				}
			}
		}
	}

	private void putBroadcastSequenceAction(String broadcastMessage, SequenceAction action) {
		if (BroadcastSequenceMap.containsKey(broadcastMessage)) {
			BroadcastSequenceMap.get(broadcastMessage).add(action);
		} else {
			ArrayList<SequenceAction> actionList = new ArrayList<>();
			actionList.add(action);
			BroadcastSequenceMap.put(broadcastMessage, actionList);
		}
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	public void setActionFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	@Override
	public Sprite clone() {
		final Sprite cloneSprite = new Sprite();

		cloneSprite.setName(this.getName());
		cloneSprite.isBackpackObject = false;

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject == null || !currentProject.getSpriteList().contains(this)) {
			throw new RuntimeException("The sprite must be in the current project before cloning it.");
		}

		Sprite originalSprite = ProjectManager.getInstance().getCurrentSprite();
		ProjectManager.getInstance().setCurrentSprite(cloneSprite);

		cloneSpriteVariables(currentProject, cloneSprite);
		cloneLooks(cloneSprite);
		cloneSounds(cloneSprite);
		cloneUserBricks(cloneSprite);
		cloneNfcTags(cloneSprite);
		cloneScripts(cloneSprite);

		setUserAndVariableBrickReferences(cloneSprite, userBricks);

		ProjectManager.getInstance().checkCurrentSprite(cloneSprite, false);
		ProjectManager.getInstance().setCurrentSprite(originalSprite);

		return cloneSprite;
	}

	public void setUserAndVariableBrickReferences(Sprite cloneSprite, List<UserBrick> originalPrototypeUserBricks) {
		setDefinitionBrickReferences(cloneSprite, originalPrototypeUserBricks);
		setVariableReferencesOfClonedSprite(cloneSprite);
	}

	private void setDefinitionBrickReferences(Sprite cloneSprite, List<UserBrick> originalPrototypeUserBricks) {
		for (int scriptPosition = 0; scriptPosition < cloneSprite.getScriptList().size(); scriptPosition++) {
			Script clonedScript = cloneSprite.getScript(scriptPosition);
			for (int brickPosition = 0; brickPosition < clonedScript.getBrickList().size(); brickPosition++) {
				Brick clonedBrick = clonedScript.getBrick(brickPosition);
				if (!(clonedBrick instanceof UserBrick)) {
					continue;
				}
				UserBrick clonedUserBrick = ((UserBrick) clonedBrick);
				UserBrick originalUserBrick = ((UserBrick) getScript(scriptPosition).getBrick(brickPosition));
				int originalIndexOfDefinitionBrick = 0;
				for (int prototypeUserBrickPosition = 0; prototypeUserBrickPosition < originalPrototypeUserBricks.size();
						prototypeUserBrickPosition++) {
					UserBrick originalPrototypeUserBrick = originalPrototypeUserBricks.get(prototypeUserBrickPosition);
					if (originalPrototypeUserBrick.getDefinitionBrick().equals(originalUserBrick.getDefinitionBrick())) {
						originalIndexOfDefinitionBrick = prototypeUserBrickPosition;
						break;
					}
				}

				UserBrick clonedPrototypeUserBrick = cloneSprite.getUserBrickList().get(originalIndexOfDefinitionBrick);
				UserScriptDefinitionBrick correctClonedDefinitionBrick = clonedPrototypeUserBrick.getDefinitionBrick();
				clonedUserBrick.setDefinitionBrick(correctClonedDefinitionBrick);

				clonedPrototypeUserBrick.updateUserBrickParametersAndVariables();
				clonedUserBrick.updateUserBrickParametersAndVariables();
			}
		}
	}

	private void setVariableReferencesOfClonedSprite(Sprite cloneSprite) {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentProject().getDataContainer();
		List<UserVariable> clonedSpriteVariables = dataContainer.getOrCreateVariableListForSprite(cloneSprite);
		cloneSprite.updateUserVariableReferencesInUserVariableBricks(clonedSpriteVariables);

		List<UserVariable> clonedProjectVariables = dataContainer.getProjectVariables();
		cloneSprite.updateUserVariableReferencesInUserVariableBricks(clonedProjectVariables);
	}

	private void cloneSpriteVariables(Project currentProject, Sprite cloneSprite) {
		DataContainer userVariables = currentProject.getDataContainer();
		List<UserVariable> originalSpriteVariables = userVariables.getOrCreateVariableListForSprite(this);
		List<UserVariable> clonedSpriteVariables = userVariables.getOrCreateVariableListForSprite(cloneSprite);
		for (UserVariable variable : originalSpriteVariables) {
			clonedSpriteVariables.add(new UserVariable(variable.getName(), variable.getValue()));
		}
	}

	private void cloneLooks(Sprite cloneSprite) {
		List<LookData> cloneLookList = new ArrayList<>();
		for (LookData element : this.lookList) {
			cloneLookList.add(element.clone());
		}
		cloneSprite.lookList = cloneLookList;

		cloneSprite.look = this.look.copyLookForSprite(cloneSprite);
		try {
			cloneSprite.look.setLookData(cloneSprite.getLookDataList().get(0));
		} catch (IndexOutOfBoundsException indexOutOfBoundsException) {
			Log.e(TAG, Log.getStackTraceString(indexOutOfBoundsException));
		}
	}

	private void cloneSounds(Sprite cloneSprite) {
		List<SoundInfo> cloneSoundList = new ArrayList<>();
		for (SoundInfo element : this.soundList) {
			cloneSoundList.add(element.clone());
		}
		cloneSprite.soundList = cloneSoundList;
	}

	private void cloneUserBricks(Sprite cloneSprite) {
		List<UserBrick> clonedUserBrickList = new ArrayList<>();

		for (UserBrick original : userBricks) {
			ProjectManager.getInstance().checkCurrentScript(original.getDefinitionBrick().getScriptSafe(), false);
			UserBrick clonedUserBrick = original.copyBrickForSprite(cloneSprite);
			clonedUserBrickList.add(clonedUserBrick);
			clonedUserBrick.updateUserBrickParametersAndVariables();
		}
		cloneSprite.userBricks = clonedUserBrickList;
	}

	private void cloneNfcTags(Sprite cloneSprite) {
		List<NfcTagData> cloneNfcTagList = new ArrayList<>();
		for (NfcTagData element : this.nfcTagList) {
			cloneNfcTagList.add(element.clone());
		}
		cloneSprite.nfcTagList = cloneNfcTagList;
	}

	private void cloneScripts(Sprite cloneSprite) {
		List<Script> cloneScriptList = new ArrayList<>();
		for (Script element : this.scriptList) {
			Script addElement = element.copyScriptForSprite(cloneSprite);
			cloneScriptList.add(addElement);
		}
		cloneSprite.scriptList = cloneScriptList;
	}

	public Sprite cloneForBackPack() {
		final Sprite cloneSprite = new Sprite();
		cloneSprite.setName(this.getName());
		return cloneSprite;
	}

	public void createWhenScriptActionSequence(String action) {
		ParallelAction whenParallelAction = actionFactory.parallel();
		for (Script s : scriptList) {
			if (s instanceof WhenScript && (((WhenScript) s).getAction().equalsIgnoreCase(action))) {
				SequenceAction sequence = createActionSequence(s);
				whenParallelAction.addAction(sequence);
			}
		}
		look.setWhenParallelAction(whenParallelAction);
		look.addAction(whenParallelAction);
	}

	private SequenceAction createActionSequence(Script script) {
		SequenceAction sequence = ActionFactory.sequence();
		script.run(this, sequence);
		return sequence;
	}

	public void createWhenNfcScriptAction(String uid) {
		ParallelAction whenParallelAction = ActionFactory.parallel();
		for (Script s : scriptList) {
			if (s instanceof WhenNfcScript) {
				WhenNfcScript whenNfcScript = (WhenNfcScript) s;
				if (whenNfcScript.isMatchAll()
						|| whenNfcScript.getNfcTag().getNfcTagUid().equals(uid)) {
					SequenceAction sequence = createActionSequence(s);
					whenParallelAction.addAction(sequence);
				}
			}
		}
		//TODO: quick fix for faulty behaviour - nfc action triggers again after touchevents
		//look.setWhenParallelAction(whenParallelAction);
		look.addAction(whenParallelAction);
	}

	public void createTouchDownAction() {
		ParallelAction whenParallelAction = ActionFactory.parallel();
		for (Script s : scriptList) {
			if (s instanceof WhenTouchDownScript) {
				SequenceAction sequence = createActionSequence(s);
				whenParallelAction.addAction(sequence);
			}
		}
		look.addAction(whenParallelAction);
	}

	public void pause() {
		for (Script s : scriptList) {
			s.setPaused(true);
		}
		this.isPaused = true;
	}

	public void resume() {
		for (Script s : scriptList) {
			s.setPaused(false);
		}
		this.isPaused = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addScript(Script script) {
		if (script != null && !scriptList.contains(script)) {
			scriptList.add(script);
		}
	}

	public void addScript(int index, Script script) {
		if (script != null && !scriptList.contains(script)) {
			scriptList.add(index, script);
		}
	}

	public Script getScript(int index) {
		if (index < 0 || index >= scriptList.size()) {
			Log.e(TAG, "getScript() Index out of Scope! scriptList size: " + scriptList.size());
			return null;
		}
		return scriptList.get(index);
	}

	public int getNumberOfScripts() {
		if (scriptList != null) {
			return scriptList.size();
		}
		return 0;
	}

	public int getNumberOfBricks() {
		int brickCount = 0;
		for (Script s : scriptList) {
			brickCount += s.getBrickList().size();
		}
		return brickCount;
	}

	public int getScriptIndex(Script script) {
		return scriptList.indexOf(script);
	}

	public void removeAllScripts() {
		scriptList.clear();
	}

	public boolean removeScript(Script script) {
		return scriptList.remove(script);
	}

	public List<LookData> getLookDataList() {
		return lookList;
	}

	public void setLookDataList(List<LookData> list) {
		lookList = list;
	}

	public boolean existLookDataByName(LookData look) {
		for (LookData lookdata : lookList) {
			if (lookdata.getLookName().equals(look.getLookName())) {
				return true;
			}
		}
		return false;
	}

	public boolean existLookDataByFileName(LookData look) {
		for (LookData lookdata : lookList) {
			if (lookdata.getLookFileName().equals(look.getLookFileName())) {
				return true;
			}
		}
		return false;
	}

	public void addLookData(LookData data) {
		FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();
		container.addChecksum(data.getChecksum(), data.getAbsolutePath());
		lookList.add(data);
	}

	public List<SoundInfo> getSoundList() {
		return soundList;
	}

	public void setSoundList(List<SoundInfo> list) {
		soundList = list;
	}

	public int getRequiredResources() {
		int resources = Brick.NO_RESOURCES;

		for (Script script : scriptList) {
			resources |= script.getRequiredResources();
		}

		for (LookData lookData : getLookDataList()) {
			resources |= lookData.getRequiredResources();
		}

		return resources;
	}

	public List<NfcTagData> getNfcTagList() {
		return nfcTagList;
	}

	public void setNfcTagList(List<NfcTagData> list) {
		nfcTagList = list;
	}

	public int getNextNewUserBrickId() {
		return userBricks.size();
	}

	@Override
	public String toString() {
		return name;
	}

	public void rename(String newSpriteName) {
		if ((getRequiredResources() & Brick.PHYSICS) > 0) {
			List<Sprite> spriteList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
			for (Sprite currentSprite : spriteList) {
				if ((currentSprite.getRequiredResources() & Brick.PHYSICS) > 0) {
					currentSprite.updateCollisionBroadcastMessages(getName(), newSpriteName);
				}
			}
		}
		setName(newSpriteName);
	}

	public void updateCollisionBroadcastMessages(String oldCollisionObjectIdentifier, String newCollisionObjectIdentifier) {
		for (int scriptIndex = 0; scriptIndex < getNumberOfScripts(); scriptIndex++) {
			Script currentScript = getScript(scriptIndex);
			if (currentScript instanceof CollisionScript) {
				((CollisionScript) currentScript).updateBroadcastMessage(oldCollisionObjectIdentifier, newCollisionObjectIdentifier);
			}
		}
	}

	public boolean containsLookData(LookData lookData) {
		for (LookData lookOfSprite : lookList) {
			if (lookOfSprite.equals(lookData)) {
				return true;
			}
		}
		return false;
	}

	public boolean existSoundInfoByName(SoundInfo sound) {
		for (SoundInfo soundInfo : soundList) {
			if (soundInfo.getTitle().equals(sound.getTitle())) {
				return true;
			}
		}
		return false;
	}

	public boolean existSoundInfoByFileName(SoundInfo sound) {
		for (SoundInfo soundInfo : soundList) {
			if (soundInfo.getSoundFileName().equals(sound.getSoundFileName())) {
				return true;
			}
		}
		return false;
	}

	public void addSound(SoundInfo sound) {
		FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();
		container.addChecksum(sound.getChecksum(), sound.getAbsolutePath());
		soundList.add(sound);
	}

	public void updateUserVariableReferencesInUserVariableBricks(List<UserVariable> variables) {
		for (Brick brick : getListWithAllBricks()) {
			if (brick instanceof UserVariableBrick) {
				UserVariableBrick userVariableBrick = (UserVariableBrick) brick;
				for (UserVariable variable : variables) {
					UserVariable userVariableBrickVariable = userVariableBrick.getUserVariable();
					if (userVariableBrickVariable != null
							&& variable.getName().equals(userVariableBrickVariable.getName())) {
						userVariableBrick.setUserVariable(variable);
						break;
					}
				}
			}
		}
	}
}
