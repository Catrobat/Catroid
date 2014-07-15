

import org.junit.After
import org.junit.Before
import org.junit.Test

public class ManifestTests {
    def manifestXML
    def namespace

    @Before void setUp() {
        manifestXML = new XmlParser().parse(new File("build/intermediates/manifests/catroid/debug/AndroidManifest.xml"))
        namespace = new groovy.xml.Namespace('http://schemas.android.com/apk/res/android')
    }

    @After void tearDown() {
        manifestXML = null;
    }

    @Test
    public void testAppName() {
        def codeXML = new XmlParser().parse(new File("catroidSourceTest/res/code.xml"))
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
        def codeXML = new XmlParser().parse(new File("catroidSourceTest/res/code.xml"))
        def programName = codeXML.header.programName.text()
        programName = programName.replaceAll(" ", "")
        println manifestXML.attribute('package') + ' vs ' + ("org.catrobat.catroid." + programName)
        assert manifestXML.attribute('package') == ("org.catrobat.catroid." + programName) //change this!
    }
}