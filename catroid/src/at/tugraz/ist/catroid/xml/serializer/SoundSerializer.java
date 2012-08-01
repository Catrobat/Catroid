/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.xml.serializer;

import java.util.ArrayList;
import java.util.List;

import at.tugraz.ist.catroid.common.SoundInfo;

public class SoundSerializer extends Serializer {

	private final String soundInfoTag = "SoundInfo";
	private final String soundFileNameTag = "fileName";
	private final String soundNameTag = "name";
	private final String soundListTag = "soundList";
	private final String soundTabs = tab + tab + tab;

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException {
		SoundInfo soundInfo = (SoundInfo) object;
		String costumeFileName = soundInfo.getSoundFileName();
		String costumeName = soundInfo.getTitle();
		List<String> soundStringList = new ArrayList<String>();
		String xmlElementString = "";
		xmlElementString = soundTabs + tab + getStartTag(soundInfoTag);
		soundStringList.add(xmlElementString);
		xmlElementString = soundTabs + tab + tab + getElementString(soundFileNameTag, costumeFileName);
		soundStringList.add(xmlElementString);
		xmlElementString = soundTabs + tab + tab + getElementString(soundNameTag, costumeName);
		soundStringList.add(xmlElementString);
		xmlElementString = soundTabs + tab + getEndTag(soundInfoTag);
		soundStringList.add(xmlElementString);

		return soundStringList;
	}

	public List<String> serializeSoundList(List<SoundInfo> soundList) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		List<String> soundStrings = new ArrayList<String>();
		soundStrings.add(soundTabs + getStartTag(soundListTag));
		for (SoundInfo soundInfo : soundList) {
			soundStrings.addAll(this.serialize(soundInfo));
		}
		soundStrings.add(soundTabs + getEndTag(soundListTag));
		return soundStrings;
	}

}
