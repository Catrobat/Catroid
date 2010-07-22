package com.tugraz.android.app;

import java.security.KeyStore.Entry;
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
	
	//group number 1 Control
	public final static int SET_BACKGROUND = 1001; 
	public final static int WAIT = 1002;
	
	//group number 2 Sound
	public final static int PLAY_SOUND = 2001;
	
	//group number 3 Motion
	public final static int GO_TO = 3001;
	
	//group number 4 Looks
	public final static int HIDE = 4001;
	public final static int SHOW = 4002;
	public final static int SET_COSTUME = 4003;
	
	//group number 9 Error
	public final static int NOT_DEFINED = 9999;
	
	
	public final static HashMap<String, String> WAITBRICK = new HashMap<String, String>();
	public final static HashMap<String, String> SOUNDBRICK = new HashMap<String, String>();
	public final static HashMap<String, String> BACKGROUNDBRICK = new HashMap<String, String>();
	
	public final static ArrayList<HashMap<String, String>> toolbox = new ArrayList<HashMap<String,String>>();
	
	static {
	        //Create different Bricks-> Toolbox
		    WAITBRICK.put(BrickDefine.BRICK_ID, "1");
		    WAITBRICK.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
		    WAITBRICK.put(BrickDefine.BRICK_NAME, "Wait");
		    WAITBRICK.put(BrickDefine.BRICK_VALUE, "1");
		    
		    SOUNDBRICK.put(BrickDefine.BRICK_ID, "2");
		    SOUNDBRICK.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
		    SOUNDBRICK.put(BrickDefine.BRICK_NAME, "Play Sound");
		    SOUNDBRICK.put(BrickDefine.BRICK_VALUE, "Sound");
		    
		    BACKGROUNDBRICK.put(BrickDefine.BRICK_ID, "3");
		    BACKGROUNDBRICK.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
		    BACKGROUNDBRICK.put(BrickDefine.BRICK_NAME, "Set Backgroud");
		    BACKGROUNDBRICK.put(BrickDefine.BRICK_VALUE, "Background");
		    
		    toolbox.add(WAITBRICK);
		    toolbox.add(SOUNDBRICK);
		    toolbox.add(BACKGROUNDBRICK);
		    
		  }
	
}
