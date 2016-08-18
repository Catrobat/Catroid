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
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.bricks.SetLookBrick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookCommands {

	public static class RenameLookCommand implements Command {
		private String oldName;
		private String newName;
		private LookData lookData;

		public RenameLookCommand(LookData lookData, String newName) {
			this.oldName = lookData.getLookName();
			this.newName = newName;
			this.lookData = lookData;
		}

		@Override
		public void execute() {
			lookData.setLookName(newName);
		}

		@Override
		public void undo() {
			lookData.setLookName(oldName);
		}
	}

	public static class AddLookCommand implements Command {
		private ArrayList<LookData> lookDatas;
		Map<LookData, SetLookBrick> bricks = new HashMap<>();

		public AddLookCommand(ArrayList<LookData> lookDatas) {
			this.lookDatas = lookDatas;
		}

		@Override
		public void execute() {
			ProjectManager.getInstance().getCurrentSprite().getLookDataList().addAll(lookDatas);

			for (LookData lookData : bricks.keySet()) {
				bricks.get(lookData).setLook(lookData);
			}

			bricks.clear();
		}

		@Override
		public void undo() {
			for (SetLookBrick brick : ProjectManager.getInstance().getCurrentSprite().getSetLookBricks()) {
				if (lookDatas.contains(brick.getLook())) {
					bricks.put(brick.getLook(), brick);
				}
			}
			ProjectManager.getInstance().getCurrentSprite().getLookDataList().removeAll(lookDatas);
		}
	}

	public static class DeleteLookCommand implements Command {
		private ArrayList<LookData> lookDatas;
		private ArrayList<Integer> positions = new ArrayList<>();
		Map<LookData, SetLookBrick> bricks = new HashMap<>();

		public DeleteLookCommand(ArrayList<LookData> lookDatas) {
			this.lookDatas = lookDatas;
		}

		@Override
		public void execute() {
			for (SetLookBrick brick : ProjectManager.getInstance().getCurrentSprite().getSetLookBricks()) {
				if (lookDatas.contains(brick.getLook())) {
					bricks.put(brick.getLook(), brick);
				}
			}
			for (LookData lookData : lookDatas) {
				positions.add(ProjectManager.getInstance().getCurrentSprite().getLookPositionById(lookData));
			}
			ProjectManager.getInstance().getCurrentSprite().getLookDataList().removeAll(lookDatas);
		}

		@Override
		public void undo() {
			for (int i = 0; i < lookDatas.size(); i++) {
				ProjectManager.getInstance().getCurrentSprite().getLookDataList().add(positions.get(i), lookDatas.get(i));
			}

			for (LookData lookData : bricks.keySet()) {
				bricks.get(lookData).setLook(lookData);
			}

			positions.clear();
			bricks.clear();
		}
	}

	public static class EditLookCommand implements Command {
		private LookData lookData;
		private String oldFileName;
		private String newFileName;

		public EditLookCommand(LookData lookData, String oldFileName, String newFileName) {
			this.lookData = lookData;
			this.oldFileName = oldFileName;
			this.newFileName = newFileName;
		}

		@Override
		public void execute() {
			lookData.setLookFilename(newFileName);
			lookData.resetThumbnailBitmap();
		}

		@Override
		public void undo() {
			lookData.setLookFilename(oldFileName);
			lookData.resetThumbnailBitmap();
		}
	}

	public static class MoveLookCommand implements Command {
		private int previousPosition;
		private int newPosition;

		public MoveLookCommand(int previousPosition, int newPosition) {
			this.previousPosition = previousPosition;
			this.newPosition = newPosition;
		}

		@Override
		public void execute() {
			List<LookData> lookList = ProjectManager.getInstance().getCurrentSprite().getLookDataList();
			int direction = newPosition > previousPosition ? 1 : -1;
			int currentPos = previousPosition;
			do {
				Collections.swap(lookList, currentPos, currentPos + direction);
				currentPos += direction;
			} while (currentPos != newPosition);
		}

		@Override
		public void undo() {
			List<LookData> lookList = ProjectManager.getInstance().getCurrentSprite().getLookDataList();
			int direction = previousPosition > newPosition ? 1 : -1;
			int currentPos = newPosition;
			do {
				Collections.swap(lookList, currentPos, currentPos + direction);
				currentPos += direction;
			} while (currentPos != previousPosition);
		}
	}
}
