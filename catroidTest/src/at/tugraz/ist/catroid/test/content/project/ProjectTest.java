package at.tugraz.ist.catroid.test.content.project;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.content.project.Project;

public class ProjectTest extends AndroidTestCase {
	private Sprite bottomSprite = new Sprite("bottom");
	private Sprite topSprite = new Sprite("top");
	private Project project = Project.getInstance();
	
	
	public void testAddRemoveSprite() {
		assertTrue("topSprite was not added to HashSet", project.addSprite(bottomSprite));
		assertTrue("bottomSprite was not added to HashSet", project.addSprite(topSprite));
		assertTrue("topSprite was not removed from HashSet", project.removeSprite(bottomSprite));
		assertTrue("bottomSprite was not removed from HashSet", project.removeSprite(topSprite));
	}
	
	public void testGetMaxZValue() {
		bottomSprite.setZPosition(3);
		topSprite.setZPosition(4);
		assertTrue("topSprite was not added to HashSet", project.addSprite(bottomSprite));
		assertTrue("bottomSprite was not added to HashSet", project.addSprite(topSprite));
		assertEquals("Maximum z value should be 4", project.getMaxZValue(), 4);
	}
}
