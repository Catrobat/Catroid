package at.tugraz.ist.catroid.test.content.brick;

import java.io.File;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.NextCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class NextCostumeBrickTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private File testImage;
	private String projectName;

	@Override
	protected void setUp() throws Exception {

		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		Values.SCREEN_HEIGHT = 200;
		Values.SCREEN_WIDTH = 200;
	}

	@Override
	protected void tearDown() throws Exception {
		File projectFile = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (projectFile.exists()) {
			UtilFile.deleteDirectory(projectFile);
		}
		if (testImage != null && testImage.exists()) {
			testImage.delete();
		}
	}

	public void testNextCostume() {

		Sprite sprite = new Sprite("cat");

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		NextCostumeBrick nextCostumeBrick = new NextCostumeBrick(sprite);

		CostumeData costumeData1 = new CostumeData();
		costumeData1.setCostumeFilename(testImage.getName());
		costumeData1.setCostumeName("testImage1");
		sprite.getCostumeDataList().add(costumeData1);

		CostumeData costumeData2 = new CostumeData();
		costumeData2.setCostumeFilename(testImage.getName());
		costumeData2.setCostumeName("testImage2");
		sprite.getCostumeDataList().add(costumeData2);

		setCostumeBrick.setCostume(costumeData1);
		setCostumeBrick.execute();
		nextCostumeBrick.execute();

		assertEquals("Costume is not next costume", costumeData2, sprite.getCostume().getCostumeData());

	}
}
