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
package org.catrobat.catroid.common;

public final class TrackingConstants {

	// Suppress default constructor for noninstantiability
	private TrackingConstants() {
		throw new AssertionError();
	}

	//Keys
	public static final String NAME = "name";
	public static final String SCOPE = "scope";
	public static final String SOURCE = "source";
	public static final String LANDSCAPE = "landscape";
	public static final String RECORD = "record";
	public static final String DEVICE = "device";
	public static final String GLOBAL = "global";
	public static final String LOCAL = "local";
	public static final String LENGTH = "length";

	public static final String POCKET_PAINT = "PocketPaint";
	public static final String MEDIA_LIBRARY = "MediaLibrary";
	public static final String CAMERA = "Camera";

	public static final String TEMPLATE_NAME = "templateName";
	public static final String PROGRAM_NAME = "programName";
	public static final String FIRST_PROGRAM_NAME = "firstProgramName";
	public static final String SECOND_PROGRAM_NAME = "secondProgramName";
	public static final String PROGRAM_ID = "programId";
	public static final String SCENE_NAME = "sceneName";
	public static final String NEW_SCENE_NAME = "newSceneName";
	public static final String FIRST_SCENE_NAME = "firstScene";
	public static final String SECOND_SCENE_NAME = "secondScene";
	public static final String GROUP_NAME = "groupName";
	public static final String SPRITE_NAME = "objectName";
	public static final String SCRIPT_NAME = "scriptName";
	public static final String LOOK_NAME = "lookName";
	public static final String SOUND_NAME = "soundName";
	public static final String BRICK_CATEGORY = "brickCategory";
	public static final String BRICK_NAME = "brickName";
	public static final String BRICK_FIELD = "brickField";
	public static final String FORMULA = "formula";
	public static final String MESSAGE = "message";

	public static final String NO_PROGRAM = "noProgram";
	public static final String NO_SCENE = "noScene";
	public static final String NO_SPRITE = "noSprite";

	public static final String SESSION_PROGRAM_EXECUTION = "programExecutionSession";
	public static final String SESSION_START_PROGRAM_EXECUTION = "startProgramExecutionSession";
	public static final String SESSION_STOP_PROGRAM_EXECUTION = "stopProgramExecutionSession";
	public static final String SESSION_DURATION_PROGRAM_EXECUTION = "durationOfProgramSessionInMillis";

	public static final String SESSION_POCKET_PAINT_CREATE_OBJECT = "PocketPaintSessionCreateObject";
	public static final String SESSION_START_POCKET_PAINT_CREATE_OBJECT = "startPocketPaintSessionCreateObject";
	public static final String SESSION_STOP_POCKET_PAINT_CREATE_OBJECT = "stopPocketPaintSessionCreateObject";
	public static final String SESSION_DURATION_POCKET_PAINT = "durationOfPocketPaintSessionInMillis";
	public static final String SESSION_POCKET_PAINT_CREATE_LOOK = "PocketPaintSessionCreateLook";
	public static final String SESSION_START_POCKET_PAINT_CREATE_LOOK = "startPocketPaintSessionCreateLook";
	public static final String SESSION_STOP_POCKET_PAINT_CREATE_LOOK = "stopPocketPaintSessionCreateLook";
	public static final String SESSION_POCKET_PAINT_EDIT_LOOK = "PocketPaintSessionEditLook";
	public static final String SESSION_START_POCKET_PAINT_EDIT_LOOK = "startPocketPaintSessionEditLook";
	public static final String SESSION_STOP_POCKET_PAINT_EDIT_LOOK = "stopPocketPaintSessionEditLook";

	public static final String SESSION_WEB_EXPLORE = "WebSessionExplore";
	public static final String SESSION_WEB_TUTORIAL = "WebSessionTutorial";
	public static final String SESSION_START_WEB_EXPLORE = "startExploreSession";
	public static final String SESSION_STOP_WEB_EXPLORE = "stopExploreSession";
	public static final String SESSION_START_WEB_TUTORIAL = "startWebTutorialSession";
	public static final String SESSION_STOP_WEB_TUTORIAL = "stopWebTutorialSession";
	public static final String SESSION_DURATION_WEB = "durationOfWebSessionInMillis";

