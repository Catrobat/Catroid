/**
 * 
 */
package at.tugraz.ist.catroid.test.construction_site;

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
		addBrick(R.string.set_background_main_adapter);
	}
	
	@Smoke
	public void testAddPlaySoundBrick() {
		addBrick(R.string.play_sound_main_adapter);
	}
	
	@Smoke
	public void testAddWaitBrick() {
		addBrick(R.string.wait_main_adapter);
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
}
