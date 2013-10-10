package org.catrobat.catroid.test.cucumber;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;

public class BeforeAfterSteps extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo mSolo;

	public BeforeAfterSteps() {
		super(MainMenuActivity.class);
	}

	@Before
	public void before() {
//		Log.d(CucumberInstrumentation.TAG, "before step");
		Context context = getInstrumentation().getTargetContext();
		mSolo = new Solo(getInstrumentation(), getActivity());
		String defaultBackgroundName = context.getString(R.string.background);
		String defaultProjectName = context.getString(R.string.default_project_name);
		String defaultSpriteName = context.getString(R.string.default_project_sprites_mole_name);
		Cucumber.put(Cucumber.KEY_DEFAULT_BACKGROUND_NAME, defaultBackgroundName);
		Cucumber.put(Cucumber.KEY_DEFAULT_PROJECT_NAME, defaultProjectName);
		Cucumber.put(Cucumber.KEY_DEFAULT_SPRITE_NAME, defaultSpriteName);
		Cucumber.put(Cucumber.KEY_SOLO, mSolo);
	}

	@After
	public void after() {
//		Log.d(CucumberInstrumentation.TAG, "after step");
		mSolo.finishOpenedActivities();
		ProjectManager.getInstance().deleteCurrentProject();
	}
}
