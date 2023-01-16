/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.content;

import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.content.eventids.NfcEventId;

public class WhenNfcScript extends Script {

	private static final long serialVersionUID = 1L;

	private NfcTagData nfcTag;
	private boolean matchAll = true;

	public WhenNfcScript() {
	}

	public WhenNfcScript(NfcTagData nfcTag) {
		this.nfcTag = nfcTag;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (scriptBrick == null) {
			scriptBrick = new WhenNfcBrick(this);
		}
		return scriptBrick;
	}

	@Override
	public void addRequiredResources(final Brick.ResourcesSet resourcesSet) {
		resourcesSet.add(Brick.NFC_ADAPTER);
		super.addRequiredResources(resourcesSet);
	}

	public void setMatchAll(boolean matchAll) {
		this.matchAll = matchAll;
	}

	public NfcTagData getNfcTag() {
		return nfcTag;
	}

	public void setNfcTag(NfcTagData nfcTag) {
		this.nfcTag = nfcTag;
	}

	@Override
	public EventId createEventId(Sprite sprite) {
		if (matchAll) {
			return new EventId(EventId.ANY_NFC);
		} else if (nfcTag != null) {
			return new NfcEventId(nfcTag.getNfcTagUid());
		}
		throw new RuntimeException("We want to identify a specific NfcTag, but null is given.");
	}
}
