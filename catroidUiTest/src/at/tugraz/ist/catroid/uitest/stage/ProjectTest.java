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

import java.io.File;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.ListView;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Thomas Holzmann
 *
 */
public class ProjectTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity>{
	private Solo solo;
	
	public ProjectTest() {
		super("at.tugraz.ist.catroid",
				ConstructionSiteActivity.class);
	}
	
	@Override
    public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
    public void tearDown() throws Exception {
		
		try {	
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		getActivity().finish();
		super.tearDown();
		
        File projectRoot = new File(Consts.DEFAULT_ROOT + "/testProject/");
		UtilFile.deleteDirectory(projectRoot);
	}
	
	@Smoke
	public void testCreateNewProject(){
		solo.clickOnMenuItem(getActivity().getString(R.string.new_project));
		solo.clearEditText(0);
		solo.enterText(0, "testProject");
		solo.clickOnButton(0);
		// now there should be an empty new project
		
		solo.clickOnButton(getActivity().getString(R.string.stage));
		ListView spritesList = solo.getCurrentListViews().get(0);
		assertEquals("After creating a new project there is only the stage available.", 1, spritesList.getChildCount());
		solo.goBack();
		
//		ListView brickList = (ListView) getActivity().findViewById(R.id.MainListView);
//		assertEquals("After creating a new project there is no brick in construction site.", 0, brickList.getChildCount());
		
		
	}
	
}
