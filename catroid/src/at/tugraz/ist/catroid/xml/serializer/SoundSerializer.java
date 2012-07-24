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

	private final String soundInfoTag = "Common.SoundInfo";
	private final String soundFileNameTag = "fileName";
	private final String soundNameTag = "name";
	private final String soundListTag = "soundList";

	@Override
	public List<String> serialize(Object object) throws IllegalArgumentException, IllegalAccessException,
			SecurityException, NoSuchFieldException {
		SoundInfo soundInfo = (SoundInfo) object;
		String costumeFileName = soundInfo.getSoundFileName();
		String costumeName = soundInfo.getTitle();
		List<String> soundStringList = new ArrayList<String>();
		String xmlElementString = "";
		xmlElementString = getStartTag(soundInfoTag);
		soundStringList.add(xmlElementString);
		xmlElementString = getElementString(soundFileNameTag, costumeFileName);
		soundStringList.add(xmlElementString);
		xmlElementString = getElementString(soundNameTag, costumeName);
		soundStringList.add(xmlElementString);
		xmlElementString = getEndTag(soundInfoTag);
		soundStringList.add(xmlElementString);

		return soundStringList;
	}

	public List<String> serializeSoundList(List<SoundInfo> soundList) throws IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		List<String> soundStrings = new ArrayList<String>();
		soundStrings.add(getStartTag(soundListTag));
		for (SoundInfo soundInfo : soundList) {
			soundStrings.addAll(this.serialize(soundInfo));
		}
		soundStrings.add(getEndTag(soundListTag));
		return soundStrings;
	}

}
