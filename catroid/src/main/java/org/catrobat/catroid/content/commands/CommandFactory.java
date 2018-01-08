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
package org.catrobat.catroid.content.commands;

import android.content.Context;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.Translatable;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ConcurrentFormulaHashMap;
import org.catrobat.catroid.content.bricks.FormulaBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.utils.TranslationUtils;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import java8.util.stream.RefStreams;

public final class CommandFactory {

	private CommandFactory() {
	}

	public static ChangeFormulaCommand makeChangeFormulaCommand(FormulaBrick formulaBrick, Brick.BrickField brickField,
			Formula newFormula) {
		ConcurrentFormulaHashMap newFormulaMap = new ConcurrentFormulaHashMap();
		newFormulaMap.putIfAbsent(brickField, newFormula);
		return makeChangeFormulaCommand(formulaBrick, newFormulaMap);
	}

	private static ChangeFormulaCommand makeChangeFormulaCommand(FormulaBrick formulaBrick,
			ConcurrentFormulaHashMap newFormulaMap) {
		return new ChangeFormulaCommand(formulaBrick, newFormulaMap);
	}

	public static ExecuteCommand logCommand(List<Scene> scenes, List<Sprite> sprites, List<LookData> lookDataList,
			List<UserVariable> userVariables, List<UserList> userLists, List<Translatable> translatables) {
		return () -> {
			List<String> stringEntries = new ArrayList<>();
			stringEntries.addAll(UtilFile.readFile(Utils.buildPath(Constants.TMP_TRANSLATION_PATH)));
			RefStreams.of(scenes.toArray(new Scene[scenes.size()])).forEach(scene -> TranslationUtils.addToTranslationList(stringEntries, scene.getName()));
			RefStreams.of(sprites.toArray(new Sprite[sprites.size()])).forEach(sprite -> TranslationUtils.addToTranslationList(stringEntries, sprite.getName()));
			RefStreams.of(lookDataList.toArray(new LookData[lookDataList.size()])).forEach(lookData -> TranslationUtils.addToTranslationList(stringEntries, lookData.getLookName()));
			RefStreams.of(userVariables.toArray(new UserVariable[userVariables.size()])).forEach(userVariable -> TranslationUtils.addToTranslationList(stringEntries, userVariable.getName()));
			RefStreams.of(userLists.toArray(new UserList[userLists.size()])).forEach(userList -> TranslationUtils.addToTranslationList(stringEntries, userList.getName()));
			RefStreams.of(translatables.toArray(new Translatable[translatables.size()])).forEach(translatable -> TranslationUtils.addToTranslationList(stringEntries, translatable.describe()));
			TranslationUtils.writeStringEntries(stringEntries);
		};
	}

	public static ExecuteCommand translateScenesCommand(List<Scene> scenes, Context context) {
		return () -> {
			for (Scene scene : scenes) {
				String oldSceneName = scene.getName();
				scene.setSceneName(TranslationUtils.getStringResourceByName(oldSceneName, context));
				UtilFile.renameSceneDirectory(oldSceneName, scene.getName());
			}
		};
	}

	public static ExecuteCommand translateSpritesCommand(List<Sprite> sprites, Context context) {
		return () -> RefStreams.of(sprites.toArray(new Sprite[sprites.size()])).forEach(sprite -> sprite.setName(TranslationUtils.getStringResourceByName(sprite.getName(), context)));
	}

	public static ExecuteCommand translateLooksCommand(List<LookData> lookDataList, Context context) {
		return () -> RefStreams.of(lookDataList.toArray(new LookData[lookDataList.size()])).forEach(lookData -> lookData.setLookName(TranslationUtils.getStringResourceByName(lookData.getLookName(), context)));
	}

	public static ExecuteCommand translateUserVariablesCommand(List<UserVariable> userVariables, List<Formula> formulas, Context context) {
		return () -> {
			for (UserVariable userVariable : userVariables) {
				String oldName = userVariable.getName();
				userVariable.setName(TranslationUtils.getStringResourceByName(oldName, context));
				RefStreams.of(formulas.toArray(new Formula[formulas.size()])).forEach(formula -> formula.updateVariableReferences(oldName, userVariable.getName(), context));
			}
		};
	}

	public static ExecuteCommand translateUserListsCommand(List<UserList> userLists, List<Formula> formulas, Context context) {
		return () -> {
			for (UserList userList : userLists) {
				String oldName = userList.getName();
				userList.setName(TranslationUtils.getStringResourceByName(oldName, context));
				RefStreams.of(formulas.toArray(new Formula[formulas.size()])).forEach(formula -> formula.updateVariableReferences(oldName, userList.getName(), context));
			}
		};
	}

	public static ExecuteCommand translateTranslatablesCommand(List<Translatable> translatables, Context context) {
		return () -> RefStreams.of(translatables.toArray(new Translatable[translatables.size()])).forEach(translatable -> translatable.translate(context));
	}
}
