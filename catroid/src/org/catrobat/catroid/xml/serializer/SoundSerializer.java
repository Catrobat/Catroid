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
package org.catrobat.catroid.xml.serializer;

import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.FILE_NAME;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.NAME;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.SOUND_INFO_ELEMENT_NAME;
import static org.catrobat.catroid.xml.parser.CatroidXMLConstants.SOUND_LIST_ELEMENT_NAME;

import java.util.ArrayList;
import java.util.List;

import org.catrobat.catroid.common.SoundInfo;

public class SoundSerializer extends Serializer {

	private final String soundTabs = TAB + TAB + TAB;

	@Override
	public List<String> serialize(Object object) {
		SoundInfo soundInfo = (SoundInfo) object;
		String lookFileName = soundInfo.getSoundFileName();
		String lookName = soundInfo.getTitle();
		List<String> soundStringList = new ArrayList<String>();
		String xmlElementString = "";
		xmlElementString = soundTabs + TAB + getStartTag(SOUND_INFO_ELEMENT_NAME);
		soundStringList.add(xmlElementString);
		xmlElementString = soundTabs + TAB + TAB + getElementString(FILE_NAME, lookFileName);
		soundStringList.add(xmlElementString);
		xmlElementString = soundTabs + TAB + TAB + getElementString(NAME, lookName);
		soundStringList.add(xmlElementString);
		xmlElementString = soundTabs + TAB + getEndTag(SOUND_INFO_ELEMENT_NAME);
		soundStringList.add(xmlElementString);

		return soundStringList;
	}

	public List<String> serializeSoundList(List<SoundInfo> soundList) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		List<String> soundStrings = new ArrayList<String>();
		soundStrings.add(soundTabs + getStartTag(SOUND_LIST_ELEMENT_NAME));
		for (SoundInfo soundInfo : soundList) {
			soundStrings.addAll(this.serialize(soundInfo));
		}
		soundStrings.add(soundTabs + getEndTag(SOUND_LIST_ELEMENT_NAME));
		return soundStrings;
	}

}
