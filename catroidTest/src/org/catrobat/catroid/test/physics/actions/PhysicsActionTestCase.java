package org.catrobat.catroid.test.physics.actions;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.PhysicsTestUtils;
import org.catrobat.catroid.test.utils.TestUtils;

import java.io.File;
import java.util.ArrayList;

public class PhysicsActionTestCase extends InstrumentationTestCase {

	protected Sprite sprite;
	protected PhysicsObject physicsObject;
	protected PhysicsWorld physicsWorld;

	private Project project;
	private String simpleSingleConvexPolygonFileName;
	private File simpleSingleConvexPolygonFile;
	private static final int SIMPLE_SINGLE_CONVEX_POLYGON_RES_ID = R.raw.simple_single_convex_polygon;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtils.deleteTestProjects();
		physicsWorld = new PhysicsWorld(1920, 1600);
		project = new Project(getInstrumentation().getTargetContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		simpleSingleConvexPolygonFile = TestUtils.saveFileToProject(TestUtils.DEFAULT_TEST_PROJECT_NAME,
				simpleSingleConvexPolygonFileName, SIMPLE_SINGLE_CONVEX_POLYGON_RES_ID, getInstrumentation()
						.getContext(), TestUtils.TYPE_IMAGE_FILE);
		simpleSingleConvexPolygonFileName = PhysicsTestUtils
				.getInternalImageFilenameFromFilename("simple_single_convex_polygon.png");

		sprite = new Sprite("TestSprite");
		LookData lookdata = PhysicsTestUtils.generateLookData(simpleSingleConvexPolygonFile);
		lookdata.setLookFilename(simpleSingleConvexPolygonFileName);
		ArrayList<LookData> lookList = new ArrayList<LookData>();
		lookList.add(lookdata);
		sprite.setLookDataList(lookList);

		physicsObject = physicsWorld.getPhysicsObject(sprite);
	}

	@Override
	protected void tearDown() throws Exception {
		sprite = null;
		physicsObject = null;
		physicsWorld = null;

		project = null;
		simpleSingleConvexPolygonFileName = null;
		simpleSingleConvexPolygonFile = null;

		TestUtils.deleteTestProjects();
		super.tearDown();
	}

}
