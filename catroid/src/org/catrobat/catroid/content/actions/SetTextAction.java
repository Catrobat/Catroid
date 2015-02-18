/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import android.util.Log;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

public class SetTextAction extends TemporalAction {

	private Formula endX;
	private Formula endY;
	private Sprite sprite;
	private Formula duration;

	@Override
	protected void begin() {
	}

	@Override
	protected void update(float percent) {
		Log.d(getClass().getSimpleName(), "Zeichne ...");
		BitmapFont font = new BitmapFont();
		SpriteBatch spriteBatch = new SpriteBatch();
		font.setColor(0.0f, 0.0f, 1.0f, 1.0f); // tint font blue
		spriteBatch.begin();
		font.draw(spriteBatch, "Hello !!!", 25, 160);
		spriteBatch.end();

		final String message = "hello";


	}

	public void setDuration(Formula duration) {
		this.duration = duration;
	}

	public void setPosition(Formula x, Formula y) {
		endX = x;
		endY = y;
	}

	public void setText(Formula text) {
		// this.text = text;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}


}
