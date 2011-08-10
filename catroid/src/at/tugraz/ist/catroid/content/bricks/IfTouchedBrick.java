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

import android.content.Context;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class IfTouchedBrick implements Brick {
	private static final long serialVersionUID = 1L;

	protected Script touchScript;
	private Sprite sprite;

	@XStreamOmitField
	private transient View view;

	public IfTouchedBrick(Sprite sprite, Script touchScript) {
		this.touchScript = touchScript;
		this.sprite = sprite;
	}

	public void execute() {
	}

	public Sprite getSprite() {
		return sprite;
	}

	public View getView(Context context, int brickId, final BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_touched, null);
		}
		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_touched, null);
	}

	@Override
	public Brick clone() {
		return new IfTouchedBrick(getSprite(), touchScript);
	}
}
