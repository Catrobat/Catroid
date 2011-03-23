package at.tugraz.ist.catroid.uitest.construction_site;

import java.io.File;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class ScriptActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;

    private final String projectNameOne = "Ulumulu";
    private final String projectNameTwo = "Ulumulu2";
    //    private final String spriteNameOne = "Zuul";
    //    private final String spriteNameTwo = "Zuuul";
    private final String scriptNameOne = "derUlukai";
    //    private final String scriptNameTwo = "derUlukai2";

	public ScriptActivityTest() {
		super("at.tugraz.ist.catroid.ui", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
        File directory = new File("/sdcard/catroid/" + projectNameOne);
        UtilFile.deleteDirectory(directory);
        directory = new File("/sdcard/catroid/" + projectNameTwo);
        UtilFile.deleteDirectory(directory);
        super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
        File directory = new File("/sdcard/catroid/" + projectNameOne);
        UtilFile.deleteDirectory(directory);
        assertFalse(projectNameOne + " was not deleted!", directory.exists());
        directory = new File("/sdcard/catroid/" + projectNameTwo);
        UtilFile.deleteDirectory(directory);
        assertFalse(projectNameTwo + " was not deleted!", directory.exists());
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();

		super.tearDown();
	}

	public void testMainMenuButton() throws InterruptedException {
        solo.clickOnButton(getActivity().getString(R.string.resume));
        solo.clickOnText(getActivity().getString(R.string.stage));

        solo.clickOnButton(getActivity().getString(R.string.add_new_script));
        solo.clickOnEditText(0);
        solo.enterText(0, scriptNameOne);
        solo.goBack();
        solo.clickOnButton(getActivity().getString(R.string.new_script_dialog_button));

        solo.clickOnText(scriptNameOne);
		solo.clickOnButton(getActivity().getString(R.string.main_menu));
		solo.clickOnButton(getActivity().getString(R.string.resume)); //if this is possible it worked! (will throw AssertionFailedError if not working
	}

    public void testCreateNewBrickButton() throws InterruptedException {
        solo.clickOnButton(getActivity().getString(R.string.resume));
        solo.clickOnText(getActivity().getString(R.string.stage));

        solo.clickOnButton(getActivity().getString(R.string.add_new_script));
        solo.clickOnEditText(0);
        solo.enterText(0, scriptNameOne);
        solo.goBack();
        solo.clickOnButton(getActivity().getString(R.string.new_script_dialog_button));

        solo.clickOnText(scriptNameOne);
        solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
        solo.clickOnText(getActivity().getString(R.string.wait_main_adapter));

        Thread.sleep(100);
        assertTrue("in waitbrick is not in List", solo.searchText(getActivity().getString(R.string.wait_main_adapter)));
        assertEquals("not one brick in listview", 1, solo.getCurrentListViews().get(0).getCount());
    }
}
