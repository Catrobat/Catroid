package at.tugraz.ist.catroid.uitest.construction_site;

import java.io.File;
import java.io.IOException;

import android.content.pm.PackageManager.NameNotFoundException;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilFile;

import com.jayway.android.robotium.solo.Solo;

public class AddBrickDialogTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
private Solo solo;
   private String testProject = "testProject";

public AddBrickDialogTest () {
 super("at.tugraz.ist.catroid.ui", MainMenuActivity.class);
}

@Override
   public void setUp() throws Exception {
	solo = new Solo(getInstrumentation(), getActivity());
	createTestProject(testProject);
	AddBrickDialog();
}

@Override
   public void tearDown() throws Exception {
 try { 
  solo.finalize();
 } catch (Throwable e) {
  e.printStackTrace();
 }
 getActivity().finish();
 
       File directory = new File("/sdcard/catroid/" + testProject);
 UtilFile.deleteDirectory(directory);

 
 super.tearDown();
}



public void AddBrickDialog() throws NameNotFoundException, IOException {
	
    solo.clickOnButton(getActivity().getString(R.string.load_project));
       
    solo.clickOnText(testProject);
    solo.clickInList(2);
    solo.clickInList(1);
    
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2)); //Substring is needed because of the "+" sign in the button name.
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.play_sound_main_adapter));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.wait_main_adapter));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.hide_main_adapter));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.show_main_adapter));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.goto_main_adapter));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.costume_main_adapter));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.scaleCustome));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.go_back_main_adapter));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.come_to_front_main_adapter));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.add_new_brick).substring(2));
    solo.clickOnText(solo.getCurrentActivity().getString(R.string.touched_main_adapter));
 
}


public void testCheckBrick() {
	
	solo.clickOnButton(getActivity().getString(R.string.main_menu));
	solo.clickOnButton(getActivity().getString(R.string.load_project));
	solo.clickOnText(testProject);
	solo.clickInList(2);
	solo.clickInList(1);

    assertNotNull(solo.getString(R.string.play_sound_main_adapter));
    assertNotNull(solo.getString(R.string.wait_main_adapter));
    assertNotNull(solo.getString(R.string.hide_main_adapter));
    assertNotNull(solo.getString(R.string.show_main_adapter));
    assertNotNull(solo.getString(R.string.goto_main_adapter));
    assertNotNull(solo.getString(R.string.costume_main_adapter));
    assertNotNull(solo.getString(R.string.scaleCustome));
    assertNotNull(solo.getString(R.string.go_back_main_adapter));
    assertNotNull(solo.getString(R.string.come_to_front_main_adapter));
    assertNotNull(solo.getString(R.string.touched_main_adapter));
    
}



private void createTestProject(String projectName) throws IOException {
			
			StorageHandler storageHandler = StorageHandler.getInstance();
	  
	        Project project = new Project(getActivity(), projectName);
	        Sprite firstSprite = new Sprite("cat");
	        
	        Script testScript = new Script("ScriptTest");
	        
	        firstSprite.getScriptList().add(testScript);
	        project.addSprite(firstSprite);
	        
	        ProjectManager.getInstance().setProject(project);
	        ProjectManager.getInstance().setCurrentSprite(firstSprite);
	        ProjectManager.getInstance().setCurrentScript(testScript);

	        storageHandler.saveProject(project);
	 }

}
