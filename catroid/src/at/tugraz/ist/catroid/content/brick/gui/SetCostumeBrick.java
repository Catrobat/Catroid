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
import android.view.View;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.content.brick.SetCostumeBrickBase;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class SetCostumeBrick extends SetCostumeBrickBase implements Brick{
    
    private static final long serialVersionUID = 1L;
    
    public SetCostumeBrick(Sprite sprite, String imagePath) {
        super(sprite, imagePath);
    }
    
    public View getView(Context context, View convertView, BaseAdapter adapter) {
        return null; //TODO: finish it
    }

}
