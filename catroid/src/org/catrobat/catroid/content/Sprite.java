/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content;

import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.UserVariablesContainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Sprite implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private String name;
	private List<Script> scriptList;
	private ArrayList<LookData> lookList;
	private ArrayList<SoundInfo> soundList;
	public transient Look look;
	private ArrayList<UserBrick> userBricks;
	private int newUserBrickNext = 1;

	public transient boolean isPaused;

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
		if (userBricks == null) {
			userBricks = new ArrayList<UserBrick>();
		}
	}

	public void resetSprite() {
		look = new Look(this);
		for (LookData lookData : lookList) {
			lookData.resetLookData();
		}
	}

	public Sprite(String name) {
		this.name = name;
		scriptList = new ArrayList<Script>();
		lookList = new ArrayList<LookData>();
		soundList = new ArrayList<SoundInfo>();
		init();
	}

	public Sprite() {

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
			userBricks = new ArrayList<UserBrick>();
		}
		userBricks.add(brick);
		return brick;
	}

	public List<UserBrick> getUserBrickList() {
		if (userBricks == null) {
			userBricks = new ArrayList<UserBrick>();
		}
		return userBricks;
	}

	public List<UserBrick> getUserBrickListAtLeastOneBrick(String defaultText, String defaultVariable) {
		if (userBricks == null || userBricks.size() == 0) {
			int newBrickId = ProjectManager.getInstance().getCurrentProject().getUserVariables()
					.getAndIncrementUserBrickId();
			initUserBrickList(defaultText, defaultVariable, newBrickId);
		}
		return userBricks;
	}

	void initUserBrickList(String defaultText, String defaultVariable, int nextUserBrickId) {
		userBricks = new ArrayList<UserBrick>();

		// the UserBrick constructor will insert the UserBrick into this Sprite's userBricks list.
		UserBrick exampleBrick = new UserBrick(this, nextUserBrickId);
		exampleBrick.addUIText(defaultText);
		exampleBrick.addUIVariable(defaultVariable);

	}

	public void createStartScriptActionSequence() {
		for (Script s : scriptList) {
			if (s instanceof StartScript) {
				look.addAction(createActionSequence(s));
			}
			if (s instanceof BroadcastScript) {
				BroadcastScript script = (BroadcastScript) s;
				SequenceAction action = createBroadcastScriptActionSequence(script);
				look.putBroadcastSequenceAction(script.getBroadcastMessage(), action);

			}
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
			clonedSpriteVariables.add(new UserVariable(variable.getName(), variable.getValue(), clonedSpriteVariables));
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

		ArrayList<UserBrick> cloneUserBrickList = new ArrayList<UserBrick>();
		for (Brick brick : this.userBricks) {
			UserBrick original = (UserBrick) brick;

			UserBrick deepClone = new UserBrick(cloneSprite, original.getId());
			deepClone.uiDataArray = original.uiDataArray.clone();
			deepClone.updateUIComponents(null);

			UserScriptDefinitionBrick clonedDefinitionBrick = new UserScriptDefinitionBrick(cloneSprite, deepClone,
					original.getId());
			deepClone.setDefinitionBrick(clonedDefinitionBrick);

			cloneUserBrickList.add(deepClone);
		}

		// once all the UserBricks have been copied over, we can copy thier scripts over as well
		// (preserve recursive references)
		for (Brick cloneBrick : cloneUserBrickList) {
			UserBrick deepClone = (UserBrick) cloneBrick;
			UserBrick original = findBrickWithId(userBricks, deepClone.getId());

			UserScript originalScript = original.getDefinitionBrick().getUserScript();
			UserScript newScript = originalScript.copyScriptForSprite(cloneSprite, cloneUserBrickList);
			deepClone.getDefinitionBrick().setUserScript(newScript);
		}

		//The scripts have to be the last copied items
		List<Script> cloneScriptList = new ArrayList<Script>();
		for (Script element : this.scriptList) {
			Script addElement = element.copyScriptForSprite(cloneSprite, cloneUserBrickList);
			cloneScriptList.add(addElement);
		}
		cloneSprite.scriptList = cloneScriptList;

		for (UserBrick cloneBrick : cloneUserBrickList) {
			cloneBrick.setId(cloneBrick.getId() + cloneUserBrickList.size());
			UserScriptDefinitionBrick definitionBrick = cloneBrick.getDefinitionBrick();
			definitionBrick.setUserBrickId(definitionBrick.getUserBrickId() + cloneUserBrickList.size());
		}
		cloneSprite.userBricks = cloneUserBrickList;

		cloneSprite.init();

		cloneSprite.look = this.look.copyLookForSprite(cloneSprite);
		try {
			cloneSprite.look.setLookData(cloneSprite.getLookDataList().get(0));
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return cloneSprite;

	}

	protected UserBrick findBrickWithId(List<UserBrick> list, int id) {
		for (UserBrick brick : list) {
			if (brick.getId() == id) {
				return brick;
			}
		}
		return null;
	}

	public void createWhenScriptActionSequence(String action) {
		ParallelAction whenParallelAction = ExtendedActions.parallel();
		for (Script s : scriptList) {
			if (s instanceof WhenScript) {
				if (((WhenScript) s).getAction().equalsIgnoreCase(action)) {
					SequenceAction sequence = createActionSequence(s);
					whenParallelAction.addAction(sequence);
				}
			}
		}
		look.setWhenParallelAction(whenParallelAction);
		look.addAction(whenParallelAction);
	}

	public SequenceAction createBroadcastScriptActionSequence(BroadcastScript script) {
		return createActionSequence(script);
	}

	private SequenceAction createActionSequence(Script s) {
		SequenceAction sequence = ExtendedActions.sequence();
		s.run(sequence);
		return sequence;
	}

	public void startScriptBroadcast(Script s, boolean overload) {
		SequenceAction sequence = ExtendedActions.sequence();
		s.run(sequence);
		look.addAction(sequence);
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
		int ressources = Brick.NO_RESOURCES;

		for (Script script : scriptList) {
			ressources |= script.getRequiredResources();
		}
		return ressources;
	}

	public int getNextNewUserBrickId() {
		return newUserBrickNext++;
	}

	@Override
	public String toString() {
		return name;
	}
}
