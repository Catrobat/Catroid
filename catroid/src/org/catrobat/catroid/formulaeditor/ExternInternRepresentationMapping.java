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
package org.catrobat.catroid.formulaeditor;

import android.util.SparseArray;
import android.util.SparseIntArray;

public class ExternInternRepresentationMapping {

	private SparseIntArray externInternMapping;
	private SparseArray<ExternToken> internExternMapping;

	public static final int MAPPING_NOT_FOUND = Integer.MIN_VALUE;

	private int externStringLength = 0;

	public ExternInternRepresentationMapping() {
		externInternMapping = new SparseIntArray();
		internExternMapping = new SparseArray<ExternToken>();
	}

	public void putMapping(int externStringStartIndex, int externStringEndIndex, int internListIndex) {
		externInternMapping.put(externStringStartIndex, internListIndex);

		// Set externStringEndIndex -1 because of token separation.
		// Otherwise, tokens would overlap and mapping would fail.
		externInternMapping.put(externStringEndIndex - 1, internListIndex);

		ExternToken externToken = new ExternToken(externStringStartIndex, externStringEndIndex);
		internExternMapping.put(internListIndex, externToken);

		if (externStringEndIndex >= externStringLength) {
			externStringLength = externStringEndIndex;
		}
	}

	public int getExternTokenStartIndex(int internIndex) {
		ExternToken externToken = internExternMapping.get(internIndex);

		if (externToken == null) {
			return MAPPING_NOT_FOUND;
		}

		return externToken.getStartIndex();
	}

	public int getExternTokenEndIndex(int internIndex) {
		ExternToken externToken = internExternMapping.get(internIndex);

		if (externToken == null) {
			return MAPPING_NOT_FOUND;
		}

		return externToken.getEndIndex();
	}

	public int getInternTokenByExternIndex(int externIndex) {

		if (externIndex < 0) {
			return MAPPING_NOT_FOUND;
		}

		int searchDownInternToken = searchDown(externInternMapping, externIndex - 1);
		int currentInternToken = externInternMapping.get(externIndex, MAPPING_NOT_FOUND);
		int searchUpInternToken = searchUp(externInternMapping, externIndex + 1);

		if (currentInternToken != MAPPING_NOT_FOUND) {
			return currentInternToken;
		}
		if (searchDownInternToken != MAPPING_NOT_FOUND && searchUpInternToken != MAPPING_NOT_FOUND
				&& searchDownInternToken == searchUpInternToken) {
			return searchDownInternToken;
		}

		return MAPPING_NOT_FOUND;
	}

	public int getExternTokenStartOffset(int externIndex, int internTokenOffsetTo) {
		for (int searchIndex = externIndex; searchIndex >= 0; searchIndex--) {
			if (externInternMapping.get(searchIndex, MAPPING_NOT_FOUND) != MAPPING_NOT_FOUND
					&& externInternMapping.get(searchIndex, MAPPING_NOT_FOUND) == internTokenOffsetTo) {
				int rightEdgeSelectionToken = getExternTokenStartOffset(searchIndex - 1, internTokenOffsetTo);
				if (rightEdgeSelectionToken == -1) {
					return externIndex - searchIndex;
				}
				return externIndex - searchIndex + rightEdgeSelectionToken + 1;
			}
		}
		return -1;
	}

	private int searchDown(SparseIntArray mapping, int index) {
		for (int searchIndex = index; searchIndex >= 0; searchIndex--) {
			if (mapping.get(searchIndex, MAPPING_NOT_FOUND) != MAPPING_NOT_FOUND) {
				return mapping.get(searchIndex);
			}
		}
		return MAPPING_NOT_FOUND;
	}

	private int searchUp(SparseIntArray mapping, int index) {
		for (int searchIndex = index; searchIndex < externStringLength; searchIndex++) {
			if (mapping.get(searchIndex, MAPPING_NOT_FOUND) != MAPPING_NOT_FOUND) {
				return mapping.get(searchIndex);
			}
		}
		return MAPPING_NOT_FOUND;
	}
}
