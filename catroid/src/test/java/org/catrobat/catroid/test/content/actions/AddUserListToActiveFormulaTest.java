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

package org.catrobat.catroid.test.content.actions;

import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.fragment.CategoryListFragment;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;
import org.catrobat.catroid.utils.AddUserListDialog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

import static org.mockito.ArgumentMatchers.any;

import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
@PrepareForTest(AddUserListDialog.class)
public class AddUserListToActiveFormulaTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return asList(new Object[][] {
				{"FunctionNumberOfListItems", R.string.formula_editor_function_number_of_items},
				{"FunctionGetListItem", R.string.formula_editor_function_list_item},
				{"FunctionContainsListItem", R.string.formula_editor_function_contains},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public int listFunction;

	@InjectMocks
	CategoryListFragment categoryListFragment;

	@Mock
	CategoryListRVAdapter.CategoryListItem categoryListItemMock;
	@Mock
	FragmentActivity activityMock;
	@Mock
	UniqueNameProvider uniqueNameProviderMock;
	@Mock
	TextInputDialog.Builder builderMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		uniqueNameProviderMock = PowerMockito.mock(UniqueNameProvider.class);
		PowerMockito.when(uniqueNameProviderMock.isUnique(any())).thenReturn(true);
		builderMock = PowerMockito.mock(TextInputDialog.Builder.class, Mockito.RETURNS_DEEP_STUBS);
		PowerMockito.when(builderMock.setHint(any())).thenReturn(builderMock);
		PowerMockito.when(builderMock.getContext().getString(R.string.default_list_name)).thenReturn("List");
		PowerMockito.when(builderMock.createUniqueNameProvider(R.string.default_list_name)).thenReturn(uniqueNameProviderMock);
	}

	@Test
	public void testNoSpriteAndProjectUserLists() {
		List<UserList> emptyList = new ArrayList<>();
		categoryListFragment.insertLastUserListToActiveFormula(categoryListItemMock,
				emptyList, emptyList, activityMock, builderMock);

		Mockito.verify(builderMock).show();
	}
}
