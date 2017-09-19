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

import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

public class AndroidPackageRunner extends ParentRunner<Runner> {
	private static final String TAG = AndroidPackageRunner.class.getSimpleName();

	public static Runner emptySuite() {
		try {
			return new AndroidPackageRunner((Class<?>) null, new Class<?>[0]);
		} catch (InitializationError e) {
			throw new RuntimeException("This shouldn't be possible");
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	@interface PackagePath {
		String value();
	}

	private static Class<?>[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
		PackagePath annotation = klass.getAnnotation(PackagePath.class);
		if (annotation == null) {
			throw new InitializationError(String.format("class '%s' must have a PackagePath annotation", klass.getName()));
		}
		ArrayList<Class> classes = new ArrayList<>();
		try {
			String packageCodePath = InstrumentationRegistry.getContext().getPackageCodePath();
			DexFile dexFile = new DexFile(packageCodePath);
			for (Enumeration<String> iter = dexFile.entries(); iter.hasMoreElements(); ) {
				String className = iter.nextElement();
				if (className.contains(annotation.value()) && className.endsWith("Test")) {
					classes.add(Class.forName(className));
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			throw new InitializationError("Exception during loading Test classes from Dex");
		}
		return classes.toArray(new Class[0]);
	}

	private final List<Runner> runners;

	public AndroidPackageRunner(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		this(builder, klass, getAnnotatedClasses(klass));
	}

	public AndroidPackageRunner(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
		this(null, builder.runners(null, classes));
	}

	private AndroidPackageRunner(Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
		this(new AllDefaultPossibilitiesBuilder(true), klass, suiteClasses);
	}

	private AndroidPackageRunner(RunnerBuilder builder, Class<?> klass, Class<?>[] suiteClasses) throws
			InitializationError {
		this(klass, builder.runners(klass, suiteClasses));
	}

	private AndroidPackageRunner(Class<?> klass, List<Runner> runners) throws InitializationError {
		super(klass);
		this.runners = Collections.unmodifiableList(runners);
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner runner, final RunNotifier notifier) {
		runner.run(notifier);
	}
}
