/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import android.util.SparseArray;

public class ExternInternRepresentationMapping {

	private SparseArray<Integer> externInternMapping;
	private SparseArray<ExternToken> internExternMapping;

	private int externStringLength = 0;

	public ExternInternRepresentationMapping() {
		externInternMapping = new SparseArray<Integer>();
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

	public Integer getExternTokenStartIndex(int internIndex) {
		ExternToken externToken = internExternMapping.get(internIndex);

		if (externToken == null) {
			return null;
		}

		return externToken.getStartIndex();
	}

	public Integer getExternTokenEndIndex(int internIndex) {
		ExternToken externToken = internExternMapping.get(internIndex);

		if (externToken == null) {
			return null;
		}

		return externToken.getEndIndex();
	}

	public Integer getInternTokenByExternIndex(int externIndex) {

		if (externIndex < 0) {
			return null;
		}

		Integer searchDownInternToken = searchDown(externInternMapping, externIndex - 1);
		Integer currentInternToken = externInternMapping.get(externIndex);
		Integer searchUpInternToken = searchUp(externInternMapping, externIndex + 1);

		if (currentInternToken != null) {
			return currentInternToken;
		}
		if (searchDownInternToken != null && searchUpInternToken != null) {
			if (searchDownInternToken.equals(searchUpInternToken)) {
				return searchDownInternToken;
			}
		}

		return null;
	}

	public int getExternTokenStartOffset(int externIndex, Integer internTokenOffsetTo) {
		for (int searchIndex = externIndex; searchIndex >= 0; searchIndex--) {
			if (externInternMapping.get(searchIndex) == null) {
			} else if (externInternMapping.get(searchIndex).equals(internTokenOffsetTo)) {
				int rightEdgeSelectionToken = getExternTokenStartOffset(searchIndex - 1, internTokenOffsetTo);
				if (rightEdgeSelectionToken == -1) {
					return externIndex - searchIndex;
				} else {
					return externIndex - searchIndex + rightEdgeSelectionToken + 1;
				}
			}
		}
		return -1;
	}

	private Integer searchDown(SparseArray<Integer> mapping, int index) {

		for (int searchIndex = index; searchIndex >= 0; searchIndex--) {
			if (mapping.get(searchIndex) != null) {
				return mapping.get(searchIndex);
			}
		}
		return null;
	}

	private Integer searchUp(SparseArray<Integer> mapping, int index) {
		for (int searchIndex = index; searchIndex < externStringLength; searchIndex++) {
			if (mapping.get(searchIndex) != null) {
				return mapping.get(searchIndex);
			}
		}
		return null;
	}

}
