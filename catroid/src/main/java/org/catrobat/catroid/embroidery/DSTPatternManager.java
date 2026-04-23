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

package org.catrobat.catroid.embroidery;

import org.catrobat.catroid.content.Sprite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class DSTPatternManager implements EmbroideryPatternManager {

	private TreeMap<Integer, EmbroideryWorkSpace> layerWorkspaceMap = new TreeMap<>();
	private TreeMap<Integer, EmbroideryStream> layerStreamMap = new TreeMap<>();
	private HashMap<Sprite, StitchCommand> lastCommandOfSpriteMap = new HashMap<>();

	@Override
	public void addStitchCommand(StitchCommand stitchCommand) {
		if (!layerWorkspaceMap.containsKey(stitchCommand.getLayer())) {
			layerWorkspaceMap.put(stitchCommand.getLayer(), new DSTWorkSpace());
			layerStreamMap.put(stitchCommand.getLayer(), new DSTStream(new DSTHeader()));
		}
		stitchCommand.act(layerWorkspaceMap.get(stitchCommand.getLayer()), layerStreamMap.get(stitchCommand.getLayer()),
				lastCommandOfSpriteMap.get(stitchCommand.getSprite()));
		lastCommandOfSpriteMap.put(stitchCommand.getSprite(), stitchCommand);
	}

	@Override
	public ArrayList<StitchPoint> getEmbroideryPatternList() {
		return getEmbroideryStream().getPointList();
	}

	@Override
	public EmbroideryStream getEmbroideryStream() {
		EmbroideryStream stream = new DSTStream(new DSTHeader());
		if (layerStreamMap.isEmpty()) {
			return stream;
		}
		Iterator<Map.Entry<Integer, EmbroideryStream>> iterator = layerStreamMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = iterator.next();
			ArrayList<StitchPoint> stitchPoints = ((EmbroideryStream) entry.getValue()).getPointList();
			stream.addAllStitchPoints(stitchPoints);
			if (iterator.hasNext() && !stitchPoints.isEmpty()) {
				StitchPoint lastValidPoint = stitchPoints.get(stitchPoints.size() - 1);
				stream.addColorChange();
				stream.addStitchPoint(lastValidPoint.getX(), lastValidPoint.getY(), lastValidPoint.getColor());
			}
		}
		return stream;
	}

	@Override
	public boolean validPatternExists() {
		ArrayList<StitchPoint> embroideryList = new ArrayList<>();
		for (Map.Entry entry : layerStreamMap.entrySet()) {
			embroideryList.addAll(((EmbroideryStream) entry.getValue()).getPointList());
		}
		return (embroideryList.size() > 1);
	}

	@Override
	public void clear() {
		layerWorkspaceMap.clear();
		layerStreamMap.clear();
		lastCommandOfSpriteMap.clear();
	}
}
