package com.chungbv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import info.androidhive.googlemapsv2.*;
public class GridAdapter extends BaseAdapter
{
	private Context context;
	
	public static final String TOURISM_TYPE[] = { "Place", "Restaurant", "Drink", "Hotel", "Transport",
			"Shopping", "ATM", "Other" };
	
	private final int ID_GRID[] = { R.drawable.place, R.drawable.restaurant, R.drawable.drink, R.drawable.hotel,
			R.drawable.transport, R.drawable.shopping, R.drawable.bank, R.drawable.other };
	
	public GridAdapter(Context context) {
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView;

		if (convertView == null) {
			gridView = new View(context);

			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.grid_item, null);

			// set value into text view
			TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
			textView.setText(TOURISM_TYPE[position]);

			// set image based on selected text
			ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
			imageView.setImageResource(ID_GRID[position]);
		}
		else {
			gridView = (View) convertView;
		}

		return gridView;
	}

	@Override
	public int getCount()
	{
		return TOURISM_TYPE.length;
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}
}
