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
package at.tugraz.ist.catroid.content.brick.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.GoNStepsBackBrickBase;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.brickdialogs.EditIntegerDialog;

public class GoNStepsBackBrick extends GoNStepsBackBrickBase implements Brick {

	private static final long serialVersionUID = 1L;

	public GoNStepsBackBrick(Sprite sprite, int steps) {
		super(sprite, steps);
	}

	public View getView(Context context, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_go_back, null);
		EditText edit = (EditText) view.findViewById(R.id.InputValueEditText);
		
		edit.setText(steps.getValue() + "");
		
        EditIntegerDialog dialog = new EditIntegerDialog(context, edit);
		
		dialog.setInteger(steps);
		edit.setOnClickListener(dialog);
		
		return view;
	}
	
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_go_back, null);
		return view;
	}
	
	@Override
    public Brick clone() {
		return new GoNStepsBackBrick(getSprite(), getSteps());
	}
}
