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

package org.catrobat.catroidfeature.machinelearning

import android.content.Context
import com.google.firebase.components.ComponentRegistrar
import com.google.mlkit.common.internal.CommonComponentRegistrar
import com.google.mlkit.common.sdkinternal.MlKitContext
import com.google.mlkit.dynamic.DynamicLoadingRegistrar
import com.google.mlkit.nl.languageid.internal.LanguageIdRegistrar
import com.google.mlkit.vision.common.internal.VisionCommonRegistrar
import com.google.mlkit.vision.face.internal.FaceRegistrar
import com.google.mlkit.vision.objects.defaults.internal.DefaultObjectsRegistrar
import com.google.mlkit.vision.pose.internal.PoseRegistrar
import com.google.mlkit.vision.text.internal.TextRegistrar
import com.google.mlkit.vision.vkp.VkpRegistrar
import org.catrobat.catroid.utils.MachineLearningModule

object MachineLearningModule : MachineLearningModule {
    override fun init(context: Context) {
        val arr = mutableListOf<ComponentRegistrar>(
            DynamicLoadingRegistrar(),
            CommonComponentRegistrar(),
            FaceRegistrar(),
            TextRegistrar(),
            DefaultObjectsRegistrar(),
            LanguageIdRegistrar(),
            PoseRegistrar(),
            VkpRegistrar(),
            VisionCommonRegistrar(),
            )
        MlKitContext.initialize(context, arr)
    }
}