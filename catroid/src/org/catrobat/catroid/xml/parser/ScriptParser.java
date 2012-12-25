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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ScriptParser {
	ObjectCreator objectGetter = new ObjectCreator();
	BrickParser brickParser;

	public void parseScripts(NodeList scriptListNodes, Sprite foundSprite, Map<String, Object> referencedObjects,
			List<ForwardReference> forwardReferences) throws IllegalAccessException, InstantiationException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException, XPathExpressionException,
			ParseException, SecurityException, NoSuchFieldException {

		brickParser = new BrickParser();
		for (int j = 0; j < scriptListNodes.getLength(); j++) {

			Script foundScript = null;

			if (scriptListNodes.item(j).getNodeType() != Node.TEXT_NODE) {
				Element scriptElement = (Element) scriptListNodes.item(j);
				foundScript = getPopulatedScript(scriptElement, foundSprite);
				Node brickListNode = scriptElement.getElementsByTagName(CatroidXMLConstants.BRICK_LIST_ELEMENT_NAME)
						.item(0);
				if (brickListNode != null) {
					brickParser.parseBricks(foundSprite, foundScript, scriptElement, brickListNode, referencedObjects,
							forwardReferences);
				}
				String scriptXPath = ParserUtil.getElementXPath(scriptElement);
				referencedObjects.put(scriptXPath, foundScript);
				foundSprite.addScript(foundScript);
			}

		}
	}

	private Script getPopulatedScript(Element element, Sprite sprite) throws IllegalArgumentException,
			IllegalAccessException, SecurityException, InstantiationException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException, ParseException {
		String scriptClassName = element.getNodeName();

		Class<?> scriptClass = Class.forName(CatroidXMLConstants.CONTENT_PACKAGE + scriptClassName);
		Script newScript = objectGetter.getScriptObject(scriptClassName, sprite);

		Map<String, Field> scriptClassFieldMap = objectGetter.getFieldMap(scriptClass);
		NodeList scriptChildren = element.getChildNodes();
		String valueInString = null;
		for (int o = 0; o < scriptChildren.getLength(); o++) {
			Node child = scriptChildren.item(o);
			if (child.getNodeType() != Node.TEXT_NODE) {
				String childNodeName = child.getNodeName();
				if (childNodeName.equals(CatroidXMLConstants.BRICK_LIST_ELEMENT_NAME)) {
					continue;
				}
				if (childNodeName.equals(CatroidXMLConstants.SPRITE)) {
					continue;
				}
				Field scriptClassField = scriptClassFieldMap.get(childNodeName);

				valueInString = child.getChildNodes().item(0).getNodeValue();
				if (valueInString != null) {

					Object fieldValue = objectGetter.getObjectOfClass(scriptClassField.getType(), valueInString);
					objectGetter.setFieldOfObject(scriptClassField, newScript, fieldValue);
				}

			}

		}

		return newScript;
	}

}
