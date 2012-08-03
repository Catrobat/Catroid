/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;

public class ElseBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private IfThenElseBrick ifThenElseBrick;

	public ElseBrick(Sprite sprite, IfThenElseBrick ifStartingBrick) {
		this.sprite = sprite;
		this.ifThenElseBrick = ifStartingBrick;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		Script script = getScript();

		while (!sprite.isFinished) {

			if (script.getExecutingBrickIndex() == script.getBrickList().size()) {
				script.setExecutingBrickIndex(script.getBrickList().indexOf(ifThenElseBrick));
				Log.i(this.getClass().toString(), "Executed!!!!");
				return;
			}
			if (ifThenElseBrick.checkCondition()) {
				script.setExecutingBrickIndex(script.getBrickList().indexOf(ifThenElseBrick));
				Log.i(this.getClass().toString(), "Executed!!!!");
				return;
			}
		}
	}

	protected Script getScript() {
		for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
			Script script = sprite.getScript(i);
			if (script.getBrickList().contains(this)) {
				return script;
			}
		}
		return null;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public IfThenElseBrick getIfThenElseBrick() {
		return ifThenElseBrick;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.brick_else, null);
	}

	@Override
	public Brick clone() {
		return new ElseBrick(getSprite(), getIfThenElseBrick());
	}

	public View getPrototypeView(Context context) {
		return null;
	}

}
