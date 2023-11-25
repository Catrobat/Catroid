/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.io.catlang.parser.project

//import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick
//import org.reflections.Reflections
//import org.reflections.scanners.Scanners.*
//import io.github.classgraph.ClassGraph
//import org.apache.commons.lang3.ClassUtils.getClass
//import kotlin.reflect.KClass
//import kotlin.reflect.KFunction

class CatrobatLanguageParserHelper {
    companion object {
        fun getStringContent(value: String): String = value.substring(1, value.length - 1)
        fun getStringToDouble(value: String): Double = getStringContent(value).toDouble()
        fun getStringToBoolean(value: String): Boolean = getStringContent(value).toBoolean()
        fun getStringToInt(value: String): Int = getStringContent(value).toInt()
    }

    private val brickCommandToClass: HashMap<String, CatrobatLanguageBrick> = hashMapOf()

    init {
//        val result = getAllAnnotatedWith(CatrobatLanguageBrick::class)
//        result.forEach { function ->
//            val annotation = function.annotations.find { it is CatrobatLanguageBrick } as CatrobatLanguageBrick?
//            if (annotation != null) {
//                brickCommandToClass[annotation.command] = annotation
//            }
//        }
//        val allClasses = getAllCatrobatLanguageBricks()
//        allClasses?.forEach { clazz ->
//            val annotation = clazz.javaClass.annotations.find { it is CatrobatLanguageBrick } as CatrobatLanguageBrick?
//            if (annotation != null) {
//                brickCommandToClass[annotation.command] = annotation
//            }
//        }
    }

    private fun test() {
//        val classLoader = ClassLoader.getSystemClassLoader()
//        val packageName = "org.catrobat.catroid.content.bricks"
//        classLoader.loadClass(packageName)
//        val classes = classLoader.loadClasses(packageName)
//
//        for (clazz in classes) {
//            if (clazz.isAnnotationPresent(CatrobatLanguageBrick::class)) {
//                val annotation = clazz.getAnnotation(CatrobatLanguageBrick::class)
//                val command = annotation.command
//                val brickClass = clazz as Class<CatrobatLanguageBrickClass>
//
//                commandClassMap[command] = brickClass
//            }
//        }
    }

//    private fun getAllAnnotatedWith(annotation: KClass<out Annotation>): List<KFunction<*>> {
//        val `package` = annotation.java.`package`.name
//        val annotationName = annotation.java.canonicalName
//
//        return ClassGraph()
//            .enableClassInfo()
//            .verbose()
//            .enableAllInfo()
//            .acceptPackages(`package`)
////            .acceptPackages("org.catrobat.catroid.content.bricks.*")
//            .scan().use { scanResult ->
//                scanResult.getClassesWithAnnotation(annotationName).flatMap { classInfo ->
//                    classInfo.loadClass().kotlin.constructors
//                        .filter { it.annotations.any { it.annotationClass == annotation } }
//                }
//            }
//    }

//    private fun getAllCatrobatLanguageBricks(): Collection<Any>? {
//        val reflections = Reflections("org.catrobat.catroid")
//        val annotated = reflections[SubTypes.of(Brick::class.java)]
////        val annotated: Set<Class<*>> = reflections[SubTypes.of(TypesAnnotated.with(SomeAnnotation::class.java)).asClass()]
////
////        val classes = findAnnotatedClasses("org.catrobat.catroid.content.bricks", CatrobatLanguageBrick::class.java)
////        val reflection = Reflections()
////        val allClassesInProject = reflection.getSubTypesOf(Any::class.java)
////        val allAnnotatedClasses = reflection.getTypesAnnotatedWith(CatrobatLanguageBrick::class.java)
////        val reflections = Reflections("org.catrobat.catroid.content.bricks")
////        val allAnnotaedClasses = reflections.getTypesAnnotatedWith(CatrobatLanguageBrick::class.java)
//        return annotated
////        return reflections.getSubTypesOf(Any::class.java)
////            .filter { it.kotlin.isData || it.kotlin.isSealed || it.kotlin.isAbstract }
////            .map { it.kotlin }
//    }
}