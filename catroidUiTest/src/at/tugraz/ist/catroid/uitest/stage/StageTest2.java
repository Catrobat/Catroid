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
package at.tugraz.ist.catroid.uitest.stage;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;

import com.jayway.android.robotium.solo.Solo;

/**
 * @author Peter Treitler
 * 
 * Tests the features of the construction site
 */
public class StageTest2 extends
		ActivityInstrumentationTestCase2<ConstructionSiteActivity> {
	private Solo solo;
	private String toolbar = "B\nu\ni\nl\nd\ni\nn\ng\n \nB\nl\no\nc\nk\ns";
	
	public StageTest2() {
		super("at.tugraz.ist.catroid",
				ConstructionSiteActivity.class);
	}
	
	private void clearConstructionSite() {
		solo.clickOnMenuItem(getActivity().getString(R.string.reset));
	}
	
	private void addBrick(int brickTextId) {
		solo.clickOnButton(toolbar);
		solo.clickOnText(getActivity().getString(brickTextId));
	}
	
	private void addAndCheckBrick(int brickTextId) {
		addBrick(brickTextId);
		boolean foundText = solo.searchText(getActivity().getString(brickTextId));
		assertTrue("Found brick in construction site", foundText);
	}

	@Override
    public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		clearConstructionSite();
	}
	
	@Override
    public void tearDown() throws Exception {
		clearConstructionSite();
		try {	
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
	
	@Smoke
	public void testAddSetBackgroundBrick() {
		addAndCheckBrick(R.string.set_background_main_adapter);
	}
	
	@Smoke
	public void testAddPlaySoundBrick() {
		addAndCheckBrick(R.string.play_sound_main_adapter);
	}
	
	@Smoke
	public void testAddWaitBrick() {
		addAndCheckBrick(R.string.wait_main_adapter);
	}
	
//	@Smoke
//	public void testSelectSound() throws InterruptedException {
//		addBrick(R.string.play_sound_main_adapter);
//		
//		Thread.sleep(400);
//		ListView lv = (ListView)getActivity().findViewById(R.id.MainListView);
//		System.out.println("lv children: " + lv.getChildCount());
//		RelativeLayout rl = (RelativeLayout)lv.getChildAt(0);
//		
//		int itemToSelect = 2;
//		Spinner soundSpinner = (Spinner)rl.getChildAt(1);
//		assertNotNull("There are sound files present to select", soundSpinner.getItemAtPosition(itemToSelect));
//
//		solo.clickOnView(soundSpinner);
//		solo.clickInList(itemToSelect);
//		
//		assertEquals("Selected item of Spinner is the Sound that was selected",
//				itemToSelect, soundSpinner.getSelectedItemId());
//	}
	
	@Smoke
	public void testAddSprite() {
		solo.clickOnButton(getActivity().getString(R.string.stage));
		solo.clearEditText(0);
		solo.enterText(0, "NewSprite");
		solo.clickOnButton(getActivity().getString(R.string.SpriteButtonText));
		boolean foundText = solo.searchText("NewSprite");
		assertTrue("Found newly created sprite", foundText);
	}
	
    /*
     * @Smoke public void testRemoveBrick() {
     * addBrick(R.string.wait_main_adapter);
     * 
     * solo.clickLongOnText(getActivity().getString(R.string.wait_main_adapter));
     * solo.clickOnButton(0);
     * 
     * boolean foundText =
     * solo.searchText(getActivity().getString(R.string.wait_main_adapter));
     * assertFalse("Brick not found after deleting it.", foundText); }
     */
}
