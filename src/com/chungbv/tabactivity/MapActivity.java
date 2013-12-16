package com.chungbv.tabactivity;

import com.chungbv.huetourism.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chungbv.adapter.GridAdapter;
import com.chungbv.util.GMapV2Direction;
import com.chungbv.util.GetDirectionsAsyncTask;
import com.chungbv.util.XMLParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends FragmentActivity
{
	private Polyline newPolyline;
	private final double[] HUE_POS = { 16.463826, 107.583955 };

	private enum STATE {
		AROUND, MAP, PAGE
	};

	private STATE state;

	int[] locationListButton = { R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6,
			R.id.button7, R.id.button8 };
	int[] LOCATION_ICON = { R.drawable.location1, R.drawable.location2, R.drawable.location3, R.drawable.location4,
			R.drawable.location5, R.drawable.location6, R.drawable.location7, R.drawable.location8 };

	private String[] items = { "Place", "Restaurant", "Drink", "Hotel", "Transport", "Shopping", "ATM", "Other" };
	private boolean[] itemsChecked = new boolean[items.length];

	// Google Map
	private GoogleMap googleMap;
	private Location myLocation;
	private MarkerOptions markerOption;
	private Marker marker;
	private Hashtable<String, String> markers;
	private CameraPosition cameraPosition;
	private double latitude;
	private double longitude;
	private String name_pos;
	private String label;
	private String URL_PAGE_THUMBNAIL;

	private boolean NEARBY = false;
	private int RADIUS = 0;

	// Constructor
	public MapActivity() {
		this.latitude = HUE_POS[0];
		this.longitude = HUE_POS[1];
		this.name_pos = "HUE CITY";
		this.label = this.name_pos;
	}

	public MapActivity(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_map);

		try {
			// Loading map
			initilizeMap();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().getString("NAME").equals("AROUND_HERE")) {
				// Constructor for around here
				Log.d("ChungBv", "AROUND_HERE");
				state = STATE.AROUND;
				aroundHereConstructor();
			}
			else {
				// Constructor from function page, search page & favorite page
				Log.d("ChungBv", "PAGE");
				state = STATE.PAGE;
				pageContructor();
			}
		}
		else {
			// Constructor from tab map
			Log.d("ChungBv", "MAP");
			state = STATE.MAP;
			normalMapConstructor();
		}

		setLocationButtonListener();
	}

	public void setLocationButtonListener()
	{
		Button btnMyLocation = (Button) findViewById(R.id.btnLocation);
		btnMyLocation.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				onCreateLocationDialog();
			}
		});
	}

	// AROUND HERE - NEAR BY
	public void aroundHereConstructor()
	{
		// Show all maker in limit radius around current positon
		// Set multi check menu : all checked
		setCameraPosView(latitude, longitude, 11);
		for (int i = 0; i < itemsChecked.length; i++) {
			itemsChecked[i] = true;
		}
		onCreateNearByDialog();
	}

	// MAP VIEW
	public void normalMapConstructor()
	{
		// Set multi check menu : all unchecked
		setCameraPosView(latitude, longitude, 12);
		for (int i = 0; i < itemsChecked.length; i++) {
			itemsChecked[i] = false;
		}
	}

	// PAGE
	public void pageContructor()
	{
		String extra[] = getIntent().getExtras().getString("POS").split(",");
		this.latitude = Double.parseDouble(extra[0]);
		this.longitude = Double.parseDouble(extra[1]);
		this.name_pos = getIntent().getExtras().getString("NAME");
		this.label = getIntent().getExtras().getString("LABEL");
		String snippet = getIntent().getExtras().getString("SNIPPET");
		String url = getIntent().getExtras().getString("URL");
		for (int i = 0; i < itemsChecked.length; i++) {
			itemsChecked[i] = false;
		}
		setMarkerMap(latitude, longitude, name_pos, label, snippet, url);
		setCameraPosView(latitude, longitude, 15);
		// Set multi check menu : all unchecked
	}

	public void onCreateLocationDialog()
	{
		final Dialog dialog = new Dialog(MapActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.location_dialog);
		dialog.show();

		for (int i : locationListButton) {
			final Button btn = (Button) dialog.findViewById(i);
			btn.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					for (int j = 0; j < locationListButton.length; j++) {
						if (btn.getId() == locationListButton[j]) {
							if (itemsChecked[j]) {
								// Uncheck button
								itemsChecked[j] = false;
							}
							else {
								// Check button
								itemsChecked[j] = true;
							}
							// Set all marker again
							googleMap.clear();
							showAllMarker();
						}
					}
				}
			});
		}

		Button btnCancel = (Button) dialog.findViewById(R.id.btnCloseLDialog);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

	}

	public void onCreateNearByDialog()
	{
		final Dialog dialog = new Dialog(MapActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.dialog_around_here);
		dialog.show();

		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancelNBDialog);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();
				showAllMarker();
			}
		});

		Button btnOk = (Button) dialog.findViewById(R.id.btnOkNBDialog);
		btnOk.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				EditText edtxt = (EditText) dialog.findViewById(R.id.editTextNearBy);	
				if (edtxt != null && edtxt.length() != 0) {
					NEARBY = true;
					RADIUS = Integer.parseInt(edtxt.getText().toString());
				}
				dialog.dismiss();
				showAllMarker();
			}
		});
	}

	public void showAllMarker()
	{
		String[] xmlName = GridAdapter.TOURISM_TYPE;

		for (int iFile = 0; iFile < xmlName.length; iFile++) {
			if (itemsChecked[iFile]) {
				XMLParser parser = new XMLParser();
				String xml = parser.readXMLFromFile(this, xmlName[iFile] + ".xml", true);
				Document doc = parser.getDomElement(xml);
				NodeList nl = doc.getElementsByTagName("item");

				// looping through all item nodes <item>
				for (int i = 0; i < nl.getLength(); i++) {
					Element e = (Element) nl.item(i);
					String title = parser.getValue(e, "title");
					String position = parser.getValue(e, "position");
					String content = parser.getValue(e, "content");
					String url = parser.getValue(e, "thumb");
					String[] temp = position.split(",");
					double latitude = Double.parseDouble(temp[0]);
					double longitude = Double.parseDouble(temp[1]);

					if (NEARBY) {
						if (getMyCurrentLocation()) {
							double distance = calculateDistance(myLocation.getLongitude(), myLocation.getLatitude(),
									longitude, latitude);
							if (distance <= RADIUS)
								setMarkerMap(latitude, longitude, title, xmlName[iFile], content, url);
						}
					}
					else {
						// Add all marker items
						setMarkerMap(latitude, longitude, title, xmlName[iFile], content, url);
					}
				}
			}
		}
	}

	public void setCameraPosView(Double lati, Double longi, int zoomFactor)
	{
		cameraPosition = new CameraPosition.Builder().target(new LatLng(lati, longi)).zoom(zoomFactor).build();
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	public void setMarkerMap(Double lati, Double longi, String title, String label, String snippet, String url)
	{
		markerOption = new MarkerOptions().position(new LatLng(lati, longi)).title(title).snippet(snippet);
		setMarkerIcon(label);
		final Marker temp = googleMap.addMarker(markerOption);
		if (state == STATE.PAGE) {
			URL_PAGE_THUMBNAIL = url;
		}
		else {
			markers.put(temp.getId(), url);
		}
	}

	public void setMarkerIcon(String label)
	{
		// // changing marker color
		for (int i = 0; i < items.length; i++) {
			if (label.equals(items[i])) {
				markerOption.icon(BitmapDescriptorFactory.fromResource(LOCATION_ICON[i]));
			}
		}

		// if (label.equals("Place"))
		// markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		// if (label.equals("Restaurant"))
		// markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		// if (label.equals("Drink"))
		// markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
		// if (label.equals("Hotel"))
		// markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		// if (label.equals("Transport"))
		// markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
		// if (label.equals("Shopping"))
		// markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
		// if (label.equals("ATM"))
		// markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		// if (label.equals("Other"))
		// markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
		// if (label.equals(""))
		// marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
		// if (label.equals(""))
		// marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

	}

	public boolean getMyCurrentLocation()
	{
		myLocation = googleMap.getMyLocation();

		if (myLocation == null) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		initilizeMap();
	}

	/**
	 * function to load map If map is not created it will create it for you
	 * */
	private void initilizeMap()
	{
		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}
		}
		markers = new Hashtable<String, String>();

		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		googleMap.setMyLocationEnabled(true);
		googleMap.getUiSettings().setZoomControlsEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		googleMap.getUiSettings().setCompassEnabled(true);
		googleMap.getUiSettings().setRotateGesturesEnabled(true);
		googleMap.getUiSettings().setZoomGesturesEnabled(true);
		googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
		{
			@Override
			public void onInfoWindowClick(Marker arg0)
			{
				// TODO Auto-generated method stub
				latitude = arg0.getPosition().latitude;
				longitude = arg0.getPosition().longitude;
				showDialog(1);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
			case 0:
				return null;

			case 1:
				// For Page
				return new AlertDialog.Builder(this).setTitle("Direction").setMessage("Would you like to go there?")
						.setPositiveButton("OK", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								// Toast.makeText(getBaseContext(),
								// "OK clicked!", Toast.LENGTH_SHORT).show();
								if (getMyCurrentLocation()) {
									findDirections(myLocation.getLatitude(), myLocation.getLongitude(), latitude,
											longitude, GMapV2Direction.MODE_DRIVING);
								}
								else {
									Toast.makeText(getBaseContext(),
											"Cannot find you current location!\nCheck internet/gps connection !",
											Toast.LENGTH_SHORT).show();
								}
							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								//
							}
						}).create();

			case 2:
				// For around
				return new AlertDialog.Builder(this).setView(
						this.getLayoutInflater().inflate(R.layout.dialog_around_here, null)).create();
		}
		return null;
	}

	public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints)
	{
		PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.RED);

		for (int i = 0; i < directionPoints.size(); i++) {
			rectLine.add(directionPoints.get(i));
		}
		if (newPolyline != null) {
			newPolyline.remove();
		}
		newPolyline = googleMap.addPolyline(rectLine);
	}

	public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat,
			double toPositionDoubleLong, String mode)
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
		map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
		map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

		GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
		asyncTask.execute(map);
	}

	/**
	 * Reference from anh Hùng
	 * 
	 * @param fromLong
	 * @param fromLat
	 * @param toLong
	 * @param toLat
	 * @return
	 */
	private double calculateDistance(double fromLong, double fromLat, double toLong, double toLat)
	{
		double d2r = Math.PI / 180;
		double dLong = (toLong - fromLong) * d2r;
		double dLat = (toLat - fromLat) * d2r;
		double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r) * Math.cos(toLat * d2r)
				* Math.pow(Math.sin(dLong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367000 * c;
		return Math.round(d);
	}

	private class CustomInfoWindowAdapter implements InfoWindowAdapter
	{

		private View view;

		public CustomInfoWindowAdapter() {
			view = getLayoutInflater().inflate(R.layout.custom_info_window, null);
		}

		@Override
		public View getInfoContents(Marker marker)
		{
			if (MapActivity.this.marker != null && MapActivity.this.marker.isInfoWindowShown()) {
				MapActivity.this.marker.hideInfoWindow();
				MapActivity.this.marker.showInfoWindow();
			}
			return null;
		}

		@Override
		public View getInfoWindow(final Marker marker)
		{
			MapActivity.this.marker = marker;

			// Image
			String url = null;

			if (state == STATE.PAGE) {
				url = URL_PAGE_THUMBNAIL;
			}
			else {
				if (marker.getId() != null && markers != null && markers.size() > 0) {
					if (markers.get(marker.getId()) != null && markers.get(marker.getId()) != null) {
						url = markers.get(marker.getId());
					}
				}
			}

			final ImageView image = ((ImageView) view.findViewById(R.id.badge));

			if (url != null && !url.equalsIgnoreCase("null") && !url.equalsIgnoreCase("")) {
				// Set image from URL
				try {
					AssetManager assetManager = MapActivity.this.getAssets();
					InputStream istr = assetManager.open(url);
					Bitmap bitmap = BitmapFactory.decodeStream(istr);
					image.setImageBitmap(bitmap);
				}
				catch (IOException e) {
					image.setImageResource(R.drawable.no_image);
				}
			}
			else {
				// No image
				image.setImageResource(R.drawable.no_image);
			}

			// Title
			final String title = marker.getTitle();
			final TextView titleUi = ((TextView) view.findViewById(R.id.title));
			if (title != null) {
				titleUi.setText(title);
			}
			else {
				titleUi.setText("");
			}

			// Snippet
			final String snippet = marker.getSnippet();
			final TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
			if (snippet != null) {
				snippetUi.setText(snippet);
			}
			else {
				snippetUi.setText("");
			}

			return view;
		}
	}
}
