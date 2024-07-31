/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test

import org.catrobat.catroid.ProjectManager
import org.junit.After
import org.junit.Before
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock
import org.powermock.api.mockito.PowerMockito

open class UnitTestWithMockedContext : KoinTest {
    private var _koin: Koin? = null

    lateinit var projectManager: ProjectManager

    @Before
    fun initEach() {
        _koin = startKoin(appDeclaration = { modules(module { }) }).koin
        MockProvider.register { clazz -> PowerMockito.mock(clazz.java) }
        projectManager = declareMock<ProjectManager>()
    }

    @After
    fun afterEach() {
        // Context has to be stopped manually when the test fails.
        stopKoin()
        _koin = null
    }
}
