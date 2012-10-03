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
package at.tugraz.ist.catroid.xml.parser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;

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
					String suffix = soundRef.substring(soundRef
							.lastIndexOf(CatroidXMLConstants.SCRIPT_LIST_ELEMENT_NAME));

					if (referencedObjects.containsKey(suffix)) {
						foundSoundInfo = (SoundInfo) referencedObjects.get(suffix);
						referencedObjects.remove(suffix);
					} else {

						foundSoundInfo = (SoundInfo) references.resolveReference(foundSoundInfo, soundNode, soundRef,
								referencedObjects, forwardRefs);
					}
				} else {

					Node soundFileNameNode = soundElement.getElementsByTagName(CatroidXMLConstants.FILE_NAME).item(0);
					String soundFileName = null;
					if (soundFileNameNode != null) {
						soundFileName = soundFileNameNode.getChildNodes().item(0).getNodeValue();
					}
					Node soundNameNode = soundElement.getElementsByTagName(CatroidXMLConstants.NAME).item(0);
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
				String playSoundQeuery = soundInfoXPath.substring(soundInfoXPath
						.lastIndexOf(CatroidXMLConstants.SOUND_LIST_ELEMENT_NAME));
				PlaySoundBrick playSoundBrickWithRef = (PlaySoundBrick) referencedObjects
						.get("PlaySounfRef../../../../../" + playSoundQeuery);
				if (playSoundBrickWithRef != null) {
					playSoundBrickWithRef.setSoundInfo(foundSoundInfo);
				}
			}
		}
		Field soundListField = sprite.getClass().getDeclaredField("soundList");
		objectGetter.setFieldOfObject(soundListField, sprite, soundList);

	}
}
