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

/**
 * This class handles the connection to the townster server
 */
public class ConnectionWrapper {

	private HttpURLConnection urlConn;
	
	public void testing() {
		
	}
	/**
	 * This Method converts a given InputStream in a String
	 * @param 	is	the InputStream to convert
	 * @return				the converted String	
	 */
	private String getString(InputStream is) {
		if(is == null)
			return "";
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
		
			String line;
			String resp = "";
			while((line = br.readLine()) != null) {
				 //System.out.println("line:"+line);
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
	
	/**
	 * send data with the post method
	 * @param urlstring 	the url to call
	 * @param post_values	the post parameters
	 * @param filetag			name of the file parameter
	 * @param file_path		path to the file or null if no file upload is needed
	 * @return							the response of the call
	 * @exception APIException is thrown if an error occurs
	 */
	public String doHttpPostFileUpload(String urlstring, HashMap<String, String> post_values, 
					String filetag, String file_path) throws IOException, WebconnectionException {
		
		MultiPartFormOutputStream out = buildPost(urlstring, post_values);
		    
	    // upload a file
	    if(file_path != null) {
	    	String ext = file_path.substring(file_path.lastIndexOf(".")+1).toLowerCase();
	    	String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
//	    	if(mime == null)
//	    		throw new APIException(APIException.MIME_ERROR);
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
		
		
	
	/**
	 * This Method sends a http post 
	 * @param urlstring 	the url to call
	 * @param post_values	the post parameters
	 * @return				the response of the call
	 * @throws IOException 
	 * @exception APIException is thrown if an error occurs
	 */
	public String doHttpPost(String urlstring, HashMap<String, String> post_values) 
			throws IOException, WebconnectionException {
		
		MultiPartFormOutputStream out = buildPost(urlstring, post_values);	
	    out.close();
	
	    // respone code != 2xx -> error
		if(urlConn.getResponseCode() / 100 != 2)
			throw new WebconnectionException(urlConn.getResponseCode());
		
		InputStream resultStream = urlConn.getInputStream();
		return getString(resultStream);	
		 
	}
	
	private MultiPartFormOutputStream buildPost(String urlstring, HashMap<String, String> post_values) 
							throws IOException {
		if(post_values == null)
			post_values = new HashMap<String, String>();
		
		
		URL url;
	    url = new URL(urlstring);
	    System.out.println("url: "+urlstring);

	    // create a boundary string
	    String boundary = MultiPartFormOutputStream.createBoundary();
	    urlConn = (HttpURLConnection)MultiPartFormOutputStream.createConnection(url);
	
	    urlConn.setRequestProperty("Accept", "*/*");
	    urlConn.setRequestProperty("Content-Type", 
	    	MultiPartFormOutputStream.getContentType(boundary));
	    
	    // set some other request headers...
	    urlConn.setRequestProperty("Connection", "Keep-Alive");
	    urlConn.setRequestProperty("Cache-Control", "no-cache");
	    
	    // no need to connect cuz getOutputStream() does it
	    MultiPartFormOutputStream out = 
	    	new MultiPartFormOutputStream(urlConn.getOutputStream(), boundary);
	    // write a text field element
	    
	    Set<Entry<String, String>> entries = post_values.entrySet();
	    for (Entry<String, String> entry : entries) {
	    	System.out.println("key: "+entry.getKey()+", value: "+entry.getValue());
	    	out.writeField(entry.getKey(), entry.getValue());
	    }
	   
	    return out;
				
	}
	
}

