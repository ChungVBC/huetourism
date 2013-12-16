package com.chungbv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chungbv.huetourism.R;
public class GridAdapter extends BaseAdapter
{
	private Context context;
	
	public static final String TOURISM_TYPE[] = { "Place", "Restaurant", "Drink", "Hotel", "Transport",
			"Shopping", "ATM", "Other" };
	
	public static final int[] TOURISM_NAME_ID = {R.string.n_home_place_name, R.string.n_home_restaurant_name,
			R.string.n_home_drink_name, R.string.n_home_hotel_name, R.string.n_home_transport_name,
			R.string.n_home_shopping_name, R.string.n_home_bank_name, R.string.n_home_other_name};
	
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
			textView.setText(TOURISM_NAME_ID[position]);

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
