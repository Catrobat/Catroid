/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import java.io.Serializable;

public class ScratchProjectData implements Serializable {

    public static class HttpImage {

        private String url;
        private int width;
        private int height;

        public HttpImage(String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }

        public String getUrl() { return url; }

        public int getWidth() { return width; }

        public int getHeight() { return height; }

    }

    private static final long serialVersionUID = 1L;

    private String title;
    private String content;
    private String projectUrl;
    private HttpImage projectImage;
    private HttpImage projectThumbnail;

    public ScratchProjectData(String title, String content, String projectUrl) {
        this.title = title;
        this.content = content;
        this.projectUrl = projectUrl;
        this.projectImage = null;
        this.projectThumbnail = null;
    }

    public String getProjectUrl() { return projectUrl; }

    public String getTitle() { return title; }

    public String getContent() { return content; }

    public HttpImage getProjectThumbnail() { return projectThumbnail; }

    public void setProjectThumbnail(HttpImage projectThumbnail) { this.projectThumbnail = projectThumbnail; }

    public HttpImage getProjectImage() { return projectImage; }

    public void setProjectImage(HttpImage projectImage) { this.projectImage = projectImage; }

}
