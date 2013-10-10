package org.catrobat.catroid.test.cucumber;

import android.test.AndroidTestCase;
import cucumber.api.java.en.Given;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.test.cucumber.util.Util;

import java.io.File;

public class ObjectSteps extends AndroidTestCase {
	////////////////////////////////////////////////////////////////////////////
	///// LEGACY STEP DEFINTIONS ///////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	@Deprecated
	@Given("^an object '(\\w+)'$")
	public void object(String name) {
		Project project = (Project) Cucumber.get(Cucumber.KEY_PROJECT);
		Sprite object = newObject(name);
		project.addSprite(object);
		Cucumber.put(Cucumber.KEY_CURRENT_OBJECT, object);
	}

	private Sprite newObject(String name) {
		Sprite object = new Sprite(name);
		object.look.setZIndex(0);

		StartScript startScript = new StartScript(object);
		SetLookBrick setLookBrick = new SetLookBrick(object);
		startScript.addBrick(setLookBrick);
		object.addScript(startScript);

		File image = Util.createObjectImage(getContext(), name, R.drawable.default_project_mole_1);
		LookData lookData = Util.newLookData(name, image);
		object.getLookDataList().add(lookData);
		setLookBrick.setLook(lookData);
		return object;
	}
}
