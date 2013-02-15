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
package org.catrobat.catroid.content.actions;

import java.util.ArrayList;

import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class NextLookAction extends TemporalAction {

	private Sprite sprite;

	@Override
	protected void update(float delta) {
		final ArrayList<LookData> lookDataList = sprite.getLookDataList();
		int lookDataListSize = lookDataList.size();

		if (lookDataListSize > 0 && sprite.look.getLookData() != null) {
			LookData currentLookData = sprite.look.getLookData();
			LookData finalLookData = lookDataList.get(lookDataListSize - 1);
			boolean executeOnce = true;

			for (LookData lookData : lookDataList) {
				int currentIndex = lookDataList.indexOf(lookData);
				int newIndex = currentIndex + 1;

				if (currentLookData.equals(finalLookData) && executeOnce) {
					executeOnce = false;
					currentLookData = lookDataList.get(0);
				}

				else if (currentLookData.equals(lookData) && executeOnce) {
					executeOnce = false;
					currentLookData = lookDataList.get(newIndex);
				}

				sprite.look.setLookData(currentLookData);
			}
		} else {
			// If there are no looks do nothing
		}
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

}
