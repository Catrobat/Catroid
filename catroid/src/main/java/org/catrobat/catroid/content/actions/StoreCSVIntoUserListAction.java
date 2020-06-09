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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserList;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StoreCSVIntoUserListAction extends TemporalAction {

	private Sprite sprite;

	private Formula formulaColumnToExtract;
	private Formula formulaCSVData;

	private UserList userList;

	@Override
	protected void update(float percent) {
		if (userList == null) {
			return;
		}

		String csvData;
		try {
			csvData = formulaCSVData == null ? "" : formulaCSVData.interpretString(sprite);

			int columnToExtract;
			columnToExtract = formulaColumnToExtract == null ? 0
					: formulaColumnToExtract.interpretInteger(sprite);

			columnToExtract--;

			char separator = ',';
			Pattern pattern = Pattern.compile("^(?:\".*?\"|[^,;]*?)([,;])");
			Matcher matcher = pattern.matcher(csvData);
			if (matcher.find()) {
				separator = matcher.group(1).charAt(0);
			}
			if (separator != ',' && separator != ';') {
				separator = ',';
			}

			userList.reset();
			boolean resetList = true;

			CSVParser parser = new CSVParserBuilder().withSeparator(separator).build();
			CSVReader reader = new CSVReaderBuilder(new StringReader(csvData)).withCSVParser(parser)
					.build();

			List<String[]> allRows = reader.readAll();
			if (allRows.size() > 0 && columnToExtract >= 0) {
				for (String[] row : allRows) {
					if (row.length <= columnToExtract) {
						userList.addListItem("");
					} else {
						resetList = false;
						userList.addListItem(row[columnToExtract]);
					}
				}

				if (resetList) {
					userList.reset();
				}
			}
		} catch (IOException | CsvException | InterpretationException exception) {
			userList.reset();
		}
	}

	public void setUserList(UserList userVariable) {
		this.userList = userVariable;
	}

	public void setFormulaColumnToExtract(Formula formulaColumnToExtract) {
		this.formulaColumnToExtract = formulaColumnToExtract;
	}

	public void setFormulaCSVData(Formula formulaCSVData) {
		this.formulaCSVData = formulaCSVData;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
