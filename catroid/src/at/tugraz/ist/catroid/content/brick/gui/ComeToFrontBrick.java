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
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrickBase;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class ComeToFrontBrick extends ComeToFrontBrickBase implements Brick {

	private static final long serialVersionUID = 1L;

	public ComeToFrontBrick(Sprite sprite, Project project) {
		super(sprite, project);
	}

	public View getView(Context context, BaseAdapter adapter) {
		return getPrototypeView(context);
	}
	
	@Override
    public Brick clone() {
		return new ComeToFrontBrick(getSprite(), project);
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toolbox_brick_come_to_front, null);
		return view;
	}
}
