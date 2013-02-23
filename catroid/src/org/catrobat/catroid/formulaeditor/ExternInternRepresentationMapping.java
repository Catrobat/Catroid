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

	private int externStringLength = 10;

	public ExternInternRepresentationMapping() {
		externInternMapping = new SparseArray<Integer>();
		internExternMapping = new SparseArray<ExternToken>();
	}

	public void putExternInternMapping(int externStartIndex, int externEndIndex, int internIndex) {
		externInternMapping.put(externStartIndex, internIndex);
		externInternMapping.put(externEndIndex, internIndex);

		if (externEndIndex >= externStringLength) {
			externStringLength = externEndIndex + 1;
		}
	}

	public void putInternExternMapping(int internStartIndex, int externStartIndex, int externEndIndex) {
		ExternToken externToken = new ExternToken(externStartIndex, externEndIndex);
		internExternMapping.put(internStartIndex, externToken);

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
		Integer searchUpInternToken = searchUp(externInternMapping, externIndex + 1, externStringLength);

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

	private Integer searchUp(SparseArray<Integer> mapping, int index, int maximalIndex) {
		for (int searchIndex = index; searchIndex < maximalIndex; searchIndex++) {
			if (mapping.get(searchIndex) != null) {
				return mapping.get(searchIndex);
			}
		}
		return null;
	}

}
