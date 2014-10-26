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
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class NavActivity extends Activity {

	 private List<CardBuilder> mCards;
	    private CardScrollView mCardScrollView;
	    private ScrollAdapter mAdapter;
	    Intent intent;
	    double lat, lon;
	    String storePhone, storeAddress, storeHours, storeServices, storeName;
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        getIntentInfo();
	        createCards();

	        mCardScrollView = new CardScrollView(this);
	        mAdapter = new ScrollAdapter();
	        mCardScrollView.setAdapter(mAdapter);
	        mCardScrollView.activate();
	        selectedVal();
	        setContentView(mCardScrollView);
	    }

	    private void getIntentInfo() {
			intent = getIntent();
			lat = intent.getDoubleExtra("lat", 0.0);
			lon = intent.getDoubleExtra("lon", 0.0);
			
			storeName = intent.getStringExtra("storeName");
			storeAddress = intent.getStringExtra("addr");
			storePhone = intent.getStringExtra("pNum");
			storeServices = intent.getStringExtra("serv");
			storeHours = intent.getStringExtra("hrs");
			
		}

		private void createCards() {
	        mCards = new ArrayList<CardBuilder>();

	        mCards.add(new CardBuilder(this, CardBuilder.Layout.MENU)
	                .setText("Navigate")
	                .setFootnote("To The Nearest Location")
	                .setIcon(R.drawable.navigate));		// Arrow to go

	        mCards.add(new CardBuilder(this, CardBuilder.Layout.MENU)
	                .setText("Set as Favourite")
	                .setFootnote("Easily Navigate to your Store")
	                .setIcon(R.drawable.favorite));		// Heart for Favorite

	        mCards.add(new CardBuilder(this, CardBuilder.Layout.MENU)
	                .setText("Tell Me More")
	                .setIcon(R.drawable.more));
	    }
	    
	    private void selectedVal(){
	    	mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {		// Tap on Click
	                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	                am.playSoundEffect(Sounds.TAP);
	               
	                switch(position){
	                
	                case 0:		// Navigate
	                	Intent intent = new Intent(Intent.ACTION_VIEW);
	                	intent.setData(Uri.parse("google.navigation:q=" + lat + "," + lon));
	                	startActivity(intent);
	                	break;
	                	
	                case 1:		// Set as favorite
	                	
	                	break;
	                	
	                case 2:		// More information
	                	Intent newIntent = new Intent(getBaseContext(), TellMore.class);
						
	                	newIntent.putExtra("lat", lat);
	                	newIntent.putExtra("lon", lon);
	                	newIntent.putExtra("storeName", storeName);
	                	newIntent.putExtra("addr", storeAddress);
	                	newIntent.putExtra("pNum", storePhone);
	                	newIntent.putExtra("serv", storeServices);
	                	newIntent.putExtra("hrs", storeHours);
						
						startActivity(newIntent);
	                	break;
	                
	                }
	                
	            }
	        });
	    }

	    private class ScrollAdapter extends CardScrollAdapter {

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