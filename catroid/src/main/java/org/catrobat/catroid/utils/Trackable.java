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

package org.catrobat.catroid.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.FormulaBrick;

import java.util.List;

public interface Trackable {
	void trackCreateProgram(String projectName, Boolean landscapeMode, boolean exampleProgram);
	void trackCreateObject(String newSpriteName, String spriteSource);
	void trackStartWebSessionExplore();
	void trackStopWebSessionExplore();
	void trackStartWebSessionTutorial();
	void trackStopWebSessionTutorial();
	void trackStartPocketPaintSessionCreateObject();
	void trackPocketPaintSessionLook(String timerId, String trackingMessage);
	void trackCreateLook(String lookName, String lookSource, String customEventMessage, String customEventMessageStop, String timerId);
	void trackCreateSound(String soundName, String soundSource, long lengthMilliseconds);
	void trackAddBrick(Fragment fragment, Brick brickToBeAdded);
	void trackBrick(String brickName, String trackMessage);
	void trackData(String name, String variableScope, String trackMessage);
	void trackMenuButtonProject(String projectName, String trackMessage);
	void trackProject(String name, String trackMessage);
	void trackDeleteSprite(Sprite spriteToEdit);
	void trackSprite(String name, String trackMessage);
	void trackLook(String lookName, String trackMessage);
	void trackSound(String soundName, String trackMessage);
	void trackDropBrick(Brick draggedBrick);
	void trackScene(String projectName, String sceneName, String trackMessage);
	void trackMerge(String firstProject, String secondProject);
	void trackFormula(FormulaBrick formulaBrick, String brickField, String formula, String trackingMessage);
	void trackStartExecution();
	void trackStopExecution();
	void trackBackpackSprite(String name, String trackingMessage);
	void trackBackpackScenes(String name, String trackingMessage);
	void trackMergeScenes(String firstScene, String secondScene, String name);
	void trackBackpackBricks(List<Script> scriptsToAdd, int brickAmount, String groupName, String trackingMessage);
	void trackUseTemplate(String templateName, boolean landscape);
	void trackApplyAccessibilityPreferences(String profileName, String settingName);
	void trackUseBrickHelp(Brick brick);
	void trackCreateBroadcastMessage(String message);
	void trackSubmitProject(String programId);
	void trackEnableHints(String enabled);
	void trackLoginInitSessionEvent(Context context);
	void trackLogoutEndSessionEvent(Activity activity);
}
