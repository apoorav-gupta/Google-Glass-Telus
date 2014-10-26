package com.ic.googletelus;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;


public class StoreDetails extends Activity{
	private List<CardBuilder> mCards;
	private CardScrollView mCardScrollView;
	private MyCardScrollAdapter mAdapter;
	Intent intent;
	String storeNum, storeServices;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getIntents();
		createCards();
		
		mCardScrollView = new CardScrollView(this);
		mAdapter = new MyCardScrollAdapter();
		mCardScrollView.setAdapter(mAdapter);
		mCardScrollView.activate();
			
		setContentView(mCardScrollView);
	}

	private void createCards() {
        mCards = new ArrayList<CardBuilder>();

        mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT)	// Store details
                .setText(storeServices)
                .setFootnote(storeNum));
        
        // Can add more cards as necessary
		
	}
	
	private void getIntents(){
		intent = getIntent();
		storeNum = intent.getStringExtra("pNum");
		storeServices = intent.getStringExtra("serv");

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
