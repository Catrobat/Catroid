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
package at.tugraz.ist.catroid.xml.serializer;

import java.util.ArrayList;
import java.util.List;

import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.xml.parser.CatroidXMLConstants;

public class SpriteSerializer extends Serializer {

	public SpriteSerializer(Project serializedProject) {
		super.serializedProject = serializedProject;
	}

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException {
		Sprite sprite = (Sprite) object;
		List<String> spriteStrings = new ArrayList<String>();
		String xmlElementString = "";
		xmlElementString = tab + tab + getStartTag(CatroidXMLConstants.SPRITE_ELEMENT_NAME);
		spriteStrings.add(xmlElementString);

		if (sprite.getCostumeDataList().size() > 0) {
			CostumeSerializer costumeStrings = new CostumeSerializer();
			spriteStrings.addAll(costumeStrings.serializeCostumeList(sprite.getCostumeDataList()));
		}
		spriteStrings.add(tab + tab + tab + getSpriteNameElement(sprite));

		if (sprite.getNumberOfScripts() > 0) {
			ScriptSerializer scriptSerializer = new ScriptSerializer(sprite, serializedProject);
			spriteStrings.add(tab + tab + tab + getStartTag(CatroidXMLConstants.SCRIPT_LIST_ELEMENT_NAME));
			for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
				spriteStrings.addAll(scriptSerializer.serialize(sprite.getScript(i)));
			}
			spriteStrings.add(tab + tab + tab + getEndTag(CatroidXMLConstants.SCRIPT_LIST_ELEMENT_NAME));
		}

		if (sprite.getSoundList().size() > 0) {
			SoundSerializer soundSerializer = new SoundSerializer();
			spriteStrings.addAll(soundSerializer.serializeSoundList(sprite.getSoundList()));
		}

		spriteStrings.add(tab + tab + getEndTag(CatroidXMLConstants.SPRITE_ELEMENT_NAME));

		return spriteStrings;
	}

	private String getSpriteNameElement(Sprite sprite) {
		return getElementString(CatroidXMLConstants.SPRITE_NAME, sprite.getName());
	}

	public List<String> serializeList() throws IllegalArgumentException, SecurityException, IllegalAccessException,
			NoSuchFieldException {
		List<String> spriteListStrings = new ArrayList<String>();
		List<String> spriteElements = new ArrayList<String>();

		for (Sprite projectSprite : serializedProject.getSpriteList()) {
			spriteElements.addAll(serialize(projectSprite));
		}
		if (spriteElements.isEmpty()) {
			spriteListStrings.add(getEmptyTag(CatroidXMLConstants.SPRITE_LIST_ELEMENT_NAME));
		} else {
			spriteListStrings.add(tab + getStartTag(CatroidXMLConstants.SPRITE_LIST_ELEMENT_NAME));
			spriteListStrings.addAll(spriteElements);
			spriteListStrings.add(tab + getEndTag(CatroidXMLConstants.SPRITE_LIST_ELEMENT_NAME));

		}
		return spriteListStrings;

	}
}
