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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.common.images.WebImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScratchProgramData implements Serializable, Parcelable {

	private static final long serialVersionUID = 1L;

	private long id;
	private String title;
	private String owner;
	private WebImage image;
	private String instructions;
	private String notesAndCredits;
	private int views;
	private int favorites;
	private int loves;
	private Date createdDate;
	private Date modifiedDate;
	private Date sharedDate;
	private List<String> tags;
	private ScratchVisibilityState visibilityState;
	private List<ScratchProgramData> remixes;

	public ScratchProgramData(long id, String title, String owner, WebImage image) {
		this.id = id;
		this.title = title;
		this.owner = owner;
		this.image = image;
		this.instructions = null;
		this.notesAndCredits = null;
		this.views = 0;
		this.favorites = 0;
		this.loves = 0;
		this.createdDate = null;
		this.modifiedDate = null;
		this.sharedDate = null;
		this.tags = new ArrayList<>();
		this.visibilityState = null;
		this.remixes = new ArrayList<>();
	}

	private ScratchProgramData(Parcel in) {
		this.id = in.readLong();
		this.title = in.readString();
		this.owner = in.readString();
		this.image = in.readParcelable(WebImage.class.getClassLoader());
		this.instructions = in.readString();
		this.notesAndCredits = in.readString();
		this.views = in.readInt();
		this.favorites = in.readInt();
		this.loves = in.readInt();
		long createdDateTime = in.readLong();
		this.createdDate = createdDateTime == 0 ? null : new Date(createdDateTime);
		long modifiedDateTime = in.readLong();
		this.modifiedDate = modifiedDateTime == 0 ? null : new Date(modifiedDateTime);
		long sharedDateTime = in.readLong();
		this.sharedDate = sharedDateTime == 0 ? null : new Date(sharedDateTime);
		this.tags = new ArrayList<>();
		in.readStringList(this.tags);
		this.visibilityState = in.readParcelable(ScratchVisibilityState.class.getClassLoader());
		this.remixes = new ArrayList<>();
		in.readTypedList(remixes, CREATOR);
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getOwner() {
		return owner;
	}

	public WebImage getImage() {
		return image;
	}

	public void setImage(WebImage image) {
		this.image = image;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getNotesAndCredits() {
		return notesAndCredits;
	}

	public void setNotesAndCredits(String notesAndCredits) {
		this.notesAndCredits = notesAndCredits;
	}

	public void addRemixProgram(ScratchProgramData remixProgramData) {
		remixes.add(remixProgramData);
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getFavorites() {
		return favorites;
	}

	public void setFavorites(int favorites) {
		this.favorites = favorites;
	}

	public int getLoves() {
		return loves;
	}

	public void setLoves(int loves) {
		this.loves = loves;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date getSharedDate() {
		return sharedDate;
	}

	public void setSharedDate(Date sharedDate) {
		this.sharedDate = sharedDate;
	}

	public List<String> getTags() {
		return tags;
	}

	public void addTag(String tagName) {
		tags.add(tagName);
	}

	public ScratchVisibilityState getVisibilityState() {
		return visibilityState;
	}

	public void setVisibilityState(ScratchVisibilityState visibilityState) {
		this.visibilityState = visibilityState;
	}

	public List<ScratchProgramData> getRemixes() {
		return remixes;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(title);
		dest.writeString(owner);
		dest.writeParcelable(image, flags);
		dest.writeString(instructions);
		dest.writeString(notesAndCredits);
		dest.writeInt(views);
		dest.writeInt(favorites);
		dest.writeInt(loves);
		dest.writeLong(createdDate != null ? createdDate.getTime() : 0);
		dest.writeLong(modifiedDate != null ? modifiedDate.getTime() : 0);
		dest.writeLong(sharedDate != null ? sharedDate.getTime() : 0);
		dest.writeStringList(tags);
		dest.writeParcelable(visibilityState, flags);
		dest.writeTypedList(remixes);
	}

	public static final Parcelable.Creator<ScratchProgramData> CREATOR = new Parcelable.Creator<ScratchProgramData>() {
		@Override
		public ScratchProgramData createFromParcel(Parcel source) {
			return new ScratchProgramData(source);
		}

		@Override
		public ScratchProgramData[] newArray(int size) {
			return new ScratchProgramData[size];
		}
	};
}
