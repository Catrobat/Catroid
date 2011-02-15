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
