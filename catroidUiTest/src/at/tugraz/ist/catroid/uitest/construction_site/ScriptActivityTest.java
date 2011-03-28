package at.tugraz.ist.catroid.uitest.construction_site;

import java.util.ArrayList;

import android.graphics.Rect;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ListView;
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
	private ArrayList<Brick> brickListToCheck;
	
	public ScriptActivityTest() {
		super(ScriptActivity.class);
		
	}

	@Override
	public void setUp() throws Exception {
		createTestProject("testProject");
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
		ArrayList<Integer> yposlist = getListItemYPositions();
		Thread.sleep(2000);
		solo.drag(30, 30, yposlist.get(1), (yposlist.get(3)+yposlist.get(4))/2, 20);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();
		
		assertEquals(brickListToCheck.size(), brickList.size());
		assertEquals(brickListToCheck.get(0), brickList.get(0));
		assertEquals(brickListToCheck.get(1), brickList.get(3));
		assertEquals(brickListToCheck.get(2), brickList.get(1));
		assertEquals(brickListToCheck.get(3), brickList.get(2));
		assertEquals(brickListToCheck.get(4), brickList.get(4));
		
		Thread.sleep(4000);
		brickListToCheck = brickList;
	}
	
	public void testDeleteItem() throws InterruptedException {
		ArrayList<Integer> yposlist = getListItemYPositions();
		Thread.sleep(2000);
		solo.drag(30, 400, yposlist.get(1), (yposlist.get(3)+yposlist.get(4))/2, 20);
		Thread.sleep(2000);
		ArrayList<Brick> brickList = ProjectManager.getInstance().getCurrentScript().getBrickList();
		
		assertEquals(brickListToCheck.size()-1, brickList.size());
		assertEquals(brickListToCheck.get(0), brickList.get(0));
		assertEquals(brickListToCheck.get(2), brickList.get(1));
		assertEquals(brickListToCheck.get(3), brickList.get(2));
		assertEquals(brickListToCheck.get(4), brickList.get(3));
		
		Thread.sleep(4000);
		brickListToCheck = brickList;
	}
	
	private ArrayList<Integer> getListItemYPositions() {
		ArrayList<Integer> yposlist = new ArrayList<Integer>();		
		ListView listView = solo.getCurrentListViews().get(0);
		for(int i=0;i<listView.getChildCount();++i) {
			View currentViewInList = listView.getChildAt(i);
			
			Rect rect = new Rect();
			currentViewInList.getGlobalVisibleRect(rect);
			yposlist.add(rect.top+rect.height()/2);
			System.out.println("y:"+rect.top);	
		}
		
		return yposlist;
	}
	
	private void createTestProject(String projectName) {
		
		int xPosition = 457;
        int yPosition = 598;
        double scaleValue = 0.8;
        
        Project project = new Project(null, projectName);
        Sprite firstSprite = new Sprite("cat");
        
        Script testScript = new Script();
       
        brickListToCheck = new ArrayList<Brick>();
        brickListToCheck.add(new HideBrick(firstSprite));
        brickListToCheck.add(new ShowBrick(firstSprite));
        brickListToCheck.add(new ScaleCostumeBrick(firstSprite, scaleValue));
        brickListToCheck.add(new ComeToFrontBrick(firstSprite, null));
        brickListToCheck.add(new PlaceAtBrick(firstSprite, xPosition, yPosition));

        // adding Bricks: ----------------
        for (Brick brick : brickListToCheck) {
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
