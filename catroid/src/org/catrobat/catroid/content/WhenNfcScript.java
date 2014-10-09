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
package org.catrobat.catroid.content;

import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;

import java.util.ArrayList;
import java.util.List;

public class WhenNfcScript extends Script {

	private static final long serialVersionUID = 1L;

    private NfcTagData nfcTag;
	private boolean matchAll = true;

	public WhenNfcScript() {
		super();
        nfcTag = null;
	}

	@Override
	public Script copyScriptForSprite(Sprite copySprite, List<UserBrick> preCopiedUserBricks) {
		WhenNfcScript cloneScript = new WhenNfcScript(nfcTag);
		doCopy(copySprite, cloneScript, preCopiedUserBricks);
		return cloneScript;
	}

	public WhenNfcScript(NfcTagData nfcTag) {
		super();
		this.nfcTag = nfcTag;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new WhenNfcBrick(this);
		}
		return brick;
	}

    @Override
    public int getRequiredResources() {
        int resources = Brick.NO_RESOURCES;
        resources |= getScriptBrick().getRequiredResources();
        ArrayList<Brick> brickList = getBrickList();
        for (Brick brick : brickList) {
            resources |= brick.getRequiredResources();
        }
        return resources;
    }

	public void setMatchAll(boolean matchAll) {
		this.matchAll = matchAll;
	}

	public boolean isMatchAll() {
		return matchAll;
	}

    public NfcTagData getNfcTag() {
        return nfcTag;
    }

    public void setNfcTag(NfcTagData nfcTag) {
        this.nfcTag = nfcTag;
    }
}
