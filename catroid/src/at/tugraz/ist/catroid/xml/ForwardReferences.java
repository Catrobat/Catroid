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
package at.tugraz.ist.catroid.xml;

import java.lang.reflect.Field;

/**
 * @author Sam
 * 
 */
public class ForwardReferences {

	private Object objectWithReferencedField;
	private String referenceString;
	private Field fieldWithReference;

	/**
	 * @return the objectWithReferencedField
	 */

	/**
	 * 
	 */
	public ForwardReferences(Object obj, String ref, Field valField) {
		objectWithReferencedField = obj;
		referenceString = ref;
		fieldWithReference = valField;
	}

	public Object getObjectWithReferencedField() {
		return objectWithReferencedField;
	}

	/**
	 * @param objectWithReferencedField
	 *            the objectWithReferencedField to set
	 */
	public void setObjectWithReferencedField(Object objectWithReferencedField) {
		this.objectWithReferencedField = objectWithReferencedField;
	}

	/**
	 * @return the referenceString
	 */
	public String getReferenceString() {
		return referenceString;
	}

	/**
	 * @param referenceString
	 *            the referenceString to set
	 */
	public void setReferenceString(String referenceString) {
		this.referenceString = referenceString;
	}

	/**
	 * @return the fieldWithReference
	 */
	public Field getFieldWithReference() {
		return fieldWithReference;
	}

	/**
	 * @param fieldWithReference
	 *            the fieldWithReference to set
	 */
	public void setFieldWithReference(Field fieldWithReference) {
		this.fieldWithReference = fieldWithReference;
	}
}
