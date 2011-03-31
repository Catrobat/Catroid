/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.web;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.webkit.MimeTypeMap;

public class ConnectionWrapper {

	private HttpURLConnection urlConn;
	
	private String getString(InputStream is) {
		if(is == null)
			return "";
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
		
			String line;
			String resp = "";
			while((line = br.readLine()) != null) {
				 resp+=line;
			}
			return resp;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public String doHttpPostFileUpload(String urlstring, HashMap<String, String> post_values, 
					String filetag, String file_path) throws IOException, WebconnectionException {
		
		MultiPartFormOutputStream out = buildPost(urlstring, post_values);
		    
	    if(file_path != null) {
	    	String ext = file_path.substring(file_path.lastIndexOf(".")+1).toLowerCase();
	    	String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);

	    	out.writeFile(filetag, mime, new File(file_path));
	    }
	    out.close();
	    
	    // respone code != 2xx -> error
		if(urlConn.getResponseCode() / 100 != 2)
			throw new WebconnectionException(urlConn.getResponseCode());
		
		InputStream resultStream = urlConn.getInputStream();
		return getString(resultStream);
		
	}
	
	public void doHttpPostFileDownload(String urlstring, HashMap<String, String> post_values, 
				String file_path) throws IOException {
		MultiPartFormOutputStream out = buildPost(urlstring, post_values);
		out.close();
		
	    // read response from server
	    DataInputStream input = new DataInputStream (urlConn.getInputStream ());
	    
	    File file = new File(file_path);
	    file.getParentFile().mkdirs();
	    FileOutputStream f = new FileOutputStream(file);

	    byte[] buffer = new byte[1024];
	    int len1 = 0;
	    while ( (len1 = input.read(buffer)) != -1 ) {
	         f.write(buffer,0,len1);
	    }
	    input.close();
	    f.flush();
	    f.close();
		
	}
	
	private MultiPartFormOutputStream buildPost(String urlstring, HashMap<String, String> post_values) 
							throws IOException {
		if(post_values == null)
			post_values = new HashMap<String, String>();
		
		URL url;
	    url = new URL(urlstring);
	    System.out.println("url: "+urlstring);

	    String boundary = MultiPartFormOutputStream.createBoundary();
	    urlConn = (HttpURLConnection)MultiPartFormOutputStream.createConnection(url);
	
	    urlConn.setRequestProperty("Accept", "*/*");
	    urlConn.setRequestProperty("Content-Type", 
	    	MultiPartFormOutputStream.getContentType(boundary));

	    urlConn.setRequestProperty("Connection", "Keep-Alive");
	    urlConn.setRequestProperty("Cache-Control", "no-cache");
	    
	    MultiPartFormOutputStream out = 
	    	new MultiPartFormOutputStream(urlConn.getOutputStream(), boundary);
	    
	    Set<Entry<String, String>> entries = post_values.entrySet();
	    for (Entry<String, String> entry : entries) {
	    	System.out.println("key: "+entry.getKey()+", value: "+entry.getValue());
	    	out.writeField(entry.getKey(), entry.getValue());
	    }
	   
	    return out;
				
	}
	
}

