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
package at.tugraz.ist.catroid.xml.serializer;

import java.lang.reflect.Field;
import java.util.Map;

import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.xml.ObjectCreator;

public abstract class Serializer {
	ObjectCreator objectCreator;
	Map<String, Field> fieldMap;
	Project serializedProject;
	Sprite serializedSprite;
	Script serializedScript;

	public abstract String serialize(Object object) throws IllegalArgumentException, IllegalAccessException;

	public String getReference(Field fieldNeedingReference, Object objectWIthField) throws IllegalArgumentException,
			IllegalAccessException {
		Object referencedObject = fieldNeedingReference.get(objectWIthField);
		if (referencedObject.getClass().isInstance(Brick.class)) {
			if (serializedScript.getBrickList().contains(referencedObject)) {

			}
		}
		return null;

	}
}
