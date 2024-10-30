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

package org.catrobat.catroid.ui.recyclerview.util;

import org.catrobat.catroid.common.Nameable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UniqueNameProvider implements UniqueNameProviderInterface {
	private List<String> scope;

	public String getUniqueName(String name, List<String> scope) {
		this.scope = scope;

		if (isUnique(name)) {
			return name;
		}

		Pattern pattern = Pattern.compile("\\((\\d+)\\)");
		Matcher matcher = pattern.matcher(name);

		int n = 1;

		if (matcher.find()) {
			name = name.replace(matcher.group(0), "").trim();
			n = Integer.parseInt(matcher.group(1));
		}

		while (n < Integer.MAX_VALUE) {
			String newName = name + " (" + n + ")";
			if (isUnique(newName)) {
				return newName;
			}
			n++;
		}

		return name;
	}

	@Override
	public boolean isUnique(String newName) {
		return !scope.contains(newName);
	}

	public String getUniqueNameInNameables(String name, List<? extends Nameable> scope) {
		List<String> names = new ArrayList<>();
		for (Nameable nameable : scope) {
			names.add(nameable.getName());
		}
		return getUniqueName(name, names);
	}
}
