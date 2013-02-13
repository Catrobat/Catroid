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

public class CatroidXMLConstants {

	public static final String PARENT_ELEMENT = "..";
	public static final String REFERENCE_ATTRIBUTE = "reference";

	public static final String PROJECT_ELEMENT_NAME_OPTIONAL_STARTTAG = " xmlns:xsi= " + "\""
			+ "http://www.w3.org/2001/XMLSchema-instance" + "\"" + " xsi:noNamespaceSchemaLocation=" + "\""
			+ "http://catroid.org/CatrobatLanguage/xmlSchema/version-0.3/catrobatXmlSchema.xsd" + "\"";
	public static final String PROJECT_ELEMENT_NAME = "CatrobatProgram";// + OPTIONAL_HEADER;// xmlns:xsi= \"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://catroid.org/CatrobatLanguage/xmlSchema/version-0.3/catrobatXmlSchema.xsd\"";
	public static final String PROJECT_HEADER_NAME = "Header";

	public static final String CONTENT_PACKAGE = "org.catrobat.catroid.content.";

	public static final String SPRITE_ELEMENT_NAME = "Sprite";
	public static final String SPRITE_NAME = "Name";
	public static final String SPRITE_LIST_ELEMENT_NAME = "SpriteList";
	public static final String SPRITE_LIST_FIELD_NAME = "spriteList";
	public static final String SPRITE = "Sprite";

	public static final String SCRIPT_LIST_ELEMENT_NAME = "ScriptList";
	public static final String SCRIPTCLASS_SUFFIX = "Script";

	public static final String BRICK_LIST_ELEMENT_NAME = "BrickList";
	public static final String BRICK_PACKAGE = "org.catrobat.catroid.content.bricks.";
	public static final String BRICK_CLASS_SUFFIX = "Brick";
	public static final String LOOP_END_BRICK = "LoopEndBrick";
	public static final String LOOP_BEGIN_BRICK = "LoopBeginBrick";
	public static final String LOOP_END_BRICKREFERENCE = "loopEndBrickRef";

	public static final String SOUND_INFO = "SoundInfo";
	public static final String SOUND_LIST_ELEMENT_NAME = "SoundList";
	public static final String SOUND_LIST_FIELD_NAME = "soundList";
	public static final String SOUND_INFO_ELEMENT_NAME = "Sound";
	public static final String SOUND_INFO_CLASS_NAME = "SoundInfo";
	public static final String SOUND_INFO_FIELD_NAME = "sound";

	public static final String LOOK_DATA_ELEMENT_NAME = "Look";
	public static final String LOOK_LIST_ELEMENT_NAME = "LookList";
	public static final String LOOK_LIST_FIELD_NAME = "lookList";
	public static final String LOOK_DATA_FIELD_NAME = "look";
	public static final String LOOK_DATA_CLASS_NAME = "LookData";

	public static final String FILE_NAME = "FileName";
	public static final String NAME = "Name";

	public static final String LOOKREFERENCE_FROM_BRICK = "../../../../../LookList/Look";
	public static final String SPRITEREFERENCE_FROM_BRICK = "../../../../../../Sprite";
	public static final String SOUNDREFERENCE_FROM_BRICK = "../../../../../SoundList/Sound";
}
