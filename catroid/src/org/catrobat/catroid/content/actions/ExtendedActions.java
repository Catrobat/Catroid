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

import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.content.BroadcastEvent;
import org.catrobat.catroid.content.BroadcastEvent.BroadcastType;
import org.catrobat.catroid.content.Sprite;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class ExtendedActions extends Actions {

	public static BroadcastAction broadcast(Sprite sprite, String broadcastMessage) {
		BroadcastAction action = action(BroadcastAction.class);
		action.setReceiverSprite(null);
		action.setBroadcastMessage(broadcastMessage);
		BroadcastEvent event = new BroadcastEvent();
		event.setSenderSprite(sprite);
		event.setBroadcastMessage(broadcastMessage);
		event.setType(BroadcastType.broadcast);
		action.setBroadcastEvent(event);
		return action;
	}

	public static BroadcastAction broadcastFromWaiter(Sprite sprite, String broadcastMessage) {
		BroadcastAction action = action(BroadcastAction.class);
		action.setReceiverSprite(null);
		action.setBroadcastMessage(broadcastMessage);
		BroadcastEvent event = new BroadcastEvent();
		event.setSenderSprite(sprite);
		event.setBroadcastMessage(broadcastMessage);
		event.setType(BroadcastType.broadcastFromWaiter);
		action.setBroadcastEvent(event);
		return action;
	}

	public static BroadcastAction broadcastToWaiter(Sprite sprite, String broadcastMessage) {
		BroadcastAction action = action(BroadcastAction.class);
		action.setReceiverSprite(sprite);
		action.setBroadcastMessage(broadcastMessage);
		BroadcastEvent event = new BroadcastEvent();
		event.setSenderSprite(null);
		event.setBroadcastMessage(broadcastMessage);
		event.setType(BroadcastType.broadcastToWaiter);
		action.setBroadcastEvent(event);
		return action;
	}

	public static ChangeGhostEffectByNAction changeGhostEffectByN(Sprite sprite, float ghostEffectValue) {
		ChangeGhostEffectByNAction action = action(ChangeGhostEffectByNAction.class);
		action.setSprite(sprite);
		action.setGhostEffectValue(ghostEffectValue);
		return action;
	}

	public static ComeToFrontAction comeToFront(Sprite sprite) {
		ComeToFrontAction action = action(ComeToFrontAction.class);
		action.setSprite(sprite);
		return action;
	}

	public static GlideToAction glideTo(float x, float y, float duration) {
		GlideToAction action = action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(duration);
		return action;
	}

	public static GlideToAction glideTo(Sprite sprite, float x, float y, float duration, Interpolation interpolation) {
		GlideToAction action = action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}

	public static GlideToAction placeAt(Sprite sprite, float x, float y) {
		GlideToAction action = action(GlideToAction.class);
		action.setPosition(x, y);
		action.setDuration(0);
		action.setInterpolation(null);
		return action;
	}

	public static GoNStepsBackAction goNStepsBack(Sprite sprite, int steps) {
		GoNStepsBackAction action = action(GoNStepsBackAction.class);
		action.setSprite(sprite);
		action.setSteps(steps);
		return action;
	}

	public static HideAction hide(Sprite sprite) {
		HideAction action = action(HideAction.class);
		action.setSprite(sprite);
		return action;
	}

	public static NextCostumeAction nextCostume(Sprite sprite) {
		NextCostumeAction action = action(NextCostumeAction.class);
		action.setSprite(sprite);
		return action;
	}

	public static SetCostumeAction setCostume(Sprite sprite, CostumeData costume) {
		SetCostumeAction action = action(SetCostumeAction.class);
		action.setSprite(sprite);
		action.setCostumeData(costume);
		return action;
	}

	public static SetSizeToAction setSizeTo(Sprite sprite, float size) {
		SetSizeToAction action = action(SetSizeToAction.class);
		action.setSprite(sprite);
		action.setSize(size);
		return action;
	}

	public static SetXAction setX(Sprite sprite, int x) {
		SetXAction action = action(SetXAction.class);
		action.setSprite(sprite);
		action.setX(x);
		return action;
	}

	public static SetYAction setY(Sprite sprite, int y) {
		SetYAction action = action(SetYAction.class);
		action.setSprite(sprite);
		action.setY(y);
		return action;
	}

	public static ShowAction show(Sprite sprite) {
		ShowAction action = action(ShowAction.class);
		action.setSprite(sprite);
		return action;
	}

}
