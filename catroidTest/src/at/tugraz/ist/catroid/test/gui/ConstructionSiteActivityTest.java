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

//package at.tugraz.ist.catroid.test.gui;
//
//import android.content.res.XmlResourceParser;
//import android.test.ActivityInstrumentationTestCase2;
//import at.tugraz.ist.catroid.ConstructionSiteActivity;
//import at.tugraz.ist.catroid.R;
//
//public class ConstructionSiteActivityTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity> {
//
//	private ConstructionSiteActivity mActivity;
//
//	public ConstructionSiteActivityTest() {
//		super("at.tugraz.ist.catroid", ConstructionSiteActivity.class);
//	}
//
//	protected void setUp() throws Exception {
//		super.setUp();
//		mActivity = getActivity();
//	}
//
//	/**
//	 * tests if the scroll bar in the tool box dialog is set to show up as long
//	 * as expected
//	 */
//	public void testToolBoxScrollBarDelay() {
//		XmlResourceParser parser = mActivity.getApplicationContext().getResources().getXml(R.layout.dialog_toolbox);
//		int scrollbarDelay = 0;
//
//		try {
//			boolean attributeFound = false;
//			while (!attributeFound && parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
//				String attributeValue = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "scrollbarDefaultDelayBeforeFade");
//				if (attributeValue != null) {
//					attributeFound = true;
//					scrollbarDelay = Integer.parseInt(attributeValue);
//				}
//				parser.next();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		parser.close();
//
//		assertEquals("Scrollbar delay is set", 2000, scrollbarDelay);
//	}
//
//}
