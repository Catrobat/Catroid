/**
+ *  Catroid: An on-device graphical programming language for Android devices
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
package at.tugraz.ist.catroid.content;

import java.io.Serializable;
import java.util.List;

public class Formula implements Serializable {

	private static final long serialVersionUID = 1L;
	private FormulaElement root;
	private int numberOfElements = 0;

	public Formula() {
		root = new FormulaElement(0, FormulaElement.ELEMENT_REPLACED_BY_CHILDREN, "root");
	}

	public void addChild(int type, String name, int parentId) {
		numberOfElements++;
		FormulaElement parentItem = findItem(parentId);
		parentItem.addChild(new FormulaElement(numberOfElements, type, name));
	}

	public FormulaElement findItem(int parentID) {
		return root.getItemWithId(parentID);
	}

	public List<FormulaElement> getAllChildren(int parentID) {
		return root.getAllChildren(parentID);
	}

}
