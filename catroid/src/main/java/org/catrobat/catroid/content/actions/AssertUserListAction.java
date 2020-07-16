/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.content.actions;

import org.catrobat.catroid.formulaeditor.UserList;

import java.util.List;

public class AssertUserListAction extends AssertAction {

	private UserList actualUserList = null;
	private UserList expectedUserList = null;

	@Override
	public boolean act(float delta) {
		assertTitle = "\nAssertUserListError\n";
		String message = "";

		if (actualUserList == null) {
			failWith("Actual list is null");
			return false;
		}
		if (expectedUserList == null) {
			failWith("Expected list is null");
			return false;
		}

		List<Object> actualList = actualUserList.getValue();
		List<Object> expectedList = expectedUserList.getValue();
		if (actualList.size() != expectedList.size()) {
			message = "The number of list elements are not equal\nexpected: " + expectedList.size()
					+ " element/s\nactual:   " + actualList.size() + " element/s\n\n";
		}

		for (int listPosition = 0; listPosition < expectedList.size(); listPosition++) {
			try {
				if (!equalValues(actualList.get(listPosition).toString(),
						expectedList.get(listPosition).toString())) {
					message = message.concat(formattedAssertError(listPosition,
							actualList.get(listPosition),
							expectedList.get(listPosition)));
				}
			} catch (IndexOutOfBoundsException e) {
				break;
			}
		}

		if (message.isEmpty()) {
			return true;
		} else {
			failWith(message);
			return false;
		}
	}

	private String formattedAssertError(int listPosition, Object actual, Object expected) {
		String indicator = generateIndicator(actual, expected);
		return "position: " + listPosition + "\nexpected: <" + expected + ">\nactual:   <"
				+ actual + ">\ndeviation: " + indicator + "\n\n";
	}

	public void setActual(UserList actual) {
		this.actualUserList = actual;
	}

	public void setExpected(UserList expected) {
		this.expectedUserList = expected;
	}
}
