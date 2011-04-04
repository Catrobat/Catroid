package at.tugraz.ist.catroid.uitest.construction_site.script_adapter;

import java.io.IOException;
import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.PlaySoundBrick;
import at.tugraz.ist.catroid.content.entities.SoundInfo;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

/**
 * 
 * @author Daniel Burtscher
 *
 */
public class SoundBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity>{
	private Solo solo;
	private Project project;
	private PlaySoundBrick soundBrick;
	private String selectedTitle;
	private String path;
	private String title;

	public SoundBrickTest() {
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
	public void testPlaySoundBrick() throws Throwable {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();
		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);
		
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScriptList().get(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		
		assertEquals("Wrong Brick instance.", projectBrickList.get(0), getActivity().getAdapter().getChild(groupCount-1, 0));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.play_sound_main_adapter)));
		
		assertTrue("Wrong title selected", solo.searchText(selectedTitle));
		assertTrue("Wrong title selected", solo.searchText(selectedTitle));
		
		solo.clickOnButton(selectedTitle);
		solo.clickInList(2);
		Thread.sleep(500);
		assertTrue("Wrong title selected", solo.searchText(title));
		
	}
	
	private void createProject() throws IOException {
		title = "myTitle";
		path = "path/to/sound/";
		ArrayList<SoundInfo> soundlist = new ArrayList<SoundInfo>();
		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setId(5);
		soundInfo.setTitle("something");
		soundInfo.setPath("path/path/1/");
		soundlist.add(soundInfo);
		soundInfo = new SoundInfo();
		soundInfo.setId(6);
		soundInfo.setTitle(title);
		soundInfo.setPath(path);
		soundlist.add(soundInfo);
		soundInfo = new SoundInfo();
		soundInfo.setId(7);
		selectedTitle = "selectedTitle";
		soundInfo.setTitle(selectedTitle);
		soundInfo.setPath(path);
		soundlist.add(soundInfo);
		StorageHandler.getInstance().setSoundContent(soundlist);
		
		project = new Project(null, "testProject");
        Sprite sprite = new Sprite("cat");
        Script script = new Script(); 
        soundBrick = new PlaySoundBrick(sprite, soundInfo.getTitleWithPath());
        script.addBrick(soundBrick);
        
        sprite.getScriptList().add(script);
        project.addSprite(sprite);
        
        ProjectManager.getInstance().setProject(project);
        ProjectManager.getInstance().setCurrentSprite(sprite);
        ProjectManager.getInstance().setCurrentScript(script);
	}
	
}