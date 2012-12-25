/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
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

import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.FILE_NAME;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.NAME;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.SCRIPT_LIST_ELEMENT_NAME;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.SOUND_LIST_ELEMENT_NAME;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SoundInfoParser {
	References references = new References();
	ObjectCreator objectGetter = new ObjectCreator();

	public void parseSoundInfo(NodeList soundNodes, Sprite sprite, Map<String, Object> referencedObjects,
			List<ForwardReference> forwardReferences) throws Throwable {

		List<SoundInfo> soundInfos = new ArrayList<SoundInfo>();
		for (int n = 0; n < soundNodes.getLength(); n++) {
			Node soundNode = soundNodes.item(n);
			if (soundNode.getNodeType() != Node.TEXT_NODE) {
				Element soundElement = (Element) soundNode;
				SoundInfo foundSoundInfo = new SoundInfo();
				String soundReference = References.getReferenceAttribute(soundNode);
				if (soundReference != null) {
					String suffix = soundReference.substring(soundReference.lastIndexOf(SCRIPT_LIST_ELEMENT_NAME));

					if (referencedObjects.containsKey(suffix)) {
						foundSoundInfo = (SoundInfo) referencedObjects.get(suffix);
						referencedObjects.remove(suffix);
					} else {
						foundSoundInfo = (SoundInfo) references.resolveReference(foundSoundInfo, soundNode,
								soundReference, referencedObjects, forwardReferences);
					}
				} else {
					Node soundFileNameNode = soundElement.getElementsByTagName(FILE_NAME).item(0);
					String soundFileName = null;
					if (soundFileNameNode != null) {
						soundFileName = soundFileNameNode.getChildNodes().item(0).getNodeValue();
					}
					Node soundNameNode = soundElement.getElementsByTagName(NAME).item(0);
					String soundName = null;
					if (soundNameNode != null) {
						soundName = soundNameNode.getChildNodes().item(0).getNodeValue();
					}
					foundSoundInfo = new SoundInfo();
					foundSoundInfo.setSoundFileName(soundFileName);
					foundSoundInfo.setTitle(soundName);
				}
				soundInfos.add(foundSoundInfo);
				String soundInfoXPath = ParserUtil.getElementXPath(soundElement);
				referencedObjects.put(soundInfoXPath, foundSoundInfo);
				String playSoundQuery = soundInfoXPath.substring(soundInfoXPath.lastIndexOf(SOUND_LIST_ELEMENT_NAME));
				PlaySoundBrick playSoundBrickWithReference = (PlaySoundBrick) referencedObjects
						.get("PlaySounfRef../../../../../" + playSoundQuery);
				if (playSoundBrickWithReference != null) {
					playSoundBrickWithReference.setSoundInfo(foundSoundInfo);
				}
			}
		}
		Field soundListField = sprite.getClass().getDeclaredField("soundList");
		objectGetter.setFieldOfObject(soundListField, sprite, soundInfos);
	}
}
