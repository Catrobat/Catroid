/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.content.commands;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.PointToBrick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteCommands {
	public static class RenameSpriteCommand implements Command {
		private String oldName;
		private String newName;
		private Sprite sprite;

		public RenameSpriteCommand(Sprite sprite, String newName) {
			this.oldName = sprite.getName();
			this.newName = newName;
			this.sprite = sprite;
		}

		@Override
		public void execute() {
			sprite.setName(newName);
		}

		@Override
		public void undo() {
			sprite.setName(oldName);
		}
	}

	public static class AddSpriteCommand implements Command {
		private ArrayList<Sprite> sprites;
		private Sprite backgroundSpriteBackup = null;
		private Sprite currentBackground = null;
		Map<Sprite, PointToBrick> bricks = new HashMap<>();

		public AddSpriteCommand(ArrayList<Sprite> sprites, Sprite backgroundNew, Sprite backgroundOld) {
			this.sprites = sprites;
			currentBackground = backgroundNew;
			backgroundSpriteBackup = backgroundOld;
		}

		@Override
		public void execute() {
			for (Sprite sprite : sprites) {
				ProjectManager.getInstance().getCurrentProject().addSprite(sprite);
			}

			if (currentBackground != null) {
				backgroundSpriteBackup = ProjectManager.getInstance().getCurrentProject().replaceBackgroundSprite(currentBackground);
			}

			for (Sprite sprite : bricks.keySet()) {
				bricks.get(sprite).setSprite(sprite);
			}

			bricks.clear();
		}

		@Override
		public void undo() {
			for (PointToBrick brick : ProjectManager.getInstance().getCurrentProject().getPointToBricks()) {
				if (sprites.contains(brick.getSprite())) {
					bricks.put(brick.getSprite(), brick);
				}
			}

			for (Sprite sprite : sprites) {
				ProjectManager.getInstance().getCurrentProject().removeSprite(sprite);
			}

			if (backgroundSpriteBackup != null) {
				currentBackground = ProjectManager.getInstance().getCurrentProject().replaceBackgroundSprite(backgroundSpriteBackup);
			}
		}
	}

	public static class DeleteSpriteCommand implements Command {
		private ArrayList<Sprite> sprites;
		private ArrayList<Integer> positions = new ArrayList<>();
		Map<Sprite, PointToBrick> bricks = new HashMap<>();

		public DeleteSpriteCommand(ArrayList<Sprite> sprites) {
			this.sprites = sprites;
		}

		@Override
		public void execute() {
			for (PointToBrick brick : ProjectManager.getInstance().getCurrentProject().getPointToBricks()) {
				if (sprites.contains(brick.getSprite())) {
					bricks.put(brick.getSprite(), brick);
				}
			}
			for (Sprite sprite : sprites) {
				if (ProjectManager.getInstance().getCurrentSprite() != null && ProjectManager.getInstance().getCurrentSprite().equals(sprite)) {
					ProjectManager.getInstance().setCurrentSprite(null);
				}

				positions.add(ProjectManager.getInstance().getCurrentProject().getSpritePositionById(sprite));
			}
			ProjectManager.getInstance().getCurrentProject().getSpriteList().removeAll(sprites);
		}

		@Override
		public void undo() {
			for (int i = 0; i < sprites.size(); i++) {
				ProjectManager.getInstance().getCurrentProject().getSpriteList().add(positions.get(i), sprites.get(i));
			}

			for (Sprite sprite : bricks.keySet()) {
				bricks.get(sprite).setSprite(sprite);
			}

			positions.clear();
			bricks.clear();
		}
	}

	public static class MoveSpriteCommand implements Command {
		private int previousPosition;
		private int newPosition;

		public MoveSpriteCommand(int previousPosition, int newPosition) {
			this.previousPosition = previousPosition;
			this.newPosition = newPosition;
		}

		@Override
		public void execute() {
			List<Sprite> lookList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
			int direction = newPosition > previousPosition ? 1 : -1;
			int currentPos = previousPosition;
			do {
				Collections.swap(lookList, currentPos, currentPos + direction);
				currentPos += direction;
			} while (currentPos != newPosition);
		}

		@Override
		public void undo() {
			List<Sprite> lookList = ProjectManager.getInstance().getCurrentProject().getSpriteList();
			int direction = previousPosition > newPosition ? 1 : -1;
			int currentPos = newPosition;
			do {
				Collections.swap(lookList, currentPos, currentPos + direction);
				currentPos += direction;
			} while (currentPos != previousPosition);
		}
	}
}
