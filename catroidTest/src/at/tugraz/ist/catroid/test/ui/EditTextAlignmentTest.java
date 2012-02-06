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
package at.tugraz.ist.catroid.test.ui;

import java.lang.reflect.Field;

import android.content.Context;
import android.test.AndroidTestCase;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;

public class EditTextAlignmentTest extends AndroidTestCase {

	static private LayoutInflater inflater;
	private Context context;

	@Override
	protected void setUp() throws Exception {
		context = getContext();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * all bricks in R.layout must have "_brick_" in their names
	 * and all bricks in R.id which have an EditText must have the same name as in R.layout plus "_edit_text" at the end
	 * of their names
	 */
	public void testAllBricks() throws IllegalArgumentException, IllegalAccessException {

		Field[] idFields = R.id.class.getFields();
		Field[] layoutFields = R.layout.class.getFields();

		for (Field layoutField : layoutFields) {
			String layoutName = layoutField.getName();

			if (layoutName.contains("_brick_")) {
				for (Field idField : idFields) {
					String idName = idField.getName();

					if (idName.contains(layoutName) && idName.contains("_edit_text")) {
						int brickId = layoutField.getInt(null);

						View brickView = inflater.inflate(brickId, null);
						int editTextId = idField.getInt(null);

						EditText edit = (EditText) brickView.findViewById(editTextId);
						if ((edit.getInputType() & InputType.TYPE_CLASS_NUMBER) == InputType.TYPE_CLASS_NUMBER) {
							assertEquals("Brick " + layoutName
									+ " does not have correct gravity (horizontal alignment)", Gravity.RIGHT,
									Gravity.RIGHT & edit.getGravity());
						} else {
							assertEquals("Brick " + layoutName
									+ " does not have correct gravity (horizontal alignment)", Gravity.LEFT,
									Gravity.LEFT & edit.getGravity());
						}
					}
				}
			}
		}
	}
}
