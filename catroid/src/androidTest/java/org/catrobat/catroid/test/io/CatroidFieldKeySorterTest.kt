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
package org.catrobat.catroid.test.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.converters.ConversionException
import com.thoughtworks.xstream.converters.reflection.FieldDictionary
import com.thoughtworks.xstream.converters.reflection.FieldKey
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider
import org.catrobat.catroid.io.CatroidFieldKeySorter
import org.catrobat.catroid.io.XStreamFieldKeyOrder
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import java.lang.reflect.Field

@RunWith(AndroidJUnit4::class)
class CatroidFieldKeySorterTest {
    @get:Rule
    val exception = ExpectedException.none()

    private class FieldKeySorterDecorator : FieldKeySorter {
        private val catroidFieldKeySorter: FieldKeySorter = CatroidFieldKeySorter()
        private val sortResults: MutableMap<Class<*>, Map<FieldKey, Field>> = HashMap()
        override fun sort(type: Class<*>, keyedByFieldKey: Map<*, *>?): Map<*, *> {
            val sortResult = catroidFieldKeySorter.sort(type, keyedByFieldKey)
            sortResults[type] = sortResult as Map<FieldKey, Field>
            return sortResult
        }

        fun getFieldNames(type: Class<*>): Array<String> {
            return getFieldNames(sortResults[type]!!)
        }

        fun getFieldNames(fieldKeys: Map<FieldKey, Field>): Array<String> {
            val fieldNames: MutableList<String> = ArrayList()
            for ((fieldKey) in fieldKeys) {
                fieldNames.add(CatroidFieldKeySorter.getAliasOrFieldName(fieldKey))
            }
            return fieldNames.toTypedArray()
        }
    }

    private var xstream: XStream? = null
    private var fieldKeySorter: FieldKeySorterDecorator? = null

    // CHECKSTYLE DISABLE MemberName FOR 1000 LINES
    @Before
    fun setUp() {
        fieldKeySorter = FieldKeySorterDecorator()
        xstream = XStream(PureJavaReflectionProvider(FieldDictionary(fieldKeySorter)))
    }

    @Test
    fun testSortTagsAlphabetically() {
        xstream!!.toXML(BaseClass())
        Assert.assertArrayEquals(
            arrayOf("a", "x"),
            fieldKeySorter!!.getFieldNames(BaseClass::class.java)
        )
    }

    @Test
    fun testSortTagsAlphabeticallyByClassHierarchy() {
        xstream!!.toXML(SubClass())
        Assert.assertArrayEquals(
            arrayOf("a", "x", "b", "y", "z"),
            fieldKeySorter!!.getFieldNames(SubClass::class.java)
        )
    }

    private open class BaseClass {
        private val x = 0
        private val a = 0
    }

    private class SubClass : BaseClass() {
        private val b = 0
        private val z = 0
        private val y = 0
    }

    @Test
    fun testGetFieldName() {
        val fieldKey = FieldKey("b", SortAlphabeticallyWithAliases::class.java, 0)
        val fieldName = CatroidFieldKeySorter.getAliasOrFieldName(fieldKey)
        junit.framework.Assert.assertEquals("b", fieldName)
    }

    @Test
    fun testGetFieldAlias() {
        val fieldKeyWithAlias = FieldKey("a", SortAlphabeticallyWithAliases::class.java, 0)
        val fieldAlias = CatroidFieldKeySorter.getAliasOrFieldName(fieldKeyWithAlias)
        junit.framework.Assert.assertEquals("x", fieldAlias)
    }

    @Test
    fun testSortAlphabeticallyWithAliases() {
        xstream!!.processAnnotations(SortAlphabeticallyWithAliases::class.java)
        xstream!!.toXML(SortAlphabeticallyWithAliases())
        Assert.assertArrayEquals(
            arrayOf("b", "x", "y"),
            fieldKeySorter!!.getFieldNames(
                SortAlphabeticallyWithAliases::class.java
            )
        )
    }

    private class SortAlphabeticallyWithAliases {
        @XStreamAlias("x")
        private val a = 0
        private val y = 0
        private val b = 0
    }

    @Test
    fun testSortByAnnotation() {
        xstream!!.toXML(SortByAnnotation())
        Assert.assertArrayEquals(
            arrayOf("c", "a", "d", "b"),
            fieldKeySorter!!.getFieldNames(SortByAnnotation::class.java)
        )
    }

    // Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
    // CHECKSTYLE DISABLE IndentationCheck FOR 6 LINES
    @XStreamFieldKeyOrder("c", "a", "d", "b")
    private class SortByAnnotation {
        private val a = 0
        private val b = 0
        private val c = 0
        private val d = 0
    }

    @Test
    fun testSortByAnnotationWithAliases() {
        xstream!!.toXML(SortByAnnotationWithAliases())
        Assert.assertArrayEquals(
            arrayOf("x", "b"),
            fieldKeySorter!!.getFieldNames(SortByAnnotationWithAliases::class.java)
        )
    }

    // Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
    // CHECKSTYLE DISABLE IndentationCheck FOR 4 LINES
    @XStreamFieldKeyOrder("x", "b")
    private class SortByAnnotationWithAliases {
        private val b = 0

        @XStreamAlias("x")
        private val a = 0
    }

    @Test
    fun testMissingFieldInAnnotationThrowsException() {
        exception.expect(ConversionException::class.java)
        xstream!!.toXML(MissingFieldInAnnotation())
    }

    // Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
    // CHECKSTYLE DISABLE IndentationCheck FOR 3 LINES
    @XStreamFieldKeyOrder("a")
    private class MissingFieldInAnnotation {
        private val a = 0
        private val b = 0
    }

    @Test
    fun testSortByAnnotationIsInBaseClass() {
        xstream!!.toXML(SubClassWithoutAnnotation())
        Assert.assertArrayEquals(
            arrayOf("b", "a"),
            fieldKeySorter!!.getFieldNames(SubClassWithoutAnnotation::class.java)
        )
    }

    @Test
    fun testMissingFieldInSubClassWithoutAnnotationThrowsException() {
        exception.expect(ConversionException::class.java)
        xstream!!.toXML(SubClassWithNewMemberButWithoutAnnotation())
    }

    // Remove checkstyle disable when https://github.com/checkstyle/checkstyle/issues/1349 is fixed
    // CHECKSTYLE DISABLE IndentationCheck FOR 4 LINES
    @XStreamFieldKeyOrder("b", "a")
    private open class BaseClassWithAnnotation {
        private val a = 0
        private val b = 0
    }

    private class SubClassWithoutAnnotation : BaseClassWithAnnotation()
    private class SubClassWithNewMemberButWithoutAnnotation : BaseClassWithAnnotation() {
        private val c = 0
    }
}
