package org.catrobat.catroid.content.bricks;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.FaceNameTrainAction;
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
 * Tests for the brick itself, separate from the action it creates.
 *
 * A brick has three jobs: point at a layout, declare what it needs from the
 * stage, and add the right action to the sequence. Each of those is checked here.
 */
@RunWith(RobolectricTestRunner.class)
public class FaceNameTrainBrickTest {

	private Sprite sprite;
	private Script script;

	@Before
	public void setUp() {
		Context context = ApplicationProvider.getApplicationContext();

		// The action factory reads the current project, so one has to exist.
		Project project = new Project(context, "FaceNameTrainBrickTest");
		ProjectManager.getInstance().setCurrentProject(project);

		sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);

		script = new StartScript();
		sprite.addScript(script);
	}

	// ---------------- Layout ----------------

	@Test
	public void theBrickPointsAtALayout() {
		FaceNameTrain brick = new FaceNameTrain();
		assertTrue("getViewResource must return a real layout id",
				brick.getViewResource() != 0);
	}

	@Test
	public void everyInstanceUsesTheSameLayout() {
		assertEquals(new FaceNameTrain().getViewResource(),
				new FaceNameTrain().getViewResource());
	}

	// ---------------- Required resources ----------------

	@Test
	public void trainingDoesNotAskForTheCamera() {
		// Training reads photos from the gallery. Asking for FACE_NAME_DETECTION
		// here would make the stage open the camera before the program even runs.
		FaceNameTrain brick = new FaceNameTrain();
		Brick.ResourcesSet resources = new Brick.ResourcesSet();
		brick.addRequiredResources(resources);

		assertFalse("training must not require the detection resource",
				resources.contains(Brick.FACE_NAME_DETECTION));
	}

	// ---------------- Action creation ----------------

	@Test
	public void addingTheBrickAddsExactlyOneAction() {
		ScriptSequenceAction sequence = new ScriptSequenceAction(script);
		assertEquals(0, sequence.getActions().size);

		new FaceNameTrain().addActionToSequence(sprite, sequence);

		assertEquals(1, sequence.getActions().size);
	}

	@Test
	public void theActionIsAFaceNameTrainAction() {
		ScriptSequenceAction sequence = new ScriptSequenceAction(script);
		new FaceNameTrain().addActionToSequence(sprite, sequence);

		Action action = sequence.getActions().get(0);
		assertNotNull(action);
		assertTrue("expected FaceNameTrainAction, got " + action.getClass().getSimpleName(),
				action instanceof FaceNameTrainAction);
	}

	@Test
	public void twoBricksProduceTwoSeparateActions() {
		ScriptSequenceAction sequence = new ScriptSequenceAction(script);
		new FaceNameTrain().addActionToSequence(sprite, sequence);
		new FaceNameTrain().addActionToSequence(sprite, sequence);

		assertEquals(2, sequence.getActions().size);
		assertNotSame("each brick must build its own action",
				sequence.getActions().get(0), sequence.getActions().get(1));
	}

	@Test
	public void oneBrickAddedTwiceStillProducesSeparateActions() {
		// The same brick object runs once per sprite that uses it.
		FaceNameTrain brick = new FaceNameTrain();
		ScriptSequenceAction first = new ScriptSequenceAction(script);
		ScriptSequenceAction second = new ScriptSequenceAction(script);

		brick.addActionToSequence(sprite, first);
		brick.addActionToSequence(sprite, second);

		assertNotSame(first.getActions().get(0), second.getActions().get(0));
	}

	// ---------------- Copying and saving ----------------


	@Test
	public void aClonedBrickStillBuildsItsAction() throws Exception {
		Brick copy = new FaceNameTrain().clone();
		ScriptSequenceAction sequence = new ScriptSequenceAction(script);
		copy.addActionToSequence(sprite, sequence);

		assertEquals(1, sequence.getActions().size);
		assertTrue(sequence.getActions().get(0) instanceof FaceNameTrainAction);
	}

	@Test
	public void theBrickIsSerializableSoProjectsCanBeSaved() {
		assertTrue("a brick that cannot be serialised breaks project saving",
				new FaceNameTrain() instanceof Serializable);
	}

	@Test
	public void theBrickDeclaresASerialVersionUid() throws Exception {
		// Without it, adding a field silently breaks every saved project.
		Field field = FaceNameTrain.class.getDeclaredField("serialVersionUID");
		field.setAccessible(true);
		assertEquals(1L, field.getLong(null));
	}

	// ---------------- Construction ----------------

	@Test
	public void theBrickHasANoArgumentConstructor() throws Exception {
		// Project loading builds bricks reflectively.
		assertNotNull(FaceNameTrain.class.getDeclaredConstructor().newInstance());
	}

	@Test
	public void aFreshBrickIsNotCommented() {
		FaceNameTrain brick = new FaceNameTrain();
		assertFalse(brick.isCommentedOut());
	}

	@Test
	public void aCommentedOutBrickCanBeToggledBack() {
		FaceNameTrain brick = new FaceNameTrain();
		brick.setCommentedOut(true);
		assertTrue(brick.isCommentedOut());
		brick.setCommentedOut(false);
		assertFalse(brick.isCommentedOut());
	}
}
