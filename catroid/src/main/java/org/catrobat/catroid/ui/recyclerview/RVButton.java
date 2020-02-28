/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class RVButton {

	public int id;
	public Drawable drawable;
	public String title;
	@Nullable
	public String subtitle;

	public RVButton(int id, Drawable drawable, String name) {
		this.id = id;
		this.drawable = drawable;
		this.title = name;
	}

	public RVButton(int id, Drawable drawable, String name, @Nullable String subtitle) {
		this.id = id;
		this.drawable = drawable;
		this.title = name;
		this.subtitle = subtitle;
	}
}
