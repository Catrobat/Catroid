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
import org.catrobat.catroid.common.NfcTagData;
import org.catrobat.catroid.content.bricks.WhenNfcBrick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NfcDataCommands {

	public static class RenameNfcCommand implements Command {
		private String oldName;
		private String newName;
		private NfcTagData nfcTagData;

		public RenameNfcCommand(NfcTagData nfcTagData, String newName) {
			this.oldName = nfcTagData.getNfcTagName();
			this.newName = newName;
			this.nfcTagData = nfcTagData;
		}

		@Override
		public void execute() {
			nfcTagData.setNfcTagName(newName);
		}

		@Override
		public void undo() {
			nfcTagData.setNfcTagName(oldName);
		}
	}

	public static class AddNfcCommand implements Command {
		private ArrayList<NfcTagData> nfcTagDatas;
		Map<NfcTagData, WhenNfcBrick> bricks = new HashMap<>();

		public AddNfcCommand(ArrayList<NfcTagData> nfcTagDatas) {
			this.nfcTagDatas = nfcTagDatas;
		}

		@Override
		public void execute() {
			ProjectManager.getInstance().getCurrentSprite().getNfcTagList().addAll(nfcTagDatas);

			for (NfcTagData nfcTagData : bricks.keySet()) {
				bricks.get(nfcTagData).setNfcTag(nfcTagData);
			}

			bricks.clear();
		}

		@Override
		public void undo() {
			for (WhenNfcBrick brick : ProjectManager.getInstance().getCurrentSprite().getNfcBrickList()) {
				if (nfcTagDatas.contains(brick.getNfcTag())) {
					bricks.put(brick.getNfcTag(), brick);
				}
			}
			ProjectManager.getInstance().getCurrentSprite().getNfcTagList().removeAll(nfcTagDatas);
		}
	}

	public static class DeleteNfcCommand implements Command {
		private ArrayList<NfcTagData> nfcTagDatas;
		private ArrayList<Integer> positions = new ArrayList<>();
		Map<NfcTagData, WhenNfcBrick> bricks = new HashMap<>();

		public DeleteNfcCommand(ArrayList<NfcTagData> nfcTagDatas) {
			this.nfcTagDatas = nfcTagDatas;
		}

		@Override
		public void execute() {
			for (WhenNfcBrick brick : ProjectManager.getInstance().getCurrentSprite().getNfcBrickList()) {
				if (nfcTagDatas.contains(brick.getNfcTag())) {
					bricks.put(brick.getNfcTag(), brick);
				}
			}
			for (NfcTagData nfcTagData : nfcTagDatas) {
				positions.add(ProjectManager.getInstance().getCurrentSprite().getNfcPositionById(nfcTagData));
			}
			ProjectManager.getInstance().getCurrentSprite().getNfcTagList().removeAll(nfcTagDatas);
		}

		@Override
		public void undo() {
			for (int i = 0; i < nfcTagDatas.size(); i++) {
				ProjectManager.getInstance().getCurrentSprite().getNfcTagList().add(positions.get(i), nfcTagDatas.get(i));
			}

			for (NfcTagData nfcTagData : bricks.keySet()) {
				bricks.get(nfcTagData).setNfcTag(nfcTagData);
			}

			positions.clear();
			bricks.clear();
		}
	}

	public static class MoveNfcCommand implements Command {
		private int previousPosition;
		private int newPosition;

		public MoveNfcCommand(int previousPosition, int newPosition) {
			this.previousPosition = previousPosition;
			this.newPosition = newPosition;
		}

		@Override
		public void execute() {
			List<NfcTagData> lookList = ProjectManager.getInstance().getCurrentSprite().getNfcTagList();
			int direction = newPosition > previousPosition ? 1 : -1;
			int currentPos = previousPosition;
			do {
				Collections.swap(lookList, currentPos, currentPos + direction);
				currentPos += direction;
			} while (currentPos != newPosition);
		}

		@Override
		public void undo() {
			List<NfcTagData> lookList = ProjectManager.getInstance().getCurrentSprite().getNfcTagList();
			int direction = previousPosition > newPosition ? 1 : -1;
			int currentPos = newPosition;
			do {
				Collections.swap(lookList, currentPos, currentPos + direction);
				currentPos += direction;
			} while (currentPos != previousPosition);
		}
	}
}
