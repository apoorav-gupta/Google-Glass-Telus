package com.ic.googletelus;

import java.util.ArrayList;
import java.util.List;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class AvailableLocations extends Activity {

	private List<CardBuilder> mCards;
	private CardScrollView mCardScrollView;
	private CScrollAdapter mAdapter;
	GlassDatabaseHelper databaseHelper;
	int storesInDB;
	double currentLong = -79.919225;
	double currentLat = 43.264567;
	boolean locFound = false;
	
	ArrayList<Store> list;
	ArrayList<Store> nearestList;
	
	String names[] = {"TELUS Store / Lime Ridge Mall", "MOBILE COMMUNICATION SERVICES", "TELUS Store / Eastgate Mall", "TELUS Store / Mapleview Mall", "C & I Technologies Inc"};
	String addresses[] = {"Unit 445 - 999 Upper Wentworth St,Hamilton", "297 Nash Rd N, Hamilton", "Unit G25 - 75 Centennial Pkwy N, Hamilton", "Unit A24A - 900 Maple Ave, Burlington", "Unit 110 - 442 Millen Rd,Stoney Creek"};
	double distances[] = new double[5];
	double lats[] = {43.218254, 43.241355, 43.230303, 43.324716, 43.235335};
	double longs[] = {-79.863199, -79.769664, -79.766091, -79.818781, -79.714462};
	String favoriteCaption = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupDatabase();
		getLocation();

		initStoresArrayList(); 		// Hardcoding
		
		AudioManager aud = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		aud.playSoundEffect(Sounds.SUCCESS);	// Play sound when location found
		
		//nearestStores();
		createCards();

		mCardScrollView = new CardScrollView(this);
		mAdapter = new CScrollAdapter();
		mCardScrollView.setAdapter(mAdapter);
		mCardScrollView.activate();
		goToOption();
		setContentView(mCardScrollView);
	}

	private void initStoresArrayList() {
		list = new ArrayList<Store>();
		nearestList = new ArrayList<Store>();
		
		// Hardcoded for demo
		list.add(new Store("TELUS Store / Lime Ridge Mall", 43.218254, -79.863199, "Unit 445 - 999 Upper Wentworth St,Hamilton", "905-387-8171", "Mobility\nProduct care centre & Trade in program", "Monday: 10:00AM - 9:00PM,Tuesday: 10:00AM - 9:00PM,Wednesday: 10:00AM - 9:00PM,Thursday: 10:00AM - 9:00PM,Friday: 10:00AM - 9:00PM,Saturday: 9:30AM - 6:00PM,Sunday: 11:00AM - 5:00PM", 0 ));
		list.add(new Store("MOBILE COMMUNICATION SERVICES", 43.241355, -79.769664, "297 Nash Rd N,Hamilton", "905-573-2388", "Mobility\nTrade in program", "Monday: 8:00AM - 5:00PM,Tuesday: 8:00AM - 5:00PM,Wednesday: 8:00AM - 5:00PM,Thursday: 8:00AM - 5:00PM,Friday: 8:00AM - 5:00PM,Saturday: Closed,Sunday: Closed", 0));
		list.add(new Store("TELUS Store / Eastgate Mall", 43.230303, -79.766091, "Unit G25 - 75 Centennial Pkwy N,Hamilton", "905-560-1739", "Mobility\nProduct care centre & Trade in program", "Monday: 10:00AM - 9:00PM,Tuesday: 10:00AM - 9:00PM,Wednesday: 10:00AM - 9:00PM,Thursday: 10:00AM - 9:00PM,Friday: 10:00AM - 9:00PM,Saturday: 9:30AM - 6:00PM,Sunday: 11:00AM - 5:00PM", 0));
		list.add(new Store("TELUS Store / Mapleview Mall", 43.324716, -79.818781, "Unit A24A - 900 Maple Ave,Burlington", "905-631-8689", "Mobility\nProduct care centre & Trade in program", "Monday: 10:00AM - 9:00PM,Tuesday: 10:00AM - 9:00PM,Wednesday: 10:00AM - 9:00PM,Thursday: 10:00AM - 9:00PM,Friday: 10:00AM - 9:00PM,Saturday: 9:30AM - 6:00PM,Sunday: 11:00AM - 6:00PM", 0));
		list.add(new Store("C & I Technologies Inc", 43.235335, -79.714462, "Unit 110 - 442 Millen Rd,Stoney Creek", "905-662-6444", "Mobility\nProduct care centre & Trade in program", "Monday: 9:00AM - 5:00PM,Tuesday: 9:00AM - 5:00PM,Wednesday: 9:00AM - 5:00PM,Thursday: 9:00AM - 5:00PM,Friday: 9:00AM - 5:00PM,Saturday: Closed,Sunday: Closed", 1));
		
		int stores = 5;
		
		for (int i = 0; i < stores; i ++){
			distances[i] = getDist(currentLat, currentLong, list.get(i).getLat(), list.get(i).getLong());
		} 
		
		/*getFavStore(stores);
		nearestStoreArrayList(stores);
		nearestStoreArrayList(stores - 1);
		nearestStoreArrayList(stores - 2);
		*/
		
		double compareDist;
		Store tempStore;
		int i, j;
		for (j = 1; j < stores; j++){
			compareDist = distances[j];
			tempStore = list.get(j);
			for (i = j; i > 0 && distances[i-1] > compareDist; i--){
				distances[i] = distances[i-1];
				list.set(i, list.get(i-1));
				
			}
			distances[i] = compareDist;
			list.set(i, tempStore);
			
		}
		
		for (j = 0; j < stores; j++){
			if (list.get(j).getFav() == 1){
				nearestList.add(list.get(j));
				list.remove(j);
				favoriteCaption = "Favorite";
			}
		}
		
		nearestList.add(list.get(0));
		nearestList.add(list.get(1));
		nearestList.add(list.get(2));
		
	}

	private void nearestStoreArrayList(int stores) {
		int shortest = 0;
		
		for (int j = 0; j < stores; j++){
			if ((distances[j] < distances[shortest]) && (list.get(j).getFav() != 1)){
				shortest = j;
				nearestList.add(list.get(j));
				list.remove(j);
			}
		}
		
	}

	private void nearestStores() {
		
		Cursor c = databaseHelper.getAll();
		List<String> nNames = new ArrayList<String>();
		List<String> nAddresses = new ArrayList<String>();
		
		List<Double> nLat = new ArrayList<Double>();
		List<Double> nLong = new ArrayList<Double>();
		List<Double> nDist = new ArrayList<Double>();
		
		List<Integer> nFav = new ArrayList<Integer>();

		int n = 0;
		
		if (c.moveToFirst()){
			do{
				nNames.add(c.getString(c.getColumnIndex("storeName")));
				nAddresses.add(c.getString(c.getColumnIndex("storeAddress")));
				
				nLat.add(c.getDouble(c.getColumnIndex("latitude")));
				nLong.add(c.getDouble(c.getColumnIndex("longitude")));
				nDist.add(getDist(nLat.get(n), nLong.get(n), currentLat, currentLong));
				nFav.add(c.getInt(c.getColumnIndex("favOrNot")));
				
				n++;
			}while(c.moveToNext());
		}
		
		boolean favFound = false;
		int remainingStores = storesInDB;
		int lowestDist = 0, secondLowestDist = 0;
		
		for (int j = 0; j < storesInDB; j++){
			if (nFav.get(j) == 1){
				names[0] = nNames.get(j);
				addresses[0] = nAddresses.get(j);
				distances[0] = nDist.get(j);
				lats[0] = nLat.get(j);
				longs[0] = nLong.get(j);
				favFound = true;
				
				nNames.remove(j);
				nAddresses.remove(j);
				nDist.remove(j);
				nLat.remove(j);
				nLong.remove(j);
				break;
			
			}
		}
		
		if (favFound) {	remainingStores = remainingStores -  1;	}
		
		for (int k = 0; k < remainingStores; k++){
			if (nDist.get(k) < nDist.get(lowestDist)){
				lowestDist = k;
			}
		}
		
		names[1] = nNames.get(lowestDist);
		addresses[1] = nAddresses.get(lowestDist);
		distances[1] = nDist.get(lowestDist);
		lats[1] = nLat.get(lowestDist);
		longs[1] = nLong.get(lowestDist);
		
		nNames.remove(lowestDist);
		nAddresses.remove(lowestDist);
		nDist.remove(lowestDist);
		nLat.remove(lowestDist);
		nLong.remove(lowestDist);
		
		for (int k = 0; k < remainingStores - 1; k++){
			if (nDist.get(k) < nDist.get(secondLowestDist)){
				secondLowestDist = k;
			}
		}
		
		names[2] = nNames.get(secondLowestDist);
		addresses[2] = nAddresses.get(secondLowestDist);
		distances[2] = nDist.get(secondLowestDist);
		lats[2] = nLat.get(secondLowestDist);
		longs[2] = nLong.get(secondLowestDist);
		
		c.close();
	}

	private double getDist(double lat1, double lon1, double lat2, double lon2) {	// Haversine Formula
		
		double R = 6372.8;
		double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double longDecimals =  R * c;
        return (double)Math.round(longDecimals * 100) / 100;
	}

	protected void onDestroy() {
		super.onDestroy();
		//closeDB();
	}

	private void closeDB() {
		databaseHelper.close();
	}

	private void setupDatabase() {
		databaseHelper = new GlassDatabaseHelper(this);
		databaseHelper.open();
		storesInDB = databaseHelper.getCount();
	}

	private void getLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new myLocationListener();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		List<String> providers = locationManager.getProviders(criteria, true);
		for (String provider : providers) {
			locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
		}
	}

	private void createCards() {
		mCards = new ArrayList<CardBuilder>();

		mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT)
				// Location One
				.setText(nearestList.get(0).getName() + "\n"  + getDist(currentLat, currentLong, nearestList.get(0).getLat(), nearestList.get(0).getLong()) + " km")
				.setFootnote(favoriteCaption));


		mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT)
				// Location Two
		.setText(nearestList.get(1).getName() + "\n" +  getDist(currentLat, currentLong, nearestList.get(1).getLat(), nearestList.get(1).getLong()) + " km"));


		mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT) // Location Three
		.setText(nearestList.get(2).getName() + "\n" + getDist(currentLat, currentLong, nearestList.get(2).getLat(), nearestList.get(2).getLong()) + " km"));


	}

	private void goToOption() {
		mCardScrollView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) { // Tap Sound
						AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						am.playSoundEffect(Sounds.TAP);

						Intent intent = new Intent(getBaseContext(), NavActivity.class);
						
						intent.putExtra("lat", nearestList.get(position).getLat());
						intent.putExtra("lon", nearestList.get(position).getLong());
						intent.putExtra("storeName", nearestList.get(position).getName());
						intent.putExtra("addr", nearestList.get(position).getAddr());
						intent.putExtra("pNum", nearestList.get(position).getPhone());
						intent.putExtra("serv", nearestList.get(position).getServ());
						intent.putExtra("hrs", nearestList.get(position).getHrs());
						
						startActivity(intent);
					}
				});
	}

	private class CScrollAdapter extends CardScrollAdapter {

		@Override
		public int getPosition(Object item) {
			return mCards.indexOf(item);
		}

		@Override
		public int getCount() {
			return mCards.size();
		}

		@Override
		public Object getItem(int position) {
			return mCards.get(position);
		}

		@Override
		public int getViewTypeCount() {
			return CardBuilder.getViewTypeCount();
		}

		@Override
		public int getItemViewType(int position) {
			return mCards.get(position).getItemViewType();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return mCards.get(position).getView(convertView, parent);
		}
	}

	class myLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if ((location != null) && (locFound == false)) {
				currentLong = location.getLongitude();
				currentLat = location.getLatitude();
				locFound = true;
			}

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

	}
}