package at.tugraz.ist.catroid.uitest.construction_site.script_adapter;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.IfTouchedBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Daniel Burtscher
 *
 */
public class IfTouchedTest extends ActivityInstrumentationTestCase2<ScriptActivity>{
	private Solo solo;
	private Project project;

	public IfTouchedTest() {
		super("at.tugraz.ist.catroid",
				ScriptActivity.class);
	}
	
	@Override
    public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
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
	public void testIfTouchedBrick() throws Throwable {
		
		assertEquals("Incorrect number of bricks.", 1, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, getActivity().getAdapter().getCount());
		
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScriptList().get(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		
		assertEquals("Wrong Brick instance.", projectBrickList.get(0), getActivity().getAdapter().getItem(0));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.touched_main_adapter)));
		
	}
	
	private void createProject() {
		project = new Project(null, "testProject");
        Sprite sprite = new Sprite("cat");
        Script script = new Script(); 
        script.addBrick(new IfTouchedBrick(sprite, script));

        sprite.getScriptList().add(script);
        project.addSprite(sprite);
        
        ProjectManager.getInstance().setProject(project);
        ProjectManager.getInstance().setCurrentSprite(sprite);
        ProjectManager.getInstance().setCurrentScript(script);
        
	}
	
//	@Smoke
//	public void testPlaceAtBrick() throws Throwable {
//		final Project testProject = new Project(getInstrumentation().getContext(), "theTest");
//		Sprite stageSprite = testProject.getSpriteList().get(0);
//		
//		Script script = new Script();
//		script.addBrick(new HideBrick(stageSprite));
//		PlaceAtBrick placeAtBrick = new PlaceAtBrick(stageSprite, 105, 206);
//		script.addBrick(placeAtBrick);
//        script.addBrick(new PlaySoundBrick(stageSprite, "sound.mp3"));
//		script.addBrick(new ScaleCostumeBrick(stageSprite, 80));
//		
//		stageSprite.getScriptList().add(script);
//		
//		runTestOnUiThread(new Runnable() {
//			public void run() {
//				getActivity().setProject(testProject);
//			}
//		});
//		
//		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.goto_main_adapter)));
//		
//		int xPosition = 987;
//		int yPosition = 654;
//		
//		solo.clickOnEditText(0);
//		solo.clearEditText(0);
//		solo.enterText(0, xPosition + "");
//		solo.clickOnButton(0);
//		
//		assertEquals("Text not updated", xPosition + "", solo.getEditText(0).getText().toString());
//		assertEquals("Value in Brick is not updated", xPosition, placeAtBrick.getXPosition());
//		
//		solo.clickOnEditText(1);
//		solo.clearEditText(0);
//		solo.enterText(0, yPosition + "");
//		solo.clickOnButton(0);
//		
//		assertEquals("Text not updated", yPosition + "", solo.getEditText(1).getText().toString());
//		assertEquals("Value in Brick is not updated", yPosition, placeAtBrick.getYPosition());
//	}
//	
//	@Smoke
//	public void testScaleCostumeBrick() throws Throwable {
//		final Project testProject = new Project(getInstrumentation().getContext(), "theTest");
//		Sprite stageSprite = testProject.getSpriteList().get(0);
//		Script script = new Script();
//		ScaleCostumeBrick brick = new ScaleCostumeBrick(stageSprite, 20);
//		script.addBrick(brick);
//		
//		stageSprite.getScriptList().add(script);
//		
//		runTestOnUiThread(new Runnable() {
//			public void run() {
//				getActivity().setProject(testProject);
//			}
//		});
//		
//		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
//		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.scaleCustome)));
//		
//		int newScale = 25;
//		
//		solo.clickOnEditText(0);
//		solo.clearEditText(0);
//		solo.enterText(0, newScale + "");
//		solo.clickOnButton(0);
//		
//		Thread.sleep(1000);
//		assertEquals("Wrong text in field", newScale, brick.getScale());
//		assertEquals("Text not updated", newScale + "", solo.getEditText(0).getText().toString());
//	}
//	
//	@Smoke
//	public void testShowBrick() throws Throwable {
//		final Project testProject = new Project(getInstrumentation().getContext(), "theTest");
//		Sprite stageSprite = testProject.getSpriteList().get(0);
//		Script script = new Script();
//		script.addBrick(new ShowBrick(stageSprite));
//		
//		stageSprite.getScriptList().add(script);
//		
//		runTestOnUiThread(new Runnable() {
//			public void run() {
//				getActivity().setProject(testProject);
//			}
//		});
//		
//		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
//		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.show_main_adapter)));
//	}
//	
//	@Smoke
//	public void testWaitBrick() throws Throwable {
//		final Project testProject = new Project(getInstrumentation().getContext(), "theTest");
//		Sprite stageSprite = testProject.getSpriteList().get(0);
//		Script script   = new Script();
//        WaitBrick brick = new WaitBrick(stageSprite, 1000);
//		script.addBrick(brick);
//		
//		stageSprite.getScriptList().add(script);
//		
//		runTestOnUiThread(new Runnable() {
//			public void run() {
//				getActivity().setProject(testProject);
//			}
//		});
//		
//		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
//		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.wait_main_adapter)));
//		
//		long waitTime = 729;
//		
//		solo.clickOnEditText(0);
//		solo.clearEditText(0);
//		solo.enterText(0, waitTime + "");
//		solo.clickOnButton(0);
//		
//		Thread.sleep(1000);
//		assertEquals("Wrong text in field", waitTime, brick.getWaitTime());
//		assertEquals("Text not updated", waitTime + "", solo.getEditText(0).getText().toString());
//	}
//	
//	@Smoke
//	public void testPlaySoundBrick() throws Throwable {
//		String title = "myTitle";
//		String path = "path/to/sound/";
//		ArrayList<SoundInfo> soundlist = new ArrayList<SoundInfo>();
//		SoundInfo soundInfo = new SoundInfo();
//		soundInfo.setId(5);
//		soundInfo.setTitle("something");
//		soundInfo.setPath("path/path/1/");
//		soundlist.add(soundInfo);
//		soundInfo = new SoundInfo();
//		soundInfo.setId(6);
//		soundInfo.setTitle(title);
//		soundInfo.setPath(path);
//		soundlist.add(soundInfo);
//		soundInfo = new SoundInfo();
//		soundInfo.setId(7);
//		String selectedTitle = "selectedTitle";
//		soundInfo.setTitle(selectedTitle);
//		soundInfo.setPath(path);
//		soundlist.add(soundInfo);
//		StorageHandler.getInstance().setSoundContent(soundlist);
//		
//		final Project testProject = new Project(getInstrumentation().getContext(), "theTest");
//		Sprite stageSprite = testProject.getSpriteList().get(0);
//		Script script = new Script();
//		// test the selected item
//        PlaySoundBrick soundBrick = new PlaySoundBrick(stageSprite, soundInfo.getTitleWithPath());
//		script.addBrick(soundBrick);
//		stageSprite.getScriptList().add(script);
//		
//		runTestOnUiThread(new Runnable() {
//			public void run() {
//				getActivity().setProject(testProject);
//			}
//		});
//		
//		Thread.sleep(500);
//		assertTrue("Wrong title selected", solo.searchText(selectedTitle));
//		
//		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
//		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.play_sound_main_adapter)));
//		
//		solo.clickOnButton(selectedTitle);
//		solo.clickInList(2);
//		Thread.sleep(500);
//		assertEquals("Wrong path", path+title, soundBrick.getPathToSoundFile());
//		assertTrue("Wrong title selected", solo.searchText(title));
//		
//	}
//	
//	@Smoke
//	public void testPlaySoundBrickNoSounds() throws Throwable {
//
//		ArrayList<SoundInfo> soundlist = new ArrayList<SoundInfo>();		
//		StorageHandler.getInstance().setSoundContent(soundlist);
//		
//		final Project testProject = new Project(getInstrumentation().getContext(), "theTest");
//		Sprite stageSprite = testProject.getSpriteList().get(0);
//		Script script = new Script();
//		String selectedTitle = "mysound";
//        PlaySoundBrick soundBrick = new PlaySoundBrick(stageSprite, "test/" + selectedTitle);
//		script.addBrick(soundBrick);
//		stageSprite.getScriptList().add(script);
//		
//		runTestOnUiThread(new Runnable() {
//			public void run() {
//				getActivity().setProject(testProject);
//			}
//		});
//		
//		Thread.sleep(500);
//		assertTrue("Wrong title selected", solo.searchText(selectedTitle));
//		
//		assertEquals("Incorrect number of bricks", 1, getActivity().getProgrammAdapter().getCount());
//		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.play_sound_main_adapter)));
//		
//		solo.clickOnButton(selectedTitle);
//		Thread.sleep(500);
//		assertEquals("wrong list size", 0, solo.getCurrentListViews().get(0).getCount());
//		solo.goBack();
//		
//		assertTrue("Wrong title selected", solo.searchText(selectedTitle));
//		assertEquals("Wrong path", "test/"+selectedTitle, soundBrick.getPathToSoundFile());
//		
//		
//	}
}