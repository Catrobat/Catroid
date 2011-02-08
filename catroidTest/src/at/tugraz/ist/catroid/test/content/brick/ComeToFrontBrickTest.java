package at.tugraz.ist.catroid.test.content.brick;

import android.content.Context;
import android.test.AndroidTestCase;
import android.view.LayoutInflater;
import android.view.View;
import at.tugraz.ist.catroid.R;
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
		assertTrue("bottomSprite not added to data structure", project.addSprite(bottomSprite));
		assertTrue("topSprite not added to data structure", project.addSprite(topSprite));
		
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
	
	public void testGetView() {
		ComeToFrontBrick brick = new ComeToFrontBrick(new Sprite("testSprite"), new Project("testProject"));
		View view = brick.getView((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		assertNotNull("getView returned null", view);
	}
}
