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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.koin.CatroidKoinHelperKt;
import org.catrobat.catroid.test.MockUtil;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LayerPropertyTest {
	Scope backgroundScope;
	Scope firstScope;
	Scope secondScope;

	private final Lazy<ProjectManager> projectManager = inject(ProjectManager.class);
	private final List<Module> dependencyModules =
			Collections.singletonList(CatroidKoinHelperKt.getProjectManagerModule());

	@Before
	public void setUp() throws Exception {
		Context context = MockUtil.mockContextForProject(dependencyModules);
		Project project = new Project(context, "Project");
		Sprite background = project.getDefaultScene().getBackgroundSprite();
		Sprite firstSprite = new Sprite("firstSprite");
		Sprite secondSprite = new Sprite("secondSprite");

		backgroundScope = new Scope(project, background, new SequenceAction());
		firstScope = new Scope(project, firstSprite, new SequenceAction());
		secondScope = new Scope(project, secondSprite, new SequenceAction());

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);

		projectManager.getValue().setCurrentProject(project);
		projectManager.getValue().setCurrentlyEditedScene(project.getDefaultScene());
	}

	@After
	public void tearDown() throws Exception {
		CatroidKoinHelperKt.stop(dependencyModules);
	}

	@Test
	public void testBackgroundLayerPropertyNoZIndex() {
		FormulaElement layerElement = new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_LAYER.name(), null);
		assertEquals(0d, layerElement.interpretRecursive(backgroundScope));
	}

	@Test
	public void testSpritesLayerPropertyNoZIndex() {
		FormulaElement layerElement = new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_LAYER.name(), null);
		assertEquals(1d, layerElement.interpretRecursive(firstScope));
		assertEquals(2d, layerElement.interpretRecursive(secondScope));
	}

	@Test
	public void testSpritesLayerPropertyZIndexSet() {
		FormulaElement layerElement = new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_LAYER.name(), null);

		Look firstLook = mock(Look.class);
		when(firstLook.getZIndex()).thenReturn(2 + Constants.Z_INDEX_NUMBER_VIRTUAL_LAYERS);
		Look secondLook = mock(Look.class);
		when(secondLook.getZIndex()).thenReturn(1 + Constants.Z_INDEX_NUMBER_VIRTUAL_LAYERS);

		firstScope.getSprite().look = firstLook;
		secondScope.getSprite().look = secondLook;

		assertEquals(2d, layerElement.interpretRecursive(firstScope));
		assertEquals(1d, layerElement.interpretRecursive(secondScope));
	}

	@Test
	public void testBackgroundLayerPropertyZIndexSet() {
		FormulaElement layerElement = new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_LAYER.name(), null);

		Look backgroundLook = mock(Look.class);
		when(backgroundLook.getZIndex()).thenReturn(0);
		backgroundScope.getSprite().look = backgroundLook;
		assertEquals(0d, layerElement.interpretRecursive(backgroundScope));
	}
}