	public static final String ADD_BRICK = "addBrick";
	public static final String ADD_SCENE = "addScene";

	public static final String AMOUNT_BRICKS = "amountOfBricks";
	public static final String AMOUNT_SCRIPTS = "amountOfScripts";
	public static final String AMOUNT_LOOKS = "amountOfLooks";
	public static final String AMOUNT_SOUNDS = "amountOfSounds";

	public static final String CREATE_PROGRAM = "createProgram";
	public static final String CREATE_EXAMPLE_PROGRAM = "createExampleProgram";
	public static final String CREATE_GROUP = "createGroup";
	public static final String CREATE_OBJECT = "createObject";
	public static final String CREATE_SOUND = "createSound";
	public static final String CREATE_LOOK = "createLook";
	public static final String CREATE_LIST = "createList";
	public static final String CREATE_VARIABLE = "createVariable";
	public static final String CREATE_BROADCAST_MESSAGE = "createBroadcastMessage";

	public static final String COPY_PROGRAM = "copyProgram";
	public static final String COPY_SPRITE = "copyObject";
	public static final String COPY_SOUND = "copySound";
	public static final String COPY_LOOK = "copyLook";
	public static final String COPY_BRICK = "copyBrick";

	public static final String DELETE_PROGRAM = "deleteProgram";
	public static final String DELETE_SCENE = "deleteScene";
	public static final String DELETE_SPRITE = "deleteObject";
	public static final String DELETE_SOUND = "deleteSound";
	public static final String DELETE_LOOK = "deleteLook";
	public static final String DELETE_LIST = "deleteList";
	public static final String DELETE_VARIABLE = "deleteVariable";
	public static final String DELETE_BRICK = "deleteBrick";

	public static final String BACKPACK_OBJECT = "backpackObject";
	public static final String UNPACK_OBJECT = "unpackObject";
	public static final String BACKPACK_LOOK = "backpackLook";
	public static final String UNPACK_LOOK = "unpackLook";
	public static final String BACKPACK_SOUND = "backpackSound";
	public static final String UNPACK_SOUND = "unpackSound";
	public static final String BACKPACK_SCRIPTS = "backpackScriptGroup";
	public static final String UNPACK_SCRIPTS = "unpackScriptGroup";
	public static final String BACKPACK_SCENES = "backpackScene";
	public static final String UNPACK_SCENES = "unpackScene";

	public static final String MERGE_PROGRAMS = "mergePrograms";
	public static final String MERGE_SCENES = "mergeScenes";
	public static final String USE_TEMPLATE = "useTemplate";
	public static final String DOWNLOAD_PROGRAM = "downloadProgram";
	public static final String UPLOAD_PROGRAM = "uploadProgram";
	public static final String OPEN_PROGRAM = "openProgram";
	public static final String OPEN_FORMULA_EDITOR = "openFormulaEditor";
	public static final String SAVE_FORMULA = "saveFormula";
	public static final String BRICK_HELP = "openBrickHelpOption";
	public static final String EDIT_LOOK = "editLook";
	public static final String DROP_BRICK = "dropBrick";
	public static final String SUBMIT_PROGRAM = "submitProgram";
	public static final String HINTS_OPTION = "hintsOption";
	public static final String ALLOW_HINTS = "allowHints";

	public static final String MAIN_MENU_CONTINUE = "ContinueButton";

	public static final String APPLY_ACCESSIBILITY_SETTINGS = "applyAccessibilitySetting";
	public static final String ACTIVE_PROFILE = "activeProfile";
	public static final String ACTIVE_SETTINGS = "activeSettings";
	public static final String VERSION_CODE = "versionCode";

	//Shared preferences
	public static final String LOGIN_TIME = "login";
}
