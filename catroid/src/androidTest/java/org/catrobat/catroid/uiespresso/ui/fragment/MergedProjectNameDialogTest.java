package org.catrobat.catroid.uiespresso.ui.fragment;

import android.support.test.runner.AndroidJUnit4;

import android.support.test.InstrumentationRegistry;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;

@RunWith(AndroidJUnit4.class)
public class MergedProjectNameDialogTest {

	@Rule
	public BaseActivityInstrumentationRule<ProjectListActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectListActivity.class, true, false);

	private String firstProjectName = "firstProjectName";
	private String secondProjectName = "secondProjectName";

	@Before
	public void setUp() throws Exception {
		createProject(firstProjectName);
		createProject(secondProjectName);

		baseActivityTestRule.launchActivity(null);
	}


	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void MergedDialogTitleTest() {
		openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

		onView(withText(R.string.merge))
				.perform(click());

		onRecyclerView().atPosition(0)
				.performCheckItem();

		onRecyclerView().atPosition(1)
				.performCheckItem();
		onView(withId(R.id.confirm)).perform(click());

		onView(withText(R.string.merge_project)).inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new SingleSprite("firstSprite");

		Script script = new StartScript();
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		script.addBrick(new SetXBrick(new Formula(BrickValues.X_POSITION)));
		sprite.addScript(script);

		project.getDefaultScene().addSprite(sprite);

		XstreamSerializer.getInstance().saveProject(project);
	}
}
