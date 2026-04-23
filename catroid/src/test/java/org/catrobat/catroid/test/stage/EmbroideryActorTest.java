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

package org.catrobat.catroid.test.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.embroidery.DSTPatternManager;
import org.catrobat.catroid.embroidery.DSTStitchCommand;
import org.catrobat.catroid.stage.EmbroideryActor;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GdxNativesLoader.class)
public class EmbroideryActorTest {
	private Sprite sprite;
	private ShapeRenderer renderer;
	private Batch batch;

	@Before
	public void setUp() throws Exception {
		PowerMockito.mockStatic(GdxNativesLoader.class);

		sprite = new Sprite();
		batch = Mockito.mock(Batch.class);
		StageActivity.stageListener = Mockito.mock(StageListener.class);
		StageActivity.stageListener.embroideryPatternManager = new DSTPatternManager();
		renderer = Mockito.mock(ShapeRenderer.class);
		StageActivity.stageListener.shapeRenderer = renderer;
	}

	@After
	public void tearDown() {
		StageActivity.stageListener = null;
	}

	@Test
	public void testDrawLine() {
		float startX = 0.0f;
		float startY = 0.0f;
		float endX = 60.0f;
		float endY = 0.0f;

		EmbroideryActor embroideryActor = setUpEmbroideryPatternManager(startX, startY, endX, endY);
		verify(renderer, times(1)).rectLine(startX, startY, endX, endY,
				embroideryActor.getStitchSize());
	}

	@Test
	public void testDrawLineWithCircles() {
		float startX = 0.0f;
		float startY = 0.0f;
		float endX = 30.0f;
		float endY = 0.0f;

		EmbroideryActor embroideryActor = setUpEmbroideryPatternManager(startX, startY, endX, endY);

		verify(renderer, times(1)).circle(startX, startY, embroideryActor.getStitchSize());
		verify(renderer, times(1)).rectLine(startX, startY, endX, endY,
				embroideryActor.getStitchSize());
		verify(renderer, times(1)).circle(endX, endY, embroideryActor.getStitchSize());
	}

	@Test
	public void testThreadColorStitchesOneColor() {
		float startX = 0.0f;
		float startY = 0.0f;
		float endX = 30.0f;
		float endY = 0.0f;
		sprite.setEmbroideryThreadColor(Color.ORANGE);
		setUpEmbroideryPatternManager(startX, startY, endX, endY);
		verify(renderer, times(3)).setColor(Color.ORANGE);
	}

	@Test
	public void testThreadColorStitchesTwoColors() {
		float startX = 0.0f;
		float startY = 0.0f;
		float endX = 30.0f;
		float endY = 0.0f;
		sprite.setEmbroideryThreadColor(Color.ORANGE);
		setUpEmbroideryPatternManager(startX, startY, endX, endY);
		verify(renderer, times(3)).setColor(Color.ORANGE);
		startX = 30.0f;
		startY = 0.0f;
		endX = 40.0f;
		endY = 0.0f;
		sprite.setEmbroideryThreadColor(Color.RED);
		setUpEmbroideryPatternManager(startX, startY, endX, endY);
		verify(renderer, times(1)).setColor(Color.RED);
	}

	public EmbroideryActor setUpEmbroideryPatternManager(float startX, float startY, float endX, float endY) {
		StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(startX,
				startY, 1, sprite, sprite.getEmbroideryThreadColor()));
		StageActivity.stageListener.embroideryPatternManager.addStitchCommand(new DSTStitchCommand(endX,
				endY, 1, sprite, sprite.getEmbroideryThreadColor()));

		EmbroideryActor embroideryActor = new EmbroideryActor(1.f,
				StageActivity.stageListener.embroideryPatternManager, renderer);

		embroideryActor.draw(batch, 1.f);
		return embroideryActor;
	}
}
