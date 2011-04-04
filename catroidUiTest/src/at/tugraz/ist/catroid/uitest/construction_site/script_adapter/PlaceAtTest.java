package at.tugraz.ist.catroid.uitest.construction_site.script_adapter;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.PlaySoundBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
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
public class PlaceAtTest extends ActivityInstrumentationTestCase2<ScriptActivity>{
	private Solo solo;
	private Project project;
	private PlaceAtBrick placeAtBrick;

	public PlaceAtTest() {
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
	public void testPlaceAtBrick() throws Throwable {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();
		assertEquals("Incorrect number of bricks.", 5, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 4, childrenCount);
		
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScriptList().get(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		
		assertEquals("Wrong Brick instance.", projectBrickList.get(0), getActivity().getAdapter().getChild(groupCount-1, 0));
		assertEquals("Wrong Brick instance.", projectBrickList.get(1), getActivity().getAdapter().getChild(groupCount-1, 1));
		assertEquals("Wrong Brick instance.", projectBrickList.get(2), getActivity().getAdapter().getChild(groupCount-1, 2));
		assertEquals("Wrong Brick instance.", projectBrickList.get(3), getActivity().getAdapter().getChild(groupCount-1, 3));
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.goto_main_adapter)));
	
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
	
	private void createProject() {
		project = new Project(null, "testProject");
        Sprite sprite = new Sprite("cat");
        Script script = new Script(); 
        script.addBrick(new HideBrick(sprite));
        placeAtBrick = new PlaceAtBrick(sprite, 105, 206);
        script.addBrick(placeAtBrick);
        script.addBrick(new PlaySoundBrick(sprite, "sound.mp3"));
        script.addBrick(new ScaleCostumeBrick(sprite, 80));

        sprite.getScriptList().add(script);
        project.addSprite(sprite);
        
        ProjectManager.getInstance().setProject(project);
        ProjectManager.getInstance().setCurrentSprite(sprite);
        ProjectManager.getInstance().setCurrentScript(script);
        
	}
	
}