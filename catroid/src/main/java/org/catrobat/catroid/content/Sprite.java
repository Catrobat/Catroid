/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.BroadcastSequenceMap;
import org.catrobat.catroid.common.BroadcastWaitSequenceMap;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.io.XStreamFieldKeyOrder;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.fragment.SpriteFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
// CHECKSTYLE DISABLE IndentationCheck FOR 8 LINES
@XStreamFieldKeyOrder({
		"name",
		"lookList",
		"soundList",
		"scriptList",
		"userBricks",
		"nfcTagList"
})
public class Sprite implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = Sprite.class.getSimpleName();

	private static SpriteFactory spriteFactory = new SpriteFactory();

	public transient Look look = new Look(this);
	public transient boolean isBackpackObject = false;
	public transient PenConfiguration penConfiguration = new PenConfiguration();
	private transient boolean convertToSingleSprite = false;
	private transient boolean convertToGroupItemSprite = false;
	private transient BroadcastSequenceMap broadcastSequenceMap = new BroadcastSequenceMap();
	private transient BroadcastWaitSequenceMap broadcastWaitSequenceMap = new BroadcastWaitSequenceMap();

	@XStreamAsAttribute
	private String name;
	private List<Script> scriptList = new ArrayList<>();
	private List<LookData> lookList = new ArrayList<>();
	private List<SoundInfo> soundList = new ArrayList<>();
	private List<UserBrick> userBricks = new ArrayList<>();
	private List<NfcTagData> nfcTagList = new ArrayList<>();
	private transient ActionFactory actionFactory = new ActionFactory();
	public transient boolean isClone = false;
	private transient boolean isMobile = false;

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
			PhysicsWorld physicsWorld = ProjectManager.getInstance().getSceneToPlay().getPhysicsWorld();
			look = new PhysicsLook(this, physicsWorld);
		} else {
			look = new Look(this);
		}
		for (LookData lookData : lookList) {
			lookData.dispose();
		}
		penConfiguration = new PenConfiguration();
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

	public void createStartScriptActionSequenceAndPutToMap(Map<String, List<String>> scriptActions,
			boolean includeStartScripts) {
		for (int scriptCounter = 0; scriptCounter < scriptList.size(); scriptCounter++) {
			Script script = scriptList.get(scriptCounter);
			if (script instanceof StartScript && !isClone && includeStartScripts) {
				Action sequenceAction = createActionSequence(script);
				look.addAction(sequenceAction);
				BroadcastHandler.getActionScriptMap().put(sequenceAction, script);
				BroadcastHandler.getScriptSpriteMap().put(script, this);
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
				BroadcastHandler.getScriptSpriteMap().put(script, this);
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
			} else if (script instanceof WhenConditionScript) {
				createWhenConditionBecomesTrueAction((WhenConditionScript) script);
			}
		}
	}

	public void createStartScriptActionSequenceAndPutToMap(Map<String, List<String>> scriptActions) {
		createStartScriptActionSequenceAndPutToMap(scriptActions, true);
	}

	private void createWhenConditionBecomesTrueAction(WhenConditionScript script) {
		ActionFactory actionFactory = getActionFactory();
		Formula condition = ((WhenConditionBrick) script.getScriptBrick()).getConditionFormula();
		Formula negatedCondition = new Formula(condition.getRoot().clone()) {
			@Override
			public Boolean interpretBoolean(Sprite sprite) throws InterpretationException {
				return !super.interpretBoolean(sprite);
			}
		};

		Action waitAction = actionFactory.createWaitUntilAction(this, condition);
		Action waitActionNegated = actionFactory.createWaitUntilAction(this, negatedCondition);

		SequenceAction foreverSequence = ActionFactory.sequence(ActionFactory.sequence(waitAction,
				createActionSequence(script)), waitActionNegated);

		Action whenConditionBecomesTrueAction = actionFactory.createForeverAction(this, foreverSequence);
		look.addAction(whenConditionBecomesTrueAction);
	}

	private void putBroadcastSequenceAction(String broadcastMessage, SequenceAction action) {
		String sceneName = ProjectManager.getInstance().getSceneToPlay().getName();
		List<SequenceAction> actions = new ArrayList<>();
		actions.add(action);
		broadcastSequenceMap.put(sceneName, broadcastMessage, actions);
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	public void setActionFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	@Override
	public Sprite clone() {
		final Sprite cloneSprite = createSpriteInstance();

		cloneSprite.setName(this.getName());
		cloneSprite.isBackpackObject = false;
		cloneSprite.convertToSingleSprite = false;
		cloneSprite.convertToGroupItemSprite = false;
		cloneSprite.isMobile = false;

		ProjectManager projectManager = ProjectManager.getInstance();
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		if (currentScene == null) {
			throw new RuntimeException("Current scene was null, cannot clone Sprite.");
		}

		Sprite originalSprite = projectManager.getCurrentSprite();
		projectManager.setCurrentSprite(cloneSprite);

		cloneSpriteVariables(currentScene, cloneSprite);
		cloneLooks(cloneSprite);
		cloneSounds(cloneSprite);
		cloneUserBricks(cloneSprite);
		cloneNfcTags(cloneSprite);
		cloneScripts(cloneSprite);
		cloneSprite.resetSprite();
		cloneLook(cloneSprite);

		setUserAndVariableBrickReferences(cloneSprite, userBricks);

		projectManager.checkCurrentSprite(cloneSprite, false);
		projectManager.setCurrentSprite(originalSprite);

		return cloneSprite;
	}

	public Sprite cloneForCloneBrick() {
		final Sprite cloneSprite = new SpriteFactory().newInstance(SpriteFactory.SPRITE_SINGLE);

		cloneSprite.setName(this.getName() + "-c" + StageActivity.getAndIncrementNumberOfClonedSprites());
		cloneSprite.isClone = true;
		cloneSprite.actionFactory = this.actionFactory;

		cloneSprite.soundList = this.soundList;
		cloneSprite.nfcTagList = this.nfcTagList;

		cloneSprite.broadcastSequenceMap = this.broadcastSequenceMap;
		cloneSprite.broadcastWaitSequenceMap = this.broadcastWaitSequenceMap;

		Sprite originalSprite = ProjectManager.getInstance().getCurrentSprite();
		ProjectManager.getInstance().setCurrentSprite(cloneSprite);

		cloneLooks(cloneSprite, false);
		cloneUserBricks(cloneSprite);
		cloneSpriteVariables(ProjectManager.getInstance().getCurrentScene(), cloneSprite);
		cloneScripts(cloneSprite);
		cloneSprite.resetSprite();
		cloneLook(cloneSprite);
		setUserAndVariableBrickReferences(cloneSprite, userBricks);

		ProjectManager.getInstance().setCurrentSprite(originalSprite);

		return cloneSprite;
	}

	public Sprite shallowClone() {
		final Sprite cloneSprite = createSpriteInstance();
		cloneSprite.setName(this.name);

		cloneSprite.isBackpackObject = false;
		cloneSprite.convertToSingleSprite = false;
		cloneSprite.convertToGroupItemSprite = false;
		cloneSprite.isMobile = this.isMobile;

		cloneSprite.look = this.look;
		cloneSprite.scriptList = this.scriptList;
		cloneSprite.lookList = this.lookList;
		cloneSprite.soundList = this.soundList;
		cloneSprite.userBricks = this.userBricks;
		cloneSprite.nfcTagList = this.nfcTagList;
		cloneSprite.broadcastSequenceMap = this.broadcastSequenceMap;
		cloneSprite.broadcastWaitSequenceMap = this.broadcastWaitSequenceMap;

		ProjectManager projectManager = ProjectManager.getInstance();

		shallowCloneSpriteVariables(projectManager.getCurrentScene().getDataContainer(), cloneSprite);
		shallowCloneSpriteLists(projectManager.getCurrentScene().getDataContainer(), cloneSprite);

		if (projectManager.getCurrentSprite() != null && projectManager.getCurrentSprite().equals(this)) {
			projectManager.setCurrentSprite(cloneSprite);
		}

		return cloneSprite;
	}

	private void shallowCloneSpriteVariables(DataContainer dataContainer, Sprite cloneSprite) {
		List<UserVariable> originalSpriteVariables = dataContainer.getOrCreateVariableListForSprite(this);
		List<UserVariable> clonedSpriteVariables = dataContainer.getOrCreateVariableListForSprite(cloneSprite);
		for (UserVariable variable : originalSpriteVariables) {
			clonedSpriteVariables.add(variable);
		}
		dataContainer.cleanVariableListForSprite(this);
	}

	private void shallowCloneSpriteLists(DataContainer dataContainer, Sprite cloneSprite) {
		List<UserList> originalSpriteLists = dataContainer.getOrCreateUserListForSprite(this);
		List<UserList> clonedSpriteLists = dataContainer.getOrCreateUserListForSprite(cloneSprite);
		for (UserList list : originalSpriteLists) {
			clonedSpriteLists.add(list);
		}
		dataContainer.cleanUserListForSprite(this);
	}

	private Sprite createSpriteInstance() {
		Sprite cloneSprite;
		if (convertToSingleSprite) {
			cloneSprite = spriteFactory.newInstance(SpriteFactory.SPRITE_SINGLE);
		} else if (convertToGroupItemSprite) {
			cloneSprite = spriteFactory.newInstance(SpriteFactory.SPRITE_GROUP_ITEM);
		} else {
			cloneSprite = spriteFactory.newInstance(getClass().getSimpleName());
		}
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
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		List<UserVariable> clonedSpriteVariables = dataContainer.getOrCreateVariableListForSprite(cloneSprite);
		cloneSprite.updateUserVariableReferencesInUserVariableBricks(clonedSpriteVariables);

		List<UserVariable> clonedProjectVariables = dataContainer.getProjectVariables();
		cloneSprite.updateUserVariableReferencesInUserVariableBricks(clonedProjectVariables);
	}

	private void cloneSpriteVariables(Scene currentScene, Sprite cloneSprite) {
		DataContainer userVariables = currentScene.getDataContainer();
		List<UserVariable> originalSpriteVariables = userVariables.getOrCreateVariableListForSprite(this);
		List<UserVariable> clonedSpriteVariables = userVariables.getOrCreateVariableListForSprite(cloneSprite);
		for (UserVariable variable : originalSpriteVariables) {
			clonedSpriteVariables.add(new UserVariable(variable.getName(), variable.getValue()));
		}
	}

	private void cloneLook(Sprite cloneSprite) {
		int currentLookDataIndex = this.lookList.indexOf(this.look.getLookData());
		if (currentLookDataIndex != -1) {
			cloneSprite.look.setLookData(cloneSprite.lookList.get(currentLookDataIndex));
		}
		this.look.copyTo(cloneSprite.look);
	}

	private void cloneLooks(Sprite cloneSprite) {
		cloneLooks(cloneSprite, true);
	}

	private void cloneLooks(Sprite cloneSprite, boolean incrementUsage) {
		List<LookData> cloneLookList = new ArrayList<>();
		for (LookData element : this.lookList) {
			cloneLookList.add(element.clone(incrementUsage));
		}
		cloneSprite.lookList = cloneLookList;
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

	public void createWhengamepadButtonScriptActionSequence(String action) {
		ParallelAction whenParallelAction = Actions.parallel();
		for (Script script : scriptList) {
			if (script instanceof WhenGamepadButtonScript && (((WhenGamepadButtonScript) script).getAction().equalsIgnoreCase(action))) {
				SequenceAction sequence = createActionSequence(script);
				whenParallelAction.addAction(sequence);
			}
		}
		look.setWhenParallelAction(whenParallelAction);
		look.addAction(whenParallelAction);
	}

	public Sprite cloneForBackPack() {
		final Sprite cloneSprite = spriteFactory.newInstance(SpriteFactory.SPRITE_SINGLE);
		cloneSprite.setName(name);
		return cloneSprite;
	}

	public void createWhenScriptActionSequence(String action) {
		ParallelAction whenParallelAction = ActionFactory.parallel();
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

	public ParallelAction createBackgroundChangedAction(LookData lookData) {
		ParallelAction whenParallelAction = ActionFactory.parallel();
		for (Script s : scriptList) {
			if (s instanceof WhenBackgroundChangesScript
					&& ((WhenBackgroundChangesScript) s).getLook().equals(lookData)) {
				SequenceAction sequence = createActionSequence(s);
				SequenceAction sequenceWithNotifyAtEnd = ActionFactory.sequence(sequence,
						ActionFactory.createBackgroundNotifyAction(lookData));
				whenParallelAction.addAction(sequenceWithNotifyAtEnd);
			}
		}
		look.addAction(whenParallelAction);

		return whenParallelAction;
	}

	public int getNumberOfWhenBackgroundChangesScripts(LookData lookData) {
		int numberOfScripts = 0;
		for (Script s : scriptList) {
			if (s instanceof WhenBackgroundChangesScript
					&& ((WhenBackgroundChangesScript) s).getLook().equals(lookData)) {
				numberOfScripts++;
			}
		}
		return numberOfScripts;
	}

	public void createWhenClonedAction() {
		ParallelAction whenParallelAction = ActionFactory.parallel();
		for (Script s : scriptList) {
			if (s instanceof WhenClonedScript) {
				SequenceAction sequence = createActionSequence(s);
				whenParallelAction.addAction(sequence);
			}
		}
		look.addAction(whenParallelAction);
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

	public List<SoundInfo> getSoundList() {
		return soundList;
	}

	public void setSoundList(List<SoundInfo> list) {
		soundList = list;
	}

	public int getRequiredResources() {
		int resources = Brick.NO_RESOURCES;

		for (Script script : scriptList) {
			if (!script.isCommentedOut()) {
				resources |= script.getRequiredResources();
			}
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
			List<Sprite> spriteList = ProjectManager.getInstance().getCurrentScene().getSpriteList();
			for (Sprite currentSprite : spriteList) {
				if ((currentSprite.getRequiredResources() & Brick.PHYSICS) > 0) {
					currentSprite.updateCollisionBroadcastMessages(getName(), newSpriteName);
				}
			}
		}
		if (hasCollision()) {
			renameSpriteInCollisionFormulas(newSpriteName, CatroidApplication.getAppContext());
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

	public boolean hasCollision() {
		boolean hasCollision = (this.getRequiredResources() & Brick.COLLISION) > 0;
		if (hasCollision) {
			return true;
		}
		Scene scene = ProjectManager.getInstance().getCurrentScene();
		for (Sprite sprite : scene.getSpriteList()) {
			if (sprite.hasToCollideWith(this)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasToCollideWith(Sprite other) {
		for (Script script : getScriptList()) {
			Brick scriptBrick = script.getScriptBrick();
			if (scriptBrick instanceof FormulaBrick) {
				FormulaBrick formulaBrick = (FormulaBrick) scriptBrick;
				for (Formula formula : formulaBrick.getFormulas()) {
					if (formula.containsSpriteInCollision(other.getName())) {
						return true;
					}
				}
			}
			for (Brick brick : script.brickList) {
				if (brick instanceof FormulaBrick) {
					FormulaBrick formulaBrick = (FormulaBrick) brick;
					for (Formula formula : formulaBrick.getFormulas()) {
						if (formula.containsSpriteInCollision(other.getName())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public void updateCollisionFormulasToVersion(float catroidLanguageVersion) {
		for (Script script : getScriptList()) {
			Brick scriptBrick = script.getScriptBrick();
			if (scriptBrick instanceof FormulaBrick) {
				FormulaBrick formulaBrick = (FormulaBrick) scriptBrick;
				for (Formula formula : formulaBrick.getFormulas()) {
					formula.updateCollisionFormulasToVersion(catroidLanguageVersion);
				}
			}
			for (Brick brick : script.getBrickList()) {
				if (brick instanceof UserBrick) {
					UserBrick formulaBrick = (UserBrick) brick;
					for (Formula formula : formulaBrick.getFormulas()) {
						formula.updateCollisionFormulasToVersion(catroidLanguageVersion);
					}
				} else if (brick instanceof FormulaBrick) {
					FormulaBrick formulaBrick = (FormulaBrick) brick;
					for (Formula formula : formulaBrick.getFormulas()) {
						formula.updateCollisionFormulasToVersion(catroidLanguageVersion);
					}
				}
			}
		}
	}

	public void updateSetPenColorFormulas() {
		for (Script script : getScriptList()) {
			for (Brick brick : script.getBrickList()) {
				if (brick instanceof SetPenColorBrick) {
					SetPenColorBrick spcBrick = (SetPenColorBrick) brick;
					spcBrick.correctBrickFieldsFromPhiro();
				}
			}
		}
	}

	public void updateArduinoValues994to995() {
		for (Script script : getScriptList()) {
			for (Brick brick : script.getBrickList()) {
				if (brick instanceof ArduinoSendPWMValueBrick) {
					ArduinoSendPWMValueBrick spcBrick = (ArduinoSendPWMValueBrick) brick;
					spcBrick.updateArduinoValues994to995();
				}
			}
		}
	}

	private void renameSpriteInCollisionFormulas(String newName, Context context) {
		String oldName = getName();
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentScene().getSpriteList();
		for (Sprite sprite : spriteList) {
			for (Script currentScript : sprite.getScriptList()) {
				if (currentScript == null) {
					return;
				}
				Brick scriptBrick = currentScript.getScriptBrick();
				if (scriptBrick instanceof FormulaBrick) {
					FormulaBrick formulaBrick = (FormulaBrick) scriptBrick;
					for (Formula formula : formulaBrick.getFormulas()) {
						formula.updateCollisionFormulas(oldName, newName, context);
					}
				}
				List<Brick> brickList = currentScript.getBrickList();
				for (Brick brick : brickList) {
					if (brick instanceof UserBrick) {
						List<Formula> formulaList = ((UserBrick) brick).getFormulas();
						for (Formula formula : formulaList) {
							formula.updateCollisionFormulas(oldName, newName, context);
						}
					}
					if (brick instanceof FormulaBrick) {
						List<Formula> formulaList = ((FormulaBrick) brick).getFormulas();
						for (Formula formula : formulaList) {
							formula.updateCollisionFormulas(oldName, newName, context);
						}
					}
				}
			}
		}
	}

	public void createCollisionPolygons() {
		for (LookData lookData : getLookDataList()) {
			lookData.getCollisionInformation().calculate();
		}
	}

	public class PenConfiguration {
		public boolean penDown = false;
		public float penSize = BrickValues.PEN_SIZE;
		public Color penColor = BrickValues.PEN_COLOR;
		public PointF previousPoint = null;
		public boolean stamp = false;
	}

	public void setConvertToSingleSprite(boolean convertToSingleSprite) {
		this.convertToGroupItemSprite = false;
		this.convertToSingleSprite = convertToSingleSprite;
	}

	public void setConvertToGroupItemSprite(boolean convertToGroupItemSprite) {
		this.convertToSingleSprite = false;
		this.convertToGroupItemSprite = convertToGroupItemSprite;
	}

	public boolean isMobile() {
		return isMobile;
	}

	public void setIsMobile(boolean isMobile) {
		this.isMobile = isMobile;
	}

	public List<Brick> getBricksRequiringResource(int resource) {
		List<Brick> resourceBrickList = new ArrayList<>();

		for (Script script : scriptList) {
			resourceBrickList.addAll(script.getBricksRequiringResources(resource));
		}
		return resourceBrickList;
	}

	public boolean isClone() {
		return isClone;
	}

	public BroadcastSequenceMap getBroadcastSequenceMap() {
		return broadcastSequenceMap;
	}

	public BroadcastWaitSequenceMap getBroadcastWaitSequenceMap() {
		return broadcastWaitSequenceMap;
	}
}
