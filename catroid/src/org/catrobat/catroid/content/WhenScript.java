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
package org.catrobat.catroid.content;

import org.catrobat.catroid.content.bricks.ScriptBrick;
import org.catrobat.catroid.content.bricks.WhenBrick;

public class WhenScript extends Script {

	private static final long serialVersionUID = 1L;
	private static final String LONGPRESSED = "Long Pressed";
	private static final String TAPPED = "Tapped";
	private static final String DOUBLETAPPED = "Double Tapped";
	private static final String SWIPELEFT = "Swipe Left";
	private static final String SWIPERIGHT = "Swipe Right";
	private static final String SWIPEUP = "Swipe Up";
	private static final String SWIPEDOWN = "Swipe Down";
	private static final String[] actions = { TAPPED, DOUBLETAPPED, LONGPRESSED, SWIPEUP, SWIPEDOWN, SWIPELEFT,
			SWIPERIGHT };
	private String action;
	private transient int position;

	public WhenScript() {

	}

	public WhenScript(Sprite sprite) {
		super(sprite);
		super.isFinished = true;
		this.position = 0;
		this.action = TAPPED;
	}

	public WhenScript(Sprite sprite, WhenBrick brick) {
		this(sprite);
		this.brick = brick;
	}

	@Override
	protected Object readResolve() {
		isFinished = true;
		super.readResolve();
		return this;
	}

	public void setAction(int position) {
		this.position = position;
		this.action = actions[position];
	}

	public String getAction() {
		return action;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public ScriptBrick getScriptBrick() {
		if (brick == null) {
			brick = new WhenBrick(sprite, this);
		}

		return brick;
	}
}
