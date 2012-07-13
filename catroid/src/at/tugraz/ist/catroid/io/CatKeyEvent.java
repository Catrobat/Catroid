/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.io;

import java.util.HashMap;

import android.view.KeyEvent;

/**
 * @author obusher
 * 
 */
public class CatKeyEvent extends KeyEvent {

	HashMap<Integer, String> keyMap;

	/* FUNCTIONS */
	public static final int KEYCODE_SIN = 1000;
	public static final int KEYCODE_COS = 1001;
	public static final int KEYCODE_TAN = 1002;
	public static final int KEYCODE_LN = 1003;
	public static final int KEYCODE_LOG = 1004;
	public static final int KEYCODE_PI = 1005;
	public static final int KEYCODE_SQUAREROOT = 1006;
	public static final int KEYCODE_EULER = 1007;
	public static final int KEYCODE_RANDOM = 1008;

	/* SENSOR */
	public static final int KEYCODE_SENSOR1 = 1100;
	public static final int KEYCODE_SENSOR2 = 1101;
	public static final int KEYCODE_SENSOR3 = 1102;
	public static final int KEYCODE_SENSOR4 = 1103;
	public static final int KEYCODE_SENSOR5 = 1104;

	// Please update the functions of this class if you add new KEY_CODE constants ^_^

	/**
	 * @param origEvent
	 */
	public CatKeyEvent(KeyEvent origEvent) {
		super(origEvent);

		this.keyMap = new HashMap<Integer, String>();

		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_SIN)), new String("sin"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_COS)), new String("cos"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_TAN)), new String("tan"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_LN)), new String("ln"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_LOG)), new String("log"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_PI)), new String("pi"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_SQUAREROOT)), new String("sqrt"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_EULER)), new String("e"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_RANDOM)), new String("rand"));

		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_SENSOR1)), new String("sensor1"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_SENSOR2)), new String("sensor2"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_SENSOR3)), new String("sensor3"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_SENSOR4)), new String("sensor4"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_SENSOR5)), new String("sensor5"));

		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_PLUS)), new String("+"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_MINUS)), new String("-"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_STAR)), new String("*"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_SLASH)), new String("/"));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_COMMA)), new String(","));
		this.keyMap.put(new Integer(Integer.valueOf(CatKeyEvent.KEYCODE_PERIOD)), new String("."));
	}

	public boolean isOperator(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_PLUS || event.getKeyCode() == KeyEvent.KEYCODE_MINUS
				|| event.getKeyCode() == KeyEvent.KEYCODE_STAR || event.getKeyCode() == KeyEvent.KEYCODE_SLASH) {
			return true;
		}

		return false;
	}

	public boolean isNumber(KeyEvent event) {
		if (event.getKeyCode() >= KeyEvent.KEYCODE_0 && event.getKeyCode() <= KeyEvent.KEYCODE_9) {
			return true;
		}

		return false;
	}

	public boolean isFunction(KeyEvent event) {
		if (event.getKeyCode() >= CatKeyEvent.KEYCODE_SIN && event.getKeyCode() <= CatKeyEvent.KEYCODE_RANDOM) {
			return true;
		}
		return false;

	}

	public boolean isSensor(KeyEvent event) {
		if (event.getKeyCode() >= CatKeyEvent.KEYCODE_SENSOR1 && event.getKeyCode() <= CatKeyEvent.KEYCODE_SENSOR5) {
			return true;
		}
		return false;

	}

	public String getDisplayLabelString() {
		if (this.isNumber(this)) {
			return "" + super.getDisplayLabel();
		} else {

			return this.keyMap.get(this.getKeyCode());
		}
	}
}
