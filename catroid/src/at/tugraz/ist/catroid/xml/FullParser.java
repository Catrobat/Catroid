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
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.stage.NativeAppActivity;

public class FullParser extends DefaultHandler {

	List<Sprite> sprites = new ArrayList<Sprite>();
	List<Script> scripts = new ArrayList<Script>();
	List<CostumeData> costumeList = new ArrayList<CostumeData>();
	Map<String, Object> referencedObjects = new HashMap<String, Object>();
	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();
	List<ForwardReferences> forwardRefs = new ArrayList<ForwardReferences>();

	public Project fullParser(String xmlFile) throws ParseException {
		ObjectCreator projectCreator = new ObjectCreator();
		Project parsedProject = null;

		try {

			InputStream inputStreamForHeaders = NativeAppActivity.getContext().getAssets().open(xmlFile);
			parsedProject = projectCreator.reflectionSet(inputStreamForHeaders);
			InputStream inputStreamForSprites = NativeAppActivity.getContext().getAssets().open(xmlFile);
			List<Sprite> spriteList = this.parseSprites(inputStreamForSprites);

			for (int i = 0; i < spriteList.size(); i++) {
				parsedProject.addSprite(spriteList.get(i));
			}
		} catch (ParseException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParseException("IO exception in full parser", e);
		}
		return parsedProject;

	}

