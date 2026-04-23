/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.test.content.backwardcompatibility;

import org.catrobat.catroid.content.backwardcompatibility.ProjectMetaDataParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ProjectMetaDataParserTest {

	@Parameterized.Parameter(0)
	public String xmlContent;

	@Parameterized.Parameter(1)
	public boolean assertCausePresent;

	@Parameterized.Parameters(name = "{index}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"<program><header><catrobatLanguageVersion>0.999</catrobatLanguageVersion><scenesEnabled>true</scenesEnabled></header></program>", false},
				{"<program><header><programName></programName><catrobatLanguageVersion>0.999</catrobatLanguageVersion><scenesEnabled>true</scenesEnabled></header></program>", false},
				{"<program><header><programName>My Project</programName><catrobatLanguageVersion>0.999</catrobatLanguageVersion><scenesEnabled>true</scenesEnabled></header>", true},
		});
	}

	@Test
	public void testGetProjectMetaDataWithInvalidXmlThrowsIOException() throws Exception {
		File xmlFile = createTempXml(xmlContent);
		assertInvalidMetadata(xmlFile, assertCausePresent);
	}

	private void assertInvalidMetadata(File xmlFile, boolean assertCausePresent) throws Exception {
		try {
			new ProjectMetaDataParser(xmlFile).getProjectMetaData();
			fail("Expected IOException for invalid metadata XML");
		} catch (IOException expected) {
			assertEquals("Project metadata invalid", expected.getMessage());
			if (assertCausePresent) {
				assertNotNull("Malformed XML should keep parsing exception as cause", expected.getCause());
			}
		}
	}

	private File createTempXml(String xmlContent) throws IOException {
		File xmlFile = File.createTempFile("project-metadata-parser", ".xml");
		xmlFile.deleteOnExit();
		Files.write(xmlFile.toPath(), xmlContent.getBytes(StandardCharsets.UTF_8));
		return xmlFile;
	}
}
