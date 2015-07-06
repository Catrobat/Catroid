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
package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.BroadcastEvent;
import org.catrobat.catroid.content.BroadcastEvent.BroadcastType;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class BroadcastAction extends Action {

	private BroadcastEvent event;
	private boolean executeOnce = true;

	@Override
	public boolean act(float delta) {
		if (executeOnce) {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();
			for (Sprite spriteOfList : sprites) {
				spriteOfList.look.fire(event);
			}
			executeOnce = false;
		}
		if (event.getRun() || event.getNumberOfReceivers() == 0) {
			return true;
		}
		return false;
	}

	@Override
	public void restart() {
		executeOnce = true;
		if (event.getType().equals(BroadcastType.broadcastWait)) {
			event.setRun(false);
		}
	}

	public void setBroadcastEvent(BroadcastEvent event) {
		this.event = event;
	}
}
