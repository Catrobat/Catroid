/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.test.formulaeditor;

import android.content.Context;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.utils.NumberFormats;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.koin.core.module.Module;

import java.util.Collections;
import java.util.List;

import kotlin.Lazy;

import static org.junit.Assert.assertEquals;
import static org.koin.java.KoinJavaComponent.inject;

@RunWith(JUnit4.class)
public class FormulaEditorDataListAdapterArraysValueTest {

	private Project project;
	private final String userVarName = "userVar";
	private final String userListName = "LIST";
	private final String multiplayerVarName = "multiplayerVar";

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws Exception {
		createProject();

		UserVariable userVariable = new UserVariable(userVarName);
		userVariable.setValue(NumberFormats.trimTrailingCharacters("1.0"));
		UserList userList = new UserList(userListName);
		UserVariable multiplayerVariable = new UserVariable(multiplayerVarName);
		multiplayerVariable.setValue(NumberFormats.trimTrailingCharacters("2.0"));
		userList.addListItem(NumberFormats.trimTrailingCharacters("1.0"));
		userList.addListItem(NumberFormats.trimTrailingCharacters("1.0"));
		userList.addListItem(NumberFormats.trimTrailingCharacters("1.05"));
		project.addUserList(userList);
		project.addUserVariable(userVariable);
		project.addMultiplayerVariable(multiplayerVariable);
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testValuesOfUserList() {
		List<Object> userList = project.getUserList(userListName).getValue();

		assertEquals(3, userList.size());
		assertEquals(String.valueOf(1), userList.get(0));
		assertEquals(String.valueOf(1), userList.get(1));
		assertEquals(String.valueOf(1.05), userList.get(2));
	}

	@Test
	public void testValueOfUserVariable() {
		UserVariable userVar = project.getUserVariable(userVarName);
		assertEquals(String.valueOf(1), userVar.getValue());
	}

	@Test
	public void testValueOfMultiplayerVariable() {
		UserVariable multiplayerVariable = project.getMultiplayerVariable(multiplayerVarName);
		assertEquals(String.valueOf(2), multiplayerVariable.getValue());
	}

	private void createProject() {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		project = new Project(context, "Pro");

		Sprite firstSprite = new Sprite("firstSprite");

		Script firstScript = new StartScript();
		firstScript.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		firstScript.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		firstSprite.addScript(firstScript);

		Script secondScript = new StartScript();
		secondScript.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		secondScript.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		secondScript.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		firstSprite.addScript(secondScript);

		LookData lookData = new LookData();
		lookData.setName("red");
		firstSprite.getLookList().add(lookData);

		LookData anotherLookData = new LookData();
		anotherLookData.setName("blue");
		firstSprite.getLookList().add(anotherLookData);

		project.getDefaultScene().addSprite(firstSprite);

		projectManager.getValue().setCurrentProject(project);
		projectManager.getValue().setCurrentSprite(firstSprite);
	}
}
