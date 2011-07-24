package at.tugraz.ist.catroid.uitest.ui;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.CostumeActivity;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class CostumeActivityTest extends ActivityInstrumentationTestCase2<CostumeActivity> {
	private Solo solo;
	private String testingcostume = "testingcostume";
	private int SELECT_IMAGE = 0;
	private ImageView setCostumeImageView;

	public CostumeActivityTest() {
		super("at.tugraz.ist.catroid", CostumeActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_add_sprite);

		// Override OnClickListener to launch MockGalleryActivity
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getInstrumentation().getContext(),
						at.tugraz.ist.catroid.uitest.mockups.MockGalleryActivity.class);
				getActivity().startActivityForResult(intent, SELECT_IMAGE);
			}
		};

		setCostumeImageView = (ImageView) solo.getView(R.id.costume_image);

		assertNotNull("ImageView of the costume was not found", setCostumeImageView);
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		UiTestUtils.clearAllUtilTestProjects();
		super.tearDown();
	}

	public void testMainMenuButton() {
		UiTestUtils.clickOnImageButton(solo, R.id.btn_action_home);
		assertTrue("Clicking on main menu button did not cause main menu to be displayed",
				solo.getCurrentActivity() instanceof MainMenuActivity);
	}

	public void testCopyButton() {
		solo.clickOnButton("Copy");
		solo.sleep(100);
		//compare image in array 1 and array2
		//assertEquals("The copied image is not the same", solo.getCurrentActivity() instanceof MainMenuActivity);
	}

	//
	public void testDeleteButton() {
		solo.clickOnImageButton(R.id.delete_button);
		solo.sleep(100);
		//check size of array
		//assertFalse("The image is not deleted", solo.getCurrentActivity() instanceof MainMenuActivity);
	}

	public void testEditText() {
		solo.clickOnEditText(R.id.edit_costume);
		solo.sleep(100);
		solo.enterText(R.id.edit_costume, testingcostume);
		solo.sleep(100);
		solo.clickOnButton("Done");
		//check the editText
		//assertEquals("The name of the costume is not change", solo.getCurrentActivity() instanceof MainMenuActivity);
	}
}
