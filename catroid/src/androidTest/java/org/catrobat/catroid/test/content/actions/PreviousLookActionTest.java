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
package org.catrobat.catroid.test.content.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.NextLookAction;
import org.catrobat.catroid.content.actions.PreviousLookAction;
import org.catrobat.catroid.content.actions.SetLookAction;
import org.catrobat.catroid.test.BaseInstrumentationTest;

import java.io.File;

public class PreviousLookActionTest extends BaseInstrumentationTest {

	private File testImage;
	private Sprite sprite;
	private ActionFactory actionFactory;
	private Project project;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		final String imagePath = Constants.DEFAULT_ROOT + "/testImage.png";
		testImage = new File(imagePath);
		sprite = createSprite("cat");
		actionFactory = sprite.getActionFactory();
		project = new Project(null, "testProject");
		ProjectManager.getInstance().setProject(project);
	}

	public void testPreviousLook() {
		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setLookName("testImage1");
		sprite.getLookDataList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setLookFilename(testImage.getName());
		lookData2.setLookName("testImage2");
		sprite.getLookDataList().add(lookData2);

		SetLookAction setLookAction = (SetLookAction) actionFactory.createSetLookAction(sprite, lookData1);
		NextLookAction nextLookAction = (NextLookAction) actionFactory.createNextLookAction(sprite);
		PreviousLookAction previousLookAction = (PreviousLookAction) actionFactory.createPreviousLookAction(sprite);

		setLookAction.act(1.0f);
		nextLookAction.act(1.0f);

		assertEquals("Look is not next look", lookData2, sprite.look.getLookData());

		previousLookAction.act(1.0f);

		assertEquals("Look is not previous look", lookData1.getLookName(), sprite.look.getLookData().getLookName());
	}

	public void testLastLook() {
		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setLookName("testImage1");
		sprite.getLookDataList().add(lookData1);

		LookData lookData2 = new LookData();
		lookData2.setLookFilename(testImage.getName());
		lookData2.setLookName("testImage2");
		sprite.getLookDataList().add(lookData2);

		LookData lookData3 = new LookData();
		lookData3.setLookFilename(testImage.getName());
		lookData3.setLookName("testImage3");
		sprite.getLookDataList().add(lookData3);

		Action setLookAction = actionFactory.createSetLookAction(sprite, lookData1);
		Action previousLookAction = actionFactory.createPreviousLookAction(sprite);

		setLookAction.act(1.0f);
		previousLookAction.act(1.0f);

		assertEquals("Look is not last look", lookData3.getLookName(), sprite.look.getLookData().getLookName());
	}

	public void testLookGalleryNull() {
		Action previousLookAction = actionFactory.createPreviousLookAction(sprite);
		previousLookAction.act(1.0f);

		assertEquals("Look is not null", null, sprite.look.getLookData());
	}

	public void testLookGalleryWithOneLook() {
		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setLookName("testImage1");
		sprite.getLookDataList().add(lookData1);

		Action setLookAction = actionFactory.createSetLookAction(sprite, lookData1);
		Action previousLookAction = actionFactory.createPreviousLookAction(sprite);

		setLookAction.act(1.0f);
		previousLookAction.act(1.0f);

		assertEquals("Wrong look after executing PreviousLookBrick with just one look", lookData1.getLookName(),
				sprite.look.getLookData().getLookName());
	}

	public void testPreviousLookWithNoLookSet() {
		Action previousLookAction = actionFactory.createPreviousLookAction(sprite);

		LookData lookData1 = new LookData();
		lookData1.setLookFilename(testImage.getName());
		lookData1.setLookName("testImage1");
		sprite.getLookDataList().add(lookData1);

		previousLookAction.act(1.0f);

		assertNull("No look should be set.", sprite.look.getLookData());
	}
}
