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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Sprite;

public class SoundInfoParser {
	References references = new References();
	List<SoundInfo> soundList;
	ObjectCreator objectGetter = new ObjectCreator();

	public void parseSoundInfo(NodeList soundNodes, Sprite sprite, Map<String, Object> referencedObjects,
			List<ForwardReferences> forwardRefs) throws Throwable {
		soundList = new ArrayList<SoundInfo>();
		for (int n = 0; n < soundNodes.getLength(); n++) {
			Node soundNode = soundNodes.item(n);
			if (soundNode.getNodeType() != Node.TEXT_NODE) {
				Element soundElement = (Element) soundNode;
				SoundInfo foundSoundInfo = new SoundInfo();
				String soundRef = References.getReferenceAttribute(soundNode);
				if (soundRef != null) {
					String suffix = soundRef.substring(soundRef.lastIndexOf("scriptList"));

					if (referencedObjects.containsKey(suffix)) {
						foundSoundInfo = (SoundInfo) referencedObjects.get(suffix);
						referencedObjects.remove(suffix);
					} else {

						foundSoundInfo = (SoundInfo) references.resolveReference(foundSoundInfo, soundNode, soundRef,
								referencedObjects, forwardRefs);
					}
				} else {

					Node soundFileNameNode = soundElement.getElementsByTagName("fileName").item(0);
					String soundFileName = null;
					if (soundFileNameNode != null) {
						soundFileName = soundFileNameNode.getChildNodes().item(0).getNodeValue();
					}
					Node soundNameNode = soundElement.getElementsByTagName("name").item(0);
					String soundName = null;
					if (soundNameNode != null) {
						soundName = soundNameNode.getChildNodes().item(0).getNodeValue();
					}
					foundSoundInfo = new SoundInfo();
					foundSoundInfo.setSoundFileName(soundFileName);
					foundSoundInfo.setTitle(soundName);

				}
				soundList.add(foundSoundInfo);
				String soundInfoXPath = ParserUtil.getElementXpath(soundElement);
				referencedObjects.put(soundInfoXPath, foundSoundInfo);
			}
		}
		Field soundListField = sprite.getClass().getDeclaredField("soundList");
		objectGetter.setFieldOfObject(soundListField, sprite, soundList);

	}
}
