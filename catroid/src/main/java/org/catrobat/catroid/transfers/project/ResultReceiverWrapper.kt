/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.transfers.project

import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import java.lang.ref.WeakReference

class ResultReceiverWrapper(
    resultReceiverWrapperInterface: ResultReceiverWrapperInterface,
    handler: Handler
) : ResultReceiver(handler) {
    val resultReceiverWrapperInterface = WeakReference<ResultReceiverWrapperInterface>(resultReceiverWrapperInterface)

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        resultReceiverWrapperInterface.get()?.onReceiveResult(resultCode, resultData)
    }
}

interface ResultReceiverWrapperInterface {
    fun onReceiveResult(resultCode: Int, resultData: Bundle?)
}
