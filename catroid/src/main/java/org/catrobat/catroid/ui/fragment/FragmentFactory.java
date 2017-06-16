/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.ui.fragment;

import android.app.Fragment;
import android.content.Context;

final class FragmentFactory {

	private FragmentFactory() {
	}

	static FormulaEditorCategoryListFragment createFormulaEditorCategoryListFragment(Context context) {
		String fragmentName = FormulaEditorCategoryListFragment.flavoredTag;
		return (FormulaEditorCategoryListFragment) FragmentFactory.createFragment(fragmentName, context);
	}

	static BrickCategoryFragment createBrickCategoryFragment(Context context) {
		String fragmentName = BrickCategoryFragment.flavoredTag;
		return (BrickCategoryFragment) FragmentFactory.createFragment(fragmentName, context);
	}

	private static Fragment createFragment(String className, Context context) {
		String classNameWithPackage = context.getPackageName() + "." + className;

		if (!classNameExists(classNameWithPackage, context)) {
			classNameWithPackage = "org.catrobat.catroid" + "." + className;
		}

		return Fragment.instantiate(context, classNameWithPackage);
	}

	private static boolean classNameExists(String className, Context context) {
		boolean classExists;
		try {
			Class.forName(className, false, context.getClass().getClassLoader());
			classExists = true;
		} catch (ClassNotFoundException e) {
			classExists = false;
		}
		return classExists;
	}
}
