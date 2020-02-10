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

import com.google.common.collect.LinkedHashMultimap;
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
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.WhenConditionBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.embroidery.RunningStitch;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.XStreamFieldKeyOrder;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XStreamFieldKeyOrder({
		"name",
		"lookList",
		"soundList",
		"scriptList",
		"nfcTagList",
		"userVariables",
		"userLists"
})
public class Sprite implements Cloneable, Nameable, Serializable {

	private static final long serialVersionUID = 1L;

	private static final String TAG = Sprite.class.getSimpleName();

	public transient Look look = new Look(this);
	public transient PenConfiguration penConfiguration = new PenConfiguration();
	public transient RunningStitch runningStitch = new RunningStitch();
	private transient boolean convertToSingleSprite = false;
	private transient boolean convertToGroupItemSprite = false;
	private transient Multimap<EventId, EventThread> idToEventThreadMap = LinkedHashMultimap.create();
	private transient Set<ConditionScriptTrigger> conditionScriptTriggers = new HashSet<>();

	@XStreamAsAttribute
	private String name;
	private List<Script> scriptList = new ArrayList<>();
	private List<LookData> lookList = new ArrayList<>();
	private List<SoundInfo> soundList = new ArrayList<>();
	private List<NfcTagData> nfcTagList = new ArrayList<>();
	private List<UserVariable> userVariables = new ArrayList<>();
	private List<UserList> userLists = new ArrayList<>();

	private transient ActionFactory actionFactory = new ActionFactory();

	public transient boolean isClone = false;
	public transient Sprite myOriginal = null;

	public Sprite() {
	}

	public Sprite(String name) {
		this.name = name;
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
		return allBricks;
	}

	public List<UserVariable> getUserVariables() {
		return userVariables;
	}

	public UserVariable getUserVariable(String name) {
		for (UserVariable variable : userVariables) {
			if (variable.getName().equals(name)) {
				return variable;
			}
		}
		return null;
	}

	public boolean addUserVariable(UserVariable userVariable) {
		return userVariables.add(userVariable);
	}

	public List<UserList> getUserLists() {
		return userLists;
	}

	public UserList getUserList(String name) {
		for (UserList list : userLists) {
			if (list.getName().equals(name)) {
				return list;
			}
		}
		return null;
	}

	public boolean addUserList(UserList userList) {
		return userLists.add(userList);
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

	public void resetUserData() {
		for (UserVariable userVariable : userVariables) {
			userVariable.reset();
		}
		for (UserList userList : userLists) {
			userList.reset();
		}
	}

	public void resetSprite() {
		Brick.ResourcesSet resourcesSet = new Brick.ResourcesSet();
		addRequiredResources(resourcesSet);
		if (resourcesSet.contains(Brick.PHYSICS)) {
			PhysicsWorld physicsWorld = ProjectManager.getInstance().getCurrentlyPlayingScene().getPhysicsWorld();
			look = new PhysicsLook(this, physicsWorld);
		} else {
			look = new Look(this);
		}
		for (LookData lookData : lookList) {
			lookData.dispose();
		}

		if (!getLookList().isEmpty()) {
			look.setLookData(getLookList().get(0));
		}

		penConfiguration = new PenConfiguration();
		runningStitch = new RunningStitch();
	}

	public void invalidate() {
		idToEventThreadMap = null;
		conditionScriptTriggers = null;
		penConfiguration = null;
		runningStitch = null;
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
		convertedSprite.runningStitch = runningStitch;

		convertedSprite.lookList = lookList;
		convertedSprite.soundList = soundList;
		convertedSprite.nfcTagList = nfcTagList;
		convertedSprite.scriptList = scriptList;

		convertedSprite.userVariables = userVariables;
		convertedSprite.userLists = userLists;

		return convertedSprite;
	}

	private EventThread createEventThread(Script script) {
		EventThread sequence = (EventThread) ActionFactory.createEventThread(script);
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
		List<Brick> flatList = new ArrayList<>();
		for (Script script : scriptList) {
			script.addToFlatList(flatList);
		}
		return flatList.size() - scriptList.size();
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

	public void addRequiredResources(final Brick.ResourcesSet resourcesSet) {
		for (Script script : scriptList) {
			if (!script.isCommentedOut()) {
				script.addRequiredResources(resourcesSet);
			}
		}

		for (LookData lookData : getLookList()) {
			lookData.addRequiredResources(resourcesSet);
		}
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

	public boolean hasCollision() {
		Brick.ResourcesSet resourcesSet = new Brick.ResourcesSet();
		addRequiredResources(resourcesSet);
		if (resourcesSet.contains(Brick.COLLISION)) {
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
			List<Brick> flatList = new ArrayList();
			script.addToFlatList(flatList);
			for (Brick brick : flatList) {
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

	private void renameSpriteInCollisionFormulas(String newName, Context context) {
		String oldName = getName();
		List<Sprite> spriteList = ProjectManager.getInstance().getCurrentlyEditedScene().getSpriteList();
		for (Sprite sprite : spriteList) {
			for (Script currentScript : sprite.getScriptList()) {
				if (currentScript == null) {
					return;
				}
				List<Brick> flatList = new ArrayList();
				currentScript.addToFlatList(flatList);
				for (Brick brick : flatList) {
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
		public PenColor penColor = new PenColor(0, 0, 1, 1);
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

	public boolean isBackgroundSprite() {
		return look.getZIndex() == Constants.Z_INDEX_BACKGROUND;
	}

	public Multimap<EventId, EventThread> getIdToEventThreadMap() {
		return idToEventThreadMap;
	}
}
