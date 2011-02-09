package at.tugraz.ist.catroid.uitest.construction_site;

import java.io.File;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.widget.EditText;
import android.widget.ListView;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.IfTouchedBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.PlaySoundBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.brick.WaitBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Thomas Holzmann
 *
 */
public class ProgrammAdapterTest extends ActivityInstrumentationTestCase2<ConstructionSiteActivity>{
	private Solo solo;
	
	public ProgrammAdapterTest() {
		super("at.tugraz.ist.catroid",
				ConstructionSiteActivity.class);
	}
	
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	public void tearDown() throws Exception {
		
		try {	
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		getActivity().finish();
		super.tearDown();

	}
	
	@Smoke
	public void testPlaceAtBrick() throws Throwable {
		System.out.println("start");
		final Project testProject = new Project("theTest");
		Sprite stageSprite = testProject.getSpriteList().get(0);
		Script script = new Script();
		System.out.println("middle");
		script.addBrick(new HideBrick(stageSprite));
		PlaceAtBrick placeAtBrick = new PlaceAtBrick(stageSprite, 105, 206);
		script.addBrick(placeAtBrick);
		script.addBrick(new PlaySoundBrick("sound.mp3"));
		script.addBrick(new ScaleCostumeBrick(stageSprite, 1.2));
		
		stageSprite.getScriptList().add(script);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				getActivity().setProject(testProject);
			}
		});
		
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, "987");
		solo.clickOnButton(0);
		
		assertEquals("Text not updated", "987", solo.getEditText(0).getText().toString());
		assertEquals("Value in Brick is not updated", 987, placeAtBrick.getxPosition().intValue());
		
	}
	
}
