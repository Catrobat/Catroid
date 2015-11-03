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
	public static class RenameSpriteCommand implements MediaCommand {
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

		@Override
		public void update() {
			Sprite temp = ProjectManager.getInstance().getSpriteById(sprite.getId());
			if (temp != null) {
				sprite = temp;
			}
		}
	}

	public static class AddSpriteCommand implements MediaCommand {
		private ArrayList<Sprite> sprites;
		Map<Sprite, PointToBrick> bricks = new HashMap<>();

		public AddSpriteCommand(ArrayList<Sprite> sprites) {
			this.sprites = sprites;
		}

		@Override
		public void execute() {
			ProjectManager.getInstance().getCurrentProject().getSpriteList().addAll(sprites);

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
			ProjectManager.getInstance().getCurrentProject().getSpriteList().removeAll(sprites);
		}

		@Override
		public void update() {
			for (int i = 0; i < this.sprites.size(); i++) {
				Sprite temp = ProjectManager.getInstance().getSpriteById(sprites.get(i).getId());
				if (temp != null) {
					sprites.remove(i);
					sprites.add(i, temp);
				}
			}

			Map<Sprite, PointToBrick> temp = new HashMap<>();

			for (Map.Entry<Sprite, PointToBrick> entry : bricks.entrySet()) {
				Map.Entry<Sprite, PointToBrick> tempEntry = entry;
				PointToBrick tempBrick = ProjectManager.getInstance().getPointToBrickById(entry.getValue().getId());
				Sprite tempSprite = ProjectManager.getInstance().getSpriteById(entry.getKey().getId());
				if (tempBrick != null) {
					tempEntry.setValue(tempBrick);
				}

				if (tempSprite != null) {
					temp.put(tempSprite, tempEntry.getValue());
				} else {
					temp.put(tempEntry.getKey(), tempEntry.getValue());
				}
			}

			bricks.clear();
			bricks.putAll(temp);
		}
	}

	public static class DeleteSpriteCommand implements MediaCommand {
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

		@Override
		public void update() {
			for (int i = 0; i < this.sprites.size(); i++) {
				Sprite temp = ProjectManager.getInstance().getSpriteById(sprites.get(i).getId());
				if (temp != null) {
					sprites.remove(i);
					sprites.add(i, temp);
				}
			}

			bricks.clear();

			for (PointToBrick brick : ProjectManager.getInstance().getCurrentProject().getPointToBricks()) {
				if (sprites.contains(brick.getSprite())) {
					bricks.put(brick.getSprite(), brick);
				}
			}
		}
	}

	public static class MoveSpriteCommand implements MediaCommand {
		private int previousPosition;
		private int newPosition;

		public MoveSpriteCommand(int previousPosition, int newPosition) {
			this.previousPosition = previousPosition;
			this.newPosition = newPosition;
		}

		@Override
		public void execute() {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();
			Collections.swap(sprites, previousPosition, newPosition);
		}

		@Override
		public void undo() {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();
			Collections.swap(sprites, newPosition, previousPosition);
		}

		@Override
		public void update() {
		}
	}

	public static class MoveSpriteToBottomCommand implements MediaCommand {
		private int position;

		public MoveSpriteToBottomCommand(int position) {
			this.position = position;
		}

		@Override
		public void execute() {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();

			for (int i = position; i < sprites.size() - 1; i++) {
				Collections.swap(sprites, i, i + 1);
			}
		}

		@Override
		public void undo() {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();

			for (int i = sprites.size() - 1; i > position; i--) {
				Collections.swap(sprites, i, i - 1);
			}
		}

		@Override
		public void update() {
		}
	}

	public static class MoveSpriteToTopCommand implements MediaCommand {
		private int position;

		public MoveSpriteToTopCommand(int position) {
			this.position = position;
		}

		@Override
		public void execute() {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();

			for (int i = position; i > 1; i--) {
				Collections.swap(sprites, i, i - 1);
			}
		}

		@Override
		public void undo() {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentProject().getSpriteList();

			for (int i = 1; i < position; i++) {
				Collections.swap(sprites, i, i + 1);
			}
		}

		@Override
		public void update() {
		}
	}
}
