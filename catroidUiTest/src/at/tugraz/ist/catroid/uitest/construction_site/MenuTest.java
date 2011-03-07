/**
 * 
 */
package at.tugraz.ist.catroid.uitest.construction_site;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;

import com.jayway.android.robotium.solo.Solo;

/**
 * @author Peter Treitler
 * 
 */
public class MenuTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity> {
	private Solo solo;

	public MenuTest() {
		super("at.tugraz.ist.catroid", ConstructionSiteActivity.class);
	}
	
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void tearDown() throws Exception {
		try {	
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}
	
	public void testMenuItemsPresent() {
		solo.sendKey(Solo.MENU);
		
		int menuItemCount = getMenuItemCount();
		int expectedMenuItemCount = 6;
		
		assertEquals("Number of displayed menu items is correct", expectedMenuItemCount, menuItemCount);
		
		String[] menuItems = {
			getActivity().getString(R.string.construction_site_play),
			getActivity().getString(R.string.reset),
			getActivity().getString(R.string.new_project),
			getActivity().getString(R.string.load),
			getActivity().getString(R.string.change_project_name_main),
			//getActivity().getString(R.string.about)
		};
		
		for(String menuItem: menuItems) {
			assertTrue("Menu item " + menuItem + " was not found", solo.searchText(menuItem));
		}
		
		solo.sendKey(Solo.MENU);
		// click the more button
		solo.pressMenuItem(6); 
		
		String[] moreMenueItems = {
			getActivity().getString(R.string.about),
			getActivity().getString(R.string.upload_project)	
		};
		for(String menuItem: moreMenueItems) {
			assertTrue("More Menu item " + menuItem + " was not found", solo.searchText(menuItem));
		}
		
	}

	private int getMenuItemCount() {
		int menuItemCount = 0;
		final String MENU_ITEM_CLASS = "class com.android.internal.view.menu.IconMenuItemView";
		
		ArrayList<View> views = solo.getViews();
		for(View v: views) {
			if(v.getClass().toString().equals(MENU_ITEM_CLASS))
				menuItemCount++;
		}
		return menuItemCount;
	}
	
	// TODO: Try to test correct behavior too
}
