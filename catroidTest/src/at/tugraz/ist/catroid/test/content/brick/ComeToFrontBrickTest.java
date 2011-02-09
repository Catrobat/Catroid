package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.content.project.Project;

public class ComeToFrontBrickTest extends AndroidTestCase {
	
	public void testComeToFront() {
		Project project = new Project("testProject");
		
		Sprite bottomSprite = new Sprite("catroid");
		assertEquals("Unexpected initial z position of bottomSprite", 0, bottomSprite.getZPosition());
		
		Sprite topSprite    = new Sprite("scratch");
		assertEquals("Unexpected initial z position of topSprite", 	  0, topSprite.getZPosition());
		
		topSprite.setZPosition(2);
		assertEquals("topSprite z position should now be 2", 2, topSprite.getZPosition());
		project.addSprite(bottomSprite);
		project.addSprite(topSprite);
		
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(bottomSprite, project);
		comeToFrontBrick.execute();
		assertEquals("bottomSprite z position should now be 3", bottomSprite.getZPosition(), 3);
	}
	
	public void testNullSprite() {
		Project project = new Project("testProject");
		ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(null, project);
		
		try {
			comeToFrontBrick.execute();
			fail("Execution of ComeToFrontBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException e) {
			// expected behavior
		}
	}
	
	public void testBoundaries() {
		Project project = new Project("testProject");
		
		Sprite sprite = new Sprite("testSprite");
		sprite.setZPosition(Integer.MAX_VALUE);
		
		project.addSprite(sprite);
		
		ComeToFrontBrick brick = new ComeToFrontBrick(sprite, project);
		brick.execute();
		
		assertEquals("An Integer overflow occured during ComeToFrontBrick Execution"
				, Integer.MAX_VALUE, sprite.getZPosition());
	}
}
