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

package org.catrobat.catroid.stage;

import android.graphics.PointF;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.catrobat.catroid.embroidery.EmbroideryManager;

import java.util.ArrayList;

public class EmbroideryActor extends Actor {
	private EmbroideryManager embroideryManager = EmbroideryManager.getInstance();

	public EmbroideryActor() {
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		ArrayList<PointF> stitchPoints = embroideryManager.getStitchPoints();

		if (stitchPoints.size() >= 2) {
			batch.end();

			ShapeRenderer renderer = StageActivity.stageListener.shapeRenderer;
			renderer.setColor(Color.BLACK);
			renderer.begin(ShapeRenderer.ShapeType.Filled);

			for (int index = 0; index < embroideryManager.getLastIndex(); index++) {
				PointF point = stitchPoints.get(index);
				PointF nextPoint = stitchPoints.get(index + 1);
				renderer.circle(point.x, point.y, embroideryManager.getStitchSize());
				renderer.rectLine(point.x, point.y, nextPoint.x, nextPoint.y, embroideryManager.getStitchSize());
			}

			PointF lastPoint = stitchPoints.get(embroideryManager.getLastIndex());
			renderer.circle(lastPoint.x, lastPoint.y, embroideryManager.getStitchSize());
			renderer.end();

			batch.begin();
		}
	}

	public void reset() {
		embroideryManager.clearStitchPoints();
	}

	public void dispose() {
		embroideryManager.clearStitchPoints();
	}
}
