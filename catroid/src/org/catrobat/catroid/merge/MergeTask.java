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

package org.catrobat.catroid.merge;

import android.app.Activity;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.common.ProjectData;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.adapter.ProjectAdapter;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.util.List;

public class MergeTask {

	private Activity activity;
	private Project mergeResult = null;
	private Project firstProject = null;
	private Project secondProject = null;
	private Project current = null;
	private ProjectAdapter adapter = null;

	public MergeTask(Project firstProject, Project secondProject, Activity activity, ProjectAdapter adapter) {
		this.firstProject = firstProject;
		this.secondProject = secondProject;
		this.activity = activity;
		this.adapter = adapter;
	}

	public boolean mergeProjects(String mergeResultName) {
		if (mergeResult != null) {
			return false;
		}

		mergeResult = new Project(activity, mergeResultName);

		createHeader();
		createSpritesAllProjects();
		copySpriteScriptsAllProjects();

		if (!ConflictHelper.checkMergeConflict(activity, mergeResult)) {
			return false;
		}

		StorageHandler.getInstance().saveProject(mergeResult);
		StorageHandler.getInstance().copyImageFiles(mergeResult.getName(), firstProject.getName());
		StorageHandler.getInstance().copySoundFiles(mergeResult.getName(), firstProject.getName());
		StorageHandler.getInstance().copyImageFiles(mergeResult.getName(), secondProject.getName());
		StorageHandler.getInstance().copySoundFiles(mergeResult.getName(), secondProject.getName());

		mergeResult = StorageHandler.getInstance().loadProject(mergeResultName);

		if (adapter != null) {
			File projectCodeFile = new File(Utils.buildPath(Utils.buildProjectPath(mergeResultName),
					Constants.PROJECTCODE_NAME));
			adapter.insert(new ProjectData(mergeResultName, projectCodeFile.lastModified()), 0);
			adapter.notifyDataSetChanged();

			String msg = firstProject.getName() + " " + activity.getString(R.string.merge_info) + " " + secondProject.getName() + "!";
			ToastUtil.showSuccess(activity, msg);
		}
		return true;
	}

	private void createHeader() {
		XmlHeader mainHeader;
		XmlHeader subHeader;
		XmlHeader mergeHeader = mergeResult.getXmlHeader();

		if (firstProject.getSpriteList().size() < 2 && secondProject.getSpriteList().size() > 1) {
			mainHeader = secondProject.getXmlHeader();
			subHeader = firstProject.getXmlHeader();
		} else {
			mainHeader = firstProject.getXmlHeader();
			subHeader = secondProject.getXmlHeader();
		}
		mergeHeader.setVirtualScreenWidth(mainHeader.virtualScreenWidth);
		mergeHeader.setVirtualScreenHeight(mainHeader.virtualScreenHeight);

		String name = mainHeader.getProgramName();
		if (!mainHeader.getRemixOf().equals("")) {
			name += "[" + mainHeader.getRemixOf() + "]";
		}

		name += ", " + subHeader.getProgramName();
		if (!subHeader.getRemixOf().equals("")) {
			name += "[" + subHeader.getRemixOf() + "]";
		}
		mergeHeader.setRemixOf(name);
		mergeResult.setXmlHeader(mergeHeader);
	}

	private void createSpritesAllProjects() {
		current = firstProject;
		createSprites(firstProject);
		current = secondProject;
		createSprites(secondProject);
	}

	private void createSprites(Project project) {
		for (Sprite sprite : project.getSpriteList()) {
			if (!mergeResult.containsSpriteBySpriteName(sprite)) {
				mergeResult.addSprite(sprite);
			}
		}
	}

	private void copySpriteScriptsAllProjects() {
		current = firstProject;
		copySpriteScripts(firstProject);
		current = secondProject;
		copySpriteScripts(secondProject);
		mergeResult.removeUnusedBroadcastMessages();
	}

	private void copySpriteScripts(Project project) {
		ReferenceHelper helper = new ReferenceHelper(mergeResult, project);

		for (Sprite sprite : project.getSpriteList()) {
			checkScripts(sprite);
			addSoundsAndLooks(sprite);
		}
		mergeResult = helper.updateAllReferences();
	}

	private void checkScripts(Sprite sprite) {
		Sprite spriteInto = mergeResult.getSpriteBySpriteName(sprite);

		for (Script fromScript : sprite.getScriptList()) {
			boolean equal = false;
			for (Script intoScript : spriteInto.getScriptList()) {
				equal |= isEqualScript(intoScript, fromScript);
			}
			if (spriteInto.getScriptList().size() == 0 || !equal) {
				mergeResult.getSpriteBySpriteName(sprite).addScript(fromScript);
			}
		}
	}

	private void addSoundsAndLooks(Sprite sprite) {
		Sprite spriteInto = mergeResult.getSpriteBySpriteName(sprite);

		for (LookData look : sprite.getLookDataList()) {
			if (!spriteInto.existLookDataByName(look) && !spriteInto.existLookDataByFileName(look)) {
				spriteInto.addLookData(look);
			} else if (spriteInto.existLookDataByName(look) && !spriteInto.existLookDataByFileName(look)) {
				for (int i = 2; i < 100; i++) {
					look.setLookName(look.getLookName() + "_" + String.valueOf(i));
					if (!spriteInto.existLookDataByName(look)) {
						break;
					}
				}
				spriteInto.addLookData(look);
			}
		}

		for (SoundInfo sound : sprite.getSoundList()) {
			if (!spriteInto.existSoundInfoByName(sound) || !spriteInto.existSoundInfoByFileName(sound)) {
				spriteInto.addSound(sound);
			}
		}
	}

	private boolean isEqualScript(Script intoProjectScript, Script fromProjectScript) {
		List<Brick> intoProjectBrickList = intoProjectScript.getBrickList();
		List<Brick> fromProjectBrickList = fromProjectScript.getBrickList();

		int counter = 0;
		int numberOfEqualBricks = (intoProjectBrickList.size() < fromProjectBrickList.size()) ? intoProjectBrickList.size() : fromProjectBrickList.size();

		for (int i = 0; i < numberOfEqualBricks; i++) {
			Brick intoProjectBrick = intoProjectBrickList.get(i);
			Brick fromProjectBrick = fromProjectBrickList.get(i);

			if (intoProjectBrick.isEqualBrick(fromProjectBrick, mergeResult, current)) {
				counter++;
			}
		}

		if (counter != numberOfEqualBricks) {
			return false;
		}
		return true;
	}
}
