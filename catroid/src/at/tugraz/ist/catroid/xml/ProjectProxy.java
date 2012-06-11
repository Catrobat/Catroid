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
package at.tugraz.ist.catroid.xml;

public class ProjectProxy {

	private String projectName;

	private String deviceName;

	private int androidVersion;

	private String catroidVersionName;

	private int catroidVersionCode;

	public int virtualScreenWidth = 0;

	public int virtualScreenHeight = 0;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getAndroidVersion() {
		return androidVersion;
	}

	public void setAndroidVersion(int androidVersion) {
		this.androidVersion = androidVersion;
	}

	public String getCatroidVersionName() {
		return catroidVersionName;
	}

	public void setCatroidVersionName(String catroidVersionName) {
		this.catroidVersionName = catroidVersionName;
	}

	public int getCatroidVersionCode() {
		return catroidVersionCode;
	}

	public void setCatroidVersionCode(int catroidVersionCode) {
		this.catroidVersionCode = catroidVersionCode;
	}

	public int getVirtualScreenWidth() {
		return virtualScreenWidth;
	}

	public void setVirtualScreenWidth(int virtualScreenWidth) {
		this.virtualScreenWidth = virtualScreenWidth;
	}

	public int getVirtualScreenHeight() {
		return virtualScreenHeight;
	}

	public void setVirtualScreenHeight(int virtualScreenHeight) {
		this.virtualScreenHeight = virtualScreenHeight;
	}

	public ProjectProxy() {

	}
}
