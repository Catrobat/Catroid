package at.tugraz.ist.catroid.test.content.brick;

import java.io.File;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.TurnLeftBrick;
import at.tugraz.ist.catroid.content.bricks.TurnRightBrick;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.test.utils.TestUtils;
import at.tugraz.ist.catroid.utils.UtilFile;

public class TurnLeftBrickTest extends InstrumentationTestCase {

	private static final int IMAGE_FILE_ID = R.raw.icon;

	private final String projectName = "testProject";
	private File testImage;

	@Override
	public void setUp() throws Exception {

		File defProject = new File(Consts.DEFAULT_ROOT + "/" + projectName);

		if (defProject.exists()) {
			UtilFile.deleteDirectory(defProject);
		}

		Project project = new Project(getInstrumentation().getTargetContext(), projectName);
		StorageHandler.getInstance().saveProject(project);
		ProjectManager.getInstance().setProject(project);

		testImage = TestUtils.saveFileToProject(this.projectName, "testImage.png", IMAGE_FILE_ID, getInstrumentation()
				.getContext(), TestUtils.TYPE_IMAGE_FILE);

		Values.SCREEN_HEIGHT = 800;
		Values.SCREEN_WIDTH = 480;

	}

	public void testTurnLeftTwice() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().setImagePath(testImage.getAbsolutePath());

		TurnLeftBrick brick = new TurnLeftBrick(sprite, 10);

		brick.execute();
		assertEquals("Wrong direction!", 80, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());

		brick.execute();
		assertEquals("Wrong direction!", 70, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
	}

	public void testTurnLeftAndScale() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().setImagePath(testImage.getAbsolutePath());

		TurnLeftBrick brick = new TurnLeftBrick(sprite, 10);
		SetSizeToBrick brickScale = new SetSizeToBrick(sprite, 50);

		int width = sprite.getCostume().getImageWidthHeight().first;
		int height = sprite.getCostume().getImageWidthHeight().second;

		brick.execute();
		brickScale.execute();

		int widthNew = sprite.getCostume().getImageWidthHeight().first;
		int heightNew = sprite.getCostume().getImageWidthHeight().second;

		assertEquals("Wrong direction!", 80, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong width!", width / 2, widthNew, 1e-3);
		assertEquals("Wrong height!", height / 2, heightNew, 1e-3);

	}

	public void testScaleAndTurnLeft() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().setImagePath(testImage.getAbsolutePath());

		TurnLeftBrick brick = new TurnLeftBrick(sprite, 10);
		SetSizeToBrick brickScale = new SetSizeToBrick(sprite, 50);

		int width = sprite.getCostume().getImageWidthHeight().first;
		int height = sprite.getCostume().getImageWidthHeight().second;

		brickScale.execute();
		brick.execute();

		int widthNew = sprite.getCostume().getImageWidthHeight().first;
		int heightNew = sprite.getCostume().getImageWidthHeight().second;

		assertEquals("Wrong direction!", 80, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
		assertEquals("Wrong width!", width / 2, widthNew, 1e-3);
		assertEquals("Wrong height!", height / 2, heightNew, 1e-3);

	}

	public void testTurnLeftNegative() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().setImagePath(testImage.getAbsolutePath());

		TurnLeftBrick brick = new TurnLeftBrick(sprite, -10);

		brick.execute();

		assertEquals("Wrong direction!", 100, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
	}

	public void testTurnLeft() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().setImagePath(testImage.getAbsolutePath());

		TurnLeftBrick brick = new TurnLeftBrick(sprite, 370);

		brick.execute();

		assertEquals("Wrong direction!", 80, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
	}

	public void testTurnLeftAndTurnRight() {
		Sprite sprite = new Sprite("test");
		sprite.getCostume().setImagePath(testImage.getAbsolutePath());

		TurnLeftBrick brickTurnLeft = new TurnLeftBrick(sprite, 50);
		TurnRightBrick brickTurnRight = new TurnRightBrick(sprite, 30);

		brickTurnLeft.execute();
		brickTurnRight.execute();

		assertEquals("Wrong direction!", 70, sprite.getDirection(), 1e-3);
		assertEquals("Wrong X-Position!", 0, sprite.getXPosition());
		assertEquals("Wrong Y-Position!", 0, sprite.getYPosition());
	}
}
