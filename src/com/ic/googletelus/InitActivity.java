package com.ic.googletelus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class InitActivity extends Activity {

	String name = "";
    double lat = 0.0;
    double lon = 0.0;
    String address = "";
    String phone = "";
    String service = "";
    String hours = "";
	GlassDatabaseHelper databaseHelper;
	int numStores;
	int storesInDB;
	
	static double dbLat[] = new double[10];
	static double dbLong[] = new double[10];
	File file = new File( "C:\\Users\\Apoorav\\Desktop\\Projects\\Google Glass\\Glass\\telus.txt");
	private List<CardBuilder> mCards;
	private CardScrollView mCardScrollView;
	private CardScrollAdapterC mAdapter;
	
	
	@Override

	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		createCards();
		
		mCardScrollView = new CardScrollView(this);
        mAdapter = new CardScrollAdapterC();
        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.activate();
        setupClickListener();
        setContentView(mCardScrollView);
		
	//	openDB();

	}

	private void createCards() {
		mCards = new ArrayList<CardBuilder>();
		
        mCards.add(new CardBuilder(this, CardBuilder.Layout.CAPTION)	// Splash Screen
		        .addImage(R.drawable.telus_background)
		        .setFootnote("TAP to Continue"));
   
    }
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//closeDB();
	}

    private void setupClickListener() {
        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.TAP);

                Intent newIntent = new Intent(getBaseContext(), AvailableLocations.class);
                startActivity(newIntent);
            }
        });
    }
	
	private void closeDB() {	// Not using for demo
		databaseHelper.close();
	}

	private void openDB() {		// Not using for demo
		databaseHelper = new GlassDatabaseHelper(this);
		databaseHelper.open();
		databaseHelper.addStore(43.218254, -79.863199, "phoneNumbers", "Store Hours", "Services", 0, "Store Name", "Store Address");
		databaseHelper.addStore(43.241355, -79.769664, "905-573-2388", "Monday: 8:00AM - 5:00PM,Tuesday: 8:00AM - 5:00PM,Wednesday: 8:00AM - 5:00PM,Thursday: 8:00AM - 5:00PM,Friday: 8:00AM - 5:00PM,Saturday: Closed,Sunday: Closed", "Mobility - Trade in program", 0, "MOBILE COMMUNICATION SERVICES", "297 Nash Rd N,Hamilton, ON");
		databaseHelper.addStore(43.230303, -79.766091, "905-560-1739", "Monday: 10:00AM - 9:00PM,Tuesday: 10:00AM - 9:00PM,Wednesday: 10:00AM - 9:00PM,Thursday: 10:00AM - 9:00PM,Friday: 10:00AM - 9:00PM,Saturday: 9:30AM - 6:00PM,Sunday: 11:00AM - 5:00PM", "Mobility - Product care centre & Trade in program", 0, "TELUS Store / Eastgate Mall", "Unit G25 - 75 Centennial Pkwy N,Hamilton, ON");
		databaseHelper.addStore(43.324716, -79.818781, "905-631-8689", "Monday: 10:00AM - 9:00PM,Tuesday: 10:00AM - 9:00PM,Wednesday: 10:00AM - 9:00PM,Thursday: 10:00AM - 9:00PM,Friday: 10:00AM - 9:00PM,Saturday: 9:30AM - 6:00PM,Sunday: 11:00AM - 6:00PM", "Mobility - Product care centre & Trade in program", 0, "TELUS Store / Mapleview Mall  ", "Unit A24A - 900 Maple Ave,Burlington, ON");
		databaseHelper.addStore(43.235335, -79.714462, "905-662-6444", "Monday: 9:00AM - 5:00PM,Tuesday: 9:00AM - 5:00PM,Wednesday: 9:00AM - 5:00PM,Thursday: 9:00AM - 5:00PM,Friday: 9:00AM - 5:00PM,Saturday: Closed,Sunday: Closed", "Mobility - Product care centre & Trade in program", 0, "C & I Technologies Inc", "Unit 110 - 442 Millen Rd,Stoney Creek, ON");
		dbSetup();
		storesInDB = databaseHelper.getCount();
		getLatLong();
		
	}

	private void getLatLong() {		// Stores location information about each store

		for (int i = 0; i < storesInDB; i++){
			dbLat[i] = databaseHelper.getLat(i);
		}
		
	}

	private void dbSetup() {

		Boolean addStore = true;
		BufferedReader read = null;

		try {
			read = new BufferedReader(new FileReader(file));
			numStores = 0;
			while (read.readLine() != null) {
				numStores++;
			}

			numStores = numStores / 8;
			read.close();

			read = new BufferedReader(new FileReader(file));

			for (int i = 1; i <= numStores; ++i) {
				for (int k = 1; k <= 8; ++k) {
					if ((k % 8) == 0) {		// Next line
						read.readLine();
					} else if ((k % 7) == 0) {		// Store hours
						hours = read.readLine();
					} else if ((k % 6) == 0) {		// Store services
						service = read.readLine();
					} else if ((k % 5) == 0) {		// Store contact information
						phone = read.readLine();
					} else if ((k % 4) == 0) {		// Store address
						address = read.readLine();	
					} else if ((k % 3) == 0) {
						
						lon = Double.parseDouble(read.readLine());
						
						for (int b = 0; b < storesInDB; b++){
							if (lon == dbLong[b]){	// Checking for repeated stores
								addStore = false;
							}
						}
					} else if ((k % 2) == 0) {		// Store latitude
						lat = Double.parseDouble(read.readLine());
					} else {
						name = read.readLine();		// Store name
					}
				}
				if (addStore == true){		// Not currently in the DB
					databaseHelper.addStore(lat, lon, phone, hours, service, 0, name, address);
				}
			}
			read.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (read != null) {
					read.close();
				}
			} catch (IOException e) {
			}
		}

	}

	private class CardScrollAdapterC extends CardScrollAdapter {

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
        public int getItemViewType(int position){
            return mCards.get(position).getItemViewType();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mCards.get(position).getView(convertView, parent);
        }
    }
	
}
