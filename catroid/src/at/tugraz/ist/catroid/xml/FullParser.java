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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;

public class FullParser extends DefaultHandler {

	List<Sprite> sprites = new ArrayList<Sprite>();
	List<Script> scripts = new ArrayList<Script>();
	List<CostumeData> costumeList = new ArrayList<CostumeData>();
	Map<String, Object> referencedLists = new HashMap<String, Object>();
	int index = 0;
	XPathFactory xpathFactory = XPathFactory.newInstance();
	List<ForwardReferences> forwardRefs = new ArrayList<ForwardReferences>();

	public Project fullParser(InputStream xmlFile) {
		ObjectCreator projectInit = new ObjectCreator();
		Project parsedProject = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int len;

		try {

			while ((len = xmlFile.read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();

			InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
			InputStream is2 = new ByteArrayInputStream(baos.toByteArray());
			parsedProject = projectInit.reflectionSet(is1);

			List<Sprite> spriteList = this.parseSprites(is2);

			for (int i = 0; i < spriteList.size(); i++) {
				parsedProject.addSprite(spriteList.get(i));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parsedProject;

	}

	public List<Sprite> parseSprites(InputStream fXmlFile) throws ParseException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			Log.i("Sprite parse method begin", "Sprite parsing started");
			NodeList spritesNodes = doc.getElementsByTagName("Content.Sprite");
			Log.i("sprite parsing", "no of sprites: " + spritesNodes.getLength());
			for (int i = 0; i < spritesNodes.getLength(); i++) {
				Log.i("sprite parsing", "current Sprite no " + i);
				Node sprite = spritesNodes.item(i);
				Element spriteElement = (Element) sprite;
				NodeList spriteChildren = spriteElement.getChildNodes();
				String spriteName = null;
				for (int o = 0; o < spriteChildren.getLength(); o++) {
					Node child = spriteChildren.item(o);
					if (child.getNodeType() != Node.TEXT_NODE) {
						String childNodeName = child.getNodeName();
						Log.i("sprite parsing", "current Sprite child node " + childNodeName);
						if (childNodeName.equals("name")) {
							spriteName = child.getChildNodes().item(0).getNodeValue();
							Log.i("sprite parsing", "current Sprite name " + spriteName);
							break;
						}
					}
				}

				Sprite foundSprite = new Sprite(spriteName);
				Log.i("sprite parsing", "Sprite created, costume parsing");
				Node costumeListItem = spriteElement.getElementsByTagName("costumeDataList").item(0);

				if (costumeListItem != null) {
					costumeList = new ArrayList<CostumeData>();
					NodeList costumeNodes = costumeListItem.getChildNodes();
					int costumeIndex = 0;
					for (int m = 0; m < costumeNodes.getLength(); m++) {

						CostumeData foundCostumeData = null;
						Node costumeElement = costumeNodes.item(m);
						if (costumeElement.getNodeType() != Node.TEXT_NODE) {
							Log.i("costume parsing", "current costume no " + m);
							Element cel = (Element) costumeElement;
							Node costumeFileNameNode = cel.getElementsByTagName("fileName").item(0);
							String costumeFileName = null;
							if (costumeFileNameNode != null) {
								costumeFileName = costumeFileNameNode.getChildNodes().item(0).getNodeValue();
								Log.i("costume parsing", "current costume filename " + costumeFileName);
							}
							String costumeName = cel.getElementsByTagName("name").item(0).getChildNodes().item(0)
									.getNodeValue();
							Log.i("costume parsing", "current costume name " + costumeName);
							foundCostumeData = new CostumeData();
							foundCostumeData.setCostumeFilename(costumeFileName);
							foundCostumeData.setCostumeName(costumeName);
							costumeList.add(foundCostumeData);
							String costumeindexString = "";
							if (costumeIndex > 0) {
								int showNo = costumeIndex + 1;
								costumeindexString = "[" + showNo + "]";
							}
							String xp = getElementXpath(cel);
							referencedLists.put(xp, foundCostumeData);
							costumeIndex++;
							Log.i("costume parsing", "costume created");
						}
					}
					Field costumeListField = foundSprite.getClass().getDeclaredField("costumeDataList");
					costumeListField.setAccessible(true);
					costumeListField.set(foundSprite, costumeList);

					Log.i("costume parsing", costumeList.size() + " costumes added");
				}

				Log.i("script parsing", "Script parsing started ");
				Node scriptListItem = spriteElement.getElementsByTagName("scriptList").item(0);
				if (scriptListItem != null) {

					NodeList scriptListNodes = scriptListItem.getChildNodes();

					for (int j = 0; j < scriptListNodes.getLength(); j++) {
						Script foundScript = null;
						Node scriptElement = scriptListNodes.item(j);
						if (scriptElement.getNodeType() != Node.TEXT_NODE) {
							Log.i("script parsing", "parsing a script indexed " + j);
							String scriptName = scriptElement.getNodeName();
							String scriptImplName = scriptName.substring(8);

							Log.i("script parsing", "Script implementation " + scriptImplName);
							foundScript = getScriptObject(scriptImplName, foundSprite);
							Element el = (Element) scriptElement;
							foundScript = getAdditionalScriptInfo(el, foundScript);
							Node brickListNode = el.getElementsByTagName("brickList").item(0);
							if (brickListNode != null) {
								NodeList brickListNodes = brickListNode.getChildNodes();
								Log.i("brick parsing", "Brick parsing started");
								for (int k = 0; k < brickListNodes.getLength(); k++) {
									Brick brickImpleObj = null;
									Node currentBrickNode = brickListNodes.item(k);
									if (currentBrickNode.getNodeType() != Node.TEXT_NODE) {
										String brickName = currentBrickNode.getNodeName();
										Log.i("brick parsing", "current brick name " + brickName);

										Element brickElement = (Element) currentBrickNode;
										NodeList spriteNodesList = brickElement.getElementsByTagName("sprite");
										if (spriteNodesList != null) {
											Log.i("brick parsing", "brick sprite element found");
											Node sni = spriteNodesList.item(0);
											if (sni != null) {
												NamedNodeMap attributes = sni.getAttributes();
												if (attributes != null) {
													String spriteAttr = attributes.getNamedItem("reference")
															.getTextContent();
													Log.i("brick parsing", "Brick sprite attribute " + spriteAttr);
												}
											}
										}
										NodeList brickValueNodes = brickElement.getChildNodes();
										brickImpleObj = getBrickObject(brickName, foundSprite, brickValueNodes,
												brickElement);
										Log.i("brick parsing", "Brick object: " + brickImpleObj.toString() + " added");
										String brickReferenceAttr = getReferenceAttribute(brickElement);

										if (brickReferenceAttr != null) {

											XPath xpath = xpathFactory.newXPath();
											Log.i("brick parsing", "brick reference: " + brickReferenceAttr);
											XPathExpression exp = xpath.compile(brickReferenceAttr);
											Element refList = (Element) exp.evaluate(brickElement, XPathConstants.NODE);

											String brickXPath = getElementXpath(refList);
											ForwardReferences forwardRef = new ForwardReferences(brickImpleObj,
													brickXPath, null);
											forwardRefs.add(forwardRef);
											brickImpleObj = (Brick) referencedLists.get(brickXPath);
											//											if (valueObject != null) {
											//												String brickImplName = brickName.substring(7);
											//												Class brickClass = Class
											//														.forName("at.tugraz.ist.catroid.content.bricks."
											//																+ brickImplName);
											//
											//												Field[] superClassFields = brickClass.getSuperclass()
											//														.getDeclaredFields();
											//												Field[] brickFields = brickClass.getDeclaredFields();
											//												if (superClassFields.length > 0) {
											//													Field[] combined = new Field[superClassFields.length
											//															+ brickFields.length];
											//													System.arraycopy(brickFields, 0, combined, 0, brickFields.length);
											//													System.arraycopy(superClassFields, 0, combined, brickFields.length,
											//															superClassFields.length);
											//													brickFields = combined;
											//												}
											//												for (Field fl : brickFields) {
											//													if (fl.getDeclaringClass().equals(valueObject.getClass())) {
											//														fl.set(brickImpleObj, valueObject);
											//													}
											//												}
											//
											//											}
										}
										String brickXPath = getElementXpath(brickElement);
										referencedLists.put(brickXPath, brickImpleObj);
										foundScript.addBrick(brickImpleObj);

									}

								}
							}
							Log.i("script parsing", "no of bricks added : " + foundScript.getBrickList().size());
							String brickXPath = getElementXpath((Element) scriptElement);
							referencedLists.put(brickXPath, foundScript);
							foundSprite.addScript(foundScript);
							Log.i("script parsing", "script added to Sprite: " + foundSprite.getName());
						}

					}
				}
				Log.i("sprite parsing", "Sprite added");
				String brickXPath = getElementXpath(spriteElement);
				referencedLists.put(brickXPath, foundSprite);
				sprites.add(foundSprite);

				Node soundListItem = spriteElement.getElementsByTagName("soundList").item(0);
				if (soundListItem != null) {
					Log.i("sound parsing", "sound parsing started");
					List<SoundInfo> soundList = new ArrayList<SoundInfo>();
					NodeList soundNodes = soundListItem.getChildNodes();
					Log.i("sound parsing", "no of sound children " + soundNodes.getLength());
					for (int n = 0; n < soundNodes.getLength(); n++) {
						SoundInfo foundSoundInfo = null;
						Node soundElement = soundNodes.item(n);
						if (soundElement.getNodeType() != Node.TEXT_NODE) {
							Log.i("sound parsing", "current sound node:" + n);
							Element cel = (Element) soundElement;
							Node soundFileNameNode = cel.getElementsByTagName("fileName").item(0);
							String soundFileName = null;
							if (soundFileNameNode != null) {
								soundFileName = soundFileNameNode.getChildNodes().item(0).getNodeValue();
								Log.i("sound parsing", "sound fileName " + soundFileName);
							}
							Node soundNameNode = cel.getElementsByTagName("name").item(0);
							String soundName = null;
							if (soundNameNode != null) {
								soundName = soundNameNode.getChildNodes().item(0).getNodeValue();
								Log.i("sound parsing", "sound fileName " + soundName);
							}
							foundSoundInfo = new SoundInfo();
							foundSoundInfo.setSoundFileName(soundFileName);
							foundSoundInfo.setTitle(soundName);
							soundList.add(foundSoundInfo);
						}
					}
					Field soundListField = foundSprite.getClass().getDeclaredField("soundList");
					soundListField.setAccessible(true);
					soundListField.set(foundSprite, soundList);
					Log.i("sound parsing", "sound List added with size: " + soundList.size());
				}
			}

			resolveForwardReferences();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("sprite parsing", "no of sprites returned:" + sprites.size());

		return sprites;

	}

	/**
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * 
	 */
	@SuppressWarnings("unused")
	private void resolveForwardReferences() throws IllegalArgumentException, IllegalAccessException {
		for (ForwardReferences ref : forwardRefs) {
			Field refField = ref.getFieldWithReference();
			if (refField == null) {
				Object objref = ref.getObjectWithReferencedField();
				objref = referencedLists.get(ref.getReferenceString());
			} else {
				Object parentObj = ref.getObjectWithReferencedField();
				Object valueObj = referencedLists.get(ref.getReferenceString());
				refField.set(parentObj, valueObj);
			}
		}

	}

	private Script getAdditionalScriptInfo(Element el, Script foundScript) throws IllegalArgumentException,
			IllegalAccessException, SecurityException, InstantiationException, InvocationTargetException,
			NoSuchMethodException {
		Log.i("script parsing, additionals", "getting additional script info ");
		Field[] scriptClassFields = foundScript.getClass().getDeclaredFields();
		for (Field field : scriptClassFields) {
			boolean isCurrentFieldTransient = Modifier.isTransient(field.getModifiers());

			if (isCurrentFieldTransient) {
				continue;
			}

			String tagName = field.getName();

			NodeList scriptChildren = el.getChildNodes();
			String valueInString = null;
			for (int o = 0; o < scriptChildren.getLength(); o++) {
				Node child = scriptChildren.item(o);
				if (child.getNodeType() != Node.TEXT_NODE) {
					String childNodeName = child.getNodeName();

					if (childNodeName.equals(tagName)) {
						Log.i("script parsing, additionals", "getting additional script info of" + childNodeName);
						valueInString = child.getChildNodes().item(0).getNodeValue();
						if (valueInString != null) {
							Log.i("script parsing, additionals", "value of" + childNodeName + ": " + valueInString);
							Object finalObject = getobjectOfClass(field.getType(), valueInString);
							field.setAccessible(true);
							field.set(foundScript, finalObject);
							Log.i("script parsing, additionals", "additional script info set");

						}
					}
				}
			}
		}

		return foundScript;
	}

	private Brick getBrickObject(String brickName, Sprite foundSprite, NodeList valueNodes, Element brickElement)
			throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException, XPathExpressionException {

		String brickImplName = brickName.substring(7);
		Brick brickObject = null;
		Map<String, Field> brickFieldsToSet = new HashMap<String, Field>();
		Log.i("brick parsing, getBrickObject", "getting brick object of " + brickName);
		Class brickClass = Class.forName("at.tugraz.ist.catroid.content.bricks." + brickImplName);
		brickFieldsToSet = getFieldMap(brickClass);
		brickObject = (Brick) getobjectOfClass(brickClass, "0");
		Log.i("brick parsing, getBrickObject", "brick value nodes:" + valueNodes.getLength());
		for (int l = 0; l < valueNodes.getLength(); l++) {
			Node brickValue = valueNodes.item(l);
			if (brickValue.getNodeType() != Node.TEXT_NODE) {
				String brickvalueName = brickValue.getNodeName();
				Log.i("brick parsing, getBrickObject, value parsing", "brick value name:" + brickvalueName);

				Field valueField = brickFieldsToSet.get(brickvalueName);
				if (valueField != null) {
					valueField.setAccessible(true);
					if (brickvalueName.equals("sprite")) {
						valueField.set(brickObject, foundSprite);
					} else if (brickValue.getChildNodes().getLength() > 1) {
						if (brickvalueName.endsWith("Brick")) {
							char c = (brickvalueName.toUpperCase().charAt(0));
							Character d = c;
							brickvalueName = d.toString().concat(brickvalueName.substring(1));
							String prefix = "Bricks.";
							brickvalueName = prefix.concat(brickvalueName);
							Brick valueBrick = getBrickObject(brickvalueName, foundSprite, brickValue.getChildNodes(),
									(Element) brickValue);
							valueField.set(brickObject, valueBrick);
						} else {
							NodeList valueChildren = brickValue.getChildNodes();
							Map<String, Field> fieldMap = getFieldMap(valueField.getClass());
							Object valueObj = getobjectOfClass(valueField.getClass(), "0");
							for (int m = 0; m < valueChildren.getLength(); m++) {
								Node valueChild = valueChildren.item(m);
								if (valueChild.getNodeType() != Node.TEXT_NODE) {
									String childValueName = valueChild.getNodeName();
									Field valF = fieldMap.get(childValueName);
									if (valF != null) {
										valF.setAccessible(true);
										Node valueChildValue = valueChild.getChildNodes().item(0);
										if (valueChildValue != null) {
											String valStr = valueChildValue.getNodeValue();
											Object valobj = getobjectOfClass(valF.getType(), valStr);
											valF.set(valueObj, valobj);
										}
									}
								}
							}
							valueField.set(brickObject, valueObj);
						}
					} else {
						Log.i("brick parsing, getBrickObject, value parsing", "value field found, type:"
								+ valueField.getType().getCanonicalName());
						Node valueNode = brickValue.getChildNodes().item(0);
						if (valueNode != null) {
							String valueOfValue = valueNode.getNodeValue();
							Log.i("brick parsing, getBrickObject, value parsing", "brick value in string"
									+ valueOfValue);
							Object valueObject = getobjectOfClass(valueField.getType(), valueOfValue);
							valueField.set(brickObject, valueObject);

						}
					}

					String referenceAttr = getReferenceAttribute(brickValue);
					if (referenceAttr != null) {

						Log.i("brick parsing, getBrickObject, value parsing", "brick value name:" + brickvalueName
								+ "reference: " + referenceAttr);
						XPath xpath = xpathFactory.newXPath();
						XPathExpression exp = xpath.compile(referenceAttr);
						Element refList = (Element) exp.evaluate(brickValue, XPathConstants.NODE);
						String xp = getElementXpath(refList);
						ForwardReferences forwardRef = new ForwardReferences(brickObject, xp, valueField);
						forwardRefs.add(forwardRef);
						Object valueObject = referencedLists.get(xp);
						if (valueObject != null) {
							valueField.set(brickObject, valueObject);
						}
					}
				}
			}
		}
		String xp = getElementXpath(brickElement);
		referencedLists.put(xp, brickObject);
		return brickObject;
	}

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
			//if (!(tagName.equals("sprite"))) {
			fieldsToSet.put(tagName, field);
			//}
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
					String refNodeName = referenceNode.getNodeName();

					spriteAttr = referenceNode.getTextContent();
					Log.i("brick parsing", "Brick sprite attribute " + spriteAttr);

				}
			}
		}
		return spriteAttr;
	}

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
			Log.i("brick parsing, getBrickObject, gettingObjNoCons", "param class " + cls.getCanonicalName()
					+ ", new method used");
			Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
			newInstance.setAccessible(true);
			return newInstance.invoke(null, cls, Object.class);
		}

		clsConstructor = cls.getConstructor(String.class);
		obj = clsConstructor.newInstance(val);
		Log.i("brick parsing, getBrickObject, gettingObjNoCons", "param class " + cls.getCanonicalName()
				+ "object returned");
		return obj;

	}

	private Script getScriptObject(String scriptImplName, Sprite foundSprite) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Script scriptObject = null;
		Log.i("script parsing, getScriptObj", "retrieving script object");
		Class scriptClass = Class.forName("at.tugraz.ist.catroid.content." + scriptImplName);
		Constructor scriptConstructor = scriptClass.getConstructor(Sprite.class);
		scriptObject = (Script) scriptConstructor.newInstance(foundSprite);

		return scriptObject;
	}

}
