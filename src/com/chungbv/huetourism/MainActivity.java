package com.chungbv.huetourism;

import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.chungbv.tabactivity.Home;
import com.chungbv.tabactivity.MapActivity;
import com.chungbv.tabactivity.More;
import com.chungbv.util.ChangeLocale;
import com.chungbv.util.XMLGetData;
import com.google.android.gms.internal.di;

public class MainActivity extends TabActivity
{
	private TabHost tabHost;
	private final int ID_S[] = { R.drawable.navbar_home, R.drawable.navbar_map, R.drawable.navbar_moreselector };
	private final int ID_TAB_NAME[] = {R.string.navbar_home_name, R.string.navbar_map_name, R.string.navbar_more_name};
	private final Class<?> C[] = { Home.class, MapActivity.class, More.class };

	/**
	 * LANGUAGE: (0)Vietnamese (1)English
	 */
	public static int CURRENT_LANGUAGE;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getSystemConfig();
		setContentView(R.layout.main);
		setTabHostSpec();
	}

	public void setTabHostSpec()
	{
		// Get TabHost Reference
		tabHost = getTabHost();

		for (int i = 0; i < ID_S.length; i++) {
			addTabSpec(C[i], ID_S[i], ID_TAB_NAME[i]);
		}

		// Set Home as Default tab and change image
		tabHost.getTabWidget().setCurrentTab(0);
	}

	public void addTabSpec(Class<?> c, int ids, int idName)
	{
		TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, c);
		spec = tabHost.newTabSpec(c.getName());
		spec.setIndicator(getResources().getText(idName), getResources().getDrawable(ids));
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	public void getSystemConfig()
	{
		final XMLGetData xml = new XMLGetData(this, "Config.xml");
		xml.getNodeList();

		if (Integer.parseInt(xml.getData(0, "first_run")) == 1) {
			// Show dialog
			onCreateSelectLanguageDialog();	
			xml.setData(0, "first_run", 0);
		} 
		else {
			CURRENT_LANGUAGE = Integer.parseInt(xml.getData(0, "language"));
			new ChangeLocale(MainActivity.class, MainActivity.this, CURRENT_LANGUAGE);
		}
	}

	public void onCreateSelectLanguageDialog()
	{
		final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		dialog.setContentView(R.layout.dialog_select_language);
		dialog.show();

		Button btnVI = (Button) dialog.findViewById(R.id.btnVNLanguage);
		btnVI.setOnClickListener(new ButtonSelectLanguageDialogListener(0, dialog, this));

		Button btnEN = (Button) dialog.findViewById(R.id.btnENLanguage);
		btnEN.setOnClickListener(new ButtonSelectLanguageDialogListener(1, dialog, this));
	}
		
	private class ButtonSelectLanguageDialogListener implements OnClickListener
	{
		private int id;	 // 0:VI 1:EN
		private Dialog dialog;
		private Activity context;
		
		public ButtonSelectLanguageDialogListener(int id, Dialog dialog, Activity context) 
		{
			this.id = id;
			this.dialog = dialog;
			this.context = context;
		}
		
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			if (id == 0) 
				CURRENT_LANGUAGE = 0;
			
			if (id == 1) 
				CURRENT_LANGUAGE = 1;
			
			dialog.dismiss();					
			ChangeLocale locate = new ChangeLocale(MainActivity.class, MainActivity.this, CURRENT_LANGUAGE);
			locate.writeXMLFile();
			locate.refreshActivity(context);
		}		
	}
}