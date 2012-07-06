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
package at.tugraz.ist.catroid.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FormulaElement implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int ELEMENT_REPLACED_BY_CHILDREN = -1;
	public static final int ELEMENT_FUNCTION = 0;
	public static final int ELEMENT_VALUE = 1;
	public static final int ELEMENT_OPERATOR = 2;

	private int id;
	private int type;
	private String value;
	private List<FormulaElement> children = null;

	public FormulaElement(int id, int type, String value) {
		this.id = id;
		this.type = type;
		this.value = value;

	}

	public FormulaElement getItemWithId(int searchedId) {
		FormulaElement result = null;
		if (this.id == searchedId) {
			return this;
		} else if (children == null) {
			return null;
		} else {
			for (FormulaElement nextChild : children) {
				result = nextChild.getItemWithId(searchedId);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	public void addChild(FormulaElement element) {
		if (children == null) {
			children = new ArrayList<FormulaElement>();
		}
		this.type = ELEMENT_REPLACED_BY_CHILDREN;
		children.add(element);
	}

	public List<FormulaElement> getAllChildren(int searchedId) {

		List<FormulaElement> result = null;

		if (this.id == searchedId) {
			return children;
		} else if (children == null) {
			return null;
		} else {
			for (FormulaElement nextChild : children) {
				result = nextChild.getAllChildren(searchedId);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

}
