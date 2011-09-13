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
package at.tugraz.ist.catroid.content.bricks;

import java.io.Serializable;

import android.content.Context;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import at.tugraz.ist.catroid.content.Sprite;

public interface Brick extends Serializable {

	//combine values with bitwise and for desired behavior!!!
	public static final int INVISIBLE_BRICK = 0x0;
	public static final int NORMAL_BRICK = 0x1;
	public static final int BACKGROUND_BRICK = 0x2;
	public static final int SCRIPT_BRICK = 0x4;
	public static final int IS_LOOP_BEGIN_BRICK = 0x10;
	public static final int IS_LOOP_END_BRICK = 0x20;

	public static final int NO_RESOURCES = 0x0;
	public static final int TEXT_TO_SPEECH = 0x8;

	//public static final int BLUETOOTH_LEGO = 64;
	//public static final int BLUETOOTH_ARDUINO = 128;

	//protected abstract int brickBehavior = NORMAL_BRICK;

	public void execute();

	public Sprite getSprite();

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter);

	public View getPrototypeView(Context context);

	public Brick clone();

}
