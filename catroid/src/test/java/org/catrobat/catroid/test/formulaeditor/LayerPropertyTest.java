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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.MockUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LayerPropertyTest {
	Sprite background;
	Sprite firstSprite;
	Sprite secondSprite;

	@Before
	public void setUp() throws Exception {
		Project project = new Project(MockUtil.mockContextForProject(), "Project");
		background = project.getDefaultScene().getBackgroundSprite();
		firstSprite = new Sprite("firstSprite");
		secondSprite = new Sprite("secondSprite");

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
	}

	@Test
	public void testBackgroundLayerPropertyNoZIndex() {
		FormulaElement layerElement = new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_LAYER.name(), null);
		assertEquals(0d, layerElement.interpretRecursive(background));
	}

	@Test
	public void testSpritesLayerPropertyNoZIndex() {
		FormulaElement layerElement = new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_LAYER.name(), null);
		assertEquals(1d, layerElement.interpretRecursive(firstSprite));
		assertEquals(2d, layerElement.interpretRecursive(secondSprite));
	}

	@Test
	public void testSpritesLayerPropertyZIndexSet() {
		FormulaElement layerElement = new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_LAYER.name(), null);

		Look firstLook = mock(Look.class);
		when(firstLook.getZIndex()).thenReturn(2 + Constants.Z_INDEX_NUMBER_VIRTUAL_LAYERS);
		Look secondLook = mock(Look.class);
		when(secondLook.getZIndex()).thenReturn(1 + Constants.Z_INDEX_NUMBER_VIRTUAL_LAYERS);

		firstSprite.look = firstLook;
		secondSprite.look = secondLook;

		assertEquals(2d, layerElement.interpretRecursive(firstSprite));
		assertEquals(1d, layerElement.interpretRecursive(secondSprite));
	}

	@Test
	public void testBackgroundLayerPropertyZIndexSet() {
		FormulaElement layerElement = new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.OBJECT_LAYER.name(), null);

		Look backgroundLook = mock(Look.class);
		when(backgroundLook.getZIndex()).thenReturn(0);
		background.look = backgroundLook;
		assertEquals(0d, layerElement.interpretRecursive(background));
	}
}
