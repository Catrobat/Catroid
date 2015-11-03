/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.common.ScreenModes;

import java.io.Serializable;

public class XmlHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	private String programName;
	private String description;

	@XStreamAlias("screenWidth")
	public int virtualScreenWidth = 0;
	@XStreamAlias("screenHeight")
	public int virtualScreenHeight = 0;
	@XStreamAlias("screenMode")
	public ScreenModes screenMode = ScreenModes.STRETCH;

	private float catrobatLanguageVersion;

	private boolean isPhiroProProject = false;

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
	private double platformVersion = 0;
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

	public int getVirtualScreenHeight() {
		return virtualScreenHeight;
	}

	public int getVirtualScreenWidth() {
		return virtualScreenWidth;
	}

	public void setVirtualScreenHeight(int height) {
		virtualScreenHeight = height;
	}

	public void setVirtualScreenWidth(int width) {
		virtualScreenWidth = width;
	}

	public String getRemixOf() {
		return remixOf;
	}

	public void setRemixOf(String remixOf) {
		this.remixOf = remixOf;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getCatrobatLanguageVersion() {
		return catrobatLanguageVersion;
	}

	public void setCatrobatLanguageVersion(float catrobatLanguageVersion) {
		this.catrobatLanguageVersion = catrobatLanguageVersion;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getApplicationBuildName() {
		return applicationBuildName;
	}

	public void setApplicationBuildName(String applicationBuildName) {
		this.applicationBuildName = applicationBuildName;
	}

	public int getApplicationBuildNumber() {
		return applicationBuildNumber;
	}

	public void setApplicationBuildNumber(int applicationBuildNumber) {
		this.applicationBuildNumber = applicationBuildNumber;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public double getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(double platformVersion) {
		this.platformVersion = platformVersion;
	}

	public void setScreenMode(ScreenModes screenMode) {
		this.screenMode = screenMode;
	}

	public ScreenModes getScreenMode() {
		return this.screenMode;
	}

	public boolean isPhiroProject() {
		return isPhiroProProject;
	}

	public void setIsPhiroProject(boolean isPhiroProject) {
		this.isPhiroProProject = isPhiroProject;
	}
}
