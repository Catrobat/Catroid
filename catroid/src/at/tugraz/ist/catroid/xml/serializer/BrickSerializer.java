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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.util.Log;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.xml.ObjectCreator;

public class BrickSerializer extends Serializer {

	public BrickSerializer(Sprite serializedSprite, Script serializedScript, Project serializedProject) {
		objectCreator = new ObjectCreator();
		fieldMap = new HashMap<String, Field>();
		super.serializedSprite = serializedSprite;
		super.serializedScript = serializedScript;
		super.brickList = serializedScript.getBrickList();
		referenceStrings = new ArrayList<String>();
		costumeList = serializedSprite.getCostumeDataList();
		super.spriteList = serializedProject.getSpriteList();
		super.soundList = serializedSprite.getSoundList();
	}

	@Override
	public String serialize(Object object) throws IllegalArgumentException, IllegalAccessException {
		fieldMap = objectCreator.getFieldMap(object.getClass());
		String xmlElementString = "";
		Collection<Field> fields = fieldMap.values();
		for (Field brickClassField : fields) {
			String fieldName = brickClassField.getName();
			brickClassField.setAccessible(true);
			if (!brickClassField.getType().isPrimitive()) {
				Log.i("primitive", fieldName + " is not primitive");
				if (fieldName.equals("sprite")) {
					xmlElementString = xmlElementString + "<sprite reference=\"../../../../..\"/>" + "\n";
				} else {
					String referenceString = getReference(brickClassField, object);
					xmlElementString = xmlElementString + "<" + fieldName + " reference=\"" + referenceString + "\"/>"
							+ "\n";
				}
			} else {
				xmlElementString = xmlElementString + "<" + fieldName + ">" + brickClassField.get(object).toString()
						+ "</" + fieldName + ">" + "\n";
			}
		}
		Log.i("serializer", xmlElementString);
		return xmlElementString;
	}
}
