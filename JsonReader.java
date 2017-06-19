
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
 
/**
 * @author Crunchify.com
 */
 
public class JsonReader {
 
	public static void main(String[] args) {
		try {
			URL url = new URL("https://data.anfr.fr/api/datasets/1.0/search/?q=rt");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String strTemp = "";
			while (null != (strTemp = br.readLine())) {
				System.out.println(strTemp);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
