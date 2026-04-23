/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.embroidery.EmbroideryPatternManager;
import org.catrobat.catroid.embroidery.StitchPoint;

import java.util.List;
import java.util.ListIterator;

import androidx.annotation.VisibleForTesting;

public class EmbroideryActor extends Actor {
	private final float stitchSize;
	private final EmbroideryPatternManager embroideryPatternManager;
	private final ShapeRenderer shapeRenderer;

	public EmbroideryActor(float screenRatio, EmbroideryPatternManager embroideryPatternManager,
			ShapeRenderer shapeRenderer) {
		this.stitchSize = BrickValues.STITCH_SIZE * screenRatio;
		this.embroideryPatternManager = embroideryPatternManager;
		this.shapeRenderer = shapeRenderer;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		List<StitchPoint> stitchPoints = embroideryPatternManager.getEmbroideryPatternList();

		ListIterator<StitchPoint> iterator = stitchPoints.listIterator();

		if (stitchPoints.size() >= 2) {
			batch.end();

			StitchPoint previousStitchPoint;
			StitchPoint stitchPoint = iterator.next();

			shapeRenderer.setColor(stitchPoint.getColor());
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

			drawCircle(stitchPoint);
			boolean colorChange = false;

			while (iterator.hasNext()) {
				previousStitchPoint = stitchPoint;
				stitchPoint = iterator.next();

				colorChange |= stitchPoint.isColorChangePoint();

				if (!colorChange) {
					shapeRenderer.setColor(previousStitchPoint.getColor());
					drawLine(previousStitchPoint, stitchPoint);
				}
				if (stitchPoint.isConnectingPoint()) {
					shapeRenderer.setColor(stitchPoint.getColor());
					drawCircle(stitchPoint);
					colorChange = false;
				}
			}
			shapeRenderer.end();
			batch.begin();
		}
	}

	@VisibleForTesting
	void drawCircle(StitchPoint stitchPoint) {
		shapeRenderer.circle(stitchPoint.getX(), stitchPoint.getY(), stitchSize);
	}

	@VisibleForTesting
	void drawLine(StitchPoint stitchPoint1, StitchPoint stitchPoint2) {
		shapeRenderer.rectLine(stitchPoint1.getX(), stitchPoint1.getY(),
				stitchPoint2.getX(), stitchPoint2.getY(), stitchSize);
	}

	@VisibleForTesting
	public float getStitchSize() {
		return stitchSize;
	}
}
