package com.tugraz.android.app.parser;

public class Command {
	final static int CMD_SET_BACKGROUND = 000;
	final static int CMD_SET_SOUND = 100;
	final static int CMD_WAIT = 200;
	
	/**
	 * Sets one of the three current command types
	 */
	public int commandType;
	
	/**
	 * if commandType CMD_WAIT is set, you can set the time to wait here
	 */
	public int time;
	
	/**
	 * if commandType CMD_SET_BACKGROUND or CMD_SET_SOUND, you can set the path to the image or the sound here
	 */
	public String path;
	
	public Command(int commandType, String path, int time){
		this.commandType = commandType;
		this.path = path;
		this.time = time;
	}
	
}
