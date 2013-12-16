package com.chungbv.huetourism;

import com.chungbv.tabactivity.MapActivity;
import com.chungbv.util.*;

import com.chungbv.huetourism.R;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.chungbv.adapter.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SearchViewCompat.OnCloseListenerCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.AdapterView.OnItemClickListener;

public class ListViewActivity extends Activity implements TextWatcher, OnItemClickListener
{
	private static final int LIST_PIC_SCREEN = 0;
	private static final int VIEW_PIC_SCREEN = 1;

	private ArrayList<ListItem> listPic = new ArrayList<ListItem>();
	private ListView listview;
	private ListAdapter adapter;
	private EditText searchEdt;
	private ViewFlipper fliper;
	private WebView viewlist;
	private Button favoriteStateBtn;
	private Button showMapBtn;
	private static int currentPos;

	// for XML parser
	private XMLParser parser;
	private String xml;
	private Document doc;
	private NodeList nl;
	private String label;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.func_page);

		// Get content function page was sent from Main page
		label = getIntent().getData().toString();

		initListViewComponents();
		parseLabelGettedFromIntent();
		setListViewAdapter();

		// Set return home button listener
		// onClickReturnPrevioustActivity();
	}

	public void parseLabelGettedFromIntent()
	{
		if (label.equals("Favorite")) {
			String[] xmlName = GridAdapter.TOURISM_TYPE;
			getDataFromXML(xmlName, true);
		}
		else if (label.equals("Search")) {
			String[] xmlName = GridAdapter.TOURISM_TYPE;
			getDataFromXML(xmlName, false);
		}
		else {
			String[] xmlName = { label };
			getDataFromXML(xmlName, false);
		}
	}

	public void initListViewComponents()
	{
		fliper = (ViewFlipper) findViewById(R.id.viewFlipper);
		listview = (ListView) findViewById(R.id.listView);
		viewlist = (WebView) findViewById(R.id.webview);
		favoriteStateBtn = (Button) findViewById(R.id.favorite_btn);
		showMapBtn = (Button) findViewById(R.id.showmap_btn);

		searchEdt = (EditText) findViewById(R.id.search_edt);
		searchEdt.addTextChangedListener(this);
		String hint = label;
		if (MainActivity.CURRENT_LANGUAGE == 0)
			hint = getVietNameseHint(label);
		searchEdt.setHint(hint);
	}

	public String getVietNameseHint(String label)
	{
		String hint = "";
		for (int i = 0; i < GridAdapter.TOURISM_TYPE.length; i++)
			if (label.equals(GridAdapter.TOURISM_TYPE[i]))
				hint = getResources().getText(GridAdapter.TOURISM_NAME_ID[i]).toString();
		return hint;
	}

	/**
	 * 
	 * @param xmlName
	 * @param type
	 *            [0 : search, 1 : favorite, 2 : another case]
	 */
	public void getDataFromXML(String[] xmlName, boolean isFavorite)
	{
		for (String name : xmlName) {
			parseXMLFile(name);
			// looping through all item nodes <item>
			for (int i = 0; i < nl.getLength(); i++) {
				Element e = (Element) nl.item(i);

				int id = Integer.parseInt(parser.getValue(e, "id"));
				String title = parser.getValue(e, "title");
				int type = Integer.parseInt(parser.getValue(e, "type"));
				int favorite = Integer.parseInt(parser.getValue(e, "favorite"));
				String thumb = parser.getValue(e, "thumb");
				String view = parser.getValue(e, "view");
				String position = parser.getValue(e, "position");
				String content = parser.getValue(e, "content");

				// Create list item
				ListItem list = new ListItem(id, title, type, content, thumb, favorite, view, position, name);

				// For favorite instance
				if (isFavorite) {
					if (favorite == 1) {
						listPic.add(list); // Get favorite items
					}
				}
				else {
					listPic.add(list); // Get all items
				}
			}
		}
	}

	public void parseXMLFile(String name)
	{
		parser = new XMLParser();
		xml = parser.readXMLFromFile(this, name + ".xml", true);
		doc = parser.getDomElement(xml);
		nl = doc.getElementsByTagName("item");
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu)
	// {
	// getMenuInflater().inflate(R.menu.activity_main, menu);
	// return true;
	// }

	public void setListViewAdapter()
	{
		adapter = new ListAdapter(this, listPic);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
	}

	/**
	 * @author chungbv
	 */
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
		// TODO Auto-generated method stub
	}

	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		// TODO Auto-generated method stub
	}

	public void afterTextChanged(Editable s)
	{
		String text = searchEdt.getText().toString().toLowerCase(Locale.getDefault());
		adapter.filter(text);
	}

	/**
	 * Process View Items
	 * 
	 * @param WebView
	 *            , Location, Favorite
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Sliding.slideFromRightToLeft(VIEW_PIC_SCREEN, fliper);
		currentPos = position;
		// content
		viewlist.loadUrl(listPic.get(position).getPicView());
		viewlist.setBackgroundColor(0x00000000);

		// favorite button status show
		if (listPic.get(position).getPicFavorite() == 0) {
			// not favorite
			favoriteStateBtn.setSelected(false);
		}
		else {
			favoriteStateBtn.setSelected(true);
		}

		favoriteStateBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (favoriteStateBtn.isSelected()) {
					// Change XML
					setFavoriteStatus(false);
				}
				else {
					// Change XML
					setFavoriteStatus(true);
				}
			}
		});

		showMapBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(getBaseContext(), MapActivity.class);
				intent.putExtra("POS", listPic.get(currentPos).getPicPos());
				intent.putExtra("NAME", listPic.get(currentPos).getPicName());
				intent.putExtra("LABEL", label);
				intent.putExtra("SNIPPET", listPic.get(currentPos).getPicContent());
				intent.putExtra("URL", listPic.get(currentPos).getPicSource());
				startActivity(intent);
			}
		});

		// map button show

	}

	public void setFavoriteStatus(Boolean value)
	{
		Log.d("ChungBv", "setFavoriteStatus : " + currentPos);
		favoriteStateBtn.setSelected(value);
		String name = listPic.get(currentPos).getPicXMLName();

		XMLParser parser = new XMLParser();
		String xml = parser.readXMLFromFile(this, name + ".xml", true);
		Document doc = parser.getDomElement(xml);
		NodeList nl = doc.getElementsByTagName("item");

		Element e = (Element) nl.item(listPic.get(currentPos).getID());
		NodeList n = e.getElementsByTagName("favorite");

		parser.setValue(n.item(0), String.valueOf(value ? 1 : 0));

		listPic.get(currentPos).setPicFavorite(value ? 1 : 0);

		String serializedXML = SerializeXML(doc);
		parser.writeXMLToFile(this, name + ".xml", serializedXML);
	}

	/**
	 * @param : event, keycode of downed button
	 * @Objective : Handle keyevent on main activity
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			int screen = fliper.getDisplayedChild();

			if (screen == VIEW_PIC_SCREEN) {
				Sliding.slideFromLeftToRight(LIST_PIC_SCREEN, fliper);
				if (label.equals("Favorite")) {
					Log.d("ChungBv", "Set adapter again !!!");
					listPic.clear();
					parseLabelGettedFromIntent();
					setListViewAdapter();
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * public void onClickReturnPrevioustActivity() { Button btnHomeReturn =
	 * (Button) findViewById(R.id.btnHomeReturn);
	 * btnHomeReturn.setOnClickListener(new OnClickListener() {
	 * 
	 * @Override public void onClick(View v) { onBackPressed(); } }); }
	 */

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

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
}