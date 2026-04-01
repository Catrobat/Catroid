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

package org.catrobat.catroid.content;

import android.graphics.PointF;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Queue;

import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.stage.StageActivity;

public class PenConfiguration {

	private Queue<Queue<PointF>> positions = new Queue<>();
	private boolean penDown = false;
	private double penSize = BrickValues.PEN_SIZE;
	private PenColor penColor = new PenColor(0, 0, 1, 1);
	private boolean stamp = false;
	private int queuesToFinish = 0;

	public PenConfiguration() {
	}

	public void drawLinesForSprite(Float screenRatio, Camera camera) {

		ShapeRenderer renderer = StageActivity.stageListener.shapeRenderer;
		renderer.setColor(new Color(penColor.r, penColor.g, penColor.b, penColor.a));
		renderer.begin(ShapeRenderer.ShapeType.Filled);

		while (currentQueueHasJobToHandle()) {
			drawLine(screenRatio, renderer, camera);
			updateQueues();
		}

		renderer.end();
	}

	private boolean currentQueueHasJobToHandle() {
		return !positions.isEmpty() && (positions.first().size > 1 || queuesToFinish > 0);
	}

	private void drawLine(Float screenRatio, ShapeRenderer renderer, Camera camera) {

		PointF currentPosition = positions.first().removeFirst();
		PointF nextPosition = positions.first().first();
		currentPosition.x += camera.position.x;
		currentPosition.y += camera.position.y;
		nextPosition.x += camera.position.x;
		nextPosition.y += camera.position.y;
		if (currentPosition.x != nextPosition.x || currentPosition.y != nextPosition.y) {
			Float penSize = (float) this.penSize * screenRatio;
			renderer.circle(currentPosition.x, currentPosition.y, penSize / 2);
			renderer.rectLine(currentPosition.x, currentPosition.y, nextPosition.x, nextPosition.y,
					penSize);
			renderer.circle(nextPosition.x, nextPosition.y, penSize / 2);
		}
		nextPosition.x -= camera.position.x;
		nextPosition.y -= camera.position.y;
	}

	private void updateQueues() {
		if (queuesToFinish > 0 && positions.first().size <= 1) {
			positions.removeFirst();
			queuesToFinish--;
		}
	}

	public void addQueue() {
		positions.addLast(new Queue<>());
	}

	public void addPosition(PointF position) {
		positions.last().addLast(position);
	}

	public void incrementQueuesToFinish() {
		queuesToFinish++;
	}

	public void setPenDown(boolean penDown) {
		this.penDown = penDown;
	}

	public boolean isPenDown() {
		return penDown;
	}

	public void setPenSize(double penSize) {
		this.penSize = penSize;
	}

	public void setPenColor(PenColor penColor) {
		this.penColor = penColor;
	}

	public void setStamp(boolean stamp) {
		this.stamp = stamp;
	}

	public boolean hasStamp() {
		return stamp;
	}

	public Queue<Queue<PointF>> getPositions() {
		return positions;
	}
}
