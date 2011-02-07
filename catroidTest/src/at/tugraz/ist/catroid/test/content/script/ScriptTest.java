package at.tugraz.ist.catroid.test.content.script;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;



public class ScriptTest extends AndroidTestCase{
	private Sprite sprite = new Sprite("testSprite");
	private HideBrick hideBrick = new HideBrick(sprite);
	private ShowBrick showBrick = new ShowBrick(sprite);
	private PlaceAtBrick placeAtBrick = new PlaceAtBrick(sprite, 0, 0);
	private ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(sprite, 0);
	private ComeToFrontBrick comeToFrontBrick = new ComeToFrontBrick(sprite, null);
	private ArrayList<Brick> brickList;
	
	@SuppressWarnings("unchecked")
	private void initTest(Script script){
		Field field = null;
		try {
			field = Script.class.getDeclaredField("script");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		field.setAccessible(true);
	    try {
			brickList = (ArrayList<Brick>) field.get(script);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void testAddBricks(){
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		
		initTest(script);
		
		assertEquals("hideBrick is not at index 0", 0, brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 1", 1, brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 2", 2, brickList.indexOf(placeAtBrick));	
	}
	
	public void testMoveTopBrickDown() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		script.moveBrickBySteps(hideBrick, 1);
		
		initTest(script);
		
		assertEquals("hideBrick is not at index 1", 1, brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 0", 0, brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 2", 2, brickList.indexOf(placeAtBrick));
	}
	
	public void testMoveTopBrickUp() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		script.moveBrickBySteps(hideBrick, -1);
		
		initTest(script);
		
		assertEquals("hideBrick is not at index 0", 0, brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 1", 1, brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 2", 2, brickList.indexOf(placeAtBrick));
	}
	
	public void testMoveBottomBrickUp() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		script.moveBrickBySteps(placeAtBrick, -1);
		
		initTest(script);
		
		assertEquals("hideBrick is not at index 0", 0, brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 2", 2, brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 1", 1, brickList.indexOf(placeAtBrick));
	}
	
	public void testMoveBottomBrickDown() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		script.moveBrickBySteps(placeAtBrick, 1);
		
		initTest(script);
		
		assertEquals("hideBrick is not at index 0", 0, brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 1", 1, brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 2", 2, brickList.indexOf(placeAtBrick));
	}
	
	public void testMoveBrick() {
		Script script = new Script();
		script.addBrick(hideBrick);
		script.addBrick(showBrick);
		script.addBrick(placeAtBrick);
		script.addBrick(scaleCostumeBrick);
		script.addBrick(comeToFrontBrick);
		script.moveBrickBySteps(scaleCostumeBrick, -2);
		
		initTest(script);
		
		assertEquals("hideBrick is not at index 0", 0, brickList.indexOf(hideBrick));
		assertEquals("showBrick is not at index 2", 2, brickList.indexOf(showBrick));
		assertEquals("placeAtBrick is not at index 3", 3, brickList.indexOf(placeAtBrick));
		assertEquals("scaleCostumeBrick is not at index 1", 1, brickList.indexOf(scaleCostumeBrick));
		assertEquals("comeToFrontBrick is not at index 4", 4, brickList.indexOf(comeToFrontBrick));
	}

}
