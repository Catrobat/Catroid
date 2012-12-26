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

import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.*;

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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FileChecksumContainer;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.stage.NativeAppActivity;
import org.catrobat.catroid.utils.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FullParser {

	private Map<String, Object> referencedObjects = new HashMap<String, Object>();
	private List<ForwardReference> forwardReferences = new ArrayList<ForwardReference>();
	private ObjectCreator objectGetter = new ObjectCreator();
	private CostumeParser costumeParser = new CostumeParser();
	private SoundInfoParser soundParser = new SoundInfoParser();
	private ScriptParser scriptParser = new ScriptParser();

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
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new ParseException("Field exception, Sound handling", e);
		}
		return parsedProject;

	}

	public Project parseSpritesWithProject(InputStream xmlInputStream) throws ParseException {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilderBuilder;
		Project parsedProject = null;
		List<Sprite> sprites = new ArrayList<Sprite>();
		try {
			documentBuilderBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilderBuilder.parse(xmlInputStream);
			document.getDocumentElement().normalize();

			NodeList spriteNodes = document.getElementsByTagName(SPRITE_ELEMENT_NAME);
			for (int i = 0; i < spriteNodes.getLength(); i++) {
				Element spriteElement = (Element) spriteNodes.item(i);
				String spriteName = getSpriteName(spriteElement);
				Sprite foundSprite = new Sprite(spriteName);

				Node costumeListItem = spriteElement.getElementsByTagName(COSTUME_LIST_ELEMENT_NAME).item(0);
				if (costumeListItem != null) {
					NodeList costumeNodes = costumeListItem.getChildNodes();
					costumeParser.parseCostumeList(costumeNodes, foundSprite, referencedObjects);
				}

				Node scriptListItem = spriteElement.getElementsByTagName(SCRIPT_LIST_ELEMENT_NAME).item(0);
				if (scriptListItem != null) {
					NodeList scriptNodes = scriptListItem.getChildNodes();
					scriptParser.parseScripts(scriptNodes, foundSprite, referencedObjects, forwardReferences);
				}

				Node soundListItem = spriteElement.getElementsByTagName(SOUND_LIST_ELEMENT_NAME).item(0);
				if (soundListItem != null) {
					NodeList soundNodes = soundListItem.getChildNodes();
					soundParser.parseSoundInfo(soundNodes, foundSprite, referencedObjects, forwardReferences);
				}

				String spriteXPath = ParserUtil.getElementXPath(spriteElement);
				referencedObjects.put(spriteXPath, foundSprite);
				sprites.add(foundSprite);
			}
			References references = new References();
			references.resolveForwardReferences(referencedObjects, forwardReferences);
			parsedProject = getProjectObject(document, sprites);
			document = null;
			xmlInputStream.close();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ParseException(e);
		}

		setChecksumsOnProjectManager(parsedProject);
		return parsedProject;
	}

	private void setChecksumsOnProjectManager(Project project) {
		FileChecksumContainer checksumContainer = ProjectManager.getInstance().getFileChecksumContainer();
		File projectImageDirectory = new File(Utils.buildProjectPath(project.getName()) + "/"
				+ Constants.IMAGE_DIRECTORY);
		File projectSoundDirectory = new File(Utils.buildProjectPath(project.getName()) + "/"
				+ Constants.SOUND_DIRECTORY);
		File[] imageFiles = projectImageDirectory.listFiles();
		File[] soundFiles = projectSoundDirectory.listFiles();

		if (imageFiles != null) {
			for (File projectFile : imageFiles) {
				String checksums = Utils.md5Checksum(projectFile);
				if (!(projectFile.getName().equals(Constants.NO_MEDIA_FILE))) {
					checksumContainer.addChecksum(checksums, projectFile.getAbsolutePath());
				}
			}
		}
		if (soundFiles != null) {
			for (File projectFile : soundFiles) {
				String checksums = Utils.md5Checksum(projectFile);
				if (!(projectFile.getName().equals(Constants.NO_MEDIA_FILE))) {
					checksumContainer.addChecksum(checksums, projectFile.getAbsolutePath());
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
				if (childElement.getNodeName().equals(SPRITE_NAME)) {
					spriteName = childElement.getChildNodes().item(0).getNodeValue();
					break;
				}
			}
		}
		return spriteName;
	}

	private Project getProjectObject(Document document, List<Sprite> sprites) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException, InvocationTargetException, ParseException {
		Node rootNode = document.getDocumentElement();
		String nameOfRoot = rootNode.getNodeName();
		Class<?> projectClass = null;
		if (!nameOfRoot.equals(PROJECT_ELEMENT_NAME)) {
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

		Project newProject = (Project) objectGetter.getObjectOfClass(projectClass, "0");

		Map<String, Field> projectFieldsToSet = objectGetter.getFieldMap(projectClass);

		Element headerElement = (Element) document.getElementsByTagName(PROJECT_HEADER_NAME).item(0);
		NodeList projectHeaderChildren = headerElement.getChildNodes();
		for (int i = 0; i < projectHeaderChildren.getLength(); i++) {
			if (projectHeaderChildren.item(i).getNodeType() != Node.TEXT_NODE) {
				Element projectChildElement = (Element) projectHeaderChildren.item(i);
				Field projectField = projectFieldsToSet.get(projectChildElement.getNodeName());
				if (projectChildElement.getNodeName().equals(SPRITE_LIST_ELEMENT_NAME)) {
					objectGetter.setFieldOfObject(projectField, newProject, sprites);
					continue;
				}

				if (projectField != null) {
					NodeList childNodes = projectChildElement.getChildNodes();
					if (childNodes.getLength() > 0) {
						String valueInString = childNodes.item(0).getNodeValue();
						Object valueObject = objectGetter.getObjectOfClass(projectField.getType(), valueInString);
						objectGetter.setFieldOfObject(projectField, newProject, valueObject);
					} else {
						objectGetter.setFieldOfObject(projectField, newProject, null);
					}
				}
			}
		}
		objectGetter.setFieldOfObject(projectFieldsToSet.get(SPRITE_LIST_ELEMENT_NAME), newProject, sprites);
		return newProject;
	}

}
