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
 * @author Peter Treitler
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
		
		boolean foundText = solo.searchText(getActivity().getString(brickTextId));
		assertTrue("Found brick in construction site", foundText);
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
	public void testAddHideBrick() {
		addBrick(R.string.hide_main_adapter);
	}
	
	@Smoke
	public void testAddShowBrick() {
		addBrick(R.string.show_main_adapter);
	}
	
	@Smoke
	public void testAddGoToBrick() {
		addBrick(R.string.goto_main_adapter);
	}
	
	@Smoke
	public void testAddSetCostumeBrick() {
		addBrick(R.string.costume_main_adapter);
	}
	
	@Smoke
	public void testAddScaleCostumeBrick() {
		addBrick(R.string.scaleCustome);
	}
}
