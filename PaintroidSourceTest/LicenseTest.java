import java.io.*;
import junit.framework.TestCase;

public class LicenseTest extends TestCase {

	protected String path_to_project = "../Paintroid/src";
	protected String[] license = {
							"Catroid: An on-device graphical programming language for Android devices",
						    "Copyright (C) 2010  Catroid development team",
							"(<http://code.google.com/p/catroid/wiki/Credits>)",
							"This program is free software: you can redistribute it and/or modify",
						    "it under the terms of the GNU General Public License as published by",
						    "the Free Software Foundation, either version 3 of the License, or",
						    "(at your option) any later version.",
						    "This program is distributed in the hope that it will be useful,",
						    "but WITHOUT ANY WARRANTY; without even the implied warranty of",
						    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the",
						    "GNU General Public License for more details.",
						    "You should have received a copy of the GNU General Public License",
						    "along with this program.  If not, see <http://www.gnu.org/licenses/>."
						};
	
	public LicenseTest() {

	}

	public void setUp() throws Exception {
		
	}
	
	public void testIfGplLicenseIsInAllFiles() throws Exception{
		File dir = new File(path_to_project);
		walkThroughDirectories(dir);
		
	}

	protected void walkThroughDirectories(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	        	walkThroughDirectories(new File(dir, children[i]));
	        }
	    } else {
	        checkForLicense(dir);
	    }
	}
	
	protected void checkForLicense(File file)
	{
		System.out.println(file.getAbsolutePath());
		try
		{
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String line;
		    
		    int cnt = 0;
		    while ((line = br.readLine()) != null && cnt < license.length)   {
		    	if(line.length() <2 || line.substring(2).trim().isEmpty())
		    	{
		    		continue;
		    	}
		    	assertEquals(license[cnt], line.substring(2).trim());
		    	cnt++;
		    }
		    in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
}
