/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.xml.parser;

import java.lang.reflect.Field;

public class ForwardReference {

	private Object objectWithReferencedField;
	private String referenceString;
	private Field fieldWithReference;

	public ForwardReference(Object object, String reference, Field valueField) {
		objectWithReferencedField = object;
		referenceString = reference;
		fieldWithReference = valueField;
	}

	public Object getObjectWithReferencedField() {
		return objectWithReferencedField;
	}

	public void setObjectWithReferencedField(Object objectWithReferencedField) {
		this.objectWithReferencedField = objectWithReferencedField;
	}

	public String getReferenceString() {
		return referenceString;
	}

	public void setReferenceString(String referenceString) {
		this.referenceString = referenceString;
	}

	public Field getFieldWithReference() {
		return fieldWithReference;
	}

	public void setFieldWithReference(Field fieldWithReference) {
		this.fieldWithReference = fieldWithReference;
	}
}
