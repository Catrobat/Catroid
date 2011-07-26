/*  Catroid: An on-device graphical programming language for Android devices
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
package at.tugraz.ist.catroid.content;

import android.graphics.Bitmap;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * @author ainulhusna
 * 
 */
public class costumeData {
	private String costumeName, costumeFormat, costumeAbsoluteImagepath, costumeDisplayName;
	@XStreamOmitField
	private transient Bitmap costumeThumbnail;
	private int costumeId;

	public void setCostumeId(int costumeId) {
		this.costumeId = costumeId;
	}

	public int getCostumeId() {
		return costumeId;
	}

	public void setCostumeDisplayName(String costumeDisplayName) {
		this.costumeDisplayName = costumeDisplayName;
	}

	public void setCostumeName(String costumeName) {
		this.costumeName = costumeName;
	}

	public void setCostumeFormat(String costumeFormat) {
		this.costumeFormat = costumeFormat;
	}

	public void setCostumeImage(Bitmap costumeThumbnail) {
		this.costumeThumbnail = costumeThumbnail;
	}

	public void setCostumeAbsoluteImagepath(String costumeImage) {
		this.costumeAbsoluteImagepath = Consts.DEFAULT_ROOT + "/"
				+ ProjectManager.getInstance().getCurrentProject().getName() + Consts.IMAGE_DIRECTORY + "/"
				+ costumeImage;
	}

	public String getCostumeDisplayName() {
		return costumeDisplayName;
	}

	public String getCostumeName() {
		return costumeName;
	}

	public String getCostumeFormat() {
		return costumeFormat;
	}

	public Bitmap getCostumeImage() {
		return costumeThumbnail;
	}

	public String getCostumeAbsoluteImagepath() {
		return costumeAbsoluteImagepath;
	}

}