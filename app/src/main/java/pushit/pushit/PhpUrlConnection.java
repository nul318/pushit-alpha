package pushit.pushit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;

/*
*
* php 의 send, post 기능을 포함한 클래스.
*
* */

public class PhpUrlConnection {

	public static String sendGET(String link, HashMap<String, String> keyValue) {
		
		URL url = null;
		URLConnection urlConnection = null;

		try {
			url = new URL(link + "?" + encodeRequestData(keyValue));
			urlConnection = url.openConnection();

			return printByInputStream(urlConnection.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "null";
	}
	
	public static String sendPOST(String link, HashMap<String, String> keyValue) {
		
		HttpURLConnection conn;
	    
		try {
		    URL url = new URL(link);
		    conn = (HttpURLConnection) url.openConnection();
		    conn.setRequestMethod("POST");
		 
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		 
		    conn.setConnectTimeout(60); // method setting that unlimited waiting is 0.
		 
		    OutputStream os = conn.getOutputStream();
		    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); 
		    
		    writer.write(encodeRequestData(keyValue)); 
		    writer.flush();
		    writer.close();
		    os.close();
		 
		    conn.connect();
		 
		    String response = printByInputStream(conn.getInputStream());
		 
		    System.out.println("response:" + response);
		 

		    if(conn != null) {
		        conn.disconnect();
		    }
		    
		    return response;
		    
		} catch (Exception e) {
		    e.printStackTrace();
		    
		    return null;
		} 
	}
	
	private static String encodeRequestData(HashMap<String, String> keyValue) {
		
		String encode = "";
		
		Iterator<String> it = keyValue.keySet().iterator();
		String key = it.next();
		encode += key + "=" + keyValue.get(key);
		
		while(it.hasNext())
		{
			encode += "&";
			key = it.next();
			encode += key +"="+keyValue.get(key);
		}
		
		return encode;
	}

	// �� ������ ���� ���� �� ������ ����� �ֿܼ� ����ϴ� �޼ҵ�
	private static String printByInputStream(InputStream is) {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		StringBuilder sb = new StringBuilder();
		String line;
		
		try {
			while((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sb.toString();
	}

	// �� ������ �Ķ���͸�� ���� ���� �����ϴ� �޼ҵ�
	private static void printByOutputStream(OutputStream os, HashMap<String, String> keyValue) {
		
		Iterator<String> it = keyValue.keySet().iterator();
		
		String key;
		
		while(it.hasNext()) {
			
			key = it.next();
			
			try {
				byte[] msgBuf = (key + "=" +keyValue.get(key)).getBytes("UTF-8");
				os.write(msgBuf, 0, msgBuf.length);
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
