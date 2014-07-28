/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.physics;

public final class PhysicsDebugSettings {

	private PhysicsDebugSettings(){} // Make sure that utility classes (classes that contain only static methods or fields in their API) do not have a public constructor.

	public static class Render {
		public static final boolean RENDER_COLLISION_FRAMES = false;
		public static final boolean RENDER_BODIES = false;
		public static final boolean RENDER_JOINTS = false;
		public static final boolean RENDER_AABB = false;
		public static final boolean RENDER_INACTIVE_BODIES = false;
		public static final boolean RENDER_VELOCITIES = false;
		public static final boolean RENDER_PHYSIC_OBJECT_LABELING = false;
	}
}
