package com.chungbv.adapter;

import info.androidhive.googlemapsv2.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter
{
	private Context mContext;
	private LayoutInflater mInflater;
	private List<ListItem> picList = null;
	private ArrayList<ListItem> listpicOrigin;	

	public ListAdapter(Context context, List<ListItem> picList) {
		mContext = context;
		this.picList = picList;
		mInflater = LayoutInflater.from(mContext);
		this.listpicOrigin = new ArrayList<ListItem>();
		this.listpicOrigin.addAll(picList);
	}

	public class ViewHolder
	{
		TextView picName;
		TextView picContent;
		ImageView picIcon;
	}

	/**
	 * Process list items
	 */
	public View getView(int position, View view, ViewGroup parent)
	{
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.list_row, null);
			holder.picName = (TextView) view.findViewById(R.id.title);
			holder.picContent = (TextView) view.findViewById(R.id.brief);
			holder.picIcon = (ImageView) view.findViewById(R.id.list_image);

			view.setTag(holder);
		}
		else {
			holder = (ViewHolder) view.getTag();
		}

		holder.picName.setText(picList.get(position).getPicName());
		holder.picContent.setText(picList.get(position).getPicContent());
//		 holder.picIcon.setImageResource(picList.get(position).getPicSource());
		try {
			holder.picIcon.setImageBitmap(getBitmapFromAsset(picList.get(position).getPicSource()));
		}
		catch (IOException e) {
			 holder.picIcon.setImageResource(R.drawable.no_image);
		}

		return view;
	}

	private Bitmap getBitmapFromAsset(String strName) throws IOException
	{
	    AssetManager assetManager = mContext.getAssets();
	    InputStream istr = assetManager.open(strName);
	    Bitmap bitmap = BitmapFactory.decodeStream(istr);
	    return bitmap;
	 }
	
	public int getCount()
	{
		return picList.size();
	}

	public ListItem getItem(int position)
	{
		return picList.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public void filter(String charText)
	{
		charText = charText.toLowerCase(Locale.getDefault());
		picList.clear();
		if (charText.length() == 0) {
			picList.addAll(listpicOrigin);
		}
		else {
			for (ListItem pic : listpicOrigin) {
				if (pic.getPicName().toLowerCase(Locale.getDefault()).contains(charText)) {
					picList.add(pic);
				}
			}
		}
		notifyDataSetChanged();
	}

}