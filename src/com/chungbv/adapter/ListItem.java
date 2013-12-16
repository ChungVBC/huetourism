package com.chungbv.adapter;

public class ListItem
{
	private int id;
	private String listName;
	private int listType;
	private String listThumb;
	private int listFavorite;
	private String listHtmlView;
	private String listPosition;
	private String listContent;
	private String listXMLName;

	/**
	 * LIST ITEM
	 * 
	 * @param listName
	 *            : use for title of list item view
	 * @param listType
	 *            : use for filter
	 * @param listThumb
	 *            : use for list view thumbnail
	 * @param listFavorite
	 *            : use for favorite button status
	 * @param listHtmlView
	 *            : use for webview
	 * @param listPosition
	 *            : use for map
	 */
	public ListItem(int id, String listName, int listType, String listContent, String listThumb, int listFavorite,
			String listHtmlView, String listPosition, String listXMLName) {
		this.id = id;
		this.listName = listName;
		this.listType = listType;
		this.listThumb = listThumb;
		this.listFavorite = listFavorite;
		this.listHtmlView = listHtmlView;
		this.listPosition = listPosition;
		this.listContent = listContent;
		this.listXMLName = listXMLName;
	}

	public int getID()
	{
		return this.id;
	}
	
	public String getPicXMLName()
	{
		return this.listXMLName;
	}
	
	public String getPicName()
	{
		return this.listName;
	}

	public int getPicType()
	{
		return this.listType;
	}

	public String getPicContent()
	{
		return this.listContent;
	}

	public String getPicSource()
	{
		return this.listThumb;
	}

	public String getPicPos()
	{
		return this.listPosition;
	}

	public int getPicFavorite()
	{
		return this.listFavorite;
	}

	public String getPicView()
	{
		return this.listHtmlView;
	}
	
	public void setPicFavorite(int value)
	{
		this.listFavorite = value;
	}
}
