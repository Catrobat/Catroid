/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.junit.After
import org.junit.Before
import org.junit.Test

public class ManifestTests {
    def manifestXML
    def namespace

    @Before void setUp() {
        manifestXML = new XmlParser().parse(new File("../build/intermediates/manifests/catroid/debug/AndroidManifest.xml"))
        namespace = new groovy.xml.Namespace('http://schemas.android.com/apk/res/android')
    }

    @After void tearDown() {
        manifestXML = null;
    }

    @Test
    public void testAppName() {
        def codeXML = new XmlParser().parse(new File("res/code.xml"))
        println manifestXML.application[0].attribute(namespace.label) + ' vs ' + codeXML.header.programName.text()
        assert manifestXML.application[0].attribute(namespace.label) == codeXML.header.programName.text()
    }

    @Test
    public void testVersionName() {
        println manifestXML.attribute(namespace.versionName)
        assert manifestXML.attribute(namespace.versionName) == "0.9.-1.standalone"
    }

    @Test
    public void testVersionCode() {
        println manifestXML.attribute(namespace.versionCode)
        assert manifestXML.attribute(namespace.versionCode) == "-1"
    }

    @Test
    public void testPackageName() {
        def codeXML = new XmlParser().parse(new File("res/code.xml"))
        def programName = codeXML.header.programName.text()
        programName = programName.replaceAll(" ", "")
        println manifestXML.attribute('package') + ' vs ' + ("org.catrobat.catroid." + programName)
        assert manifestXML.attribute('package') == ("org.catrobat.catroid." + programName) //change this!
    }

    @Test
    public void testLaunchIconExists() {
        def icon = new File('../catroid/res/drawable/icon.png')
        assert icon.exists(), "no icon.png found"
    }

    @Test
    public void testSplashScreenExists() {
        def splashscreen = new File('../catroid/res/drawable/splash_screen.png')
        assert splashscreen.exists(), "no splash_screen.png found"
    }

    @Test
    public void testDownloadSucceeded() {
        def zipfile = new File('../catroid/assets/Galaxy War.zip') //TODO maybe use BuildConfig?
        assert zipfile.exists(), "Galaxy War.zip not found in assets"
    }

    @Test
    public void testUnzipSuceeded() {
        def unzipfolder = new File('../catroid/assets/standalone')
        assert unzipfolder.exists(), "standalone/ directory not found"
        assert unzipfolder.isDirectory()

    }
}