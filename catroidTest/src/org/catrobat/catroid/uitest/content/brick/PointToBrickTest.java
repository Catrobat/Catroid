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
package org.catrobat.catroid.uitest.content.brick;

import android.widget.ListView;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PointToBrick;
import org.catrobat.catroid.content.bricks.PointToBrick.SpinnerAdapterWrapper;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog;
import org.catrobat.catroid.ui.dialogs.NewSpriteDialog.ActionAfterFinished;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;

public class PointToBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Project project;
	private PointToBrick pointToBrick;
	private File lookFile;

	private final String spriteName1 = "cat1";
	private final String spriteName2 = "cat2";
	private final String newSpriteName1 = "cat3";
	private final String newSpriteName2 = "cat4";

	public PointToBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.prepareStageForTest();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo, 2);
		lookFile = UiTestUtils.setUpLookFile(solo);
	}

	@Override
	public void tearDown() throws Exception {
		if (lookFile != null) {
			lookFile.delete();
		}
		super.tearDown();
	}

	public void testPointToBrickTest() throws InterruptedException {
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) dragDropListView.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();

		assertEquals("Incorrect number of bricks.", 3, dragDropListView.getChildCount());
		assertEquals("Incorrect number of bricks.", 2, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());

		int oldSpriteListSize = project.getSpriteList().size();
		String spinnerNewText = solo.getString(R.string.new_broadcast_message);

		assertNotNull("TextView does not exist", solo.getText(solo.getString(R.string.brick_point_to)));

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.brick_point_to_spinner));
		solo.waitForText(spinnerNewText);
		solo.clickInList(0);
		assertTrue("First step of dialog not shown",
				solo.waitForFragmentByTag(NewSpriteDialog.DIALOG_FRAGMENT_TAG, 5000));
		solo.goBack();

		createNewObjectWithinBrick(newSpriteName1, R.string.no);
		assertEquals("In wrong sprite", spriteName1, ProjectManager.getInstance().getCurrentSprite().getName());

		assertEquals("Wrong number of sprites", oldSpriteListSize + 1, project.getSpriteList().size());
		assertEquals("Wrong selection", newSpriteName1, ((Spinner) solo.getView(R.id.brick_point_to_spinner))
				.getSelectedItem().toString());

		solo.clickOnView(solo.getCurrentActivity().findViewById(R.id.brick_point_to_spinner));
		solo.waitForText(spinnerNewText);
		solo.clickInList(0);
		solo.goBack();
		assertEquals("Wrong selection", newSpriteName1, ((Spinner) solo.getView(R.id.brick_point_to_spinner))
				.getSelectedItem().toString());

		createNewObjectWithinBrick(newSpriteName2, R.string.yes);
		assertEquals("In wrong sprite", newSpriteName2, ProjectManager.getInstance().getCurrentSprite().getName());

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_motion));
		solo.searchText(solo.getString(R.string.category_motion));
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollDownList(fragmentListView);
		assertTrue("Wrong selection in prototype spinner", solo.isSpinnerTextSelected(spriteName2));

		UiTestUtils.goToHomeActivity(getActivity());
		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		solo.clickLongOnText(spriteName1);
		solo.waitForText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.delete));

		solo.clickLongOnText(newSpriteName1);
		solo.waitForText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.delete));

		solo.clickLongOnText(newSpriteName2);
		solo.waitForText(solo.getString(R.string.delete));
		solo.clickOnText(solo.getString(R.string.delete));

		solo.clickOnText(spriteName2);
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.scripts));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.clickOnText(solo.getString(R.string.category_motion));
		solo.searchText(solo.getString(R.string.category_motion));
		fragmentListView = solo.getCurrentViews(ListView.class).get(solo.getCurrentViews(ListView.class).size() - 1);
		solo.scrollDownList(fragmentListView);
		assertTrue("Wrong selection in prototype spinner", solo.isSpinnerTextSelected(spinnerNewText));
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);

		Sprite sprite2 = new Sprite(spriteName2);
		Script startScript2 = new StartScript();
		PlaceAtBrick placeAt2 = new PlaceAtBrick(-400, -300);
		startScript2.addBrick(placeAt2);
		sprite2.addScript(startScript2);
		project.addSprite(sprite2);

		Sprite sprite1 = new Sprite(spriteName1);
		Script startScript1 = new StartScript();
		PlaceAtBrick placeAt1 = new PlaceAtBrick(300, 400);
		startScript1.addBrick(placeAt1);
		pointToBrick = new PointToBrick(sprite2);
		startScript1.addBrick(pointToBrick);
		sprite1.addScript(startScript1);
		project.addSprite(sprite1);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);
		ProjectManager.getInstance().setCurrentScript(startScript1);
		StorageHandler.getInstance().saveProject(project);
	}

	private void createNewObjectWithinBrick(String objectName, int stringToClickOnAtTheEnd) {
		SpinnerAdapterWrapper spinner = (SpinnerAdapterWrapper) Reflection.getPrivateField(pointToBrick,
				"spinnerAdapterWrapper");

		UiTestUtils.showAndFilloutNewSpriteDialogWithoutClickingOk(solo, objectName, lookFile,
				ActionAfterFinished.ACTION_UPDATE_SPINNER, spinner);
		solo.clickOnButton(solo.getString(R.string.ok));

		UiTestUtils.hidePocketPaintDialog(solo);
		assertTrue("Dialog not shown",
				solo.waitForText(solo.getString(R.string.dialog_new_object_switch_message), 0, 10000));
		solo.clickOnButton(solo.getString(stringToClickOnAtTheEnd));
		UiTestUtils.hidePocketPaintDialog(solo);
	}
}
