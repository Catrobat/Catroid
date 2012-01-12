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
package at.tugraz.ist.catroid.io;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;

public class CatroidFieldKeySorter implements FieldKeySorter {

	@SuppressWarnings("unchecked")
	public Map sort(final Class type, final Map keyedByFieldKey) {
		final Map map = new TreeMap(new Comparator() {

			public int compare(final Object o1, final Object o2) {
				final FieldKey fieldKey1 = (FieldKey) o1;
				final FieldKey fieldKey2 = (FieldKey) o2;
				int i = fieldKey1.getDepth() - fieldKey2.getDepth();
				if (i == 0) {
					i = fieldKey1.getFieldName().compareTo(fieldKey2.getFieldName());
				}
				return i;
			}
		});
		map.putAll(keyedByFieldKey);
		return map;
	}

}
