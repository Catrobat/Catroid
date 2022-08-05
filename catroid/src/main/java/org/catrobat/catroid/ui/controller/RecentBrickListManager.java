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
package org.catrobat.catroid.ui.controller;

import org.catrobat.catroid.common.RecentBricksHolder;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ComeToFrontBrick;
import org.catrobat.catroid.content.bricks.GoNStepsBackBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.LookRequestBrick;
import org.catrobat.catroid.content.bricks.PenDownBrick;
import org.catrobat.catroid.content.bricks.PenUpBrick;
import org.catrobat.catroid.content.bricks.SayBubbleBrick;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.SetLookByIndexBrick;
import org.catrobat.catroid.content.bricks.SetPenColorBrick;
import org.catrobat.catroid.content.bricks.SetPenSizeBrick;
import org.catrobat.catroid.content.bricks.StampBrick;
import org.catrobat.catroid.content.bricks.ThinkBubbleBrick;
import org.catrobat.catroid.content.bricks.ThinkForBubbleBrick;
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick;
import org.catrobat.catroid.io.RecentBrickListSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.common.Constants.RECENT_BRICKS_DIRECTORY;
import static org.catrobat.catroid.common.Constants.RECENT_BRICKS_FILE;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public final class RecentBrickListManager {

	private final Class[] nonBackgroundSpriteClasses = {WhenBounceOffBrick.class,
			IfOnEdgeBounceBrick.class, GoNStepsBackBrick.class, ComeToFrontBrick.class,
			SetLookBrick.class, SetLookByIndexBrick.class, SayBubbleBrick.class,
			SayForBubbleBrick.class, ThinkBubbleBrick.class, ThinkForBubbleBrick.class,
			LookRequestBrick.class, PenDownBrick.class, PenUpBrick.class, SetPenSizeBrick.class,
			SetPenColorBrick.class, StampBrick.class};

	private static final RecentBrickListManager INSTANCE = new RecentBrickListManager();

	public final File recentBrickListDirectory = new File(DEFAULT_ROOT_DIRECTORY,
			RECENT_BRICKS_DIRECTORY);
	public final File recentBricksFile = new File(recentBrickListDirectory, RECENT_BRICKS_FILE);

	private RecentBricksHolder recentBricksHolder = new RecentBricksHolder();

	private final RecentBrickListSerializer recentBricksSerializer =
			new RecentBrickListSerializer(recentBricksFile);

	public static RecentBrickListManager getInstance() {
		return INSTANCE;
	}

	private RecentBrickListManager() {
		createRecentbrickDirectories();
	}

	private void createRecentbrickDirectories() {
		DEFAULT_ROOT_DIRECTORY.mkdir();
		recentBrickListDirectory.mkdir();
	}

	boolean isNotBackgroundSpriteBrick(Brick brick) {
		for (Class c : nonBackgroundSpriteClasses) {
			if (brick.getClass().equals(c)) {
				return true;
			}
		}
		return false;
	}

	public List<Brick> getRecentBricks(boolean isBackgroundSprite) {
		List<Brick> bricks = recentBricksHolder.getRecentBricks();
		List<Brick> recentBricks = new ArrayList<>();
		if (isBackgroundSprite) {
			for (Brick brick : bricks) {
				if (!isNotBackgroundSpriteBrick(brick)) {
					recentBricks.add(brick);
				}
			}
		} else {
			recentBricks = bricks;
		}
		return recentBricks;
	}

	public void addBrick(Brick brick) {
		int index = recentBricksHolder.find(brick);
		if (index >= 0) {
			recentBricksHolder.remove(index);
		} else if (recentBricksHolder.size() == 10) {
			recentBricksHolder.remove();
		}
		recentBricksHolder.insert(brick);
	}

	public void saveRecentBrickList() {
		recentBricksSerializer.saveRecentBricks(recentBricksHolder);
	}

	public void loadRecentBricks() {
		recentBricksHolder = recentBricksSerializer.loadRecentBricks();
	}
}
