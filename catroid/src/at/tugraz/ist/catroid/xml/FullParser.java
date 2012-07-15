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
package at.tugraz.ist.catroid.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.stage.NativeAppActivity;

public class FullParser {

	List<Sprite> sprites = new ArrayList<Sprite>();
	List<Script> scripts = new ArrayList<Script>();
	List<CostumeData> costumeList = new ArrayList<CostumeData>();
	List<SoundInfo> soundList = new ArrayList<SoundInfo>();
	Map<String, Object> referencedObjects = new HashMap<String, Object>();
	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();
	List<ForwardReferences> forwardRefs = new ArrayList<ForwardReferences>();
	ObjectCreator objectGetter = new ObjectCreator();

	public Project fullParser(String xmlFile) throws ParseException {

		Project parsedProject = null;

		try {

			InputStream inputStreamForSprites = NativeAppActivity.getContext().getAssets().open(xmlFile);

			parsedProject = this.parseSpritesWithProject(inputStreamForSprites);

		} catch (ParseException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParseException("IO exception in full parser", e);
		}
		return parsedProject;

	}

	public Project parseSpritesWithProject(InputStream xmlInputStream) throws ParseException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Project parsedProject = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmlInputStream);
			doc.getDocumentElement().normalize();

			NodeList spriteNodes = doc.getElementsByTagName("Content.Sprite");
			for (int i = 0; i < spriteNodes.getLength(); i++) {
				Element spriteElement = (Element) spriteNodes.item(i);
				String spriteName = getSpriteName(spriteElement);
				Sprite foundSprite = new Sprite(spriteName);

				Node costumeListItem = spriteElement.getElementsByTagName("costumeDataList").item(0);
				if (costumeListItem != null) {
					NodeList costumeNodes = costumeListItem.getChildNodes();
					costumeList = new ArrayList<CostumeData>();
					parseCostumeList(costumeNodes, foundSprite);
				}

				Node scriptListItem = spriteElement.getElementsByTagName("scriptList").item(0);
				if (scriptListItem != null) {
					NodeList scriptNodes = scriptListItem.getChildNodes();
					parseScripts(scriptNodes, foundSprite);
				}

				Node soundListItem = spriteElement.getElementsByTagName("soundList").item(0);
				if (soundListItem != null) {
					NodeList soundNodes = soundListItem.getChildNodes();
					soundList = new ArrayList<SoundInfo>();
					parseSoundInfo(soundNodes, foundSprite);

				}

				String spriteXpath = getElementXpath(spriteElement);
				referencedObjects.put(spriteXpath, foundSprite);
				sprites.add(foundSprite);
			}
			resolveForwardReferences();
			parsedProject = getProjectObject(doc, sprites);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ParseException(e);
		}

		return parsedProject;

	}

	private String getSpriteName(Element spriteElement) {
		String spriteName = "";
		NodeList spriteChildren = spriteElement.getChildNodes();
		for (int i = 0; i < spriteChildren.getLength(); i++) {
			if (spriteChildren.item(i).getNodeType() != Node.TEXT_NODE) {
				Element childElement = (Element) spriteChildren.item(i);
				if (childElement.getNodeName().equals("name")) {
					spriteName = childElement.getChildNodes().item(0).getNodeValue();
					break;
				}
			}
		}
		return spriteName;
	}

	private Project getProjectObject(Document doc, List<Sprite> sprites2) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		Project newProject = (Project) objectGetter.getobjectOfClass(Project.class, "0");
		Map<String, Field> projectFieldsToSet = getFieldMap(Project.class);
		NodeList projectNodes = doc.getElementsByTagName("Content.Project");
		NodeList projectNodeChildren = projectNodes.item(0).getChildNodes();
		for (int i = 0; i < projectNodeChildren.getLength(); i++) {
			if (projectNodeChildren.item(i).getNodeType() != Node.TEXT_NODE) {
				Element projectChildElement = (Element) projectNodeChildren.item(i);
				Field projectField = projectFieldsToSet.get(projectChildElement.getNodeName());
				if (projectChildElement.getNodeName().equals("spriteList")) {
					objectGetter.setFieldOfObject(projectField, newProject, sprites2);
					continue;
				}

				if (projectField != null) {
					String valueInString = projectChildElement.getChildNodes().item(0).getNodeValue();
					Object valueObject = objectGetter.getobjectOfClass(projectField.getType(), valueInString);
					objectGetter.setFieldOfObject(projectField, newProject, valueObject);
				}
			}
		}
		return newProject;
	}

	private void parseScripts(NodeList scriptListNodes, Sprite foundSprite) throws IllegalAccessException,
			InstantiationException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException,
			XPathExpressionException, ParseException, SecurityException, NoSuchFieldException {
		for (int j = 0; j < scriptListNodes.getLength(); j++) {

			Script foundScript = null;

			if (scriptListNodes.item(j).getNodeType() != Node.TEXT_NODE) {
				Element scriptElement = (Element) scriptListNodes.item(j);
				foundScript = getpopulatedScript(scriptElement, foundSprite);
				Node brickListNode = scriptElement.getElementsByTagName("brickList").item(0);
				//	scriptLoopEndBrick = null;
				if (brickListNode != null) {
					parseBricks(foundSprite, foundScript, scriptElement, brickListNode);
				}
				String scriptXPath = getElementXpath(scriptElement);
				referencedObjects.put(scriptXPath, foundScript);
				foundSprite.addScript(foundScript);
			}

		}
	}

	private void parseBricks(Sprite foundSprite, Script foundScript, Element scriptElement, Node brickListNode)
			throws XPathExpressionException, InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException, ParseException, SecurityException, NoSuchFieldException {
		NodeList brickNodes = brickListNode.getChildNodes();

		for (int k = 0; k < brickNodes.getLength(); k++) {
			Node currentBrickNode = brickNodes.item(k);
			if (currentBrickNode.getNodeType() != Node.TEXT_NODE) {
				Brick foundBrickObj = null;
				Element brickElement = (Element) currentBrickNode;
				String brickName = currentBrickNode.getNodeName();
				String brickReferenceAttr = getReferenceAttribute(brickElement);
				if (brickReferenceAttr != null) {
					//NodeList loopEndBricksInThisScript = scriptElement.getElementsByTagName("Bricks.LoopEndBrick");
					String refQuery = brickReferenceAttr.substring(brickReferenceAttr.lastIndexOf("Bricks"));
					if (brickName.equals("Bricks.LoopEndBrick") && (referencedObjects.containsKey(refQuery))) {
						foundBrickObj = (Brick) referencedObjects.get(refQuery);
						referencedObjects.remove(refQuery);

					} else {
						foundBrickObj = (Brick) resolveReference(foundBrickObj, brickElement, brickReferenceAttr);
					}
				} else {

					NodeList brickValueNodes = brickElement.getChildNodes();
					foundBrickObj = getBrickObject(brickName, foundSprite, brickValueNodes, brickElement);
				}
				if (foundBrickObj != null) {
					String brickXPath = getElementXpath(brickElement);
					referencedObjects.put(brickXPath, foundBrickObj);
					foundScript.addBrick(foundBrickObj);
				} else {
					throw new ParseException("Brick parsing incomplete");
				}
			}

		}
	}

	private void parseSoundInfo(NodeList soundNodes, Sprite sprite) throws XPathExpressionException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			SecurityException, NoSuchFieldException {
		for (int n = 0; n < soundNodes.getLength(); n++) {
			Node soundNode = soundNodes.item(n);
			if (soundNode.getNodeType() != Node.TEXT_NODE) {
				Element soundElement = (Element) soundNode;
				SoundInfo foundSoundInfo = new SoundInfo();
				String soundRef = getReferenceAttribute(soundNode);
				if (soundRef != null) {
					String suffix = soundRef.substring(soundRef.lastIndexOf("scriptList"));

					if (referencedObjects.containsKey(suffix)) {
						foundSoundInfo = (SoundInfo) referencedObjects.get(suffix);
						referencedObjects.remove(suffix);
					} else {
						foundSoundInfo = (SoundInfo) resolveReference(foundSoundInfo, soundNode, soundRef);
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
				String soundInfoXPath = getElementXpath(soundElement);
				referencedObjects.put(soundInfoXPath, foundSoundInfo);
			}
		}
		Field soundListField = sprite.getClass().getDeclaredField("soundList");
		objectGetter.setFieldOfObject(soundListField, sprite, soundList);

	}

	private Object resolveReference(Object referencedObject, Node elementWithReference, String referenceString)
			throws XPathExpressionException, IllegalArgumentException, SecurityException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		XPathExpression exp = xpath.compile(referenceString);
		Log.i("resolveRef", "xpath evaluated for :" + referenceString);
		Element refferredElement = (Element) exp.evaluate(elementWithReference, XPathConstants.NODE);
		String xpathFromRoot = getElementXpath(refferredElement);
		Object object = referencedObjects.get(xpathFromRoot);
		if (object == null) {
			referencedObject = objectGetter.getobjectOfClass(referencedObject.getClass(), "");
			ForwardReferences forwardRef = new ForwardReferences(referencedObject, xpathFromRoot, null);
			forwardRefs.add(forwardRef);

		} else {
			referencedObject = object;
		}
		return referencedObject;

	}

	@SuppressWarnings("unused")
	private void resolveForwardReferences() throws IllegalArgumentException, IllegalAccessException {
		for (ForwardReferences reference : forwardRefs) {
			Field refField = reference.getFieldWithReference();
			if (refField == null) {
				Object objectWithReference = reference.getObjectWithReferencedField();
				objectWithReference = referencedObjects.get(reference.getReferenceString());
			} else {
				Object parentObj = reference.getObjectWithReferencedField();
				Object valueObj = referencedObjects.get(reference.getReferenceString());
				refField.set(parentObj, valueObj);
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private Script getpopulatedScript(Element element, Sprite sprite) throws IllegalArgumentException,
			IllegalAccessException, SecurityException, InstantiationException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		String scriptClassName = element.getNodeName().substring(8);

		Class scriptClass = Class.forName("at.tugraz.ist.catroid.content." + scriptClassName);
		Script newScript = objectGetter.getScriptObject(scriptClassName, sprite);

		Map<String, Field> scriptClassFieldMap = getFieldMap(scriptClass);
		NodeList scriptChildren = element.getChildNodes();
		String valueInString = null;
		for (int o = 0; o < scriptChildren.getLength(); o++) {
			Node child = scriptChildren.item(o);
			if (child.getNodeType() != Node.TEXT_NODE) {
				String childNodeName = child.getNodeName();
				if (childNodeName.equals("brickList")) {
					continue;
				}
				if (childNodeName.equals("sprite")) {
					continue;
				}
				Field scriptClassField = scriptClassFieldMap.get(childNodeName);

				valueInString = child.getChildNodes().item(0).getNodeValue();
				if (valueInString != null) {

					Object fieldValue = objectGetter.getobjectOfClass(scriptClassField.getType(), valueInString);
					objectGetter.setFieldOfObject(scriptClassField, newScript, fieldValue);
				}

			}

		}

		return newScript;
	}

	@SuppressWarnings("rawtypes")
	private Brick getBrickObject(String brickName, Sprite foundSprite, NodeList valueNodes, Element brickElement)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException, XPathExpressionException,
			ParseException {

		String brickImplName = brickName.substring(7);
		Brick brickObject = null;
		Class brickClass = Class.forName("at.tugraz.ist.catroid.content.bricks." + brickImplName);
		Map<String, Field> brickFieldsToSet = getFieldMap(brickClass);
		brickObject = (Brick) objectGetter.getobjectOfClass(brickClass, "0");
		if (valueNodes != null) {
			parseBrickValues(foundSprite, valueNodes, brickObject, brickFieldsToSet);
		}
		String xp = getElementXpath(brickElement);
		referencedObjects.put(xp, brickObject);
		return brickObject;
	}

	private void parseBrickValues(Sprite foundSprite, NodeList valueNodes, Brick brickObject,
			Map<String, Field> brickFieldsToSet) throws IllegalAccessException, XPathExpressionException,
			InstantiationException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException,
			ParseException {
		for (int l = 0; l < valueNodes.getLength(); l++) {
			Node brickValue = valueNodes.item(l);

			if (brickValue.getNodeType() == Node.TEXT_NODE) {
				continue;
			}
			String brickvalueName = brickValue.getNodeName();

			Field valueField = brickFieldsToSet.get(brickvalueName);
			if (valueField != null) {
				valueField.setAccessible(true);

				if (brickvalueName.equals("sprite")) {
					valueField.set(brickObject, foundSprite);
					continue;
				}
				String referenceAttribute = getReferenceAttribute(brickValue);
				if (referenceAttribute != null) {
					if (brickvalueName.equals("costumeData")) {
						setCostumedataOfBrick(brickObject, valueField, referenceAttribute);
						continue;

					}

					XPathExpression exp = xpath.compile(referenceAttribute);
					Log.i("get brick obj", "xpath evaluated :" + referenceAttribute);
					Element referencedElement = (Element) exp.evaluate(brickValue, XPathConstants.NODE);
					String xp = getElementXpath(referencedElement);
					Object valueObject = referencedObjects.get(xp);

					if (valueObject != null) {
						valueField.set(brickObject, valueObject);
					} else {
						ForwardReferences forwardRef = new ForwardReferences(brickObject, xp, valueField);
						forwardRefs.add(forwardRef);
					}
					continue;
				}

				if (brickValue.getChildNodes().getLength() > 1) {
					if (brickvalueName.endsWith("Brick")) {

						if (brickvalueName.equals("loopEndBrick")) {
							Element brickValueElement = (Element) brickValue;
							Element brickLoopBeginElement = (Element) brickValueElement.getElementsByTagName(
									"loopBeginBrick").item(0);
							String loopBeginRef = getReferenceAttribute(brickLoopBeginElement);
							if (loopBeginRef.equals("../..")) {
								LoopEndBrick foundLoopEndBrick = new LoopEndBrick(foundSprite,
										(LoopBeginBrick) brickObject);
								valueField.set(brickObject, foundLoopEndBrick);
								String childBrickXPath = getElementXpath((Element) brickValue);
								String key = childBrickXPath.substring(childBrickXPath.lastIndexOf("Bricks"));
								referencedObjects.put(key, foundLoopEndBrick);
								//	scriptLoopEndBrick = foundLoopEndBrick;
								continue;
							}
						}

						Character d = (brickvalueName.toUpperCase().charAt(0));
						brickvalueName = d.toString().concat(brickvalueName.substring(1));
						String prefix = "Bricks.";
						brickvalueName = prefix.concat(brickvalueName);
						Brick valueBrick = getBrickObject(brickvalueName, foundSprite, brickValue.getChildNodes(),
								(Element) brickValue);
						valueField.set(brickObject, valueBrick);
						String childBrickXPath = getElementXpath((Element) brickValue);
						referencedObjects.put(childBrickXPath, valueBrick);
					} else {

						Map<String, Field> fieldMap = getFieldMap(valueField.getType());
						Object valueObj = objectGetter.getobjectOfClass(valueField.getType(), "0");
						getValueObject(valueObj, brickValue, fieldMap);

						valueField.set(brickObject, valueObj);

						String valueObjXPath = getElementXpath((Element) brickValue);
						if (brickvalueName.equals("soundInfo")) {
							String suffix = valueObjXPath.substring(valueObjXPath.lastIndexOf("scriptList"));
							referencedObjects.put(suffix, valueObj);
						} else {
							referencedObjects.put(valueObjXPath, valueObj);
						}
					}
				} else {

					Node valueNode = brickValue.getChildNodes().item(0);
					if (valueNode != null) {
						String valueOfValue = valueNode.getNodeValue();
						Object valueObject = objectGetter.getobjectOfClass(valueField.getType(), valueOfValue);
						valueField.set(brickObject, valueObject);

					}
				}

			} else {
				throw new ParseException("Error when parsing values, no field in brick with the value name");
			}

		}
	}

	private void setCostumedataOfBrick(Brick brickObject, Field valueField, String referenceAttribute)
			throws IllegalAccessException {
		int lastIndex = referenceAttribute.lastIndexOf('[');
		String query = "Common.CostumeData";
		String suffix = "";
		if (lastIndex != -1) {
			char referenceNo = referenceAttribute.charAt(referenceAttribute.lastIndexOf('[') + 1);
			suffix = "[" + referenceNo + "]";

		}
		CostumeData referencedCostume = (CostumeData) referencedObjects.get(query + suffix);
		valueField.set(brickObject, referencedCostume);
	}

	private void getValueObject(Object nodeObj, Node node, Map<String, Field> nodeClassFieldsToSet)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, XPathExpressionException {
		NodeList children = node.getChildNodes();
		if (children.getLength() > 1) {
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() != Node.TEXT_NODE) {
					String childNodeName = child.getNodeName();
					Field fieldWithNodeName = nodeClassFieldsToSet.get(childNodeName);
					if (fieldWithNodeName != null) {
						fieldWithNodeName.setAccessible(true);
						String refattr = getReferenceAttribute(child);
						if (refattr != null) {
							XPathExpression exp = xpath.compile(refattr);
							Log.i("parsing get value object method", "xpath evaluated");
							Element refList = (Element) exp.evaluate(child, XPathConstants.NODE);
							String xp = getElementXpath(refList);
							Object valueObject = referencedObjects.get(xp);
							if (valueObject == null) {
								ForwardReferences forwardRef = new ForwardReferences(nodeObj, xp, fieldWithNodeName);
								forwardRefs.add(forwardRef);
							} else {
								fieldWithNodeName.set(nodeObj, valueObject);
							}
							continue;
						}
						if (child.getChildNodes().getLength() > 1) {
							Map<String, Field> fieldMap = getFieldMap(fieldWithNodeName.getType());
							Object valueObj = objectGetter.getobjectOfClass(fieldWithNodeName.getType(), "0");
							getValueObject(valueObj, child, fieldMap);
							String childXPath = getElementXpath((Element) node);
							referencedObjects.put(childXPath, valueObj);
						} else {
							Node valueChildValue = child.getChildNodes().item(0);
							if (valueChildValue != null) {
								String valStr = valueChildValue.getNodeValue();
								Object valobj = objectGetter.getobjectOfClass(fieldWithNodeName.getType(), valStr);
								fieldWithNodeName.set(nodeObj, valobj);
							}
						}

					}

				}
			}
		}

	}

	private void parseCostumeList(NodeList costumeNodes, Sprite sprite) throws SecurityException, NoSuchFieldException,
			IllegalAccessException {
		int costumeIndex = 0;
		for (int m = 0; m < costumeNodes.getLength(); m++) {
			CostumeData foundCostumeData = null;
			if (costumeNodes.item(m).getNodeType() != Node.TEXT_NODE) {

				Element costumeElement = (Element) costumeNodes.item(m);
				String costumeFileName = null;
				Node costumeFileNameNode = costumeElement.getElementsByTagName("fileName").item(0);
				if (costumeFileNameNode != null) {
					costumeFileName = costumeFileNameNode.getChildNodes().item(0).getNodeValue();
				}
				String costumeName = costumeElement.getElementsByTagName("name").item(0).getChildNodes().item(0)
						.getNodeValue();
				foundCostumeData = new CostumeData();
				foundCostumeData.setCostumeFilename(costumeFileName);
				foundCostumeData.setCostumeName(costumeName);
				costumeList.add(foundCostumeData);
				String costumeindexString = "";
				if (costumeIndex > 0) {
					costumeindexString = "[" + costumeIndex + "]";
				}
				referencedObjects.put("Common.CostumeData" + costumeindexString, foundCostumeData);
				costumeIndex++;
			}
		}
		Field costumeListField = sprite.getClass().getDeclaredField("costumeDataList");
		objectGetter.setFieldOfObject(costumeListField, sprite, costumeList);

	}

	public void addToReferredObjects(Object storedObject, Element elementOfObject) {
		String xp = getElementXpath(elementOfObject);
		referencedObjects.put(xp, storedObject);
	}

	@SuppressWarnings("rawtypes")
	private Map<String, Field> getFieldMap(Class cls) {
		Map<String, Field> fieldsToSet = new HashMap<String, Field>();

		Field[] superClassFields = cls.getSuperclass().getDeclaredFields();
		Field[] brickFields = cls.getDeclaredFields();
		if (superClassFields.length > 0) {
			Field[] combined = new Field[superClassFields.length + brickFields.length];
			System.arraycopy(brickFields, 0, combined, 0, brickFields.length);
			System.arraycopy(superClassFields, 0, combined, brickFields.length, superClassFields.length);
			brickFields = combined;
		}
		for (Field field : brickFields) {
			boolean isCurrentFieldTransient = Modifier.isTransient(field.getModifiers());

			if (isCurrentFieldTransient) {
				continue;
			}

			String tagName = field.getName();

			fieldsToSet.put(tagName, field);

		}
		return fieldsToSet;
	}

	public String getElementXpath(Element elt) {
		String path = "";

		try {
			for (; elt != null; elt = (Element) elt.getParentNode()) {
				int idx = getElementIdx(elt);
				String xname = elt.getTagName().toString();

				if (idx > 1) {
					xname += "[" + idx + "]";
				}
				path = "/" + xname + path;
			}
		} catch (Exception ee) {
		}
		return path;
	}

	public int getElementIdx(Element original) {
		int count = 1;

		for (Node node = original.getPreviousSibling(); node != null; node = node.getPreviousSibling()) {
			if (node instanceof Element) {
				Element element = (Element) node;
				if (element.getTagName().equals(original.getTagName())) {
					count++;
				}
			}
		}

		return count;
	}

	private String getReferenceAttribute(Node brickValue) {
		Element brickElement = (Element) brickValue;
		String attributeString = null;
		if (brickValue.getNodeName().equals("sprite")) {
			return null;
		}
		if (brickElement != null) {
			NamedNodeMap attributes = brickElement.getAttributes();
			if (attributes != null) {
				Node referenceNode = attributes.getNamedItem("reference");
				if (referenceNode != null) {
					attributeString = referenceNode.getTextContent();

				}
			}
		}
		return attributeString;
	}

}
