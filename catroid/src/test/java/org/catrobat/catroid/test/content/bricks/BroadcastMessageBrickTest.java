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

package org.catrobat.catroid.test.content.bricks;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.eq;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@RunWith(Parameterized.class)
public class BroadcastMessageBrickTest {

	private static String defaultValueString = "defaultString";
	private static String newOptionString = "new...";
	private static String editOptionString = "edit...";
	private Context context;

	@Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return asList(new Object[][] {
				{"MultipleCharsWithDifferentCase", asList("a", "R", "x"), asList(newOptionString, editOptionString, "a", "R", "x")},
				{"MultipleNumbers", asList("50", "3", "12"), asList(newOptionString, editOptionString, "12", "3", "50")},
				{"WithSpecialCharacters", asList(".", "a", ":", "_b", "c"), asList(newOptionString, editOptionString, ".", ":", "_b", "a", "c")},
				{"NoMessage", new ArrayList<>(), singletonList(defaultValueString)},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameter(1)
	public List<String> messages;

	@Parameter(2)
	public List<String> expectedOutput;

	@Before
	public void setUp() throws Exception {
		context = Mockito.mock(Context.class);
		Mockito.when(context.getString(eq(R.string.brick_broadcast_default_value))).thenReturn(defaultValueString);
		Mockito.when(context.getString(eq(R.string.new_option))).thenReturn(newOptionString);
		Mockito.when(context.getString(eq(R.string.edit_option))).thenReturn(editOptionString);
	}

	@Test
	public void testGetSortedItemListFromMessages() {
		List<Nameable> output = BroadcastMessageBrick.getSortedItemListFromMessages(context, messages);
		List<String> outputStrings = output.stream().map(Nameable::getName).collect(Collectors.toList());
		Assert.assertThat(outputStrings, CoreMatchers.equalTo(expectedOutput));
	}
}
