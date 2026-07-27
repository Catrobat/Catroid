package org.catrobat.catroid.content.bricks;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.FaceNameDetectAction;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.Serializable;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the detect brick, separate from the action it creates.
 *
 * Built as a mirror of FaceNameTrainBrickTest, because the detect brick is a
 * mirror of the train brick. That is the point: the train brick already runs in
 * the right order after Ask, so the detect brick built the same way does too.
 */
@RunWith(RobolectricTestRunner.class)
public class FaceNameDetectBrickTest {

	private Sprite sprite;
	private Script script;

	@Before
	public void setUp() {
		Context context = ApplicationProvider.getApplicationContext();

		// The action factory reads the current project, so one has to exist.
		Project project = new Project(context, "FaceNameDetectBrickTest");
		ProjectManager.getInstance().setCurrentProject(project);

		sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		script = new StartScript();
		sprite.addScript(script);
	}

	// ---------------- Layout ----------------

	@Test
	public void theBrickPointsAtALayout() {
		FaceNameDetect brick = new FaceNameDetect();
		assertTrue("getViewResource must return a real layout id",
				brick.getViewResource() != 0);
	}

	@Test
	public void everyInstanceUsesTheSameLayout() {
		assertEquals(new FaceNameDetect().getViewResource(),
				new FaceNameDetect().getViewResource());
	}

	// ---------------- Required resources ----------------

	@Test
	public void detectionDeclaresTheCameraResource() {
		// Declaring it is what makes Catroid request the CAMERA permission before
		// the stage starts. FaceDetector cannot ask for one itself.
		FaceNameDetect brick = new FaceNameDetect();
		Brick.ResourcesSet resources = new Brick.ResourcesSet();
		brick.addRequiredResources(resources);

		assertTrue("without this the camera permission is never requested",
				resources.contains(Brick.FACE_NAME_DETECTION));
	}

	// ---------------- Action creation ----------------

	@Test
	public void addingTheBrickAddsExactlyOneAction() {
		ScriptSequenceAction sequence = new ScriptSequenceAction(script);
		assertEquals(0, sequence.getActions().size);

		new FaceNameDetect().addActionToSequence(sprite, sequence);

		assertEquals(1, sequence.getActions().size);
	}

	@Test
	public void theActionIsAFaceNameDetectAction() {
		ScriptSequenceAction sequence = new ScriptSequenceAction(script);
		new FaceNameDetect().addActionToSequence(sprite, sequence);

		Action action = sequence.getActions().get(0);
		assertNotNull(action);
		assertTrue("expected FaceNameDetectAction, got " + action.getClass().getSimpleName(),
				action instanceof FaceNameDetectAction);
	}

	@Test
	public void twoBricksProduceTwoSeparateActions() {
		ScriptSequenceAction sequence = new ScriptSequenceAction(script);
		new FaceNameDetect().addActionToSequence(sprite, sequence);
		new FaceNameDetect().addActionToSequence(sprite, sequence);

		assertEquals(2, sequence.getActions().size);
		assertNotSame("each brick must build its own action",
				sequence.getActions().get(0), sequence.getActions().get(1));
	}

	@Test
	public void oneBrickAddedTwiceStillProducesSeparateActions() {
		// The same brick object runs once per sprite that uses it.
		FaceNameDetect brick = new FaceNameDetect();
		ScriptSequenceAction first = new ScriptSequenceAction(script);
		ScriptSequenceAction second = new ScriptSequenceAction(script);

		brick.addActionToSequence(sprite, first);
		brick.addActionToSequence(sprite, second);

		assertNotSame(first.getActions().get(0), second.getActions().get(0));
	}

	// ---------------- Copying and saving ----------------


	@Test
	public void aClonedBrickStillBuildsItsAction() throws Exception {
		Brick copy = new FaceNameDetect().clone();
		ScriptSequenceAction sequence = new ScriptSequenceAction(script);
		copy.addActionToSequence(sprite, sequence);

		assertEquals(1, sequence.getActions().size);
		assertTrue(sequence.getActions().get(0) instanceof FaceNameDetectAction);
	}

	@Test
	public void theBrickIsSerializableSoProjectsCanBeSaved() {
		assertTrue("a brick that cannot be serialised breaks project saving",
				new FaceNameDetect() instanceof Serializable);
	}

	@Test
	public void theBrickDeclaresASerialVersionUid() throws Exception {
		// Without it, adding a field silently breaks every saved project.
		Field field = FaceNameDetect.class.getDeclaredField("serialVersionUID");
		field.setAccessible(true);
		assertEquals(1L, field.getLong(null));
	}

	// ---------------- Construction ----------------

	@Test
	public void theBrickHasANoArgumentConstructor() throws Exception {
		// Project loading builds bricks reflectively.
		assertNotNull(FaceNameDetect.class.getDeclaredConstructor().newInstance());
	}

	@Test
	public void aFreshBrickIsNotCommented() {
		FaceNameDetect brick = new FaceNameDetect();
		assertFalse(brick.isCommentedOut());
	}

	@Test
	public void aCommentedOutBrickCanBeToggledBack() {
		FaceNameDetect brick = new FaceNameDetect();
		brick.setCommentedOut(true);
		assertTrue(brick.isCommentedOut());
		brick.setCommentedOut(false);
		assertFalse(brick.isCommentedOut());
	}
}
