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

import org.catrobat.catroid.common.BrickValues;

import java.util.ArrayList;
import java.util.ListIterator;

public class EmbroideryActor extends Actor {
	private float stitchSize;

	public EmbroideryActor(float screenRatio) {
		stitchSize = BrickValues.STITCH_SIZE * screenRatio;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		StageActivity.stageListener.getEmbroideryManager().lockStitchpoints();
		ArrayList<PointF> stitchPoints = StageActivity.stageListener.getEmbroideryManager().getStitchPoints();
		ListIterator<PointF> iterator = stitchPoints.listIterator();

		if (stitchPoints.size() >= 2) {
			batch.end();

			ShapeRenderer renderer = StageActivity.stageListener.shapeRenderer;
			renderer.setColor(Color.BLACK);
			renderer.begin(ShapeRenderer.ShapeType.Filled);

			PointF previousPoint = iterator.next();
			while (iterator.hasNext()) {
				PointF point = iterator.next();
				renderer.circle(previousPoint.x, previousPoint.y, stitchSize);
				renderer.rectLine(previousPoint.x, previousPoint.y, point.x, point.y, stitchSize);
				previousPoint = point;
			}
			renderer.circle(previousPoint.x, previousPoint.y, stitchSize);
			renderer.end();

			batch.begin();
		}
		StageActivity.stageListener.getEmbroideryManager().unlockStitchpoints();
	}
}
