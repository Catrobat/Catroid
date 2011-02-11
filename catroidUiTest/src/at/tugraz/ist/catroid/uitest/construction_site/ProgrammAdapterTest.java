package at.tugraz.ist.catroid.uitest.construction_site;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Log;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.gui.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.gui.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.brick.gui.HideBrick;
import at.tugraz.ist.catroid.content.brick.gui.IfTouchedBrick;
import at.tugraz.ist.catroid.content.brick.gui.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.gui.PlaySoundBrick;
import at.tugraz.ist.catroid.content.brick.gui.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.gui.ShowBrick;
import at.tugraz.ist.catroid.content.brick.gui.WaitBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Daniel Burtscher
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
	public void testComeToFrontBrick() throws Throwable {
		final Project testProject = new Project("theTest");
		Sprite stageSprite = testProject.getSpriteList().get(0);
		Script script = new Script();
		script.addBrick(new ComeToFrontBrick(stageSprite, testProject));
		
		stageSprite.getScriptList().add(script);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				getActivity().setProject(testProject);
			}
		});
		
		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.come_to_front_main_adapter)));
	}
	
	@Smoke
	public void testGoNStepsBackBrick() throws Throwable {
		
		int steps = 17;
		
		final Project testProject = new Project("theTest");
		Sprite stageSprite        = testProject.getSpriteList().get(0);
		Script script             = new Script();
		GoNStepsBackBrick brick   = new GoNStepsBackBrick(stageSprite, 0);
		
		script.addBrick(brick);
		
		stageSprite.getScriptList().add(script);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				getActivity().setProject(testProject);
			}
		});
		
		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
		
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, steps + "");
		solo.clickOnButton(0);
		
		Thread.sleep(1000);
		assertEquals("Wrong text in field", steps, brick.getSteps());
	}
	
	@Smoke
	public void testHideBrick() throws Throwable {
		final Project testProject = new Project("theTest");
		Sprite stageSprite = testProject.getSpriteList().get(0);
		Script script = new Script();
		script.addBrick(new HideBrick(stageSprite));
		
		stageSprite.getScriptList().add(script);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				getActivity().setProject(testProject);
			}
		});
		
		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
	}
	
	@Smoke
	public void testIfTouchedBrick() throws Throwable {
		final Project testProject = new Project("theTest");
		Sprite stageSprite = testProject.getSpriteList().get(0);
		Script script = new Script();
		script.addBrick(new IfTouchedBrick(stageSprite, script));
		
		stageSprite.getScriptList().add(script);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				getActivity().setProject(testProject);
			}
		});
		
		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
	}
	
	@Smoke
	public void testPlaceAtBrick() throws Throwable {
		final Project testProject = new Project("theTest");
		Sprite stageSprite = testProject.getSpriteList().get(0);
		
		Script script = new Script();
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
		
		int xPosition = 987;
		int yPosition = 654;
		
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, xPosition + "");
		solo.clickOnButton(0);
		
		assertEquals("Text not updated", xPosition + "", solo.getEditText(0).getText().toString());
		assertEquals("Value in Brick is not updated", xPosition, placeAtBrick.getXPosition());
		
		solo.clickOnEditText(1);
		solo.clearEditText(0);
		solo.enterText(0, yPosition + "");
		solo.clickOnButton(0);
		
		assertEquals("Text not updated", yPosition + "", solo.getEditText(1).getText().toString());
		assertEquals("Value in Brick is not updated", yPosition, placeAtBrick.getYPosition());
	}
	
	@Smoke
	public void testPlaySoundBrick() throws Throwable {
		final Project testProject = new Project("theTest");
		Sprite stageSprite = testProject.getSpriteList().get(0);
		Script script = new Script();
		script.addBrick(new PlaySoundBrick("invalid path"));
		
		stageSprite.getScriptList().add(script);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				getActivity().setProject(testProject);
			}
		});
		
		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
	}
	
	@Smoke
	public void testScaleCostumeBrick() throws Throwable {
		final Project testProject = new Project("theTest");
		Sprite stageSprite = testProject.getSpriteList().get(0);
		Script script = new Script();
		script.addBrick(new ScaleCostumeBrick(stageSprite, 10.0));
		
		stageSprite.getScriptList().add(script);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				getActivity().setProject(testProject);
			}
		});
		
		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
	}
	
	@Smoke
	public void testShowBrick() throws Throwable {
		final Project testProject = new Project("theTest");
		Sprite stageSprite = testProject.getSpriteList().get(0);
		Script script = new Script();
		script.addBrick(new ShowBrick(stageSprite));
		
		stageSprite.getScriptList().add(script);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				getActivity().setProject(testProject);
			}
		});
		
		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
	}
	
	@Smoke
	public void testWaitBrick() throws Throwable {
		final Project testProject = new Project("theTest");
		Sprite stageSprite = testProject.getSpriteList().get(0);
		Script script = new Script();
		script.addBrick(new WaitBrick(1000));
		
		stageSprite.getScriptList().add(script);
		
		runTestOnUiThread(new Runnable() {
			public void run() {
				getActivity().setProject(testProject);
			}
		});
		
		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
	}
}
