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
package org.catrobat.catroid.content;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import com.badlogic.gdx.graphics.Color;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.actions.EventThread;
import org.catrobat.catroid.content.bricks.ArduinoSendPWMValueBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.UserVariableBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.io.XStreamFieldKeyOrder;
import org.catrobat.catroid.physics.PhysicsCollision;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.recyclerview.controller.ScriptController;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class Sprite implements Cloneable, Nameable, Serializable{

	private static final long serialVersionUID = 1L;
	private static final String TAG = Sprite.class.getSimpleName();

	public transient Look look = new Look(this);
	public transient PenConfiguration penConfiguration = new PenConfiguration();
	private transient boolean convertToSingleSprite = false;
	private transient boolean convertToGroupItemSprite = false;
	private transient Multimap<EventId, EventThread> idToEventThreadMap = HashMultimap.create();
	private transient Set<ConditionScriptTrigger> conditionScriptTriggers = new HashSet<>();

	@XStreamAsAttribute
	private String name;
	private List<Script> scriptList = new ArrayList<>();
	private List<LookData> lookList = new ArrayList<>();
	private List<SoundInfo> soundList = new ArrayList<>();
	private List<UserBrick> userBricks = new ArrayList<>();
	private List<NfcTagData> nfcTagList = new ArrayList<>();

	private transient ActionFactory actionFactory = new ActionFactory();

	public transient boolean isClone = false;

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

	public List<Brick> getAllBricks() {
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
			PhysicsWorld physicsWorld = ProjectManager.getInstance().getCurrentlyPlayingScene().getPhysicsWorld();
			look = new PhysicsLook(this, physicsWorld);
		} else {
			look = new Look(this);
		}
		for (LookData lookData : lookList) {
			lookData.dispose();
		}
		penConfiguration = new PenConfiguration();
	}

	public void invalidate() {
		idToEventThreadMap = null;
		conditionScriptTriggers = null;
		penConfiguration = null;
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

	public void initConditionScriptTriggers() {
		conditionScriptTriggers.clear();
		for (Script script : scriptList) {
			if (script instanceof WhenConditionScript) {
				WhenConditionBrick conditionBrick = (WhenConditionBrick) script.getScriptBrick();
				Formula condition = conditionBrick.getFormulaWithBrickField(Brick.BrickField.IF_CONDITION);
				conditionScriptTriggers.add(new ConditionScriptTrigger(condition));
			}
		}
	}

	void evaluateConditionScriptTriggers() {
		for (ConditionScriptTrigger conditionScriptTrigger : conditionScriptTriggers) {
			conditionScriptTrigger.evaluateAndTriggerActions(this);
		}
	}

	public void initializeEventThreads(@EventId.EventType int startType) {
		idToEventThreadMap.clear();
		for (Script script : scriptList) {
			createThreadAndAddToEventMap(script);
		}
		look.fire(new EventWrapper(new EventId(startType), EventWrapper.NO_WAIT));
	}

	private void createThreadAndAddToEventMap(Script script) {
		if (script.isCommentedOut()) {
			return;
		}
		idToEventThreadMap.put(script.createEventId(this), createEventThread(script));
	}

	public ActionFactory getActionFactory() {
		return actionFactory;
	}

	public void setActionFactory(ActionFactory actionFactory) {
		this.actionFactory = actionFactory;
	}

	public Sprite convert() {
		Sprite convertedSprite;

		if (convertToSingleSprite) {
			convertedSprite = new SingleSprite(name);
		} else if (convertToGroupItemSprite) {
			convertedSprite = new GroupItemSprite(name);
		} else {
			Log.d(TAG, "Nothing to convert: if this is not what you wanted have a look at the convert flags.");
			return this;
		}

		convertedSprite.look = new Look(convertedSprite);
		convertedSprite.look.setLookData(look.getLookData());

		convertedSprite.penConfiguration = penConfiguration;

		convertedSprite.lookList = lookList;
		convertedSprite.soundList = soundList;
		convertedSprite.nfcTagList = nfcTagList;
		convertedSprite.scriptList = scriptList;

		return convertedSprite;
	}

	public Sprite cloneForCloneBrick() {
		Sprite clone = new Sprite(name + "-c" + StageActivity.getAndIncrementNumberOfClonedSprites());
		Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();

		DataContainer dataContainer = currentScene.getDataContainer();
		ScriptController scriptController = new ScriptController();

		clone.isClone = true;
		clone.actionFactory = actionFactory;

		for (LookData look : lookList) {
			clone.lookList.add(new LookData(look.getName(), look.getFile()));
		}

		clone.soundList.addAll(soundList);
		clone.nfcTagList.addAll(nfcTagList);

		dataContainer.copySpriteUserData(this, dataContainer, clone);

		for (Script script : scriptList) {
			try {
				clone.addScript(scriptController.copy(script, currentScene, clone));
			} catch (CloneNotSupportedException | IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		clone.resetSprite();
		cloneLook(clone);
		setDefinitionBrickReferences(clone, userBricks);

		return clone;
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

	private void cloneLook(Sprite cloneSprite) {
		int currentLookDataIndex = this.lookList.indexOf(this.look.getLookData());
		if (currentLookDataIndex != -1) {
			cloneSprite.look.setLookData(cloneSprite.lookList.get(currentLookDataIndex));
		}
		this.look.copyTo(cloneSprite.look);
	}

	private EventThread createEventThread(Script script) {
		EventThread sequence = (EventThread) actionFactory.createEventThread(script);
		script.run(this, sequence);
		return sequence;
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
		return scriptList.size();
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

	public List<LookData> getLookList() {
		return lookList;
	}

	public List<SoundInfo> getSoundList() {
		return soundList;
	}

	public int getRequiredResources() {
		int resources = Brick.NO_RESOURCES;

		for (Script script : scriptList) {
			if (!script.isCommentedOut()) {
				resources |= script.getRequiredResources();
			}
		}

		for (LookData lookData : getLookList()) {
			resources |= lookData.getRequiredResources();
		}

		return resources;
	}

	public List<NfcTagData> getNfcTagList() {
		return nfcTagList;
	}

	@Override
	public String toString() {
		return name;
	}

	public void rename(String newSpriteName) {
		if (hasCollision()) {
			renameSpriteInCollisionFormulas(newSpriteName, CatroidApplication.getAppContext());
		}
		setName(newSpriteName);
	}

	public void updateUserVariableReferencesInUserVariableBricks(List<UserVariable> variables) {
		for (Brick brick : getAllBricks()) {
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
		Scene scene = ProjectManager.getInstance().getCurrentlyEditedScene();
		for (Sprite sprite : scene.getSpriteList()) {
			if (sprite.hasToCollideWith(this)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasToCollideWith(Sprite other) {
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
			for (Brick brick : script.getBrickList()) {
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

	void updateCollisionScripts() {
		for (Script script : getScriptList()) {
			if (script instanceof CollisionScript) {
				CollisionScript collisionScript = (CollisionScript) script;
				String[] spriteNames = collisionScript.getSpriteToCollideWithName().split(PhysicsCollision.COLLISION_MESSAGE_CONNECTOR);
				String spriteToCollideWith = spriteNames[0];
				if (spriteNames[0].equals(getName())) {
					spriteToCollideWith = spriteNames[1];
				}
				collisionScript.setSpriteToCollideWithName(spriteToCollideWith);
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
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList();
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
		for (LookData lookData : getLookList()) {
			lookData.getCollisionInformation().calculate();
		}
	}

	public class PenConfiguration {
		public boolean penDown = false;
		public double penSize = BrickValues.PEN_SIZE;
		public Color penColor = BrickValues.PEN_COLOR;
		public PointF previousPoint = null;
		public boolean stamp = false;
	}

	public boolean toBeConverted() {
		return convertToSingleSprite || convertToGroupItemSprite;
	}

	public void setConvertToSingleSprite(boolean convertToSingleSprite) {
		this.convertToGroupItemSprite = false;
		this.convertToSingleSprite = convertToSingleSprite;
	}

	public void setConvertToGroupItemSprite(boolean convertToGroupItemSprite) {
		this.convertToSingleSprite = false;
		this.convertToGroupItemSprite = convertToGroupItemSprite;
	}

	public List<Brick> getBricksRequiringResource(int resource) {
		List<Brick> resourceBrickList = new ArrayList<>();

		for (Script script : scriptList) {
			resourceBrickList.addAll(script.getBricksRequiringResources(resource));
		}
		return resourceBrickList;
	}

	public boolean isBackgroundSprite() {
		return look.getZIndex() == Constants.Z_INDEX_BACKGROUND;
	}

	public Multimap<EventId, EventThread> getIdToEventThreadMap() {
		return idToEventThreadMap;
	}
}
