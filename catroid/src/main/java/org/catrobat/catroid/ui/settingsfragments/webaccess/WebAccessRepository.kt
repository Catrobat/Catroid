/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.ui.settingsfragments.webaccess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.catrobat.catroid.TrustedDomainManager

// TODO store in DB
interface WebAccessRepository {

    fun addToUserTrustList(domain: String)

    fun getUserTrustList(): LiveData<List<TrustedDomain>>

    fun deleteFromUserTrustDomain(domains: List<String>)
}

class DefaultWebAccessRepository : WebAccessRepository {

    private val domains = MutableLiveData<List<TrustedDomain>>()

    override fun addToUserTrustList(domain: String) {
        TrustedDomainManager.addToUserTrustList(domain)
        refresh()
    }

    override fun getUserTrustList(): LiveData<List<TrustedDomain>> {
        refresh()
        return domains
    }

    override fun deleteFromUserTrustDomain(domains: List<String>) {
        TrustedDomainManager.removeFromUserTrustList(domains)
        refresh()
    }

    private fun refresh() {
        domains.postValue(TrustedDomainManager.getUserTrustList().toTrustDomainList())
    }

    private fun String.toTrustDomainList(): List<TrustedDomain> {
        val domains = mutableListOf<TrustedDomain>()
        this.takeIf { it.isNotBlank() }?.let {
            split("\n").forEach {
                domains.add(TrustedDomain(it))
            }
        }
        return domains
    }
}
