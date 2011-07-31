//package at.tugraz.ist.catroid.uitest.ui;
//
//import java.io.File;
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.test.ActivityInstrumentationTestCase2;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import at.tugraz.ist.catroid.R;
//import at.tugraz.ist.catroid.test.utils.TestUtils;
//import at.tugraz.ist.catroid.ui.CostumeActivity;
//import at.tugraz.ist.catroid.ui.MainMenuActivity;
//import at.tugraz.ist.catroid.uitest.util.UiTestUtils;
//import at.tugraz.ist.catroid.utils.UtilFile;
//
//import com.jayway.android.robotium.solo.Solo;
//
//public class CostumeActivityTest extends ActivityInstrumentationTestCase2<CostumeActivity> {
//	private Solo solo;
//	private String testingcostume = "testingcostume";
//	private int SELECT_IMAGE = 0;
//	private ImageButton addCostumeButton;
//	private File imageFile;
//
//	public CostumeActivityTest() {
//		super("at.tugraz.ist.catroid", CostumeActivity.class);
//	}
//
//	@Override
//	public void setUp() throws Exception {
//		super.setUp();
//		UiTestUtils.createTestProject();
//		solo = new Solo(getInstrumentation(), getActivity());
//
//		addCostumeButton = (ImageButton) solo.getCurrentActivity().findViewById(R.id.btn_action_add_sprite);
//
//		final int RESOURCE_LOCATION = R.drawable.catroid_sunglasses;
//		imageFile = UtilFile.saveFileToProject(UiTestUtils.DEFAULT_TEST_PROJECT_NAME, "catroid_sunglasses.png",
//				RESOURCE_LOCATION, getActivity(), UiTestUtils.TYPE_IMAGE_FILE);
//
//		final File imageFileReference = imageFile;
//
//		// Override OnClickListener to launch MockGalleryActivity
//		OnClickListener listener = new OnClickListener() {
//			public void onClick(View v) {
//				final Intent intent = new Intent("android.intent.action.MAIN");
//				intent.setComponent(new ComponentName("at.tugraz.ist.catroid.uitest",
//						"at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity"));
//				intent.putExtra("filePath", imageFileReference.getAbsolutePath());
//				solo.getActivityMonitor().getLastActivity().startActivityForResult(intent, SELECT_IMAGE);
//			}
//		};
//
//		addCostumeButton.setOnClickListener(listener);
//	}
//
//	@Override
//	public void tearDown() throws Exception {
//		try {
//			solo.finalize();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		getActivity().finish();
//		UiTestUtils.clearAllUtilTestProjects();
//		super.tearDown();
//	}
//
//	public void testMainMenuButton() {
//		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_home);
//		assertTrue("Clicking on main menu button did not cause main menu to be displayed",
//				solo.getCurrentActivity() instanceof MainMenuActivity);
//	}
//
//	public void testAddCostume() {
//		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_add_sprite);
//
//		ImageView costumeView = (ImageView) solo.getCurrentActivity().findViewById(R.id.costume_image);
//		Bitmap bitmapToTest = costumeView.getDrawingCache();
//		assertNotNull("Bitmap of costume ImageView is null", bitmapToTest);
//		solo.sleep(5000);
//
//		Bitmap originalBitmap = BitmapFactory.decodeResource(getInstrumentation().getContext().getResources(),
//				at.tugraz.ist.catroid.uitest.R.drawable.catroid_sunglasses);
//		assertNotNull("Decoding the costume resource failed", originalBitmap);
//
//		solo.sleep(1000);
//
//	}
//
//	public void testCopyButton() {
//		solo.clickOnButton("Copy");
//		solo.sleep(100);
//		//compare image in array 1 and array2
//		//assertEquals("The copied image is not the same", solo.getCurrentActivity() instanceof MainMenuActivity);
//	}
//
//	//
//	public void testDeleteButton() {
//		solo.clickOnImageButton(R.id.delete_button);
//		solo.sleep(100);
//		//check size of array
//		//assertFalse("The image is not deleted", solo.getCurrentActivity() instanceof MainMenuActivity);
//	}
//
//	public void testEditText() {
//		solo.clickOnEditText(R.id.edit_costume);
//		solo.sleep(100);
//		solo.enterText(R.id.edit_costume, testingcostume);
//		solo.sleep(100);
//		//check the editText
//		//assertEquals("The name of the costume is not change", solo.getCurrentActivity() instanceof MainMenuActivity);
//	}
//}
