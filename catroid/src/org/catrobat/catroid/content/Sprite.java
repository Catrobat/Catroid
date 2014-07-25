/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BroadcastSequenceMap;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Sprite implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = Sprite.class.getSimpleName();

	public transient Look look;
	public transient boolean isPaused;

	private String name;
	private List<Script> scriptList;
	private ArrayList<LookData> lookList;
	private ArrayList<SoundInfo> soundList;

	public Sprite(String name) {
		this.name = name;
		scriptList = new ArrayList<Script>();
		lookList = new ArrayList<LookData>();
		soundList = new ArrayList<SoundInfo>();
		init();
	}

	public Sprite() {

	}

	private Object readResolve() {
		//filling FileChecksumContainer:
		if (soundList != null && lookList != null && ProjectManager.getInstance().getCurrentProject() != null) {
			FileChecksumContainer container = ProjectManager.getInstance().getFileChecksumContainer();
			if (container == null) {
				ProjectManager.getInstance().setFileChecksumContainer(new FileChecksumContainer());
			}
			for (SoundInfo soundInfo : soundList) {
				container.addChecksum(soundInfo.getChecksum(), soundInfo.getAbsolutePath());
			}
			for (LookData lookData : lookList) {
				container.addChecksum(lookData.getChecksum(), lookData.getAbsolutePath());
			}
		}
		init();
		return this;
	}

	private void init() {
		look = new Look(this);
		isPaused = false;
		if (soundList == null) {
			soundList = new ArrayList<SoundInfo>();
		}
		if (lookList == null) {
			lookList = new ArrayList<LookData>();
		}
		if (scriptList == null) {
			scriptList = new ArrayList<Script>();
		}
	}

	public void resetSprite() {
		look = new Look(this);
		for (LookData lookData : lookList) {
			lookData.resetLookData();
		}
	}

	public void createStartScriptActionSequenceAndPutToMap(Map<String, List<String>> scriptActions) {
		for (int scriptCounter = 0; scriptCounter < scriptList.size(); scriptCounter++) {
			Script script = scriptList.get(scriptCounter);
			if (script instanceof StartScript) {
				Action sequenceAction = createActionSequence(script);
				look.addAction(sequenceAction);
				BroadcastHandler.getActionScriptMap().put(sequenceAction, script);
				String actionName = sequenceAction.toString() + Constants.ACTION_SPRITE_SEPARATOR + name + scriptCounter;
				if (scriptActions.containsKey(Constants.START_SCRIPT)) {
					scriptActions.get(Constants.START_SCRIPT).add(actionName);
					BroadcastHandler.getStringActionMap().put(actionName, sequenceAction);
				} else {
					List<String> startScriptList = new ArrayList<String>();
					startScriptList.add(actionName);
					scriptActions.put(Constants.START_SCRIPT, startScriptList);
					BroadcastHandler.getStringActionMap().put(actionName, sequenceAction);
				}
			}
			if (script instanceof BroadcastScript) {
				BroadcastScript broadcastScript = (BroadcastScript) script;
				SequenceAction action = createActionSequence(broadcastScript);
				BroadcastHandler.getActionScriptMap().put(action, script);
				putBroadcastSequenceAction(broadcastScript.getBroadcastMessage(), action);
				String actionName = action.toString() + Constants.ACTION_SPRITE_SEPARATOR + name + scriptCounter;

				if (scriptActions.containsKey(Constants.BROADCAST_SCRIPT)) {
					scriptActions.get(Constants.BROADCAST_SCRIPT).add(actionName);
					BroadcastHandler.getStringActionMap().put(actionName, action);
				} else {
					List<String> broadcastScriptList = new ArrayList<String>();
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
			ArrayList<SequenceAction> actionList = new ArrayList<SequenceAction>();
			actionList.add(action);
			BroadcastSequenceMap.put(broadcastMessage, actionList);
		}
	}

	@Override
	public Sprite clone() {
		final Sprite cloneSprite = new Sprite();
		cloneSprite.setName(this.getName());

		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject == null || !currentProject.getSpriteList().contains(this)) {
			throw new RuntimeException("The sprite must be in the current project before cloning it.");
		}
		UserVariablesContainer userVariables = currentProject.getUserVariables();
		List<UserVariable> originalSpriteVariables = userVariables.getOrCreateVariableListForSprite(this);
		List<UserVariable> clonedSpriteVariables = userVariables.getOrCreateVariableListForSprite(cloneSprite);
		for (UserVariable variable : originalSpriteVariables) {
			clonedSpriteVariables.add(new UserVariable(variable.getName(), variable.getValue()));
		}

		ArrayList<LookData> cloneLookList = new ArrayList<LookData>();
		for (LookData element : this.lookList) {
			cloneLookList.add(element.clone());
		}
		cloneSprite.lookList = cloneLookList;

		ArrayList<SoundInfo> cloneSoundList = new ArrayList<SoundInfo>();
		for (SoundInfo element : this.soundList) {
			cloneSoundList.add(element.copySoundInfoForSprite(cloneSprite));
		}
		cloneSprite.soundList = cloneSoundList;

		//The scripts have to be the last copied items
		List<Script> cloneScriptList = new ArrayList<Script>();
		for (Script element : this.scriptList) {
			Script addElement = element.copyScriptForSprite(cloneSprite);
			cloneScriptList.add(addElement);
		}
		cloneSprite.scriptList = cloneScriptList;

		cloneSprite.init();

		cloneSprite.look = this.look.copyLookForSprite(cloneSprite);
		try {
			cloneSprite.look.setLookData(cloneSprite.getLookDataList().get(0));
		} catch (IndexOutOfBoundsException indexOutOfBoundsException) {
			Log.e(TAG, Log.getStackTraceString(indexOutOfBoundsException));
		}

		return cloneSprite;

	}

	public void createWhenScriptActionSequence(String action) {
		ParallelAction whenParallelAction = ExtendedActions.parallel();
		for (Script s : scriptList) {
			if (s instanceof WhenScript && (((WhenScript) s).getAction().equalsIgnoreCase(action))) {
				SequenceAction sequence = createActionSequence(s);
				whenParallelAction.addAction(sequence);

			}
		}
		look.setWhenParallelAction(whenParallelAction);
		look.addAction(whenParallelAction);
	}

	private SequenceAction createActionSequence(Script s) {
		SequenceAction sequence = ExtendedActions.sequence();
		s.run(this, sequence);
		return sequence;
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

	public ArrayList<LookData> getLookDataList() {
		return lookList;
	}

	public void setLookDataList(ArrayList<LookData> list) {
		lookList = list;
	}

	public ArrayList<SoundInfo> getSoundList() {
		return soundList;
	}

	public void setSoundList(ArrayList<SoundInfo> list) {
		soundList = list;
	}

	public int getRequiredResources() {
		int resources = Brick.NO_RESOURCES;

		for (Script script : scriptList) {
			resources |= script.getRequiredResources();
		}
		return resources;
	}

	@Override
	public String toString() {
		return name;
	}
}
