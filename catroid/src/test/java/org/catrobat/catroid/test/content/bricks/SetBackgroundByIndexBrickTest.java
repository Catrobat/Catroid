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

package org.catrobat.catroid.test.content.bricks;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class SetBackgroundByIndexBrickTest {
	private Sprite sprite;
	private final double backgroundIndex = 2.0;
	private String localUserVariableBackgroundIndex = "BGI";

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(GdxNativesLoader.class);

		Project project = new Project(MockUtil.mockContextForProject(), "testProject");
		sprite = new Sprite("Sprite");
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		sprite.addUserVariable(new UserVariable(localUserVariableBackgroundIndex, backgroundIndex));

		List<LookData> lookDataList = project.getDefaultScene().getBackgroundSprite().getLookList();
		LookData lookData = new LookData();
		lookData.setFile(Mockito.mock(File.class));
		lookData.setName("background1");
		lookDataList.add(lookData);

		LookData lookData2 = new LookData();
		lookData2.setFile(Mockito.mock(File.class));
		lookData2.setName("background2");
		lookDataList.add(lookData2);

		project.getDefaultScene().getBackgroundSprite().look.setLookData(lookData);
	}

	@Test
	public void testSetBackgroundByIndex() {
		ActionFactory actionFactory = new ActionFactory();
		actionFactory.createSetBackgroundByIndexAction(
				sprite,
				new SequenceAction(),
				createFormulaWithVariable(localUserVariableBackgroundIndex),
				true
		).act(1f);

		LookData backgroundLookData = ProjectManager.getInstance().getCurrentlyPlayingScene().getBackgroundSprite().look.getLookData();
		List<LookData> backgroundLookDataList = ProjectManager.getInstance().getCurrentlyPlayingScene().getBackgroundSprite().getLookList();

		assertEquals(backgroundLookData, backgroundLookDataList.get((int) backgroundIndex - 1));
	}

	private Formula createFormulaWithVariable(String variableName) {
		FormulaElement formulaElement = new FormulaElement(FormulaElement.ElementType.USER_VARIABLE, variableName, null);
		return new Formula(formulaElement);
	}
}
