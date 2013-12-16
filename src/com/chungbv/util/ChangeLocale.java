package com.chungbv.util;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class ChangeLocale
{
	private Locale myLocale;
	private String LANGUAGE[] = {"vi", "en"};
	private Context context;
	private int value;
	private Class<?> c;

	public ChangeLocale(Class<?> c, Context context, int langID)
	{
		this.context = context;
		this.value = langID;
		this.c = c;
		
		myLocale = new Locale(LANGUAGE[langID]);
		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		
		writeXMLFile();
	}
	
	public void refreshActivity(Activity act)
	{
		// Reload activity
		Intent refresh = new Intent(act, c);
		act.startActivity(refresh);	
		act.finish();
	}
	
	public void writeXMLFile()
	{
		final XMLGetData xml = new XMLGetData(context, "Config.xml");
		xml.getNodeList();
		xml.setData(0, "language", value);
	}
}
