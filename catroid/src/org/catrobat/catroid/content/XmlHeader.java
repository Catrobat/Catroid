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
package org.catrobat.catroid.content;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class XmlHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	private String programName;
	private String description;

	@XStreamAlias("screenWidth")
	public int virtualScreenWidth = 0;
	@XStreamAlias("screenHeight")
	public int virtualScreenHeight = 0;

	private float catrobatLanguageVersion;

	// fields only used on the catrobat.org website so far
	private String applicationBuildName = "";
	private int applicationBuildNumber = 0;
	private String applicationName = "";
	private String applicationVersion = "";
	@SuppressWarnings("unused")
	private String dateTimeUpload = "";
	private String deviceName = "";
	@SuppressWarnings("unused")
	private String mediaLicense = "";
	private String platform = "";
	private int platformVersion = 0;
	@SuppressWarnings("unused")
	private String programLicense = "";
	@SuppressWarnings("unused")
	private String remixOf = "";
	@SuppressWarnings("unused")
	private String tags = "";
	@SuppressWarnings("unused")
	private String url = "";
	@SuppressWarnings("unused")
	private String userHandle = "";

	public XmlHeader() {
	}

	String getProgramName() {
		return programName;
	}

	void setProgramName(String programName) {
		this.programName = programName;
	}

	String getDescription() {
		return description;
	}

	void setDescription(String description) {
		this.description = description;
	}

	float getCatrobatLanguageVersion() {
		return catrobatLanguageVersion;
	}

	void setCatrobatLanguageVersion(float catrobatLanguageVersion) {
		this.catrobatLanguageVersion = catrobatLanguageVersion;
	}

	String getPlatform() {
		return platform;
	}

	void setPlatform(String platform) {
		this.platform = platform;
	}

	String getApplicationBuildName() {
		return applicationBuildName;
	}

	void setApplicationBuildName(String applicationBuildName) {
		this.applicationBuildName = applicationBuildName;
	}

	int getApplicationBuildNumber() {
		return applicationBuildNumber;
	}

	void setApplicationBuildNumber(int applicationBuildNumber) {
		this.applicationBuildNumber = applicationBuildNumber;
	}

	String getApplicationName() {
		return applicationName;
	}

	void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	String getApplicationVersion() {
		return applicationVersion;
	}

	void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	String getDeviceName() {
		return deviceName;
	}

	void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	int getPlatformVersion() {
		return platformVersion;
	}

	void setPlatformVersion(int platformVersion) {
		this.platformVersion = platformVersion;
	}
}
