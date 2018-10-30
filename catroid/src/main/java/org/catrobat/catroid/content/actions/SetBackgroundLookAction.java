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

package org.catrobat.catroid.content.actions;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.EventWrapper;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.eventids.SetBackgroundEventId;

public class SetBackgroundLookAction extends EventAction{
	protected LookData lookData;
	protected Sprite background;
	@EventWrapper.WaitMode
	int waitMode;
	public SetBackgroundLookAction() {
		background = ProjectManager.getInstance().getCurrentlyPlayingScene().getBackgroundSprite();
	}
	@Override
	public boolean act(float delta) {
		if (firstStart) {
			if (lookData == null || background == null || !background.getLookList().contains(lookData)) {
				return true;
			}
			background.look.setLookData(lookData);
			EventWrapper event = new EventWrapper(new SetBackgroundEventId(background, lookData), waitMode);
			setEvent(event);
		}
		return super.act(delta);
	}

	public void setLookData(LookData lookData) {
		this.lookData = lookData;
	}

	public void setWaitMode(@EventWrapper.WaitMode int waitMode) {
		this.waitMode = waitMode;
	}
}
