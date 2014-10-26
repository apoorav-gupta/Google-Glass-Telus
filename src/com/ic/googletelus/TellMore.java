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
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class TellMore extends Activity{
	private List<CardBuilder> mCards;
	private CardScrollView mCardScrollView;
	private MyCardScrollAdapter mAdapter;
	Intent intent;
	String timings, phoneNum, services;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createCards();
		getIntents();
		mCardScrollView = new CardScrollView(this);
		mAdapter = new MyCardScrollAdapter();
		mCardScrollView.setAdapter(mAdapter);
		mCardScrollView.activate();
		
		cardTapped();
		
		setContentView(mCardScrollView);
	}

	private void getIntents() {
		intent = getIntent();
		timings = intent.getStringExtra("hrs");
		phoneNum = intent.getStringExtra("pNum");
		services = intent.getStringExtra("serv");
		
	}
	
	protected void onDestroy() {
		super.onDestroy();
		//closeDB();
	}

	private void cardTapped() {
		mCardScrollView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) { // Tap Sound
						AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						am.playSoundEffect(Sounds.TAP);
						System.out.println("Tapped");
						
		                switch(position){
		                
		                case 0:		// Store Timings
		                	Intent timingIntent = new Intent(getBaseContext(), Timings.class);
		                	timingIntent.putExtra("sHours", timings);
		                	startActivity(timingIntent);
		                	break;
		                	
		                case 1:		// Details
		                	Intent detailsIntent = new Intent(getBaseContext(), StoreDetails.class);
		                	
		                	detailsIntent.putExtra("pNum", phoneNum);
		                	detailsIntent.putExtra("serv", services);
							startActivity(detailsIntent);
		                	break;
		                
		                }
					}
				});
	}
	
	private void createCards() {
        mCards = new ArrayList<CardBuilder>();

        mCards.add(new CardBuilder(this, CardBuilder.Layout.MENU)	// Store Hours
                .setText("Timings")
                .setFootnote("Find the Hours for your Store")
                .setIcon(R.drawable.timings));

        mCards.add(new CardBuilder(this, CardBuilder.Layout.MENU)	// Store Details
        		.setText("Store Details")
        		.setFootnote("Phone Number and Available Services")
        		.setIcon(R.drawable.more));

    }

	private class MyCardScrollAdapter extends CardScrollAdapter {

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
}