	public List<Sprite> parseSprites(InputStream xnlInputStream) throws ParseException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;

		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xnlInputStream);
			doc.getDocumentElement().normalize();

			Log.i("Sprite parse method begin", "Sprite parsing started");
			NodeList spriteNodes = doc.getElementsByTagName("Content.Sprite");
			//Log.i("sprite parsing", "no of sprites: " + spriteNodes.getLength());
			for (int i = 0; i < spriteNodes.getLength(); i++) {
				final Element spriteElement = (Element) spriteNodes.item(i);

				Log.i("sprite parsing", "current Sprite no " + i);

				String spriteName = spriteElement.getElementsByTagName("name").item(0).getChildNodes().item(0)
						.getNodeValue();
				//Log.i("sprite parsing", "current Sprite name " + spriteName);
				Sprite foundSprite = new Sprite(spriteName);

				Log.i("sprite parsing", "Sprite created, costume parsing");

				Node costumeListItem = spriteElement.getElementsByTagName("costumeDataList").item(0);
				if (costumeListItem != null) {
					costumeList = new ArrayList<CostumeData>();
					NodeList costumeNodes = costumeListItem.getChildNodes();
					costumeList = parseCostumeList(costumeNodes);
					Field costumeListField = foundSprite.getClass().getDeclaredField("costumeDataList");
					costumeListField.setAccessible(true);
					costumeListField.set(foundSprite, costumeList);

					//Log.i("costume parsing", costumeList.size() + " costumes added");
				}

				Log.i("script parsing", "Script parsing started ");
				Node scriptListItem = spriteElement.getElementsByTagName("scriptList").item(0);
				if (scriptListItem != null) {
					NodeList scriptListNodes = scriptListItem.getChildNodes();
					for (int j = 0; j < scriptListNodes.getLength(); j++) {
						Script foundScript = null;
						Node scriptNode = scriptListNodes.item(j);
						if (scriptNode.getNodeType() != Node.TEXT_NODE) {
							//Log.i("script parsing", "parsing a script indexed " + j);
							Element scriptElement = (Element) scriptNode;
							foundScript = getpopulatedScript(scriptElement, foundSprite);
							Node brickListNode = scriptElement.getElementsByTagName("brickList").item(0);
							if (brickListNode != null) {
								NodeList brickNodes = brickListNode.getChildNodes();
								//	Log.i("brick parsing", "Brick parsing started");
								for (int k = 0; k < brickNodes.getLength(); k++) {
									Node currentBrickNode = brickNodes.item(k);
									if (currentBrickNode.getNodeType() != Node.TEXT_NODE) {
										Brick foundBrickObj = null;
										Element brickElement = (Element) currentBrickNode;
										String brickReferenceAttr = getReferenceAttribute(brickElement);
										if (brickReferenceAttr != null) {

											//	Log.i("brick parsing", "brick reference: " + brickReferenceAttr);
											XPathExpression exp = xpath.compile(brickReferenceAttr);
											Element referencedElement = (Element) exp.evaluate(brickElement,
													XPathConstants.NODE);
											String brickXPathFromRoot = getElementXpath(referencedElement);
											foundBrickObj = (Brick) referencedObjects.get(brickXPathFromRoot);
											if (foundBrickObj == null) {
												ForwardReferences forwardRef = new ForwardReferences(foundBrickObj,
														brickXPathFromRoot, null);
												forwardRefs.add(forwardRef);
											}

										} else {

											String brickName = currentBrickNode.getNodeName();
											//	Log.i("brick parsing", "current brick name " + brickName);
											NodeList brickValueNodes = brickElement.getChildNodes();
											foundBrickObj = getBrickObject(brickName, foundSprite, brickValueNodes,
													brickElement);
										}
										//	Log.i("brick parsing", "Brick object: " + foundBrickObj.toString() + " added");
										String brickXPath = getElementXpath(brickElement);
										referencedObjects.put(brickXPath, foundBrickObj);
										foundScript.addBrick(foundBrickObj);

									}

								}
							}
							//	Log.i("script parsing", "no of bricks added : " + foundScript.getBrickList().size());
							String brickXPath = getElementXpath((Element) scriptNode);
							referencedObjects.put(brickXPath, foundScript);
							foundSprite.addScript(foundScript);
							//	Log.i("script parsing", "script added to Sprite: " + foundSprite.getName());
						}

					}
				}

				Node soundListItem = spriteElement.getElementsByTagName("soundList").item(0);
				if (soundListItem != null) {
					//	Log.i("sound parsing", "sound parsing started");
					List<SoundInfo> soundList = new ArrayList<SoundInfo>();
					NodeList soundNodes = soundListItem.getChildNodes();
					//	Log.i("sound parsing", "no of sound children " + soundNodes.getLength());
					for (int n = 0; n < soundNodes.getLength(); n++) {
						SoundInfo foundSoundInfo = null;
						Node soundElement = soundNodes.item(n);
						if (soundElement.getNodeType() != Node.TEXT_NODE) {
							String soundRef = getReferenceAttribute(soundElement);
							if (soundRef != null) {
								foundSoundInfo = (SoundInfo) resolveReference(soundRef, soundElement);

								if (foundSoundInfo == null) {
									foundSoundInfo = (SoundInfo) getobjectOfClass(SoundInfo.class, "0");
									XPathExpression exp = xpath.compile(soundRef);
									Element refList = (Element) exp.evaluate(soundElement, XPathConstants.NODE);
									String xp = getElementXpath(refList);
									ForwardReferences forwardRef = new ForwardReferences(foundSoundInfo, xp, null);
									forwardRefs.add(forwardRef);
								} else {
									soundList.add(foundSoundInfo);
									String soundInfoXPath = getElementXpath((Element) soundElement);
									referencedObjects.put(soundInfoXPath, foundSoundInfo);
								}
							} else {
								//Log.i("sound parsing", "current sound node:" + n);
								Element cel = (Element) soundElement;
								Node soundFileNameNode = cel.getElementsByTagName("fileName").item(0);
								String soundFileName = null;
								if (soundFileNameNode != null) {
									soundFileName = soundFileNameNode.getChildNodes().item(0).getNodeValue();
									//Log.i("sound parsing", "sound fileName " + soundFileName);
								}
								Node soundNameNode = cel.getElementsByTagName("name").item(0);
								String soundName = null;
								if (soundNameNode != null) {
									soundName = soundNameNode.getChildNodes().item(0).getNodeValue();
									//Log.i("sound parsing", "sound fileName " + soundName);
								}
								foundSoundInfo = new SoundInfo();
								foundSoundInfo.setSoundFileName(soundFileName);
								foundSoundInfo.setTitle(soundName);
								soundList.add(foundSoundInfo);
								String soundInfoXPath = getElementXpath(cel);
								referencedObjects.put(soundInfoXPath, foundSoundInfo);
							}
						}
					}
					Field soundListField = foundSprite.getClass().getDeclaredField("soundList");
					soundListField.setAccessible(true);
					soundListField.set(foundSprite, soundList);
					//	Log.i("sound parsing", "sound List added with size: " + soundList.size());
				}

				//	Log.i("sprite parsing", "Sprite added");
				String brickXPath = getElementXpath(spriteElement);
				referencedObjects.put(brickXPath, foundSprite);
				sprites.add(foundSprite);
			}

			resolveForwardReferences();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ParseException(e);
		}
		//Log.i("sprite parsing", "no of sprites returned:" + sprites.size());

		return sprites;

	}

	private Object resolveReference(String refString, Node element) throws XPathExpressionException {
		XPathExpression exp = xpath.compile(refString);
		Element refList = (Element) exp.evaluate(element, XPathConstants.NODE);
		String xp = getElementXpath(refList);
		Object object = referencedObjects.get(xp);

		if (object != null) {
			return object;
		}
		return null;
	}

	@SuppressWarnings("unused")
	private void resolveForwardReferences() throws IllegalArgumentException, IllegalAccessException {
		for (ForwardReferences ref : forwardRefs) {
			Field refField = ref.getFieldWithReference();
			if (refField == null) {
				Object objref = ref.getObjectWithReferencedField();
				objref = referencedObjects.get(ref.getReferenceString());
			} else {
				Object parentObj = ref.getObjectWithReferencedField();
				Object valueObj = referencedObjects.get(ref.getReferenceString());
				refField.set(parentObj, valueObj);
			}
		}

	}

	@SuppressWarnings("rawtypes")
	private Script getpopulatedScript(Element el, Sprite sprite) throws IllegalArgumentException,
			IllegalAccessException, SecurityException, InstantiationException, InvocationTargetException,
			NoSuchMethodException, ClassNotFoundException {
		String scriptClassName = el.getNodeName().substring(8);

		Class scriptClass = Class.forName("at.tugraz.ist.catroid.content." + scriptClassName);
		Script newScript = getScriptObject(scriptClassName, sprite);

		Map<String, Field> scriptClassFieldMap = getFieldMap(scriptClass);
		NodeList scriptChildren = el.getChildNodes();
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

					Object fieldValue = getobjectOfClass(scriptClassField.getType(), valueInString);
					scriptClassField.setAccessible(true);
					scriptClassField.set(newScript, fieldValue);

					//Log.i("script parsing, additionals", "additional script info set");

				}

			}

		}

		return newScript;
	}

	@SuppressWarnings("rawtypes")
	private Brick getBrickObject(String brickName, Sprite foundSprite, NodeList valueNodes, Element brickElement)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException, XPathExpressionException {

		String brickImplName = brickName.substring(7);
		Brick brickObject = null;
		//Log.i("brick parsing, getBrickObject", "getting brick object of " + brickName);
		Class brickClass = Class.forName("at.tugraz.ist.catroid.content.bricks." + brickImplName);
		Map<String, Field> brickFieldsToSet = getFieldMap(brickClass);
		brickObject = (Brick) getobjectOfClass(brickClass, "0");
		//	Log.i("brick parsing, getBrickObject", "brick value nodes:" + valueNodes.getLength());
		for (int l = 0; l < valueNodes.getLength(); l++) {
			Node brickValue = valueNodes.item(l);

			if (brickValue.getNodeType() == Node.TEXT_NODE) {
				continue;
			}
			String brickvalueName = brickValue.getNodeName();
			//	Log.i("brick parsing, getBrickObject, value parsing", "brick value name:" + brickvalueName);

			Field valueField = brickFieldsToSet.get(brickvalueName);
			if (valueField != null) {
				valueField.setAccessible(true);

				if (brickvalueName.equals("sprite")) {
					valueField.set(brickObject, foundSprite);
					continue;
				}
				String referenceAttribute = getReferenceAttribute(brickValue);
				if (referenceAttribute != null) {

					//			Log.i("brick parsing, getBrickObject, value parsing", "brick value name:" + brickvalueName
					//			+ "reference: " + referenceAttribute);
					XPathExpression exp = xpath.compile(referenceAttribute);
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
						Object valueObj = getobjectOfClass(valueField.getType(), "0");
						getValueObject(valueObj, brickValue, fieldMap);

						valueField.set(brickObject, valueObj);
						String childBrickXPath = getElementXpath((Element) brickValue);
						referencedObjects.put(childBrickXPath, valueObj);
					}
				} else {
					//		Log.i("brick parsing, getBrickObject, value parsing", "value field found, type:"
					//				+ valueField.getType().getCanonicalName());
					Node valueNode = brickValue.getChildNodes().item(0);
					if (valueNode != null) {
						String valueOfValue = valueNode.getNodeValue();
						//			Log.i("brick parsing, getBrickObject, value parsing", "brick value in string" + valueOfValue);
						Object valueObject = getobjectOfClass(valueField.getType(), valueOfValue);
						valueField.set(brickObject, valueObject);

					}
				}

			}

		}
		String xp = getElementXpath(brickElement);
		referencedObjects.put(xp, brickObject);
		return brickObject;
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
							Object valueObj = getobjectOfClass(fieldWithNodeName.getType(), "0");
							getValueObject(valueObj, child, fieldMap);
							String childXPath = getElementXpath((Element) node);
							referencedObjects.put(childXPath, valueObj);
						} else {
							Node valueChildValue = child.getChildNodes().item(0);
							if (valueChildValue != null) {
								String valStr = valueChildValue.getNodeValue();
								Object valobj = getobjectOfClass(fieldWithNodeName.getType(), valStr);
								fieldWithNodeName.set(nodeObj, valobj);
							}
						}

					}

				}
			}
		}

	}

	private List<CostumeData> parseCostumeList(NodeList costumeNodes) {

		for (int m = 0; m < costumeNodes.getLength(); m++) {
			CostumeData foundCostumeData = null;
			if (costumeNodes.item(m).getNodeType() != Node.TEXT_NODE) {
				//Log.i("costume parsing", "current costume no " + m);
				Element costumeElement = (Element) costumeNodes.item(m);
				String costumeFileName = null;
				Node costumeFileNameNode = costumeElement.getElementsByTagName("fileName").item(0);
				if (costumeFileNameNode != null) {
					costumeFileName = costumeFileNameNode.getChildNodes().item(0).getNodeValue();
					//Log.i("costume parsing", "current costume filename " + costumeFileName);
				}
				String costumeName = costumeElement.getElementsByTagName("name").item(0).getChildNodes().item(0)
						.getNodeValue();
				//	Log.i("costume parsing", "current costume name " + costumeName);
				foundCostumeData = new CostumeData();
				foundCostumeData.setCostumeFilename(costumeFileName);
				foundCostumeData.setCostumeName(costumeName);
				costumeList.add(foundCostumeData);
				String xp = getElementXpath(costumeElement);
				referencedObjects.put(xp, foundCostumeData);
				//	Log.i("costume parsing", "costume created");
			}
		}
		return costumeList;
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

				if (idx >= 1) {
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
		String spriteAttr = null;
		if (brickValue.getNodeName().equals("sprite")) {
			return null;
		}
		if (brickElement != null) {
			NamedNodeMap attributes = brickElement.getAttributes();
			if (attributes != null) {
				Node referenceNode = attributes.getNamedItem("reference");
				if (referenceNode != null) {
					spriteAttr = referenceNode.getTextContent();
					//	Log.i("brick parsing", "Brick sprite attribute " + spriteAttr);

				}
			}
		}
		return spriteAttr;
	}

	@SuppressWarnings("rawtypes")
	private Object getobjectOfClass(Class cls, String val) throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Constructor clsConstructor = null;
		Object obj = null;
		if (cls == int.class) {
			cls = Integer.class;
		} else if (cls == float.class) {
			cls = Float.class;
		} else if (cls == double.class) {
			cls = Double.class;
		} else if (cls == boolean.class) {
			cls = Boolean.class;
		} else if (cls == byte.class) {
			cls = Byte.class;
		} else if (cls == short.class) {
			cls = Short.class;
		} else if (cls == long.class) {
			cls = Long.class;
		} else if (cls == char.class) {
			cls = Character.class;
			obj = cls.getConstructor(char.class).newInstance(val.charAt(0));
			return obj;
		} else if (cls == String.class) {
			return new String(val);
		} else {
			//			Log.i("brick parsing, getBrickObject, gettingObjNoCons", "param class " + cls.getCanonicalName()
			//					+ ", new method used");
			Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
			newInstance.setAccessible(true);
			return newInstance.invoke(null, cls, Object.class);
		}

		clsConstructor = cls.getConstructor(String.class);
		obj = clsConstructor.newInstance(val);
		//		Log.i("brick parsing, getBrickObject, gettingObjNoCons", "param class " + cls.getCanonicalName()
		//				+ "object returned");
		return obj;

	}

	@SuppressWarnings("rawtypes")
	private Script getScriptObject(String scriptImplName, Sprite foundSprite) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Script scriptObject = null;
		//	Log.i("script parsing, getScriptObj", "retrieving script object");
		Class scriptClass = Class.forName("at.tugraz.ist.catroid.content." + scriptImplName);
		Constructor scriptConstructor = scriptClass.getConstructor(Sprite.class);
		if (scriptConstructor == null) {
			return (Script) getobjectOfClass(scriptClass, "0");
		}
		scriptObject = (Script) scriptConstructor.newInstance(foundSprite);

		return scriptObject;
	}

}
