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
import java.util.List;

import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.xml.ObjectCreator;

public class ScriptSerializer extends Serializer {

	public ScriptSerializer(Sprite serializedSprite, Project serializedProject) {
		super.serializedSprite = serializedSprite;
		super.serializedProject = serializedProject;
		objectCreator = new ObjectCreator();
	}

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException {
		fieldMap = objectCreator.getFieldMap(object.getClass());
		List<String> scriptStringList = new ArrayList<String>();
		serializedScript = (Script) object;
		String xmlElementString = "";
		Collection<Field> fields = fieldMap.values();
		xmlElementString = "<Content." + object.getClass().getSimpleName() + ">\n";
		scriptStringList.add(xmlElementString);
		for (Field scriptClassField : fields) {
			String fieldName = scriptClassField.getName();
			scriptClassField.setAccessible(true);
			if (!scriptClassField.getType().isPrimitive()) {
				//Log.i("primitive", fieldName + " is not primitive");
				if (fieldName.equals("sprite")) {
					xmlElementString = "<sprite reference=\"../../..\"/>" + "\n";
					scriptStringList.add(xmlElementString);
				} else if (fieldName.equals("brickList")) {
					scriptStringList.add("<brickList>\n");
					BrickSerializer brickSerializer = new BrickSerializer(serializedSprite, (Script) object,
							serializedProject);
					List<String> brickStrings = brickSerializer.serializeBrickList(serializedScript.getBrickList());
					scriptStringList.addAll(brickStrings);
					scriptStringList.add("</brickList>\n");
				} else if (scriptClassField.getType().equals(String.class)) {
					xmlElementString = "<" + fieldName + ">" + scriptClassField.get(object) + "</" + fieldName + ">";
					scriptStringList.add(xmlElementString);
				} else {
					String referenceString = getReference(scriptClassField, object);
					xmlElementString = "<" + fieldName + " reference=\"" + referenceString + "\"/>" + "\n";
					scriptStringList.add(xmlElementString);
				}
			} else {
				xmlElementString = "<" + fieldName + ">" + scriptClassField.get(object).toString() + "</" + fieldName
						+ ">" + "\n";
				scriptStringList.add(xmlElementString);
			}
		}
		xmlElementString = "</Content." + object.getClass().getSimpleName() + ">\n";
		scriptStringList.add(xmlElementString);
		return scriptStringList;
	}

}
