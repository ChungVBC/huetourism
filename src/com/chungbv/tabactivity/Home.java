package com.chungbv.tabactivity;

import info.androidhive.googlemapsv2.ListViewActivity;
import info.androidhive.googlemapsv2.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.chungbv.adapter.GridAdapter;

public class Home extends Activity
{
	private GridView gridView;
	private Button btnSearch;
	private Button btnFavorite;
	private Button btnAroundHere;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_home);
		createGridView();
		setButtonListener();
	}

	public void setButtonListener()
	{
		btnSearch = (Button) findViewById(R.id.btnH_Search);
		btnSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				onCreateFuncPage("Search");
			}
		});

		btnFavorite = (Button) findViewById(R.id.btnH_Favorite);
		btnFavorite.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				onCreateFuncPage("Favorite");
			}
		});

		btnAroundHere = (Button) findViewById(R.id.btnH_ArounHere);
		btnAroundHere.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{				
				Intent intent = new Intent();
				intent.setClass(getBaseContext(), MapActivity.class);
				intent.putExtra("NAME", "AROUND_HERE");
				startActivity(intent);
			}
		});
	}

	public void setAutoScaleGridViewHeight()
	{
		// Layout header = (Layout) findViewById(R.id.header);
	}

	public void createGridView()
	{
		gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new GridAdapter(this));
		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				onCreateFuncPage(((TextView) v.findViewById(R.id.grid_item_label)).getText().toString());
			}
		});
	}

	public void onCreateFuncPage(String label)
	{
		Intent intent = new Intent(this, ListViewActivity.class);
		intent.setData(Uri.parse(label));
		startActivity(intent);
	}
}
