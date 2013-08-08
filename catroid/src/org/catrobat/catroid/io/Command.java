package org.catrobat.catroid.io;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Command implements Serializable {
	public static enum commandType {
		SINGLE_KEY, KEY_COMBINATION, MOUSE
	};

	private int key;
	private commandType type;
	private int[] keyComb;

	public Command(int command_key, commandType type_) {
		key = command_key;
		type = type_;
	}

	public Command(int[] command_keyComb, commandType type_) {
		keyComb = command_keyComb;
		type = type_;
	}

	public int getKey() {
		return key;
	}

	public int[] getKeyComb() {
		return keyComb;
	}

	public commandType getCommandType() {
		return type;
	}
}