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

import java.util.ArrayList;
import java.util.List;

import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;

public class SpriteSerializer extends Serializer {
	private final String spriteTag = "Sprite";
	private final String scriptListTag = "scriptList";

	public SpriteSerializer(Project serializedProject) {
		super.serializedProject = serializedProject;
	}

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException {
		Sprite sprite = (Sprite) object;
		List<String> spriteStrings = new ArrayList<String>();
		String xmlElementString = "";
		xmlElementString = getStartTag(spriteTag);
		spriteStrings.add(xmlElementString);

		if (sprite.getCostumeDataList().size() > 0) {
			CostumeSerializer costumeStrings = new CostumeSerializer();
			spriteStrings.addAll(costumeStrings.serializeCostumeList(sprite.getCostumeDataList()));
		}
		spriteStrings.add(getSpriteNameElement(sprite));

		if (sprite.getNumberOfScripts() > 0) {
			ScriptSerializer scriptSerializer = new ScriptSerializer(sprite, serializedProject);
			spriteStrings.add(getStartTag(scriptListTag));
			for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
				spriteStrings.addAll(scriptSerializer.serialize(sprite.getScript(i)));
			}
			spriteStrings.add(getEndTag(scriptListTag));
		}

		if (sprite.getSoundList().size() > 0) {
			SoundSerializer soundSerializer = new SoundSerializer();
			spriteStrings.addAll(soundSerializer.serializeSoundList(sprite.getSoundList()));
		}

		spriteStrings.add(getEndTag(spriteTag));

		return spriteStrings;
	}

	private String getSpriteNameElement(Sprite sprite) {
		return getElementString("name", sprite.getName());
	}

	public List<String> serializeList() throws IllegalArgumentException, SecurityException, IllegalAccessException,
			NoSuchFieldException {
		List<String> spriteListStrings = new ArrayList<String>();
		spriteListStrings.add(getStartTag("spriteList"));
		for (Sprite projectSprite : serializedProject.getSpriteList()) {
			spriteListStrings.addAll(serialize(projectSprite));
		}
		spriteListStrings.add(getEndTag("spriteList"));
		return spriteListStrings;

	}
}
