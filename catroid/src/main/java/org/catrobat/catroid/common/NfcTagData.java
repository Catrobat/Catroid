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
package org.catrobat.catroid.common;

import java.io.Serializable;
import java.util.Objects;

import androidx.annotation.Nullable;

public class NfcTagData implements Cloneable, Comparable<NfcTagData>, Nameable, Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String uid;

	@Override
	public NfcTagData clone() {
		NfcTagData cloneNfcTagData = new NfcTagData();

		cloneNfcTagData.name = this.name;
		cloneNfcTagData.uid = this.uid;

		return cloneNfcTagData;
	}

	public NfcTagData() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNfcTagUid(String uid) {
		this.uid = uid;
	}

	public String getNfcTagUid() {
		return uid;
	}

	@Override
	public int compareTo(NfcTagData nfcTagData) {
		return uid.compareTo(nfcTagData.uid);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj instanceof NfcTagData) {
			NfcTagData nfcTagData = (NfcTagData) obj;
			return uid.equals(nfcTagData.getNfcTagUid());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, uid);
	}
}
