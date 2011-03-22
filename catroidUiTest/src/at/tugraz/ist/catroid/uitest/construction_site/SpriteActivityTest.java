package at.tugraz.ist.catroid.uitest.construction_site;

import java.io.File;
import java.io.IOException;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class SpriteActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private Solo solo;


	public SpriteActivityTest() {
		super("at.tugraz.ist.catroid.ui", MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
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
		
		File directory = new File("/sdcard/catroid/testProject");
		UtilFile.deleteDirectory(directory);
		
		super.tearDown();
	}
	
	
	public void testAddNewScript() throws NameNotFoundException, IOException, InterruptedException {
		File directory = new File("/sdcard/catroid/testProject");
		UtilFile.deleteDirectory(directory);
		
		createTestProject("testProject");
		Thread.sleep(1000);
		
		solo.clickOnButton(getActivity().getString(R.string.load_project));
		solo.clickOnText("testProject");
		
		solo.clickInList(2);
		solo.clickOnButton(1);
		
		solo.enterText(0, "dummyScript");
		solo.clickOnButton(solo.getCurrentActivity().getString(R.string.new_script_dialog_button));
		
		ListView scriptList = (ListView) solo.getCurrentActivity().findViewById(R.id.scriptListView);
		Script dummyScript = (Script) scriptList.getItemAtPosition(1);
		
		
		assertEquals("dummyScript was not added to the list!", "dummyScript", dummyScript.getName());
	}
	
	
	public void testMainMenuButton() {
		solo.clickOnButton(getActivity().getString(R.string.resume));
		solo.clickInList(0);
		
		solo.clickOnButton(solo.getCurrentActivity().getString(R.string.main_menu));
		String title = solo.getText(0).getText().toString();
        assertEquals("MainMenuActivity is not showing!", getActivity().getString(R.string.main_menu), title);
	}
	
	
	public void testContextMenu() {
		solo.clickOnButton(getActivity().getString(R.string.resume));
		solo.clickInList(0);
		solo.clickOnButton(1);
		
		solo.enterText(0, "dummyScript");
		solo.clickOnButton(solo.getCurrentActivity().getString(R.string.new_script_dialog_button));
		
		String[] menu = solo.getCurrentActivity().getResources().getStringArray(R.array.menu_sprite_activity);
		
		solo.clickLongOnText("dummyScript");
        solo.clickOnText(menu[0]);
		solo.enterText(0, "renamedScript");
		solo.clickOnButton(solo.getCurrentActivity().getString(R.string.rename_button));
		
		ListView scriptList = (ListView) solo.getCurrentActivity().findViewById(R.id.scriptListView);
		Script renamedScript = (Script) scriptList.getItemAtPosition(0);
		
		assertEquals("dummyScript was not renamed to renamedScript", "renamedScript", renamedScript.getName());

	}
	
	public void createTestProject(String projectName) throws IOException, NameNotFoundException {
		StorageHandler storageHandler = StorageHandler.getInstance();
		
		int xPosition = 457;
        int yPosition = 598;
        double scaleValue = 0.8;

        Project project = new Project(getActivity(), projectName);
        Sprite firstSprite = new Sprite("cat");
        Sprite secondSprite = new Sprite("dog");
        Sprite thirdSprite = new Sprite("horse");
        Sprite fourthSprite = new Sprite("pig");
        Script testScript = new Script();
        Script otherScript = new Script();
        HideBrick hideBrick = new HideBrick(firstSprite);
        ShowBrick showBrick = new ShowBrick(firstSprite);
        ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(secondSprite, scaleValue);
        ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(firstSprite, null);
        PlaceAtBrick placeAtBrick = new PlaceAtBrick(secondSprite, xPosition, yPosition);

        // adding Bricks: ----------------
        testScript.addBrick(hideBrick);
        testScript.addBrick(showBrick);
        testScript.addBrick(scaleCostumeBrick);
        testScript.addBrick(comeToFrontBrick);
        testScript.setName("testScript");
        
        otherScript.addBrick(placeAtBrick); // secondSprite
        otherScript.setPaused(true);
        otherScript.setName("otherScript");
        // -------------------------------

        firstSprite.getScriptList().add(testScript);
        secondSprite.getScriptList().add(otherScript);

        project.addSprite(firstSprite);
        project.addSprite(secondSprite);
        project.addSprite(thirdSprite);
        project.addSprite(fourthSprite);

        storageHandler.saveProject(project);
		
	}

}
