package at.tugraz.ist.catroid.constructionSite.content;


import java.util.ArrayList;
import java.util.HashMap;

public class BrickDefine {
	
	//Definition of Brick
	public static final String BRICK_ID = "brick_id";
	public static final String BRICK_NAME = "brick_name";
	public static final String BRICK_VALUE = "brick_value";
	public static final String BRICK_VALUE_1 = "brick_value_1";
	public static final String BRICK_TYPE = "brick_type";
	// definition xyyy x = group y = brick id
	
	
	public final static int NUMBER_OF_BRICK_TYPES = 11; // TODO: a static number for this is *bad*
	
	public final static int SET_BACKGROUND = 0; 
	public final static int WAIT = 1;
	public final static int TOUCHED = 8;
	
	//group number 2 Sound
	public final static int PLAY_SOUND = 2;
	
	//group number 3 Motion
	public final static int GO_TO = 3;
	
	//group number 4 Looks
	public final static int HIDE = 4;
	public final static int SHOW = 5;
	public final static int SET_COSTUME = 6;
	public final static int SCALE_COSTUME = 7;
	public final static int COME_TO_FRONT = 9;
	public final static int GO_BACK = 10;
	
	//group number 9 Error
	public final static int NOT_DEFINED = 9999;
	
	public final static int STAGE_CATEGORY = 100001;
	public final static int SPRITE_CATEGORY = 100002;
	
	private static int currentId = 1;
	
	public static int getNumberOfBrickType(){
		return NUMBER_OF_BRICK_TYPES;
	}
	
	private static HashMap<String, String> createBrick(int brickType, String brickName, String brickValue, String brickValue1) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(BRICK_ID, String.valueOf(currentId));
		currentId++;
		map.put(BRICK_TYPE, String.valueOf(brickType));
		map.put(BRICK_NAME, brickName);
		map.put(BRICK_VALUE, brickValue);
		map.put(BRICK_VALUE_1, brickValue1);
		
		return map;
	}
	
	private static HashMap<String, String> createBrick(int brickType, String brickName, String brickValue) {
		return createBrick(brickType, brickName, brickValue, "");
	}
	
	private static HashMap<String, String> createBrick(int brickType) {
		return createBrick(brickType, "", "", "");
	}
	
	public static ArrayList<HashMap<String, String>> getToolBoxBrickContent(int id){
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
		
		switch(id){
		case STAGE_CATEGORY:
			list.add(createBrick(SET_BACKGROUND));
			list.add(createBrick(PLAY_SOUND, "", "1"));
			list.add(createBrick(WAIT, "", "1"));
        
	        break;		
		case SPRITE_CATEGORY:
			list.add(createBrick(PLAY_SOUND, "", "1"));
			list.add(createBrick(WAIT, "", "1"));
			list.add(createBrick(HIDE));
			list.add(createBrick(SHOW));
			list.add(createBrick(GO_TO, "", "1", "1"));
			list.add(createBrick(SET_COSTUME));
			list.add(createBrick(SCALE_COSTUME, "", "100"));
			list.add(createBrick(GO_BACK, "", "1"));
			list.add(createBrick(COME_TO_FRONT));
			list.add(createBrick(TOUCHED));
			
			break;
		}
		return list;
	}
	
}
