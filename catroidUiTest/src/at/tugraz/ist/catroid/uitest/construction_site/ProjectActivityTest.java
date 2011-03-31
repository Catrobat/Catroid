/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.uitest.construction_site;

import java.io.File;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class ProjectActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

    private final String projectNameOne = "Ulumulu";
    private final String projectNameTwo = "Ulumulu2";
    private final String spriteNameOne = "Zuul";
    private final String spriteNameTwo = "Zuuul";

	public ProjectActivityTest() {
		super("at.tugraz.ist.catroid.ui", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
        File directory = new File("/sdcard/catroid/" + projectNameOne);
        UtilFile.deleteDirectory(directory);
        directory = new File("/sdcard/catroid/" + projectNameTwo);
        UtilFile.deleteDirectory(directory);
        super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
        File directory = new File("/sdcard/catroid/" + projectNameOne);
        UtilFile.deleteDirectory(directory);
        assertFalse(projectNameOne + " was not deleted!", directory.exists());
        directory = new File("/sdcard/catroid/" + projectNameTwo);
        UtilFile.deleteDirectory(directory);
        assertFalse(projectNameTwo + " was not deleted!", directory.exists());
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();

		super.tearDown();
	}

	public void testMainMenuButton() throws InterruptedException {
		solo.clickOnButton(getActivity().getString(R.string.resume));
		solo.clickOnButton(getActivity().getString(R.string.main_menu));
		Thread.sleep(1000);
		solo.clickOnButton(getActivity().getString(R.string.resume)); //if this is possible it worked! (will throw AssertionFailedError if not working
	}

	public void testCreateNewSpriteButton() throws InterruptedException {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.clickOnEditText(0);
        solo.enterText(0, projectNameTwo);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_project_dialog_button));
		Thread.sleep(300);

		solo.clickOnButton(getActivity().getString(R.string.add_sprite));
		solo.clickOnEditText(0);
		solo.enterText(0, spriteNameOne);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_sprite_dialog_button));
		Thread.sleep(300);

		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(R.id.spriteListView);
		Sprite second = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite at index 1 is not " + spriteNameOne, spriteNameOne, second.getName());
		assertTrue("Sprite is not in current Project", ProjectManager.getInstance().getCurrentProject().getSpriteList()
		        .contains(second));
	}

	public void testContextMenu() throws InterruptedException {
		solo.clickOnButton(getActivity().getString(R.string.new_project));
		solo.clickOnEditText(0);
		solo.enterText(0, projectNameOne);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.new_project_dialog_button));
		Thread.sleep(300);

		Sprite testSprite = new Sprite(spriteNameOne);
		Sprite testSprite2 = new Sprite(spriteNameTwo);
		ProjectManager manager = ProjectManager.getInstance();
		manager.getCurrentProject().addSprite(testSprite);
		manager.getCurrentProject().addSprite(testSprite2);

		String[] menu = getActivity().getResources().getStringArray(R.array.menu_project_activity);

		solo.clickLongOnText(spriteNameOne);
        solo.clickOnText(menu[1]);
		Thread.sleep(300);
		assertFalse("Sprite is still in Project", manager.getCurrentProject().getSpriteList().contains(testSprite));
		ListView spritesList = (ListView) solo.getCurrentActivity().findViewById(R.id.spriteListView);
		Sprite sprite2 = (Sprite) spritesList.getItemAtPosition(1);
		assertEquals("Sprite on position one is not sprite2/testSprite2", testSprite2, sprite2);

		solo.clickLongOnText(spriteNameTwo);
        solo.clickOnText(menu[0]);
		solo.clickOnEditText(0);
		solo.enterText(0, spriteNameOne);
		solo.goBack();
		solo.clickOnButton(getActivity().getString(R.string.rename_button));
		Thread.sleep(300);
		assertEquals("Sprite on position one is not sprite2/testSprite2", testSprite2, sprite2);
		assertEquals("Sprite on position one has the wrong name", spriteNameOne, testSprite2.getName());
	}
}
