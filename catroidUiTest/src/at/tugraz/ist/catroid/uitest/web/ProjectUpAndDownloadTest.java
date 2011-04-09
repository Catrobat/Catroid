///**
// *  Catroid: An on-device graphical programming language for Android devices
// *  Copyright (C) 2010  Catroid development team 
// *  (<http://code.google.com/p/catroid/wiki/Credits>)
// *
// *  This program is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License as published by
// *  the Free Software Foundation, either version 3 of the License, or
// *  (at your option) any later version.
// *
// *  This program is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU General Public License for more details.
// *
// *  You should have received a copy of the GNU General Public License
// *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package at.tugraz.ist.catroid.uitest.web;
//
//import android.test.ActivityInstrumentationTestCase2;
//import android.test.UiThreadTest;
//import at.tugraz.ist.catroid.ConstructionSiteActivity;
//import at.tugraz.ist.catroid.R;
//import at.tugraz.ist.catroid.constructionSite.tasks.ProjectUploadTask;
//
//import com.jayway.android.robotium.solo.Solo;
//
//public class ProjectUpAndDownloadTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity>{
//	private Solo solo;
//	
//	public ProjectUpAndDownloadTest() {
//		super("at.tugraz.ist.catroid.test.construction_site",
//				ConstructionSiteActivity.class);
//	}
//	
//	@UiThreadTest
//	public void setUp() throws Exception {
//		solo = new Solo(getInstrumentation(), getActivity());
//	}
//	
//	public void tearDown() throws Exception {
//		try {	
//			solo.finalize();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		getActivity().finish();
//		super.tearDown();
//	}
//	
//	public void testUploadProject() throws Throwable {
//		runTestOnUiThread(new Runnable() {		
//			public void run() {
//				ProjectUploadTask.mUseTestUrl = true;
//			}
//		});
//		
//		solo.clickOnMenuItem(getActivity().getString(R.string.upload_project), true); 
//		solo.waitForDialogToClose(5000);
//		
//		assertTrue("Upload failed. Internet connection?", 
//					solo.searchText(getActivity().getString(R.string.success_project_upload)));  
//	
//		solo.clickOnButton(0);  		
//	}
//	
// }
