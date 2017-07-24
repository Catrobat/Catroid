/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.common;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TemplateContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	@SerializedName("CatrobatTemplates")
	private List<TemplateData> templateData;

	@SerializedName("BaseUrl")
	private String baseUrl;

	@SerializedName("ProjectsExtension")
	private String programsExtension;

	public List<TemplateData> getTemplateData() {
		return templateData;
	}

	public void setTemplateData(List<TemplateData> templateData) {
		this.templateData = templateData;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getProgramsExtension() {
		return programsExtension;
	}

	public void setProgramsExtension(String programsExtension) {
		this.programsExtension = programsExtension;
	}
}
