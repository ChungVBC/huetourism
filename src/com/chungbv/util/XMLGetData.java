package com.chungbv.util;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;

public class XMLGetData
{
	private Context context;
	private String xmlName;
	private XMLParser parser;
	private String xml;
	private Document doc;
	private NodeList nl;
	private Element e;
	private String data;

	public XMLGetData(Context context, String xmlName)
	{
		this.xmlName = xmlName;
		this.context = context;
	}

	public void getNodeList()
	{
		parser = new XMLParser();
		xml = parser.readXMLFromFile(context, xmlName, true);
		doc = parser.getDomElement(xml);
		nl = doc.getElementsByTagName("item");
	}

	public int getNodeListLengt()
	{
		return nl.getLength();
	}

	public String getData(int pos, String tag)
	{
		e = (Element) nl.item(pos);
		data = parser.getValue(e, tag);
		return data;
	}

	public void setData(int pos, String tag, int value)
	{
		e = (Element) nl.item(pos);
		NodeList n = e.getElementsByTagName(tag);
		parser.setValue(n.item(0), String.valueOf(value));
		String serializedXML = SerializeXML(doc);
		parser.writeXMLToFile(context, xmlName, serializedXML);
	}

	public String SerializeXML(Document doc)
	{
		// create Transformer object
		Transformer transformer = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
		}
		catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			transformer.transform(new DOMSource(doc), result);
		}
		catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return XML string
		return writer.toString();
	}
}
