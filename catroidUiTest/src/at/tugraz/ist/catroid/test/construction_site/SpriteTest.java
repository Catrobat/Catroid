/**
 * 
 */
package at.tugraz.ist.catroid.test.construction_site;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
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
	
	private void addBrickAndCheckExistence(int brickTextId) {
		solo.clickOnButton(getActivity().getString(R.string.toolbar));
		solo.clickOnText(getActivity().getString(brickTextId));
		
		boolean foundText = solo.searchText(getActivity().getString(brickTextId));
		assertTrue("Found brick in construction site", foundText);
	}
	
	private void typeInSignedNumber(int editTextId){
		solo.clickOnEditText(editTextId);
		// after that the dialog should appear and focus should be automatically on the EditText again
		
		solo.clearEditText(0);
		solo.enterText(0, "1.3");
		solo.clickOnButton(0);
	}

	@Smoke
	public void testAddPlaySoundBrick() {
		addBrickAndCheckExistence(R.string.play_sound_main_adapter);
	}
	
	@Smoke
	public void testAddWaitBrick() {
		addBrickAndCheckExistence(R.string.wait_main_adapter);
	}
	
	@Smoke
	public void testAddHideBrick() {
		addBrickAndCheckExistence(R.string.hide_main_adapter);
	}
	
	@Smoke
	public void testAddShowBrick() {
		addBrickAndCheckExistence(R.string.show_main_adapter);
	}
	
	@Smoke
	public void testAddGoToBrick() {
		addBrickAndCheckExistence(R.string.goto_main_adapter);
	}
	
	@Smoke
	public void testAddSetCostumeBrick() {
		addBrickAndCheckExistence(R.string.costume_main_adapter);
	}
	
	@Smoke
	public void testAddScaleCostumeBrick() {
		addBrickAndCheckExistence(R.string.scaleCustome);
	}
	
	@Smoke
	public void testNumberInputOfBricks(){
		solo.clickOnButton(getActivity().getString(R.string.toolbar));
		solo.clickOnText(getActivity().getString(R.string.wait_main_adapter));
		
		solo.clickOnButton(getActivity().getString(R.string.toolbar));
		solo.clickOnText(getActivity().getString(R.string.scaleCustome));
		
		solo.clickOnButton(getActivity().getString(R.string.toolbar));
		solo.clickOnText(getActivity().getString(R.string.goto_main_adapter));
		
		
		typeInSignedNumber(0);
		String number = solo.getEditText(0).getEditableText().toString();
		assertTrue("Found an signed value in wait brick.", number.contains("."));
		
		typeInSignedNumber(1);
		number = solo.getEditText(1).getEditableText().toString();
		assertTrue("Found an unsigned value in scale brick.", !number.contains("."));
		
		typeInSignedNumber(2);
		number = solo.getEditText(2).getEditableText().toString();
		assertTrue("Found an unsigned value in goto brick x.", !number.contains("."));
		
		typeInSignedNumber(3);
		number = solo.getEditText(3).getEditableText().toString();
		assertTrue("Found an unsigned value in goto brick y.", !number.contains("."));
		

		
	}
}
