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
package org.catrobat.catroid.utils;

import android.app.Fragment;
import android.graphics.PointF;
import android.util.Log;

import com.zed.bdsclient.controller.BDSClientController;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TrackingUtil {

	private static Map<String, Long> startTime = new HashMap<>();
	private static boolean showEvent = true;

	private TrackingUtil() {
	}

	public static void startTimer(String id) {
		startTime.put(id, System.currentTimeMillis());
	}

	public static long stopTimer(String id) {
		return System.currentTimeMillis() - startTime.get(id);
	}

	public static void trackCreateProgram(String projectName, boolean landscapeMode, boolean exampleProgram) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				String trackingMsg = "";
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("programname", projectName);
				jsonObject.put("landscape", landscapeMode);
				jsonObject.put("exampleProgram", exampleProgram);
				if (exampleProgram) {
					trackingMsg = "CreateExampleProgram";
				} else {
					trackingMsg = "CreateEmptyProgram";
				}

				logEvent(trackingMsg, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackCreateObject(String newSpriteName, String spriteSource) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject1();
				jsonObject.put("objectname", newSpriteName);
				jsonObject.put("source", spriteSource);

				logEvent("CreateObject", jsonObject);
				//trackStopPocketCodeSession
				if (spriteSource.equals("PocketPaint")) {
					JSONObject jsonObject2 = createBaseJsonObject1();
					jsonObject2.put("durationOfPocketPaintSessionInMillis", stopTimer
							("PocketPaintSessionCreateObject"));
					logEvent("StopPocketPaintSessionCreateObject", jsonObject2);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackStartPocketPaintSessionCreateObject() {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject1();
				startTimer("PocketPaintSessionCreateObject");
				logEvent("StartPocketPaintSessionCreateObject", jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackStartPocketPaintSessionLook(String timerId, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject2();
				startTimer(timerId);
				logEvent(trackMsg, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackCreateLook(String lookName, String lookSource, String customEventMessage, String
			customEventMessageStop, String timerId) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject2();
				jsonObject.put("lookname", lookName);
				jsonObject.put("source", lookSource);
				logEvent(customEventMessage, jsonObject);

				if (lookSource.equals("PocketPaint")) {
					JSONObject jsonObject2 = createBaseJsonObject2();
					jsonObject2.put("durationOfPocketPaintSessionInMillis", stopTimer
							(timerId));
					jsonObject2.put("lookName", lookName);
					logEvent(customEventMessageStop, jsonObject2);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackCreateSound(String soundName, String soundSource) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject2();
				jsonObject.put("soundname", soundName);
				jsonObject.put("source", soundSource);

				logEvent("CreateSound", jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackAddBrick(Fragment addBrickFragment, Brick brickToBeAdded) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				String brickCategory = addBrickFragment.getArguments().toString();
				String brickName = brickToBeAdded.getClass().getSimpleName();
				int start = brickCategory.indexOf("=");
				int end = brickCategory.indexOf("}");
				brickCategory = brickCategory.substring(start + 1, end);

				JSONObject jsonObject = createBaseJsonObject2();
				jsonObject.put("brickcategory", brickCategory);
				jsonObject.put("brickname", brickName);

				logEvent("AddBrick", jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackDropBrick(Brick draggedBrick, int positionOfInsertedBrick) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				String brickName = draggedBrick.toString();
				int end = brickName.indexOf("@");
				brickName = brickName.substring(36, end);

				JSONObject jsonObject = createBaseJsonObject2();
				jsonObject.put("brickname", brickName);
				jsonObject.put("brickposition", positionOfInsertedBrick);

				logEvent("DropBrick", jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackBrick(String brickName, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			if (showEvent) {
				try {
					if(!trackMsg.equals("DeleteBrick")) {
						int end = brickName.indexOf("@");
						brickName = brickName.substring(36, end);
					}

					JSONObject jsonObject = createBaseJsonObject2();
					jsonObject.put("brickname", brickName);
					logEvent(trackMsg, jsonObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				showEvent = true;
			}
		}
	}

	public static void trackData(String name, String variableScope, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject2();
				jsonObject.put("name", name);
				jsonObject.put("scope", variableScope);

				logEvent(trackMsg, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackMenuButtonProject(String projectName, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("programname", projectName);
				logEvent(trackMsg, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackMenuButton(String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			logEvent(trackMsg, null);
		}
	}

	public static void trackProject(String name, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("programname", name);
				logEvent(trackMsg, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackDeleteSprite(Sprite spriteToEdit) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject1();
				jsonObject.put("objectname", spriteToEdit.getName());
				jsonObject.put("amountOfBricks", spriteToEdit.getNumberOfBricks());
				jsonObject.put("amountOfScripts", spriteToEdit.getNumberOfScripts());
				jsonObject.put("amountOfLooks", spriteToEdit.getLookDataList().size());
				jsonObject.put("amountOfSounds", spriteToEdit.getSoundList().size());

				logEvent("DeleteObject", jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackSprite(String name, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject1();
				jsonObject.put("name", name);

				logEvent(trackMsg, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackLook(String lookName, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			if (showEvent) {
				try {
					JSONObject jsonObject = createBaseJsonObject2();
					jsonObject.put("lookname", lookName);
					logEvent(trackMsg, jsonObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void trackSound(String soundName, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			if (showEvent) {
				try {
					JSONObject jsonObject = createBaseJsonObject2();
					jsonObject.put("soundname", soundName);
					logEvent(trackMsg, jsonObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void trackScene(String projectName, String sceneName, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("programname", projectName);
				jsonObject.put("scenename", sceneName);

				logEvent(trackMsg, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackMerge(String firstProject, String secondProject) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("firstProgramname", firstProject);
				jsonObject.put("secondProgramname", secondProject);
				logEvent("MergePrograms", jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackFormula(String formulaBrick, String brickField, String formula, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				String brickName = formulaBrick.toString();
				int end = brickName.indexOf("@");
				brickName = brickName.substring(36, end);

				JSONObject jsonObject = createBaseJsonObject2();
				jsonObject.put("brickname", brickName);
				jsonObject.put("brickfield", brickField);
				jsonObject.put("formula", formula);

				logEvent(trackMsg, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackStartExecution() {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject1();
				startTimer("ProgramExecutionSession");
				logEvent("StartProgramExecutionSession", jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackStopExecution() {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				String programName = ProjectManager.getInstance().getCurrentProject().getName();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("programname", programName);
				jsonObject.put("DurationOfProgramSessionInMillis", stopTimer("ProgramExecutionSession"));
				logEvent("StopProgramExecutioSession", jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackBackpackSprite(String name, String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject1();
				jsonObject.put("objectname", name);
				logEvent(trackMsg, jsonObject);
				showEvent = false;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackMergeScenes(String firstScene, String secondScene, String name) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				String programName = ProjectManager.getInstance().getCurrentProject().getName();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("programname", programName);
				jsonObject.put("firstScene", firstScene);
				jsonObject.put("secondScene", secondScene);
				jsonObject.put("newScenename", name);
				logEvent("MergeScenes", jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void trackBackpackBricks(List<Script> scriptsToAdd, int brickAmount, String groupName,
			String trackMsg) {
		if (BuildConfig.NOLB_DATA_TRACKING) {
			try {
				JSONObject jsonObject = createBaseJsonObject2();
				String scriptName = "";
				for (int i = 0; i < scriptsToAdd.size(); i++) {
					String subStr = scriptsToAdd.get(i).toString();
					int end = subStr.indexOf("@");
					subStr = subStr.substring(29, end);
					if (scriptsToAdd.size() > i && scriptName != "") {
						scriptName += ", " + subStr;
					} else {
						scriptName = subStr;
					}
				}

				jsonObject.put("amountOfScripts", scriptsToAdd.size());
				jsonObject.put("scriptname", scriptName);
				if (brickAmount != 0) {
					jsonObject.put("amountOfBricks", brickAmount);
				}
				jsonObject.put("groupname", groupName);

				logEvent(trackMsg, jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private static JSONObject createBaseJsonObject1() throws JSONException {
		String programName = ProjectManager.getInstance().getCurrentProject().getName();
		String sceneName = ProjectManager.getInstance().getCurrentScene().getName();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("programname", programName);
		jsonObject.put("scenename", sceneName);

		return jsonObject;
	}

	private static JSONObject createBaseJsonObject2() throws JSONException {
		String programName = ProjectManager.getInstance().getCurrentProject().getName();
		String sceneName = ProjectManager.getInstance().getCurrentScene().getName();
		String objectName = ProjectManager.getInstance().getCurrentSprite().getName();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("programname", programName);
		jsonObject.put("scenename", sceneName);
		jsonObject.put("objectname", objectName);

		return jsonObject;
	}

	private static void logEvent(String eventName, JSONObject jsonObject) {
		BDSClientController.getInstance().generateCustomEvent(eventName,
				ProjectManager.getInstance().getUserID(), System.currentTimeMillis(), jsonObject);
		BDSClientController.getInstance().setDebugMode(true);
	}
}

