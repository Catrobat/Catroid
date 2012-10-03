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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class FullParser {

	Map<String, Object> referencedObjects = new HashMap<String, Object>();
	List<ForwardReferences> forwardRefs = new ArrayList<ForwardReferences>();
	ObjectCreator objectGetter = new ObjectCreator();
	CostumeParser costumeParser = new CostumeParser();
	SoundInfoParser soundParser = new SoundInfoParser();
	ScriptParser scriptParser = new ScriptParser();

	public Project fullParser(String xmlFile) throws ParseException {

		Project parsedProject = null;

		try {

			InputStream inputStreamForSprites = NativeAppActivity.getContext().getAssets().open(xmlFile);

			parsedProject = this.parseSpritesWithProject(inputStreamForSprites);
			inputStreamForSprites.close();
			inputStreamForSprites = null;
		} catch (ParseException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ParseException("IO exception in full parser", e);
		} catch (NoSuchFieldException e) {
			throw new ParseException("Field exception, Sound handling", e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new ParseException("Field exception, Sound handling", e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new ParseException("Field exception, Sound handling", e);
		}
		return parsedProject;

	}

	public Project parseSpritesWithProject(InputStream xmlInputStream) throws ParseException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Project parsedProject = null;
		List<Sprite> sprites = new ArrayList<Sprite>();
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(xmlInputStream);
			doc.getDocumentElement().normalize();

			NodeList spriteNodes = doc.getElementsByTagName(CatroidXMLConstants.SPRITE_ELEMENT_NAME);
			for (int i = 0; i < spriteNodes.getLength(); i++) {
				Element spriteElement = (Element) spriteNodes.item(i);
				String spriteName = getSpriteName(spriteElement);
				Sprite foundSprite = new Sprite(spriteName);

				Node costumeListItem = spriteElement
						.getElementsByTagName(CatroidXMLConstants.COSTUME_LIST_ELEMENT_NAME).item(0);
				if (costumeListItem != null) {
					NodeList costumeNodes = costumeListItem.getChildNodes();
					costumeParser.parseCostumeList(costumeNodes, foundSprite, referencedObjects);
				}

				Node scriptListItem = spriteElement.getElementsByTagName(CatroidXMLConstants.SCRIPT_LIST_ELEMENT_NAME)
						.item(0);
				if (scriptListItem != null) {
					NodeList scriptNodes = scriptListItem.getChildNodes();
					scriptParser.parseScripts(scriptNodes, foundSprite, referencedObjects, forwardRefs);
				}

				Node soundListItem = spriteElement.getElementsByTagName(CatroidXMLConstants.SOUND_LIST_ELEMENT_NAME)
						.item(0);
				if (soundListItem != null) {
					NodeList soundNodes = soundListItem.getChildNodes();
					soundParser.parseSoundInfo(soundNodes, foundSprite, referencedObjects, forwardRefs);

				}

				String spriteXpath = ParserUtil.getElementXpath(spriteElement);
				referencedObjects.put(spriteXpath, foundSprite);
				sprites.add(foundSprite);
			}
			References references = new References();
			references.resolveForwardReferences(referencedObjects, forwardRefs);
			parsedProject = getProjectObject(doc, sprites);
			doc = null;
			xmlInputStream.close();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ParseException(e);
		}

		setCheckSumsOnProjectManager(parsedProject);
		return parsedProject;

	}

	private void setCheckSumsOnProjectManager(Project project) {
		FileChecksumContainer checkSumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		File projectImageDirectory = new File(Utils.buildProjectPath(project.getName()) + "/images");
		File projectSoundDirectory = new File(Utils.buildProjectPath(project.getName()) + "/sounds");
		File[] imageFiles = projectImageDirectory.listFiles();
		File[] soundFiles = projectSoundDirectory.listFiles();

		if (imageFiles != null) {
			for (File projectFile : imageFiles) {
				String checksums = Utils.md5Checksum(projectFile);
				if (!(projectFile.getName().equals(".nomedia"))) {
					checkSumContainer.addChecksum(checksums, projectFile.getAbsolutePath());
				}
			}
		}
		if (soundFiles != null) {
			for (File projectFile : soundFiles) {
				String checksums = Utils.md5Checksum(projectFile);
				if (!(projectFile.getName().equals(".nomedia"))) {
					checkSumContainer.addChecksum(checksums, projectFile.getAbsolutePath());
				}
			}
		}
	}

	private String getSpriteName(Element spriteElement) {
		String spriteName = "";
		NodeList spriteChildren = spriteElement.getChildNodes();
		for (int i = 0; i < spriteChildren.getLength(); i++) {
			if (spriteChildren.item(i).getNodeType() != Node.TEXT_NODE) {
				Element childElement = (Element) spriteChildren.item(i);
				if (childElement.getNodeName().equals(CatroidXMLConstants.SPRITE_NAME)) {
					spriteName = childElement.getChildNodes().item(0).getNodeValue();
					break;
				}
			}
		}
		return spriteName;
	}

	private Project getProjectObject(Document doc, List<Sprite> sprites2) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, ParseException {
		Node rootNode = doc.getDocumentElement();
		String nameOfRoot = rootNode.getNodeName();
		Class<?> projectClass = null;
		if (!nameOfRoot.equals(CatroidXMLConstants.PROJECT_ELEMENT_NAME)) {
			String classNameOriginal = nameOfRoot.replace("_-", "$");
			try {
				projectClass = Class.forName(classNameOriginal);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new ParseException("project class not found");
			}
		} else {
			projectClass = Project.class;
		}

		Project newProject = (Project) objectGetter.getobjectOfClass(projectClass, "0");

		Map<String, Field> projectFieldsToSet = objectGetter.getFieldMap(projectClass);

		//NodeList projectNodes = doc.getElementsByTagName(nameOfRoot);
		Element headerElement = (Element) doc.getElementsByTagName("Header").item(0);
		NodeList projectHeaderChildren = headerElement.getChildNodes();
		for (int i = 0; i < projectHeaderChildren.getLength(); i++) {
			if (projectHeaderChildren.item(i).getNodeType() != Node.TEXT_NODE) {
				Element projectChildElement = (Element) projectHeaderChildren.item(i);
				Field projectField = projectFieldsToSet.get(projectChildElement.getNodeName());
				if (projectChildElement.getNodeName().equals("SpriteList")) {
					objectGetter.setFieldOfObject(projectField, newProject, sprites2);
					continue;
				}

				if (projectField != null) {
					NodeList childNodes = projectChildElement.getChildNodes();
					if (childNodes.getLength() > 0) {
						String valueInString = childNodes.item(0).getNodeValue();
						Object valueObject = objectGetter.getobjectOfClass(projectField.getType(), valueInString);
						objectGetter.setFieldOfObject(projectField, newProject, valueObject);
					} else {
						objectGetter.setFieldOfObject(projectField, newProject, null);
					}
				}
			}
		}
		objectGetter.setFieldOfObject(projectFieldsToSet.get("SpriteList"), newProject, sprites2);
		return newProject;
	}

}
