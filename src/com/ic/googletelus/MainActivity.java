package com.ic.googletelus;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;

import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class MainActivity extends Activity {

	private CardScrollView mCardScroller;
	CardBuilder card;
	TextToSpeech tts;
	private GestureDetector gDect;

	private View mView;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		mView = buildView("Telus Location Finder"); // Put location name here

		mCardScroller = new CardScrollView(this);
		mCardScroller.setAdapter(new CardScrollAdapter() {
			@Override
			public int getCount() {
				return 3;		// 3 Options
			}

			@Override
			public Object getItem(int position) {
				return mView;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return mView;
			}

			@Override
			public int getPosition(Object item) {
				if (mView.equals(item)) {
					return 0;
				}
				return AdapterView.INVALID_POSITION;
			}
		});
		// Handle the TAP event.
		mCardScroller
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						am.playSoundEffect(Sounds.TAP);
					}
				});
		setContentView(mCardScroller);

		gDect = createGestureDetector(this);
	}

	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);
		// Create a base listener for generic gestures
		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.TAP) {

					System.out.println("Tapped");
					openOptionsMenu();
					return true;

				} else if (gesture == Gesture.SWIPE_LEFT) {

					System.out.println("Swipe Left");
					talkTelus();
					// do something on left (backwards) swipe
					return true;

				}
				return false;
			}
		});
		gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
			@Override
			public void onFingerCountChanged(int previousCount, int currentCount) {
				// do something on finger count changes
			}
		});
		gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
			@Override
			public boolean onScroll(float displacement, float delta,
					float velocity) {

				return true;
			}
		});
		return gestureDetector;
	}


	@Override
	protected void onResume() {
		super.onResume();
		mCardScroller.activate();
	}

	@Override
	protected void onPause() {
		mCardScroller.deactivate();
		super.onPause();
	}

	private View buildView(String text) {

		card = new CardBuilder(this, CardBuilder.Layout.TEXT);
		card.setText(text);
		return card.getView();
	}

	
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.DONUT)
	void talkTelus() {
		tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				tts.speak("Testing TTS", TextToSpeech.QUEUE_FLUSH, null);
			}
		});

		// tts.shutdown();
	}

	public boolean onGenericMotionEvent(MotionEvent event) {
		if (gDect != null) {
			return gDect.onMotionEvent(event);
		}
		return false;

	}

}
