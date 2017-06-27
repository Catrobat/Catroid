/*
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

public interface Cat {
	//AppUi for all  tests focusing on the pocket code application, so the menus, fragments, lists, and their functionality
	interface AppUi {
	}

	//CatrobatLanguage for all  tests focusing on catrobat language correctness (eg. tests verify in stage correctness
	//of some catrobat program
	interface CatrobatLanguage {
	}

	//Device for all tests that are required to run / are only runnable on a physical android device
	interface Device {
	}

	//Gadgets for all  tests focusing on peripheral hardware and gadgets (eg. RasPi, Drone, LegoNXT, etc.)
	interface Gadgets {
	}

	//Network for all tests that do require an internet connection and or any network services, etc.
	interface Network {
	}

	//SensorBox include all tests utilizing the Sensor testing box
	interface SensorBox {
	}
}
