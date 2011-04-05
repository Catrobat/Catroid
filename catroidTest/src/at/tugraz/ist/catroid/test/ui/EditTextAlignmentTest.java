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
package at.tugraz.ist.catroid.test.ui;

import android.content.Context;
import android.test.AndroidTestCase;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;

public class EditTextAlignmentTest extends AndroidTestCase {

    static private LayoutInflater inflater;
    
    @Override
    protected void setUp() throws Exception {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public void testRightAlignmentConstructionChangeXBrick() {
        View brickView = inflater.inflate(R.layout.construction_brick_change_x, null);

        EditText edit = (EditText) brickView.findViewById(R.id.InputValueEditTextX);
        assertEquals(Gravity.RIGHT, Gravity.RIGHT & edit.getGravity());
    }
    
    public void testRightAlignmentConstructionChangeYBrick() {
        View brickView = inflater.inflate(R.layout.construction_brick_change_y, null);

        EditText edit = (EditText) brickView.findViewById(R.id.InputValueEditTextY);
        assertEquals(Gravity.RIGHT, Gravity.RIGHT & edit.getGravity());
    }
    
    public void testRightAlignmentConstructionPlaceAtBrick() {
        View brickView = inflater.inflate(R.layout.construction_brick_place_at, null);
        
        EditText edit = (EditText) brickView.findViewById(R.id.InputValueEditTextX);
        assertEquals(Gravity.RIGHT, Gravity.RIGHT & edit.getGravity());
    }
    
    public void testRightAlignmentConstructionSetXBrick() {
        View brickView = inflater.inflate(R.layout.construction_brick_set_x, null);

        EditText edit = (EditText) brickView.findViewById(R.id.InputValueEditTextX);
        assertEquals(Gravity.RIGHT, Gravity.RIGHT & edit.getGravity());
    }
    
    public void testRightAlignmentConstructionSetYBrick() {
        View brickView = inflater.inflate(R.layout.construction_brick_set_y, null);

        EditText edit = (EditText) brickView.findViewById(R.id.InputValueEditTextY);
        assertEquals(Gravity.RIGHT, Gravity.RIGHT & edit.getGravity());
    }
    

}
