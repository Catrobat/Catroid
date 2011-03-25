package at.tugraz.ist.catroid.uitest.construction_site;

import java.util.ArrayList;

import android.graphics.Rect;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

public class ScriptActivityTest extends ActivityInstrumentationTestCase2<ScriptActivity> {
	private Solo solo;
	private ArrayList<Brick> startBrickList;

	public ScriptActivityTest() {
		super(ScriptActivity.class);
		createTestProject("testProject");
	}

	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
        super.setUp();
        
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

	public void testSimpleDragNDrop() throws InterruptedException {
		ArrayList<ImageView> list = solo.getCurrentImageViews();
		System.out.println("count: "+list.size());
		ArrayList<Integer> yposlist = new ArrayList<Integer>();
		for (ImageView imageView : list) {
			Rect rect = new Rect();
			imageView.getGlobalVisibleRect(rect);
			yposlist.add(rect.top);
			System.out.println("y:"+rect.top);	
		}
		
		Thread.sleep(2000);
		solo.drag(30, 30, yposlist.get(1)+5, (yposlist.get(3)+yposlist.get(4))/2, 20);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();
		
		assertEquals(brickList.get(0), startBrickList.get(0));
		assertEquals(brickList.get(3), startBrickList.get(1));
		assertEquals(brickList.get(1), startBrickList.get(2));
		assertEquals(brickList.get(2), startBrickList.get(3));
		assertEquals(brickList.get(4), startBrickList.get(4));
		
		Thread.sleep(4000);
		
	}
	
	private void createTestProject(String projectName) {
		
		int xPosition = 457;
        int yPosition = 598;
        double scaleValue = 0.8;
        
        Project project = new Project(null, projectName);
        Sprite firstSprite = new Sprite("cat");
        
        Script testScript = new Script();
       
        startBrickList = new ArrayList<Brick>();
        startBrickList.add(new HideBrick(firstSprite));
        startBrickList.add(new ShowBrick(firstSprite));
        startBrickList.add(new ScaleCostumeBrick(firstSprite, scaleValue));
        startBrickList.add(new ComeToFrontBrick(firstSprite, null));
        startBrickList.add(new PlaceAtBrick(firstSprite, xPosition, yPosition));

        // adding Bricks: ----------------
        for (Brick brick : startBrickList) {
        	testScript.addBrick(brick);
		}
        // -------------------------------

        firstSprite.getScriptList().add(testScript);

        project.addSprite(firstSprite);
        
        ProjectManager.getInstance().setProject(project);
        ProjectManager.getInstance().setCurrentSprite(firstSprite);
        ProjectManager.getInstance().setCurrentScript(testScript);

		
	}

}
