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

public class CatroidXMLConstants {

	public static final String parentElement = "..";
	public static final String referenceAttribute = "reference";

	public static final String projectElementName = "Project";

	public static final String contentPackage = "at.tugraz.ist.catroid.content.";

	public static final String spriteElementName = "Sprite";
	public static final String spriteName = "name";
	public static final String spriteListElementName = "spriteList";
	public static final String sprite = "sprite";

	public static final String scriptListElementName = "scriptList";
	public static final String scriptclassSuffix = "Script";

	public static final String brickListElementName = "brickList";
	public static final String brickPackage = "at.tugraz.ist.catroid.content.bricks.";
	public static final String brickClassSuffix = "Brick";
	public static final String loopEndBrick = "loopEndBrick";
	public static final String loopBeginBrick = "loopBeginBrick";
	public static final String loopEndBrickreference = "loopEndBrickRef";

	public static final String soundInfo = "soundInfo";
	public static final String soundListElementName = "soundList";
	public static final String soundInfoElementName = "SoundInfo";

	public static final String costumeDataElementName = "CostumeData";
	public static final String costumeListElementName = "costumeDataList";

	public static final String fileName = "fileName";
	public static final String name = "name";

	public static final String costumeRefFromBrick = "../../../../../costumeDataList/CostumeData";
	public static final String spriteRefFromBrick = "../../../../../../Sprite";
	public static final String soundRefFromBrick = "../../../../../soundList/SoundInfo";
}
