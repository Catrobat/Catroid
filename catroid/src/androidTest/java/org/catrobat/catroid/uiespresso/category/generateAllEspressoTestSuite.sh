#!/bin/bash

#put header
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
 */\n" > AllEspressoTestSuite.java

#put packagename
echo "package org.catrobat.catroid.uiespresso.category;\n" >> AllEspressoTestSuite.java

#put imports
find .. -name '*Test.java' | sed 's/\.\.\//import org.catrobat.catroid.uiespresso./' | sed 's/\//\./g' | sed 's/.java/;/' | sort >> AllEspressoTestSuite.java

#put annotations
echo "import org.junit.runner.RunWith;\nimport org.junit.runners.Suite;\n\n@RunWith(Suite.class)\n@Suite.SuiteClasses({" >> AllEspressoTestSuite.java

#put classes to be importet
find .. -name '*Test.java' | sed 's/^.*\///' | sed 's/java/class/' | sed 's/$/,/' | sed '$s/,//' | sed 's/^/		/' >> AllEspressoTestSuite.java

#put class
echo "})\npublic class AllEspressoTestSuite {\n}" >> AllEspressoTestSuite.java

exit
