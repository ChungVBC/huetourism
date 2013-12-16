package com.chungbv.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class XMLParser
{	
	public XMLParser() {
	}

	public Document getDocFromAsset(Context activity, String name)
	{
		Document doc = null;

		try {
			InputStream raw = activity.getAssets().open(name);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(raw);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		}

		return doc;
	}

	/**
	 * Getting XML from URL making HTTP request
	 * 
	 * @param url
	 *            string
	 * */
	public String getXmlFromUrl(String url)
	{
		String xml = null;

		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		// return XML
		return xml;
	}

	public String readXMLFromFile(Context activity, String xmlFile, boolean useConfigDir)
	{
		InputStream is = null;
		File file = null;
		File sdCard = null;
		Writer writer = new StringWriter();
		boolean exists = false;

		if (useConfigDir) {
			sdCard = Environment.getExternalStorageDirectory();
			file = new File(sdCard.getAbsolutePath() + "/huetour/config/" + xmlFile);			
			if (!(file == null)) {
				if (exists = file.exists())
					try {
						is = new FileInputStream(file);
					}
					catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}

		if (!exists) {
			AssetManager assetManager = activity.getAssets();
			try {
				is = assetManager.open("index/" + xmlFile);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (is != null) {
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
				is.close();
			}
			catch (IOException e) {
				Log.e("Error: ", e.getMessage());
				return null;
			}
		}
		return writer.toString();
	}

	public void writeXMLToFile(Context context, String xmlFile, String xmlData)
	{
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;

		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/huetour/config");
						
			if (!dir.exists()) {
				if (!(dir.mkdirs()))
					Log.e("mkdirs", "Failed to create SDCARD mounted directory!!!");
			}

			fOut = new FileOutputStream(new File(dir, xmlFile));
			osw = new OutputStreamWriter(fOut);
			osw.write(xmlData);
			osw.flush();
			Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show();
		}
		catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "Settings not saved", Toast.LENGTH_SHORT).show();
		}
		finally {
			try {
				osw.close();
				fOut.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Getting XML DOM element
	 * 
	 * @param XML
	 *            string
	 * */
	public Document getDomElement(String xml)
	{
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		}
		catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}
		catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}

		return doc;
	}

	/**
	 * Getting node value
	 * 
	 * @param elem
	 *            element
	 */
	public final String getElementValue(Node elem)
	{
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	/**
	 * Getting node value
	 * 
	 * @param Element
	 *            node
	 * @param key
	 *            string
	 * */
	public String getValue(Element item, String str)
	{
		NodeList n = item.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}

	public void setValue(Node elem, String str)
	{
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						child.setNodeValue(str);
					}
				}
			}
		}
	}

	public String GetElementAttribute(Element item, String attribName)
	{
		return item.getAttribute(attribName);
	}
}