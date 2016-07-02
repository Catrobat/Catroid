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

import com.google.android.gms.common.images.WebImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScratchProjectData implements Serializable {

	public static class ScratchRemixProjectData implements Serializable {
		private static final long serialVersionUID = 1L;

		private long id;

		private String title;

		public String getTitle() {
			return title;
		}

		public String getOwner() {
			return owner;
		}

		public WebImage getProjectImage() {
			return projectImage;
		}

		public long getId() {
			return id;
		}

		private String owner;
		private WebImage projectImage;

		public ScratchRemixProjectData(long id, String title, String owner, WebImage projectImage) {
			this.id = id;
			this.title = title;
			this.owner = owner;
			this.projectImage = projectImage;
		}
	}

	private static final long serialVersionUID = 1L;

	private long id;
	private String title;
	private String owner;
	private String instructions;
	private String notesAndCredits;
	private int views;
	private int favorites;
	private int loves;
	private Date modifiedDate;
	private Date sharedDate;
	private List<String> tags;
	private List<ScratchRemixProjectData> remixes;

	public ScratchProjectData(long id, String title, String owner, String instructions, String notesAndCredits,
			int views, int favorites, int loves, Date modifiedDate, Date sharedDate, List<String> tags) {
		this.id = id;
		this.title = title;
		this.owner = owner;
		this.instructions = instructions;
		this.notesAndCredits = notesAndCredits;
		this.views = views;
		this.favorites = favorites;
		this.loves = loves;
		this.modifiedDate = modifiedDate;
		this.sharedDate = sharedDate;
		this.tags = tags;
		this.remixes = new ArrayList<>();
	}

	public long getId() { return id; }

	public String getTitle() { return title; }

	public String getOwner() { return owner; }

	public String getInstructions() { return instructions; }

	public String getNotesAndCredits() { return notesAndCredits; }

	public void addRemixProject(ScratchRemixProjectData remixProjectData) {
		remixes.add(remixProjectData);
	}

	public int getViews() { return views; }

	public int getFavorites() { return favorites; }

	public int getLoves() { return loves; }

	public Date getModifiedDate() { return modifiedDate; }

	public Date getSharedDate() { return sharedDate; }

	public List<String> getTags() { return tags; }

	public List<ScratchRemixProjectData> getRemixes() {
		return remixes;
	}
}
