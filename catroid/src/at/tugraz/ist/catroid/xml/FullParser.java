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
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;

public class FullParser extends DefaultHandler {

	private Map<String, TagData> parsedStrings = new HashMap<String, TagData>();
	private String tempVal;
	List<TagData> tagsCollection = new ArrayList<TagData>();
	TagData tag;
	Sprite spriteInParsing;
	Script scriptInParsing;
	List<Sprite> sprites = new ArrayList<Sprite>();
	List<Script> scripts = new ArrayList<Script>();
	Map<String, String> brickValues = new HashMap<String, String>();
	boolean newBrick = false;
	Stack<TagData> parsingTagsstack = new Stack<TagData>();
	int index = 0;
	List<TagData> parsedTagsList = new ArrayList<TagData>();

	public Project fullParser(InputStream xmlFile) {
		ObjectCreator projectInit = new ObjectCreator();
		Project parsedProject = null;

		try {

			List<Sprite> spriteList = this.parseSprites(xmlFile);
			parsedProject = projectInit.reflectionSet(xmlFile);

			for (int i = 0; i < spriteList.size(); i++) {
				parsedProject.addSprite(spriteList.get(i));
			}
		} catch (ParseException e) {
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

			NodeList spritesNodes = doc.getElementsByTagName("Content.Sprite");
			for (int i = 0; i < spritesNodes.getLength(); i++) {
				Node sprite = spritesNodes.item(i);
				Element spriteElement = (Element) sprite;
				String spriteName = spriteElement.getElementsByTagName("name").item(0).getChildNodes().item(0)
						.getNodeValue();
				Sprite foundSprite = new Sprite(spriteName);

				Node scriptListItem = spriteElement.getElementsByTagName("scriptList").item(0);
				if (scriptListItem != null) {

					NodeList scriptListNodes = scriptListItem.getChildNodes();

					for (int j = 0; j < scriptListNodes.getLength(); j++) {
						Script foundScript = null;
						Node scriptElement = scriptListNodes.item(j);
						if (scriptElement.getNodeType() != Node.TEXT_NODE) {
							String scriptName = scriptElement.getNodeName();
							String scriptImplName = scriptName.substring(8);

							System.out.println(scriptImplName);
							foundScript = getScriptObject(scriptImplName, foundSprite);

							Element el = (Element) scriptElement;
							Node brickListNode = el.getElementsByTagName("brickList").item(0);
							if (brickListNode != null) {
								NodeList brickListNodes = brickListNode.getChildNodes();

								for (int k = 0; k < brickListNodes.getLength(); k++) {
									Brick brickImpleObj = null;
									Node currentBrickNode = brickListNodes.item(k);
									if (currentBrickNode.getNodeType() != Node.TEXT_NODE) {
										String brickName = currentBrickNode.getNodeName();
										System.out.println(brickName);

										Element brickElement = (Element) currentBrickNode;
										NodeList spriteNodesList = brickElement.getElementsByTagName("sprite");
										if (spriteNodesList != null) {
											Node sni = spriteNodesList.item(0);
											if (sni != null) {
												NamedNodeMap attributes = sni.getAttributes();
												if (attributes != null) {
													String spriteAttr = attributes.getNamedItem("reference")
															.getTextContent();
												}
											}
										}
										NodeList brickValueNodes = brickElement.getChildNodes();
										brickImpleObj = getBrickObject(brickName, foundSprite, brickValueNodes);
										foundScript.addBrick(brickImpleObj);
									}

								}
							}
						}
						foundSprite.addScript(foundScript);

					}
				}

				sprites.add(foundSprite);
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//		parsedStrings = new HashMap<String, TagData>();
		//		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		//
		//		try {
		//			SAXParser parser = parserFactory.newSAXParser();
		//			parser.parse(fXmlFile, this);
		//
		//		} catch (ParserConfigurationException e) {
		//			Log.e("SimpleParser.saxparser", "parserConfiguration exception");
		//			throw new ParseException(e);
		//		} catch (SAXException e) {
		//			Log.e("SimpleParser.saxparser", "parserConfiguration exception");
		//			throw new ParseException(e);
		//		} catch (IOException e) {
		//			Log.e("SimpleParser.saxparser", "IO exception");
		//			throw new ParseException(e);
		//		}

		return sprites;

	}

	private Brick getBrickObject(String brickName, Sprite foundSprite, NodeList valueNodes) {
		String brickImplName = brickName.substring(7);
		Brick brickObject = null;
		Map<String, Field> brickFieldsToSet = new HashMap<String, Field>();
		try {
			Class brickClass = Class.forName("at.tugraz.ist.catroid.content.bricks." + brickImplName);

			Field[] brickFields = brickClass.getDeclaredFields();
			for (Field field : brickFields) {
				boolean isCurrentFieldTransient = Modifier.isTransient(field.getModifiers());

				if (isCurrentFieldTransient) {
					continue;
				}

				String tagName = field.getName();
				if (tagName != "sprite") {
					brickFieldsToSet.put(tagName, field);
				}
			}

			Constructor[] brickConstructorsArray = brickClass.getConstructors();
			for (Constructor constructor : brickConstructorsArray) {
				Class[] constructorParams = constructor.getParameterTypes();

				Object argList[] = new Object[constructorParams.length];
				int index = 0;
				for (Class cls : constructorParams) {

					if (cls.equals(Sprite.class)) {
						Object spriteOb = foundSprite;
						argList[index] = spriteOb;
					} else if (cls.equals(double.class)) {
						Object dblObj = new Double(0.0);
						argList[index] = dblObj;
					}

					index++;
				}
				brickObject = (Brick) constructor.newInstance(argList);

			}

			for (int l = 0; l < valueNodes.getLength(); l++) {
				Node brickValue = valueNodes.item(l);
				if (brickValue.getNodeType() != Node.TEXT_NODE) {
					String brickvalueName = brickValue.getNodeName();
					System.out.println(brickName);
					Field valueField = brickFieldsToSet.get(brickvalueName);
					if (valueField != null) {
						valueField.setAccessible(true);
						Node valueNode = brickValue.getChildNodes().item(0);
						if (valueNode != null) {
							String valueOfValue = valueNode.getNodeValue();
							Object valueObject = setFieldValue(valueField, valueOfValue);
							valueField.set(brickObject, valueObject);
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return brickObject;
	}

	private Object setFieldValue(Field valueField, String valueOfValue) {
		ObjectCreator objectCreator = new ObjectCreator();
		return objectCreator.getObjectWithValue(valueField, valueOfValue);

	}

	private Script getScriptObject(String scriptImplName, Sprite foundSprite) {
		Script scriptObject = null;
		try {
			Class scriptClass = Class.forName("at.tugraz.ist.catroid.content." + scriptImplName);
			Constructor scriptConstructor = scriptClass.getConstructor(Sprite.class);
			scriptObject = (Script) scriptConstructor.newInstance(foundSprite);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return scriptObject;
	}

	@Override
	public void startElement(String uri, String localName, String tagName, org.xml.sax.Attributes attributes)
			throws SAXException {
		//		if (tagName.startsWith("Bricks")) {
		//			brickValues.clear();
		//			newBrick = true;
		//		}
		//
		//		if (tagName.equals("Content.Sprite")) {
		//			spriteInParsing = new Sprite("");
		//
		//		}
		//
		//		if (tagName.equals("sprite")) {
		//			tag = new TagData(tagName, attributes);
		//			System.out.println("start el attr" + tag.attributes);
		//		}

		String lname = localName;
		tag = new TagData(tagName, attributes);
		parsingTagsstack.push(tag);
		//		if (!parsedStrings.containsKey(tagName)) {
		//			parsedStrings.put(tagName, tag);
		//		} else {
		//			parsedStrings.put(tagName + index++, tag);
		//		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch, start, length);

	}

	@Override
	public void endElement(String uri, String localName, String tagName) throws SAXException {
		//		if (tagName.startsWith("sprite")) {
		//			System.out.println("endEl temName" + tempVal);
		//		}
		//		if (tagName.equals("name")) {
		//			spriteInParsing.setName(tempVal);
		//		}
		//		if (tagName.equals("Content.Sprite")) {
		//
		//			sprites.add(spriteInParsing);
		//			spriteInParsing = null;
		//
		//		}
		//		if (tagName.equals("Content.StartScript")) {
		//			scriptInParsing = new StartScript(spriteInParsing);
		//			spriteInParsing.addScript(scriptInParsing);
		//			scriptInParsing = null;
		//
		//		}
		//		try {
		//			Class brickClass= Class.forName(tagName);
		//			Brick brick= (Brick) brickClass.newInstance();
		//			Field[] brickClassField = brickClass.getFields();
		//			
		//			for (Field fieldinProject : brickClassField) {
		//				boolean isCurrentFieldTransient = Modifier.isTransient(fieldinProject.getModifiers());
		//
		//				if (isCurrentFieldTransient) {
		//					continue;
		//				}
		//				String tagName = extractTagName(fieldinProject);
		//
		//				String valueInString = headerValues.get(tagName);
		//
		//				if (valueInString != null) {
		//					Object finalObject = getObjectWithValue(fieldinProject, valueInString);
		//					fieldinProject.setAccessible(true);
		//					fieldinProject.set(project, finalObject);
		//				}
		//			}
		//			
		//		} catch (ClassNotFoundException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		} catch (IllegalAccessException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		} catch (InstantiationException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		TagData topOfStacktag = parsingTagsstack.peek();
		if (tagName.equals(topOfStacktag.tagName)) {
			TagData addingtag = parsingTagsstack.pop();
			addingtag.value = tempVal;
			parsedTagsList.add(addingtag);
		}

		//		parsedStrings.put(tagName, tag);
		//		tag.value = tempVal;
		//		tagsCollection.add(tag);
		//		int tagsno= tagsCollection.size();
		//		Log.i("tag amount", ""+tagsno);

	}

	//	public String getvalueof(HeaderTags tag, InputStream XMLFile) throws ParseException {
	//		Map<String, TagData> parsedValues = this.parseHeader(XMLFile);
	//		return parsedValues.get(tag.getXmlTagString());
	//
	//	}

}
