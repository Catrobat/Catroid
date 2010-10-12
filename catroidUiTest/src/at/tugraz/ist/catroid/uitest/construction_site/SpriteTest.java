/**
 * 
 */
package at.tugraz.ist.catroid.uitest.construction_site;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;

/**
 * @author Peter Treitler, Thomas Holzmann
 *
 */
public class SpriteTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity> {
	private Solo solo;
	
	public SpriteTest() {
		super("at.tugraz.ist.catroid.test.construction_site",
				ConstructionSiteActivity.class);
	}
	
	private void clearConstructionSite() {
		solo.clickOnMenuItem(getActivity().getString(R.string.reset));
	}
	
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		clearConstructionSite();
		addSprite("NewSprite");
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
	
	private void addSprite(String spriteName) {
		solo.clickOnButton(getActivity().getString(R.string.stage));
		solo.clearEditText(0);
		solo.enterText(0, spriteName);
		solo.clickOnButton(getActivity().getString(R.string.SpriteButtonText));
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
	
	private void typeInDecimalNumber(int editTextId){
		solo.clickOnEditText(editTextId);
		// after that the dialog should appear and focus should be automatically on the EditText again
		
		solo.clearEditText(0);
		solo.enterText(0, "1.3");
		// TODO: Does not run on HTC Desire; it needs an additional goBack() in order to close the soft keyboard 
		// solo.goBack();
		solo.clickOnButton(0);
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
	public void testAddHideBrick() {
		addAndCheckBrick(R.string.hide_main_adapter);
	}
	
	@Smoke
	public void testAddShowBrick() {
		addAndCheckBrick(R.string.show_main_adapter);
	}

	@Smoke
	public void testAddGoToBrick() {
		addAndCheckBrick(R.string.goto_main_adapter);
	}
	
	@Smoke
	public void testAddSetCostumeBrick() {
		addAndCheckBrick(R.string.costume_main_adapter);
	}
	
	@Smoke
	public void testAddScaleCostumeBrick() {
		addAndCheckBrick(R.string.scaleCustome);
	}
	
	@Smoke
	public void testAddComeToFrontBrick() {
		addAndCheckBrick(R.string.come_to_front_main_adapter);
	}
	
	@Smoke
	public void testAddGoBackBrick() {
		addAndCheckBrick(R.string.go_back_main_adapter);
	}
	
	@Smoke
	public void testAddOnTouchBrick() {
		addAndCheckBrick(R.string.touched_main_adapter);
	}
	
	@Smoke
	public void testNumberInputOfBricks(){
		solo.clickOnButton(getActivity().getString(R.string.toolbar));
		solo.clickOnText(getActivity().getString(R.string.wait_main_adapter));
		
		solo.clickOnButton(getActivity().getString(R.string.toolbar));
		solo.clickOnText(getActivity().getString(R.string.scaleCustome));
		
		solo.clickOnButton(getActivity().getString(R.string.toolbar));
		solo.clickOnText(getActivity().getString(R.string.goto_main_adapter));
		
		solo.clickOnButton(getActivity().getString(R.string.toolbar));
		solo.clickOnText(getActivity().getString(R.string.go_back_main_adapter));
		
		typeInDecimalNumber(0);
		String number = solo.getEditText(0).getEditableText().toString();
		assertTrue("Found a decimal value in wait brick.", number.contains("."));
		
		typeInDecimalNumber(1);
		number = solo.getEditText(1).getEditableText().toString();
		assertTrue("Found an integer value in scale brick.", !number.contains("."));
		
		typeInDecimalNumber(2);
		number = solo.getEditText(2).getEditableText().toString();
		assertTrue("Found an integer value in goto brick x.", !number.contains("."));
		
		typeInDecimalNumber(3);
		number = solo.getEditText(3).getEditableText().toString();
		assertTrue("Found an integer value in goto brick y.", !number.contains("."));
		
		typeInDecimalNumber(4);
		number = solo.getEditText(4).getEditableText().toString();
		assertTrue("Found an integer value in goto brick y.", !number.contains("."));
	}
	
	@Smoke
	public void testSelectSound() throws InterruptedException {
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
		
		Thread.sleep(3000); // wait for file copying to finish
		
		assertEquals("Selected item of Spinner is the Sound that was selected",
				itemToSelect, soundSpinner.getSelectedItemId());
	}
}
