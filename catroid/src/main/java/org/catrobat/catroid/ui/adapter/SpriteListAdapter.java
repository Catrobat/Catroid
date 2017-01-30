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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.dragndrop.DragAndDropAdapterInterface;

import java.util.List;

public class SpriteListAdapter extends SpriteListBaseAdapter implements DragAndDropAdapterInterface {

	public static final String TAG = SpriteListAdapter.class.getSimpleName();
	private static final String BACKGROUND_TAG = "BACKGROUND";

	public SpriteListAdapter(Context context, int resource, List<Sprite> listItems) {
		super(context, resource, listItems);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (isBackgroundSprite(getItem(position))) {
			View emptyView = new View(getContext());
			emptyView.setTag(BACKGROUND_TAG);
			return emptyView;
		}

		boolean isBackgroundSpriteView = convertView != null && convertView.getTag().equals(BACKGROUND_TAG);

		if (isBackgroundSpriteView) {
			convertView = null;
		}

		View listItemView = super.getView(position, convertView, parent);
		return getWrappedListItem(position, listItemView);
	}

	@Override
	public int swapItems(int position1, int position2) {
		if (position2 == 0) {
			return position1;
		}
		return super.swapItems(position1, position2);
	}

	private boolean isBackgroundSprite(Sprite sprite) {
		return sprite.equals(getItem(0));
	}
}
