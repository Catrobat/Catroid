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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.common.images.WebImage;

import org.catrobat.catroid.utils.WebImageLoader;

import java.io.Serializable;

public class ScratchProjectData implements Parcelable, Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String content;
    private String projectUrl;
    private WebImage projectImage;
    private WebImage projectThumbnail;

    public ScratchProjectData(String title, String content, String projectUrl) {
        this.title = title;
        this.content = content;
        this.projectUrl = projectUrl;
        this.projectImage = null;
        this.projectThumbnail = null;
    }

    private ScratchProjectData(Parcel in) {
        this.title = in.readString();
        this.content = in.readString();
        this.projectUrl = in.readString();
        this.projectImage = in.readParcelable(WebImage.class.getClassLoader());
        this.projectThumbnail = in.readParcelable(WebImage.class.getClassLoader());
    }

    public String getProjectUrl() { return projectUrl; }

    public String getTitle() { return title; }

    public String getContent() { return content; }

    public WebImage getProjectThumbnail() { return projectThumbnail; }

    public void setProjectThumbnail(WebImage projectThumbnail) { this.projectThumbnail = projectThumbnail; }

    public WebImage getProjectImage() { return projectImage; }

    public void setProjectImage(WebImage projectImage) { this.projectImage = projectImage; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(projectUrl);
        dest.writeParcelable(projectImage, flags);
        dest.writeParcelable(projectThumbnail, flags);
    }

    public static final Parcelable.Creator<ScratchProjectData> CREATOR = new Parcelable.Creator<ScratchProjectData>() {
        @Override
        public ScratchProjectData createFromParcel(Parcel source) {
            return new ScratchProjectData(source);
        }

        @Override
        public ScratchProjectData[] newArray(int size) {
            return new ScratchProjectData[size];
        }
    };

}
