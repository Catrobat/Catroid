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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.userbrick.UserDefinedBrickData;
import org.catrobat.catroid.userbrick.UserDefinedBrickInput;
import org.catrobat.catroid.userbrick.UserDefinedBrickLabel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;

import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class UserDefinedBrickEqualityTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return asList(new Object[][] {
				{"SpriteBrickEmpty",
						Collections.singletonList(defaultInput),
						new ArrayList<>(),
						false},
				{"SameUserInputs",
						Collections.singletonList(defaultInput),
						Collections.singletonList(defaultInput),
						true},
				{"SameUserLabels",
						Collections.singletonList(defaultLabel),
						Collections.singletonList(defaultLabel),
						true},
				{"DifferentLabelsWhitespaces",
						Collections.singletonList(new UserDefinedBrickLabel(" ")),
						Collections.singletonList(new UserDefinedBrickLabel("")),
						false},
				{"DifferentAmountOfUserInput",
						asList(defaultInput, differentInput),
						Collections.singletonList(defaultInput),
						false},
				{"DifferentAmountOfUserData",
						asList(defaultLabel, defaultInput),
						Collections.singletonList(defaultInput),
						false},
				{"SameUserDataWithDifferentInput",
						asList(defaultLabel, defaultInput, defaultLabel),
						asList(defaultLabel, differentInput, defaultLabel),
						true},
				{"DifferentUserLabelsWithDifferentInput",
						asList(defaultLabel, defaultInput, differentLabel),
						asList(defaultLabel, differentInput, defaultLabel),
						false},
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public List<UserDefinedBrickData> brickToTestBrickData;

	@Parameterized.Parameter(2)
	public List<UserDefinedBrickData> alreadyDefinedUserBrickOfSpriteBrickData;

	@Parameterized.Parameter(3)
	public boolean expectedOutput;

	@Spy
	private Sprite spriteMock;

	private static UserDefinedBrickLabel defaultLabel = new UserDefinedBrickLabel("Label");
	private static UserDefinedBrickLabel differentLabel = new UserDefinedBrickLabel("DifferentLabel");
	private static UserDefinedBrickInput defaultInput = new UserDefinedBrickInput("Input");
	private static UserDefinedBrickInput differentInput = new UserDefinedBrickInput("DifferentInput");

	private UserDefinedBrick brickToTest;
	private List<Brick> userDefinedBrickListOfSprite;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		brickToTest = new UserDefinedBrick(brickToTestBrickData);
		UserDefinedBrick alreadyDefinedUserBrickOfSprite = new UserDefinedBrick(alreadyDefinedUserBrickOfSpriteBrickData);
		userDefinedBrickListOfSprite = new ArrayList<>();
		userDefinedBrickListOfSprite.add(alreadyDefinedUserBrickOfSprite);
	}

	@Test
	public void testDoesUserBrickAlreadyExist() {
		Mockito.when(spriteMock.getUserDefinedBrickList()).thenReturn(userDefinedBrickListOfSprite);
		assertSame(spriteMock.doesUserBrickAlreadyExist(brickToTest), expectedOutput);
	}
}
