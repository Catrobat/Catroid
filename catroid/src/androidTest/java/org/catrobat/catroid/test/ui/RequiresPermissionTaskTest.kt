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
package org.catrobat.catroid.test.ui

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

@RunWith(JUnit4::class)
class RequiresPermissionTaskTest {
    private lateinit var task: RequiresPermissionTask
    private lateinit var activity: Activity
    private val permission = "permission"
    private var taskCalled = false
    @Before
    fun setUp() {
        activity = Mockito.mock(AppCompatActivity::class.java)
        val permissions: MutableList<String> = ArrayList()
        permissions.add(permission)
        task = object : RequiresPermissionTask(0, permissions, 0) {
            override fun task() {
                taskCalled = true
            }
        }
    }

    @Test
    fun executeTaskIfPermissionGrantedTest() {
        Mockito.`when`(
            activity.checkPermission(
                ArgumentMatchers.eq(permission),
                ArgumentMatchers.anyInt(),
                ArgumentMatchers.anyInt()
            )
        ).thenReturn(PackageManager.PERMISSION_GRANTED)
        task.execute(activity)
        Assert.assertTrue(taskCalled)
    }
}