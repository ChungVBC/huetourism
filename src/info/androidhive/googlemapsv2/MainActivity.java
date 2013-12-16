package info.androidhive.googlemapsv2;

import com.chungbv.tabactivity.*;
import com.chungbv.util.ChangeLocale;
import com.chungbv.util.XMLGetData;

import android.app.Activity;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class MainActivity extends TabActivity
{
	private TabHost tabHost;
	private final int ID_S[] = { R.drawable.navbar_home, R.drawable.navbar_map, R.drawable.navbar_moreselector };

	private final Class<?> C[] = { Home.class, MapActivity.class, More.class };

	/**
	 * LANGUAGE: (0)Vietnamese (1)English
	 */
	public static int CURRENT_LANGUAGE;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
//		getSystemConfig();
		setContentView(R.layout.main);
		setTabHostSpec();
	}

	public void setTabHostSpec()
	{
		// Get TabHost Reference
		tabHost = getTabHost();

		for (int i = 0; i < ID_S.length; i++) {
			addTabSpec(C[i], ID_S[i]);
		}

		// Set Home as Default tab and change image
		tabHost.getTabWidget().setCurrentTab(0);
	}

	public void addTabSpec(Class<?> c, int ids)
	{
		TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, c);
		spec = tabHost.newTabSpec(c.getName());
		spec.setIndicator(c.getSimpleName(), getResources().getDrawable(ids));
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

	public void getSystemConfig()
	{
		final XMLGetData xml = new XMLGetData(this, "Config.xml");
		xml.getNodeList();

		if (Integer.parseInt(xml.getData(0, "first_run")) == 1) {
			// Show dialog
			onCreateSelectLanguageDialog(xml);	
			xml.setData(0, "first_run", 0);
		} 
		else {
			CURRENT_LANGUAGE = Integer.parseInt(xml.getData(0, "language"));
//			new ChangeLocale(MainActivity.class, MainActivity.this, CURRENT_LANGUAGE);
			Toast.makeText(getBaseContext(), "CURRENT_LANGUAGE = " + CURRENT_LANGUAGE, Toast.LENGTH_LONG).show();
		}
	}

	public void onCreateSelectLanguageDialog(final XMLGetData xml)
	{
		final Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		dialog.setContentView(R.layout.dialog_select_language);
		dialog.show();

		Button btnVI = (Button) dialog.findViewById(R.id.btnVNLanguage);
		btnVI.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				CURRENT_LANGUAGE = 0;
				dialog.dismiss();					
				xml.setData(0, "language", CURRENT_LANGUAGE);
				new ChangeLocale(MainActivity.class, MainActivity.this, CURRENT_LANGUAGE);
			}
		});

		Button btnEN = (Button) dialog.findViewById(R.id.btnENLanguage);
		btnEN.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{				
				CURRENT_LANGUAGE = 1;
				dialog.dismiss();
				xml.setData(0, "language", CURRENT_LANGUAGE);
				new ChangeLocale(MainActivity.class, MainActivity.this, CURRENT_LANGUAGE);
			}
		});
	}
		
//	private class 
}