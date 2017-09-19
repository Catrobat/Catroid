#!/bin/bash

IMPORTS="$(find .. -name '*Test.java' | sed 's/\.\.\//import org.catrobat.catroid.uiespresso./' | sed 's/\//\./g' | sed 's/.java/;/' | sort)"
IMPORTED_CLASSES="$(find .. -name '*Test.java' | sed 's/^.*\///' | sed 's/java/class/' | sed 's/$/,/' | sed '$s/,//' | sed 's/^/		/')"

echo "/*
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

package org.catrobat.catroid.uiespresso.testsuites;

${IMPORTS}
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
${IMPORTED_CLASSES}
})
public class AllEspressoTestsDebugSuite {
}" > AllEspressoTestsDebugSuite.java

exit