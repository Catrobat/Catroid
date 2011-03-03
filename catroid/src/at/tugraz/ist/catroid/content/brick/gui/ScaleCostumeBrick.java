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
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrickBase;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class ScaleCostumeBrick extends ScaleCostumeBrickBase implements Brick {

	private static final long serialVersionUID = 1L;

	public ScaleCostumeBrick(Sprite sprite, double scale) {
		super(sprite, scale);
	}

	public View getView(Context context, BaseAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view     = inflater.inflate(R.layout.construction_brick_scale_costume, null);
		EditText edit = (EditText) view.findViewById(R.id.EditText01);
		
		edit.setText(scale.getValue() + "");
		
		EditTextDialog dialog = new EditTextDialog(context, edit, adapter, false);
		dialog.setDouble(scale);
		edit.setOnClickListener(dialog);
		
		return view;
	}
	
	public Brick clone() {
		return new ScaleCostumeBrick(getSprite(), getScale());
	}
}
