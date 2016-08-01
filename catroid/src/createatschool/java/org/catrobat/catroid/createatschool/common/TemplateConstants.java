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
package org.catrobat.catroid.createatschool.common;

public final class TemplateConstants {

	// Suppress default constructor for noninstantiability
	private TemplateConstants() {
		throw new AssertionError();
	}

	public static final String TEMPLATE_ACTION_LANDSCAPE_FILENAME = "template action landscape.zip";
	public static final String TEMPLATE_ADVENTURE_LANDSCAPE_FILENAME = "template adventure landscape.zip";
	public static final String TEMPLATE_PUZZLE_LANDSCAPE_FILENAME = "template puzzle landscape.zip";
	public static final String TEMPLATE_QUIZ_LANDSCAPE_FILENAME = "template quiz landscape.zip";
	public static final String TEMPLATE_ACTION_PORTRAIT_FILENAME = "template action portrait.zip";
	public static final String TEMPLATE_ADVENTURE_PORTRAIT_FILENAME = "template adventure portrait.zip";
	public static final String TEMPLATE_PUZZLE_PORTRAIT_FILENAME = "template puzzle portrait.zip";
	public static final String TEMPLATE_QUIZ_PORTRAIT_FILENAME = "template quiz portrait.zip";

	public static final String TEMPLATE_ACTION_NAME = "Action";
	public static final String TEMPLATE_ADVENTURE_NAME = "Adventure";
	public static final String TEMPLATE_PUZZLE_NAME = "Puzzle";
	public static final String TEMPLATE_QUIZ_NAME = "Quiz";

	public static final int TEMPLATE_ACTION_IMAGE_NAME = org.catrobat.catroid.R.drawable.action;
	public static final int TEMPLATE_ADVENTURE_IMAGE_NAME = org.catrobat.catroid.R.drawable.adventure;
	public static final int TEMPLATE_PUZZLE_IMAGE_NAME = org.catrobat.catroid.R.drawable.puzzle;
	public static final int TEMPLATE_QUIZ_IMAGE_NAME = org.catrobat.catroid.R.drawable.quiz;
}
