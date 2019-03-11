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

package org.catrobat.catroid.test.formulaeditor;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.utils.NumberFormats;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FormulaEditorDataListAdapterArraysValueTest {

	private Project project;
	private String userVarName = "userVar";
	private String userListName = "LIST";

	@Before
	public void setUp() throws Exception {
		createProject();

		UserVariable userVariable = new UserVariable(userVarName);
		userVariable.setValue(NumberFormats.stringWithoutTrailingZero("1.0"));
		UserList userList = new UserList(userListName);
		userList.addListItem(NumberFormats.stringWithoutTrailingZero("1.0"));
		userList.addListItem(NumberFormats.stringWithoutTrailingZero("1.0"));
		userList.addListItem(NumberFormats.stringWithoutTrailingZero("1.05"));
		project.addUserList(userList);
		project.addUserVariable(userVariable);
	}

	@Test
	public void testValuesOfUserList() {
		List<Object> userList = project.getUserList(userListName).getList();

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

	private void createProject() {
		project = new Project(InstrumentationRegistry.getTargetContext(), "Pro");

		Sprite firstSprite = new SingleSprite("firstSprite");

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

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(firstSprite);
	}
}
