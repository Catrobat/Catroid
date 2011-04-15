package at.tugraz.ist.catroid.uitest.ui.dialog;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.uitest.util.UiTestUtils;

import com.jayway.android.robotium.solo.Solo;

public class EditDialogTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;

	public EditDialogTest() {
		super("at.tugraz.ist.catroid.ui", ScriptActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		UiTestUtils.createTestProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	public void testEditIntegerDialog() throws InterruptedException {
		solo.clickOnButton(getActivity().getString(R.string.add_new_brick));
		solo.clickOnText(solo.getCurrentActivity().getString(R.string.goto_main_adapter));

		while(solo.scrollDown())
			;

		int editTextId = solo.getCurrentEditTexts().size() - 1;
		solo.clickOnEditText(editTextId);
		UiTestUtils.pause();
		solo.clearEditText(solo.getCurrentEditTexts().get(0));
		assertTrue("Toast with warning was not found", solo.searchText(getActivity().getString(R.string.notification_no_text_entered)));
	}
}
