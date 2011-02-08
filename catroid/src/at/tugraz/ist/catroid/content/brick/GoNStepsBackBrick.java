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
package at.tugraz.ist.catroid.content.brick;

/**
 * @author Ainul, Jia Lin, Denise, Anton
 *
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class GoNStepsBackBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private int steps;

	public GoNStepsBackBrick(Sprite sprite, int steps) {
		this.sprite = sprite;
		this.steps  = steps;
	}

	public void execute() {
		if (steps <= 0)
			throw new NumberFormatException("Steps was not a positive number!");
		
		int currentPosition = sprite.getZPosition();
		
		if (currentPosition - steps > currentPosition) {
			sprite.setZPosition(Integer.MIN_VALUE);
			return;
		}
		
		sprite.setZPosition(currentPosition - steps);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	/* (non-Javadoc)
	 * @see at.tugraz.ist.catroid.content.brick.Brick#getView(android.content.Context)
	 */
	public View getView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.construction_brick_simple_text_view, null);
		TextView textView = (TextView) view.findViewById(R.id.OneElementBrick);
		textView.setText(R.string.come_to_front_main_adapter);
		return view;
	}

}
