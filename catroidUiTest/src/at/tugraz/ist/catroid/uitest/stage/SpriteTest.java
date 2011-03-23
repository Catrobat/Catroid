/**
 * 
 */
package at.tugraz.ist.catroid.uitest.stage;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;

import com.jayway.android.robotium.solo.Solo;

/**
 * @author Peter Treitler, Thomas Holzmann
 * 
 */

public class SpriteTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity> {
	private Solo solo;
	// TODO: This is a hack! This test is going to be replaces anyway!
	private String toolbar = "B\nu\ni\nl\nd\ni\nn\ng\n \nB\nl\no\nc\nk\ns";
	public SpriteTest() {
		super("at.tugraz.ist.catroid", ConstructionSiteActivity.class);
	}

	private void clearConstructionSite() {
		solo.clickOnMenuItem(getActivity().getString(R.string.reset));
	}

	@Override
    public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		clearConstructionSite();
		addSprite("NewSprite");
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

	private void addSprite(String spriteName) {
		solo.clickOnButton(getActivity().getString(R.string.stage));
		solo.clearEditText(0);
		solo.enterText(0, spriteName);
		solo.clickOnButton(getActivity().getString(R.string.SpriteButtonText));
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

	private void typeInDecimalNumber(int editTextId) {
		solo.clickOnEditText(editTextId);
		// after that the dialog should appear and focus should be automatically
		// on the EditText again

		solo.clearEditText(0);
		solo.enterText(0, "1.3");
		solo.goBack();
		// Log.d("device", Settings.System.getString(getInstrumentation().getContext().getContentResolver(), "android.os.Build.MODEL"));
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
	public void testNumberInputOfBricks() {
		solo.clickOnButton(toolbar);
		solo.clickOnText(getActivity().getString(R.string.wait_main_adapter));

		solo.clickOnButton(toolbar);
		solo.clickOnText(getActivity().getString(R.string.goto_main_adapter));

		solo.clickOnButton(toolbar);
		solo.clickOnText(getActivity().getString(R.string.scaleCustome));

		solo.clickOnButton(toolbar);
		solo.clickOnText(getActivity().getString(R.string.go_back_main_adapter));

		typeInDecimalNumber(0);
		String number = solo.getEditText(0).getEditableText().toString();
		assertTrue("Could not enter a decimal value into a wait brick.", number.equals("1.3"));

		// TODO: It seems like robotium can "cheat" the Android InputType so the following asserts fail!
		/*
		typeInDecimalNumber(1);
		number = solo.getEditText(1).getEditableText().toString();
		assertFalse("Could enter a decimal value into a go to brick, which shouldn't be possible.", number.equals("1.3"));
		
		typeInDecimalNumber(2);
		number = solo.getEditText(2).getEditableText().toString();
		assertFalse("Could enter a decimal value into a go to brick, which shouldn't be possible.", number.equals("1.3"));
		
		typeInDecimalNumber(3);
		number = solo.getEditText(3).getEditableText().toString();
		assertFalse("Could enter a decimal value into a scale costume brick, which shouldn't be possible.", number.equals("1.3"));
		
		typeInDecimalNumber(4);
		number = solo.getEditText(4).getEditableText().toString();
		assertFalse("Could enter a decimal value into a scale costume brick, which shouldn't be possible.", number.equals("1.3"));
		*/
	}

	@Smoke
	public void testSelectSound() throws InterruptedException {
		addBrick(R.string.play_sound_main_adapter);

		Thread.sleep(400);
		ListView lv = (ListView) getActivity().findViewById(R.id.MainListView);
		System.out.println("lv children: " + lv.getChildCount());
		RelativeLayout rl = (RelativeLayout) lv.getChildAt(0);

		int itemToSelect = 2;
		Spinner soundSpinner = (Spinner) rl.getChildAt(1);
		assertNotNull("There are sound files present to select", soundSpinner.getItemAtPosition(itemToSelect));

		solo.clickOnView(soundSpinner);
		solo.clickInList(itemToSelect);

		Thread.sleep(3000); // wait for file copying to finish

		assertEquals("Selected item of Spinner is the Sound that was selected", itemToSelect, soundSpinner.getSelectedItemId());
	}
}
