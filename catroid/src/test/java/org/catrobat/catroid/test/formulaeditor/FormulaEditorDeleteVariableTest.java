/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserDataWrapper;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.catroid.common.Constants.Z_INDEX_BACKGROUND;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LayoutInflater.class})
public class FormulaEditorDeleteVariableTest {

	@Mock
	LayoutInflater mockInflater;

	@Mock
	View mockView;

	@Mock
	ViewGroup mockParent;

	private Context mockContext;

	private static final String GLOBAL_USER_VARIABLE = "UserVariable";
	private static final String LOCAL_USER_VARIABLE1 = "UserVariable";
	private static final String LOCAL_USER_VARIABLE2 = "UserVariable";
	private static final String PROJECT_NAME = "testProject";
	private static final String SPRITE_NAME = "BackgroundSprite";

	private Project project;
	private Scene sceneOne;
	private DataListFragment dataListFragment;
	private Script script;
	private ChangeXByNBrick brick;
	private FormulaElement formulaElement;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockStatic(LayoutInflater.class);
		mockContext = MockUtil.mockContextForProject();

		project = new Project(MockUtil.mockContextForProject(), PROJECT_NAME);
		ProjectManager.getInstance().setCurrentProject(project);

		sceneOne = new Scene("test1", project);
		Sprite backgroundSprite = new Sprite(SPRITE_NAME);
		backgroundSprite.look.setZIndex(Z_INDEX_BACKGROUND);
		sceneOne.addSprite(backgroundSprite);
		ProjectManager.getInstance().setCurrentSprite(sceneOne.getBackgroundSprite());

		dataListFragment = new DataListFragment();

		script = new Script() {
			@Override
			public EventId createEventId(Sprite sprite) {
				return null;
			}

			@Override
			public ScriptBrick getScriptBrick() {
				return null;
			}
		};

		project.addScene(sceneOne);
		sceneOne.getBackgroundSprite().addUserVariable(new UserVariable(LOCAL_USER_VARIABLE1));
		project.getDefaultScene().getBackgroundSprite().addUserVariable(new UserVariable(LOCAL_USER_VARIABLE2));
		project.addUserVariable(new UserVariable(GLOBAL_USER_VARIABLE));

		when(mockParent.getContext()).thenReturn(mockContext);
		when(LayoutInflater.from(mockContext)).thenReturn(mockInflater);
		when(mockInflater.inflate(anyInt(), eq(mockParent), eq(false))).thenReturn(mockView);

		LayoutInflater layoutInflater = LayoutInflater.from(MockUtil.mockContextForProject());
		ViewGroup viewGroup = Mockito.mock(ViewGroup.class);

		dataListFragment.onCreateView(mockInflater, mockParent, null);
		dataListFragment.onActivityCreated(null);

	}

	@Test
	public void testDeleteFreeLocalVariable() {
		dataListFragment.showDeleteAlert(new ArrayList<>(Collections.singletonList(
				UserDataWrapper.getUserVariable(LOCAL_USER_VARIABLE1, sceneOne.getBackgroundSprite(), project))));
		assertFalse(interpretUserVariable(UserDataWrapper.getUserVariable(LOCAL_USER_VARIABLE1,
				sceneOne.getBackgroundSprite(), project)));
		assertTrue(interpretUserVariable(UserDataWrapper.getUserVariable(GLOBAL_USER_VARIABLE, null, project)));
		assertTrue(interpretUserVariable(UserDataWrapper.getUserVariable(LOCAL_USER_VARIABLE2,
				project.getDefaultScene().getBackgroundSprite(), project)));
	}

	@Test
	public void testDeleteUsedLocalVariable() {
		formulaElement = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, "userVariable", null);
		brick = new ChangeXByNBrick(new Formula(formulaElement));
		script.addBrick(brick);
		sceneOne.getBackgroundSprite().addScript(script);
		dataListFragment.showDeleteAlert(new ArrayList<>(Collections.singletonList(
				UserDataWrapper.getUserVariable(LOCAL_USER_VARIABLE1, sceneOne.getBackgroundSprite(), project))));
		assertTrue(interpretUserVariable(UserDataWrapper.getUserVariable(LOCAL_USER_VARIABLE1,
				sceneOne.getBackgroundSprite(), project)));
		assertTrue(interpretUserVariable(UserDataWrapper.getUserVariable(GLOBAL_USER_VARIABLE, null, project)));
		assertTrue(interpretUserVariable(UserDataWrapper.getUserVariable(LOCAL_USER_VARIABLE2,
				project.getDefaultScene().getBackgroundSprite(), project)));
	}

	private boolean interpretUserVariable(UserVariable userVariable){
		if(userVariable == null) return false;
		return true;
	}
}
