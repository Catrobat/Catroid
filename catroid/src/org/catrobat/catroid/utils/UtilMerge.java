/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.BroadcastScript;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.XmlHeader;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BroadcastBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class UtilMerge {

	private static final String TAG = UtilMerge.class.getSimpleName();
	private static Project project;

	private UtilMerge() {
	}

	public static void mergeProjectInCurrentProject(String projectNameToMergeFrom, FragmentActivity activity) {
		try {
			project = ProjectManager.getInstance().getCurrentProject();
			Project projectToMergeFrom = loadProjectContent(projectNameToMergeFrom, activity);

			if (!checkHeader(project.getXmlHeader(), projectToMergeFrom.getXmlHeader()) && project.getSpriteList().size() > 1) {
				showDifferentResolutionDialog(projectToMergeFrom, activity);
			} else {
				if (!checkMergeConflicts(project, projectToMergeFrom, activity)) {

					if (projectToMergeFrom.getSpriteList().get(0).getScriptList().size() != 0) {
						showBackgroundNotEmptyDialog(projectToMergeFrom, activity);
					} else {
						Project mergedProject = appendProjects(project, projectToMergeFrom, activity);
						ProjectManager.getInstance().setProject(mergedProject);
						ProjectManager.getInstance().saveProject(activity.getApplicationContext());
					}
				}
			}
		} catch (LoadingProjectException e) {
			Utils.showErrorDialog(activity, activity.getString(R.string.error_merge_with_self), R.string.merge_conflict);
			Log.e(TAG, "LoadingProjectException " + e.getMessage());
		} catch (OutdatedVersionProjectException e) {
			ToastUtil.showError(activity, R.string.error_merge);
			Log.e(TAG, "OutdatedVersionProjectException " + e.getMessage());
		} catch (CompatibilityProjectException e) {
			ToastUtil.showError(activity, R.string.error_merge);
			Log.e(TAG, "CompatibilityProjectException " + e.getMessage());
		} catch (IOException e) {
			ToastUtil.showError(activity, R.string.error_merge);
			Log.e(TAG, "IOException " + e.getMessage());
		}
	}

	private static boolean checkHeader(XmlHeader headerInto, XmlHeader headerFrom) {
		if (headerInto.getVirtualScreenHeight() != headerFrom.getVirtualScreenHeight()
				|| headerInto.getVirtualScreenWidth() != headerFrom.getVirtualScreenWidth()) {
			return false;
		}
		return true;
	}

	private static boolean checkMergeConflicts(Project featureA, Project featureB, Activity activity) {
		boolean fail = false;
		boolean first = true;
		String msg = "";

		for (Sprite spriteA : featureA.getSpriteList().subList(1, featureA.getSpriteList().size())) {
			for (Sprite spriteB : featureB.getSpriteList().subList(1, featureB.getSpriteList().size())) {
				if (spriteA.getName().equalsIgnoreCase(spriteB.getName())) {

					if (first) {
						first = false;
						msg += activity.getString(R.string.error_merge_duplicate_names) + "\n\n";
						msg += activity.getString(R.string.sprite) + ":\n";
					}
					msg += "    " + spriteA.getName() + "\n";
				}
			}
		}

		if (!first) {
			first = true;
			msg += "\n";
		}

		for (UserVariable variableA : featureA.getDataContainer().getProjectVariables()) {
			for (UserVariable variableB : featureB.getDataContainer().getProjectVariables()) {
				if (variableA.getName().equalsIgnoreCase(variableB.getName())) {
					if (first) {
						first = false;
						msg += activity.getString(R.string.variable) + ":\n";
					}
					msg += "    " + variableA.getName() + "\n";
				}
			}
		}

		if (!first) {
			first = true;
			msg += "\n";
		}

		List<String> mergeToBroadcastNames = getAllBroadcastNames(featureA);
		List<String> mergeFromBroadcastNames = getAllBroadcastNames(featureB);

		if (!mergeToBroadcastNames.isEmpty()) {
			for (String nameTo : mergeToBroadcastNames) {
				for (String nameFrom : mergeFromBroadcastNames) {
					if (nameTo.equals(nameFrom)) {
						if (first) {
							first = false;
							msg += activity.getString(R.string.broadcast) + ":\n";
						}
						msg += "    " + nameTo + "\n";
					}
				}
			}
		}

		if (!first) {
			first = true;
			msg += "\n";
		}

		for (UserList listA : featureA.getDataContainer().getProjectLists()) {
			for (UserList listB : featureB.getDataContainer().getProjectLists()) {
				if (listA.getName().equals(listB.getName())) {
					if (first) {
						first = false;
						msg += activity.getString(R.string.project_list) + ":\n";
					}
					msg += "    " + listA.getName() + "\n";
				}
			}
		}

		if (!msg.isEmpty()) {
			Utils.showErrorDialog(activity, msg, R.string.merge_conflict);
			fail = true;
		}
		return fail;
	}

	private static List<String> getAllBroadcastNames(Project project) {
		List<String> broadcastNames = new ArrayList<String>();

		for (Sprite sprite : project.getSpriteList()) {
			for (Script script : sprite.getScriptList()) {
				if (script instanceof BroadcastScript) {
					BroadcastScript broadcastScript = (BroadcastScript) script;
					if (!broadcastNames.contains(broadcastScript.getBroadcastMessage())) {
						broadcastNames.add(broadcastScript.getBroadcastMessage());
					}
				}

				for (Brick brick : script.getBrickList()) {
					if (brick instanceof BroadcastBrick) {
						BroadcastBrick broadcastBrick = (BroadcastBrick) brick;
						if (!broadcastNames.contains(broadcastBrick.getBroadcastMessage())) {
							broadcastNames.add(broadcastBrick.getBroadcastMessage());
						}
					}
				}
			}
		}
		return broadcastNames;
	}

	private static Project appendProjects(Project projectToMergeInto, Project projectToMergeFrom, Context context) throws IOException {
		projectToMergeInto.setXmlHeader(setXMLHeaderFields(projectToMergeInto.getXmlHeader(), projectToMergeFrom.getXmlHeader()));

		if (!(StorageHandler.getInstance().copyImageFiles(projectToMergeInto.getName(), projectToMergeFrom.getName()))) {
			throw new IOException("Cannot copy images!");
		}

		if (!(StorageHandler.getInstance().copySoundFiles(projectToMergeInto.getName(), projectToMergeFrom.getName()))) {
			throw new IOException("Cannot copy sounds!");
		}

		projectToMergeInto = copyScripts(projectToMergeInto, projectToMergeFrom);
		projectToMergeInto = copyDataContainer(projectToMergeInto, projectToMergeFrom);

		String msg = projectToMergeFrom.getName() + " " + context.getString(R.string.merge_info) + " " + projectToMergeInto.getName() + "!";
		ToastUtil.showSuccess(context, msg);

		return projectToMergeInto;
	}

	private static XmlHeader setXMLHeaderFields(XmlHeader headerInto, XmlHeader headerFrom) {
		if (headerInto.getRemixOf().equals("")) {
			String tmp = headerInto.getProgramName() + "\n" + headerFrom.getProgramName() + "\n";
			headerInto.setRemixOf(tmp);
		} else {
			headerInto.setRemixOf(headerInto.getRemixOf() + headerFrom.getProgramName() + "\n");
		}

		if (project.getSpriteList().size() < 2) {
			headerInto.setVirtualScreenHeight(headerFrom.getVirtualScreenHeight());
			headerInto.setVirtualScreenWidth(headerFrom.getVirtualScreenWidth());
		}
		return headerInto;
	}

	private static Project copyDataContainer(Project projectToMergeInto, Project projectToMergeFrom) {
		projectToMergeInto = copyVariables(projectToMergeInto, projectToMergeFrom);
		projectToMergeInto.getDataContainer().setProjectLists(projectToMergeFrom.getDataContainer());
		projectToMergeInto = copySpriteListOfLists(projectToMergeInto, projectToMergeFrom);
		projectToMergeInto = copyUserBrickVariables(projectToMergeInto, projectToMergeFrom);
		return projectToMergeInto;
	}

	private static Project copyScripts(Project projectToMergeInto, Project projectToMergeFrom) {
		projectToMergeInto.getSpriteList().addAll(projectToMergeFrom.getSpriteList().subList(1, projectToMergeFrom.getSpriteList().size()));

		for (Sprite sprite : projectToMergeInto.getSpriteList()) {
			for (Script script : sprite.getScriptList()) {
				if (script instanceof BroadcastScript) {
					BroadcastScript broadcastScript = (BroadcastScript) script;
					MessageContainer.addMessage(broadcastScript.getBroadcastMessage());
				}

				for (Brick brick : script.getBrickList()) {
					if (brick instanceof BroadcastBrick) {
						BroadcastBrick broadcastBrick = (BroadcastBrick) brick;
						MessageContainer.addMessage(broadcastBrick.getBroadcastMessage());
					}
				}
			}
		}
		return projectToMergeInto;
	}

	private static Project copyVariables(Project projectToMergeInto, Project projectToMergeFrom) {
		for (UserVariable var : projectToMergeFrom.getDataContainer().getProjectVariables()) {
			projectToMergeInto.getDataContainer().addProjectUserVariable(var.getName());
		}

		for (Sprite sprite : projectToMergeFrom.getSpriteList()) {
			List<UserVariable> variableList = projectToMergeFrom.getDataContainer().getOrCreateVariableListForSprite(sprite);
			projectToMergeInto.getDataContainer().getOrCreateVariableListForSprite(getSpriteInCurrentProject(sprite));

			for (UserVariable variable : variableList) {
				projectToMergeInto.getDataContainer().addSpriteUserVariableToSprite(getSpriteInCurrentProject(sprite),
						variable.getName());
			}
		}

		for (Sprite sprite : projectToMergeInto.getSpriteList()) {
			for (Script script : sprite.getScriptList()) {
				for (Brick brick : script.getBrickList()) {
					if (brick instanceof SetVariableBrick) {
						SetVariableBrick setBrick = (SetVariableBrick) brick;

						if (setBrick.getUserVariable() != null) {
							for (UserVariable variable : projectToMergeInto.getDataContainer().getProjectVariables()) {
								if (setBrick.getUserVariable().getName().equals(variable.getName())) {
									setBrick.setUserVariable(variable);
								}
							}
						}

						List<UserVariable> variableList = projectToMergeInto.getDataContainer().getOrCreateVariableListForSprite(sprite);
						for (UserVariable variable : variableList) {
							if (setBrick.getUserVariable().getName().equals(variable.getName())) {
								setBrick.setUserVariable(variable);
							}
						}
					}
				}
			}
		}
		return projectToMergeInto;
	}

	private static Project copySpriteListOfLists(Project projectToMergeInto, Project projectToMergeFrom) {
		for (Sprite sprite : projectToMergeFrom.getSpriteList()) {
			List<UserList> list = projectToMergeFrom.getDataContainer().getSpriteListOfLists(sprite);
			if (list != null) {
				projectToMergeInto.getDataContainer().addSpriteListOfLists(getSpriteInCurrentProject(sprite), list);
			}
		}
		return projectToMergeInto;
	}

	private static Project copyUserBrickVariables(Project projectToMergeInto, Project projectToMergeFrom) {
		projectToMergeInto.getDataContainer().setUserBrickVariables(projectToMergeFrom.getDataContainer());
		return projectToMergeInto;
	}

	private static Sprite getSpriteInCurrentProject(Sprite searchedSprite) {
		for (Sprite sprite : project.getSpriteList()) {
			if (sprite.getName().equals(searchedSprite.getName())) {
				return sprite;
			}
		}
		return searchedSprite;
	}

	private static Project loadProjectContent(String projectName, Activity activity) throws LoadingProjectException,
			OutdatedVersionProjectException, CompatibilityProjectException {
		if (project.getName().equals(projectName)) {
			throw new LoadingProjectException(activity.getString(R.string.error_load_project));
		}

		Project projectToMergeInto = project;
		ProjectManager.getInstance().loadProject(projectName, activity.getApplicationContext());
		Project projectToMergeFrom = ProjectManager.getInstance().getCurrentProject();
		project = projectToMergeInto;
		return projectToMergeFrom;
	}

	private static void showBackgroundNotEmptyDialog(Project projectToMergeFrom, final Activity activity) {
		final Project copyProjectToMergeFrom = projectToMergeFrom;
		final Activity copyActivity = activity;

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						try {
							Project mergedProject = appendProjects(project, copyProjectToMergeFrom, copyActivity);
							ProjectManager.getInstance().setProject(mergedProject);
							ProjectManager.getInstance().saveProject(activity.getApplicationContext());
						} catch (IOException e) {
							ToastUtil.showError(copyActivity, R.string.error_merge);
							Log.e(TAG, "IOException " + e.getMessage());
						}
						break;
				}
			}
		};

		String question = activity.getString(R.string.error_bricks_in_background);

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(activity);
		builder.setTitle(R.string.warning);
		builder.setMessage(question);
		builder.setPositiveButton(activity.getString(R.string.main_menu_continue), dialogClickListener);
		builder.setNegativeButton(activity.getString(R.string.abort), dialogClickListener);
		Dialog errorDialog = builder.create();
		errorDialog.show();
	}

	private static void showDifferentResolutionDialog(Project projectToMergeFrom, final Activity activity) {
		XmlHeader currentProject = project.getXmlHeader();
		XmlHeader headerFrom = projectToMergeFrom.getXmlHeader();
		final Project copyProjectToMergeFrom = projectToMergeFrom;
		final Activity copyActivity = activity;

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						if (!checkMergeConflicts(project, copyProjectToMergeFrom, copyActivity)) {
							Log.d("MERGE", "no mergeConflicts");
							try {
								ProjectManager.getInstance().setProject(appendProjects(project, copyProjectToMergeFrom, copyActivity));
								ProjectManager.getInstance().saveProject(activity.getApplicationContext());
							} catch (IOException e) {
								ToastUtil.showError(copyActivity, R.string.error_merge);
								Log.e(TAG, "IOException " + e.getMessage());
							}
						}
						break;
				}
			}
		};
		String question = activity.getString(R.string.error_different_resolutions1) + " " + currentProject.getProgramName() + " "
				+ activity.getString(R.string.error_different_resolutions2) + " " + currentProject.getVirtualScreenHeight()
				+ "x" + currentProject.getVirtualScreenWidth() + " " + activity.getString(R.string.and) + " " + headerFrom.getProgramName() + " "
				+ activity.getString(R.string.error_different_resolutions2) + " " + headerFrom.getVirtualScreenHeight() + "x"
				+ headerFrom.getVirtualScreenWidth() + ". " + activity.getString(R.string.error_different_resolutions3) + " "
				+ currentProject.getProgramName() + ".";

		AlertDialog.Builder builder = new CustomAlertDialogBuilder(activity);
		builder.setTitle(R.string.warning);
		builder.setMessage(question);
		builder.setPositiveButton(activity.getString(R.string.main_menu_continue), dialogClickListener);
		builder.setNegativeButton(activity.getString(R.string.abort), dialogClickListener);
		Dialog errorDialog = builder.create();
		errorDialog.show();
	}
}
