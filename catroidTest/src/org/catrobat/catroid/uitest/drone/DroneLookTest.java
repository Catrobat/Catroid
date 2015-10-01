package org.catrobat.catroid.uitest.drone;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.test.drone.DroneTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class DroneLookTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	public DroneLookTest() {
		super(MainMenuActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		TestUtils.deleteTestProjects();
		DroneTestUtils.createStandardDroneProject();
		SettingsActivity.enableARDroneBricks(getActivity(), true);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		SettingsActivity.enableARDroneBricks(getActivity(), false);
		solo.finishOpenedActivities();
		super.tearDown();
	}

	public void testAddLook() {
		ProjectManager.getInstance().initializeStandardProject(getActivity());

		solo.waitForActivity(ProgramMenuActivity.class);
		solo.clickOnText(solo.getString(R.string.programs));
		solo.waitForText(solo.getString(R.string.default_project_name));
		solo.clickOnText(solo.getString(R.string.default_project_name));

		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.waitForText(solo.getString(R.string.add_look_drone_video));
		solo.clickOnText(solo.getString(R.string.add_look_drone_video));
		solo.enterText(0, "Test 12345");
		solo.clickOnText(solo.getString(R.string.ok));

		solo.goBack();
		solo.goBack();
	}
}
