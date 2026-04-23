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
package org.catrobat.catroid.content.actions

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import com.opencsv.exceptions.CsvException
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.UserList
import java.io.IOException
import java.io.StringReader
import java.util.regex.Pattern

class StoreCSVIntoUserListAction : TemporalAction() {

    var scope: Scope? = null

    var formulaColumnToExtract: Formula? = null
    var formulaCSVData: Formula? = null

    var userList: UserList? = null

    override fun update(percent: Float) {
        userList ?: return

        userList?.reset()

        val csvData = interpretCSVData(formulaCSVData) ?: return

        var columnToExtract = interpretColumnToExtract(formulaColumnToExtract) ?: return
        columnToExtract -= 1

        val separator = extractOrReplaceSeperator(csvData)

        val csvParser = CSVParserBuilder().withSeparator(separator).build()
        val csvReader = CSVReaderBuilder(StringReader(csvData)).withCSVParser(csvParser)
            .build()

        val allRows = readCSVIntoList(csvReader) ?: return

        if (allRows.size > 0 && columnToExtract >= 0) {
            insertColumnValuesIntoUserList(allRows, columnToExtract)
        }
    }

    fun interpretCSVData(formulaCSVData: Formula?): String? {
        return try {
            formulaCSVData?.interpretString(scope)
        } catch (exception: InterpretationException) {
            Log.d(javaClass.simpleName, "Couldn't interpret formula", exception)
            null
        }
    }

    fun interpretColumnToExtract(formulaColumnToExtract: Formula?): Int? {
        return try {
            formulaColumnToExtract?.interpretInteger(scope)
        } catch (exception: InterpretationException) {
            Log.d(javaClass.simpleName, "Couldn't interpret formula", exception)
            null
        }
    }

    fun extractOrReplaceSeperator(csvData: String): Char {
        var separator = ','
        val pattern = Pattern.compile("^(?:\".*?\"|[^,;]*?)([,;])")
        val matcher = pattern.matcher(csvData)
        if (matcher.find()) {
            separator = matcher.group(1)[0]
        }
        if (separator != ',' && separator != ';') {
            separator = ','
        }
        return separator
    }

    fun readCSVIntoList(csvReader: CSVReader): List<Array<String>>? {
        return try {
            csvReader.readAll()
        } catch (exception: IOException) {
            Log.e(javaClass.simpleName, "Couldn't read csv data", exception)
            null
        } catch (exception: CsvException) {
            Log.e(javaClass.simpleName, "Couldn't validate csv data", exception)
            null
        }
    }

    fun insertColumnValuesIntoUserList(allRows: List<Array<String>>, columnToExtract: Int) {
        var resetList = true
        for (row in allRows) {
            if (row.size <= columnToExtract) {
                userList?.addListItem("")
            } else {
                resetList = false
                userList?.addListItem(row[columnToExtract])
            }
        }
        if (resetList) {
            userList?.reset()
        }
    }
}
