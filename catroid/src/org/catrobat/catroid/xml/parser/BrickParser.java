/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
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

import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class BrickParser {

	private References references;
	private ObjectCreator objectGetter = new ObjectCreator();
	private LookParser lookParser;
	private XPathFactory xPathFactory = XPathFactory.newInstance();
	private XPath xPath = xPathFactory.newXPath();

	public void parseBricks(Sprite foundSprite, Script foundScript, Element scriptElement, Node brickListNode,
			Map<String, Object> referencedObjects, List<ForwardReference> forwardReferences)
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException, ParseException, SecurityException, NoSuchFieldException {

		NodeList brickNodes = brickListNode.getChildNodes();

		for (int k = 0; k < brickNodes.getLength(); k++) {
			Node currentBrickNode = brickNodes.item(k);
			if (currentBrickNode.getNodeType() != Node.TEXT_NODE) {
				Brick foundBrickObject = null;
				Element brickElement = (Element) currentBrickNode;
				String brickName = currentBrickNode.getNodeName();
				String brickReferenceAttribute = References.getReferenceAttribute(brickElement);
				if (brickReferenceAttribute != null) {
					String loopEndReferenceQuery = brickReferenceAttribute.replace(PARENT_ELEMENT,
							BRICK_LIST_ELEMENT_NAME);
					if (brickName.equals(LoopEndBrick.class.getSimpleName())
							&& (referencedObjects.containsKey(loopEndReferenceQuery))) {
						foundBrickObject = (Brick) referencedObjects.get(loopEndReferenceQuery);
						referencedObjects.remove(loopEndReferenceQuery);
					} else {
						references = new References();
						foundBrickObject = (Brick) references.resolveReference(foundBrickObject, brickElement,
								brickReferenceAttribute, referencedObjects, forwardReferences);
					}
				} else {
					NodeList brickValueNodes = brickElement.getChildNodes();
					foundBrickObject = getBrickObject(brickName, foundSprite, brickValueNodes, brickElement,
							referencedObjects, forwardReferences);
				}
				if (foundBrickObject != null) {
					Method[] brickClassMethods = foundBrickObject.getClass().getDeclaredMethods();
					for (Method method : brickClassMethods) {
						if (method.getName().equals("readResolve")) {
							method.setAccessible(true);
							method.invoke(foundBrickObject);
						}
					}

					String brickXPath = ParserUtil.getElementXPath(brickElement);
					referencedObjects.put(brickXPath, foundBrickObject);
					foundScript.addBrick(foundBrickObject);
				} else {
					throw new ParseException("Brick parsing incomplete");
				}
			}
		}
	}

	private Brick getBrickObject(String brickName, Sprite foundSprite, NodeList valueNodes, Element brickElement,
			Map<String, Object> referencedObjects, List<ForwardReference> forwardReferences)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException, XPathExpressionException,
			ParseException, NoSuchFieldException {

		String brickClassName = brickName;
		Brick brickObject = null;
		Class<?> brickClass = Class.forName(BRICK_PACKAGE + brickClassName);
		Map<String, Field> brickFieldsToSet = objectGetter.getFieldMap(brickClass);
		brickObject = (Brick) objectGetter.getObjectOfClass(brickClass, "0");
		if (valueNodes != null) {
			brickObject = parseBrickValues(foundSprite, valueNodes, brickObject, brickFieldsToSet, referencedObjects,
					forwardReferences);
		}
		String xPath = ParserUtil.getElementXPath(brickElement);
		referencedObjects.put(xPath, brickObject);
		return brickObject;
	}

	private Brick parseBrickValues(Sprite foundSprite, NodeList valueNodes, Brick brickObject,
			Map<String, Field> brickFieldsToSet, Map<String, Object> referencedObjects,
			List<ForwardReference> forwardReferences) throws IllegalAccessException, XPathExpressionException,
			InstantiationException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException,
			ParseException, SecurityException, NoSuchFieldException {

		Field spriteField = null;
		if (brickFieldsToSet.containsKey(SPRITE_ELEMENT_NAME)) {
			spriteField = brickFieldsToSet.get(SPRITE_ELEMENT_NAME);
		} else {
			spriteField = brickObject.getClass().getSuperclass().getDeclaredField(SPRITE_ELEMENT_NAME);
		}
		spriteField.setAccessible(true);
		spriteField.set(brickObject, foundSprite);
		for (int l = 0; l < valueNodes.getLength(); l++) {
			Node brickValue = valueNodes.item(l);

			if (brickValue.getNodeType() == Node.TEXT_NODE) {
				continue;
			}
			String brickValueName = brickValue.getNodeName();

			Field valueField = brickFieldsToSet.get(brickValueName);
			if (valueField != null) {
				valueField.setAccessible(true);

				if (brickValueName.equals(SPRITE_ELEMENT_NAME)) {
					valueField.set(brickObject, foundSprite);
					continue;
				}
				String referenceAttribute = References.getReferenceAttribute(brickValue);
				if (referenceAttribute != null) {
					if (!referenceAttribute.equals("")) {
						if (brickValueName.equals(LOOK_DATA_ELEMENT_NAME)) {
							lookParser = new LookParser();
							Boolean lookSet = lookParser.setLookDataOfBrick(brickObject, valueField,
									referenceAttribute, referencedObjects);
							if (!lookSet) {
								references = new References();
								references.resolveReference(objectGetter.getObjectOfClass(LookData.class, ""),
										brickValue, referenceAttribute, referencedObjects, forwardReferences);
							}
							continue;
						}
						if (brickValueName.equals(SOUND_INFO_ELEMENT_NAME)) {
							referencedObjects.put("PlaySounfRef" + referenceAttribute, brickObject);
						}
						if (brickValueName.equals(LOOP_END_BRICK)) {
							LoopEndBrick parsedLoopEndBrick = new LoopEndBrick(foundSprite,
									(LoopBeginBrick) brickObject);
							String brickValueXPath = ParserUtil.getElementXPath((Element) brickValue);
							String referenceString = brickValueXPath.substring(brickValueXPath
									.lastIndexOf(BRICK_LIST_ELEMENT_NAME));
							referencedObjects.put(LOOP_END_BRICKREFERENCE + referenceString, parsedLoopEndBrick);
							valueField.set(brickObject, parsedLoopEndBrick);
							continue;
						}
						if (brickValueName.equals(LOOP_BEGIN_BRICK)) {
							String loopEndref = referenceAttribute.replace("../..", BRICK_LIST_ELEMENT_NAME);
							brickObject = (Brick) referencedObjects.get(LOOP_END_BRICKREFERENCE + loopEndref
									+ "/LoopEndBrick");
							if (brickObject != null) {
								return brickObject;
							}
						}

						XPathExpression xPathExpression = xPath.compile(referenceAttribute);
						Log.i("get brick object", "XPath evaluated :" + referenceAttribute);
						Element referencedElement = (Element) xPathExpression.evaluate(brickValue, XPathConstants.NODE);
						if (referencedElement == null) {
							throw new ParseException("referred element not found in brick value parsing");
						}
						String xPath = ParserUtil.getElementXPath(referencedElement);
						Object valueObject = referencedObjects.get(xPath);

						if (valueObject != null) {
							valueField.set(brickObject, valueObject);
						} else {
							ForwardReference forwardReference = new ForwardReference(brickObject, xPath, valueField);
							forwardReferences.add(forwardReference);
						}
						continue;
					} else {
						continue;
					}
				}

				if (brickValue.getChildNodes().getLength() > 1) {
					if (brickValueName.endsWith(BRICK_CLASS_SUFFIX)) {

						if (brickValueName.equals(LOOP_END_BRICK)) {
							Element brickValueElement = (Element) brickValue;
							Element brickLoopBeginElement = (Element) brickValueElement.getElementsByTagName(
									LOOP_BEGIN_BRICK).item(0);
							String loopBeginReference = References.getReferenceAttribute(brickLoopBeginElement);
							if (loopBeginReference.equals("../..")) {
								LoopEndBrick foundLoopEndBrick = new LoopEndBrick(foundSprite,
										(LoopBeginBrick) brickObject);
								valueField.set(brickObject, foundLoopEndBrick);
								String childBrickXPath = ParserUtil.getElementXPath((Element) brickValue);
								String key = childBrickXPath.substring(childBrickXPath
										.lastIndexOf(BRICK_LIST_ELEMENT_NAME));
								referencedObjects.put(key, foundLoopEndBrick);
								continue;
							}
						}

						Character brickValueStartCharacter = (brickValueName.toUpperCase(Locale.getDefault()).charAt(0));
						brickValueName = brickValueStartCharacter.toString().concat(brickValueName.substring(1));
						Brick valueBrick = getBrickObject(brickValueName, foundSprite, brickValue.getChildNodes(),
								(Element) brickValue, referencedObjects, forwardReferences);
						valueField.set(brickObject, valueBrick);
						String childBrickXPath = ParserUtil.getElementXPath((Element) brickValue);
						referencedObjects.put(childBrickXPath, valueBrick);
					} else {

						Map<String, Field> fieldMap = objectGetter.getFieldMap(valueField.getType());
						Object valueObject = objectGetter.getObjectOfClass(valueField.getType(), "0");
						getValueObject(valueObject, brickValue, fieldMap, referencedObjects, forwardReferences);

						valueField.set(brickObject, valueObject);

						String valueObjectXPath = ParserUtil.getElementXPath((Element) brickValue);
						if (brickValueName.equals(SOUND_INFO)) {
							String suffix = valueObjectXPath.substring(valueObjectXPath
									.lastIndexOf(SCRIPT_LIST_ELEMENT_NAME));
							referencedObjects.put(suffix, valueObject);
						} else {
							referencedObjects.put(valueObjectXPath, valueObject);
						}
					}
				} else {
					Node valueNode = brickValue.getChildNodes().item(0);
					if (valueNode != null) {
						String valueOfValue = valueNode.getNodeValue();
						Object valueObject = objectGetter.getObjectOfClass(valueField.getType(), valueOfValue);
						valueField.set(brickObject, valueObject);
					}
				}
			} else {
				throw new ParseException("Error when parsing values, no field in brick with the value name");
			}
		}
		return brickObject;
	}

	public void getValueObject(Object nodeObject, Node node, Map<String, Field> nodeClassFieldsToSet,
			Map<String, Object> referencedObjects, List<ForwardReference> forwardReferences)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, XPathExpressionException, ParseException {

		NodeList children = node.getChildNodes();
		if (children.getLength() > 1) {
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() != Node.TEXT_NODE) {
					String childNodeName = child.getNodeName();
					Field fieldWithNodeName = nodeClassFieldsToSet.get(childNodeName);
					if (fieldWithNodeName != null) {
						fieldWithNodeName.setAccessible(true);
						String referenceString = References.getReferenceAttribute(child);
						if (referenceString != null) {
							XPathExpression xPathExpression = xPath.compile(referenceString);
							Log.i("parsing get value object method", "XPath evaluated");
							Element referencedElement = (Element) xPathExpression.evaluate(child, XPathConstants.NODE);
							if (referencedElement == null) {
								throw new ParseException("referenced element not found at value object parsing");
							}
							String xPath = ParserUtil.getElementXPath(referencedElement);
							Object valueObject = referencedObjects.get(xPath);
							if (valueObject == null) {
								ForwardReference forwardReference = new ForwardReference(nodeObject, xPath,
										fieldWithNodeName);
								forwardReferences.add(forwardReference);
							} else {
								fieldWithNodeName.set(nodeObject, valueObject);
							}
							continue;
						}
						Object valueObject;
						if (child.getChildNodes().getLength() > 1) {
							Map<String, Field> fieldMap = objectGetter.getFieldMap(fieldWithNodeName.getType());
							valueObject = objectGetter.getObjectOfClass(fieldWithNodeName.getType(), "0");
							getValueObject(valueObject, child, fieldMap, referencedObjects, forwardReferences);
							String childXPath = ParserUtil.getElementXPath((Element) node);
							referencedObjects.put(childXPath, valueObject);
						} else {
							Node valueChildValue = child.getChildNodes().item(0);
							if (valueChildValue != null) {
								String valueString = valueChildValue.getNodeValue();
								valueObject = objectGetter.getObjectOfClass(fieldWithNodeName.getType(), valueString);
								fieldWithNodeName.set(nodeObject, valueObject);
							}
						}
					}
				}
			}
		}
	}
}
