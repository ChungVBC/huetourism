
package com.chungbv.tabactivity;

import java.util.Locale;
import com.chungbv.util.ChangeLocale;
import info.androidhive.googlemapsv2.*;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

public class More extends Activity
{
	/** Called when the activity is first created. */
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_more);
		
		Button btnBackward = (Button) findViewById(R.id.btnBackward);
		btnBackward.setOnClickListener(new ButtonListener());
		
		Button btnForward = (Button) findViewById(R.id.btnForward);
		btnForward.setOnClickListener(new ButtonListener());
		
		Button btnInfomation = (Button) findViewById(R.id.btnInfomation);
		btnInfomation.setOnClickListener(new OnClickListener()
		{			
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getBaseContext(), "Hue Tourism \nChungBv\nBrycen.com.vn", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private class ButtonListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			setLanguage();
		}
	}
	
	public void setLanguage()
	{				
		if (MainActivity.CURRENT_LANGUAGE == 1) {
			MainActivity.CURRENT_LANGUAGE = 0;
					} 
		else {
			MainActivity.CURRENT_LANGUAGE = 1;			
		}
		
		ChangeLocale locale = new ChangeLocale(More.class, More.this, MainActivity.CURRENT_LANGUAGE);
		locale.refreshActivity(this);
	}
}
