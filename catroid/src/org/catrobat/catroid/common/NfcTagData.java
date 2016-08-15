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
package org.catrobat.catroid.common;

import org.catrobat.catroid.utils.IdPool;

import java.io.Serializable;

public class NfcTagData implements Serializable, Comparable<NfcTagData>, Cloneable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String uid;
	private transient int id = IdPool.getInstance().getNewId();
	public transient boolean isScanning;

	@Override
	public NfcTagData clone() {
		NfcTagData cloneNfcTagData = new NfcTagData();

		cloneNfcTagData.name = this.name;
		cloneNfcTagData.uid = this.uid;

		return cloneNfcTagData;
	}

	public void resetNfcTagData() {
	}

	public NfcTagData() {
	}

	public String getNfcTagName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setNfcTagName(String name) {
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
}
