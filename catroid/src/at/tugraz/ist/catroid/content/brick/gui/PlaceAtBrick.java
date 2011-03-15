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
import at.tugraz.ist.catroid.constructionSite.gui.dialogs.EditTextDialog;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrickBase;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class PlaceAtBrick extends PlaceAtBrickBase implements Brick {

	private static final long serialVersionUID = 1L;

	public PlaceAtBrick(Sprite sprite, int xPosition, int yPosition) {
		super(sprite, xPosition, yPosition);
	}

	public View getView(Context context, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_goto, null);
		EditText edit = (EditText) brickView.findViewById(R.id.InputValueEditTextX);
		edit.setText(xPosition.getValue().intValue() + "");
		EditTextDialog dialog = new EditTextDialog(context, edit, adapter, true);
		dialog.setInteger(xPosition);
		edit.setOnClickListener(dialog);
		
		edit = (EditText) brickView.findViewById(R.id.InputValueEditTextY);
		dialog = new EditTextDialog(context, edit, adapter, true);
		edit.setText(yPosition.getValue().intValue() + "");
		dialog.setInteger(yPosition);
		edit.setOnClickListener(dialog);
		
		return brickView;
	}
	
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View brickView = inflater.inflate(R.layout.toolbox_brick_goto, null);
		return brickView;
	}
	
	@Override
    public Brick clone() {
		return new PlaceAtBrick(getSprite(), getXPosition(), getYPosition());
	}
}
