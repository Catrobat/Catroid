package org.catrobat.catroid.test.physics.actions;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.content.ActionPhysicsFactory;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;

public class PhysicsActionTestCase extends InstrumentationTestCase {

	protected Sprite sprite;
	protected PhysicsWorld physicsWorld;

	private Project project;
	private String rectangle_125x125_FileName;
	protected File rectangle_125x125_File;
	private static final int RECTANGLE_125x125_RES_ID = R.raw.rectangle_125x125;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.deleteTestProjects();
		rectangle_125x125_FileName = PhysicsTestUtils.getInternalImageFilenameFromFilename("rectangle_125x125.png");

		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		rectangle_125x125_File = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
				rectangle_125x125_FileName, RECTANGLE_125x125_RES_ID, getInstrumentation().getContext(),
				TestUtils.TYPE_IMAGE_FILE);

		physicsWorld = project.getPhysicsWorld();
		sprite = new Sprite("TestSprite");
		sprite.look = new PhysicsLook(sprite, physicsWorld);
		sprite.setActionFactory(new ActionPhysicsFactory());

		LookData lookdata = PhysicsTestUtils.generateLookData(rectangle_125x125_File);
		sprite.look.setLookData(lookdata);

		assertTrue("getLookData is null", sprite.look.getLookData() != null);
	}

	@Override
	protected void tearDown() throws Exception {
		sprite = null;
		physicsWorld = null;

		project = null;
		rectangle_125x125_FileName = null;
		rectangle_125x125_File = null;

		TestUtils.deleteTestProjects();
		super.tearDown();
	}

}
