/**
 * 
 */
package at.tugraz.ist.catroid.uitest.construction_site;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;

import com.jayway.android.robotium.solo.Solo;

/**
 * @author Peter Treitler
 * 
 * Tests the features of the construction site
 */
public class StageTest extends
		ActivityInstrumentationTestCase2<ConstructionSiteActivity> {
	private Solo solo;
	
	public StageTest() {
		super("at.tugraz.ist.catroid.test.construction_site",
				ConstructionSiteActivity.class);
	}
	
	private void clearConstructionSite() {
		solo.clickOnMenuItem(getActivity().getString(R.string.reset));
	}
	
	private void addBrick(int brickTextId) {
		solo.clickOnButton(getActivity().getString(R.string.toolbar));
		solo.clickOnText(getActivity().getString(brickTextId));
	}
	
	private void addAndCheckBrick(int brickTextId) {
		addBrick(brickTextId);
		boolean foundText = solo.searchText(getActivity().getString(brickTextId));
		assertTrue("Found brick in construction site", foundText);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		clearConstructionSite();
	}
	
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
	
	@Smoke
	public void testSelectSound() throws InterruptedException {
		// TODO: If there are no sounds on the device this test fails. Copy some default sounds to device?
		addBrick(R.string.play_sound_main_adapter);
		
		Thread.sleep(400);
		ListView lv = (ListView)getActivity().findViewById(R.id.MainListView);
		System.out.println("lv children: " + lv.getChildCount());
		RelativeLayout rl = (RelativeLayout)lv.getChildAt(0);
		
		int itemToSelect = 2;
		Spinner soundSpinner = (Spinner)rl.getChildAt(1);
		assertNotNull("There are sound files present to select", soundSpinner.getItemAtPosition(itemToSelect));

		solo.clickOnView(soundSpinner);
		solo.clickInList(itemToSelect);
		
		assertEquals("Selected item of Spinner is the Sound that was selected",
				itemToSelect, soundSpinner.getSelectedItemId());
	}
	
	@Smoke
	public void testAddSprite() {
		solo.clickOnButton(getActivity().getString(R.string.stage));
		solo.clearEditText(0);
		solo.enterText(0, "NewSprite");
		solo.clickOnButton(getActivity().getString(R.string.SpriteButtonText));
		boolean foundText = solo.searchText("NewSprite");
		assertTrue("Found newly created sprite", foundText);
	}
	
	/* TODO: Does not work because the shake animation keeps the UI thread busy
	@Smoke
	public void testRemoveBrick() {
		addBrick(R.string.wait_main_adapter);

		solo.clickLongOnText(getActivity().getString(R.string.wait_main_adapter));
		solo.clickOnButton(0);
		
		boolean foundText = solo.searchText(getActivity().getString(R.string.wait_main_adapter));
		assertFalse("Brick not found after deleting it.", foundText);
	}
	*/
}
