package com.example.hanzibrush;

import java.io.File;
import java.util.ArrayList;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureStore;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class HanziDraw extends Activity {
	
	/**
	 * Activity resources.
	 */
	private final File gStoreFile = new File(
			Environment.getExternalStorageDirectory(),"gestures");
	
	/**
	 * Globals.
	 */
	GestureLibrary gLibrary;
	GestureOverlayView gOverlayView;
	TextView gDebugTextView;
	int numGestures;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Initialize UI components:
		setContentView(R.layout.activity_hanzi_draw);
		gDebugTextView = (TextView) findViewById(R.id.gdebugtextview);
		gOverlayView = (GestureOverlayView) findViewById(R.id.goverlayview);
		
		// Initialize gestures library:
		gLibrary = GestureLibraries.fromFile(gStoreFile);
		gLibrary.setSequenceType(GestureStore.SEQUENCE_INVARIANT);
		gLibrary.setOrientationStyle(GestureStore.ORIENTATION_SENSITIVE);
		
		// Always call load() after the above settings changes.
		gLibrary.load();
		numGestures = gLibrary.getGestureEntries().size(); 
		
		if (numGestures <= 0) {
			gDebugTextView.setVisibility(View.VISIBLE);
			gDebugTextView.setText("No saved hanzi found!");
		} else {
			gDebugTextView.setText(numGestures + " hanzi were found.");
		}
		
		/**
		 * Add the gesture activity listener after all resources
		 * and UI have been initialized.
		 */
		gOverlayView.addOnGesturePerformedListener(gPerformedListener);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.hanzi_draw, menu);
		return true;
	}
	
	/**
	 * Code to run when the gesture overlay detects that the user
	 * has completed a gesture.
	 */
	OnGesturePerformedListener gPerformedListener = 
			new OnGesturePerformedListener() {
		
		@Override
		public void onGesturePerformed(GestureOverlayView view, Gesture gesture) {
			String debugText;
			int matches;
			double topScore=0;
			Prediction topPred = null;
			
			ArrayList<Prediction> prediction = gLibrary
					.recognize(gesture);
			
			matches = prediction.size();
			
			if (matches > 0) { 
				topPred = prediction.get(0); 
				topScore = prediction.get(0).score;
				
				if (topScore > 50) {
					debugText = matches + " matches. \n" + 
							topPred.name + ":" + topScore;
				} else {
					debugText = "Matches found, but unlikely.";
				}
				
			} else {
				debugText = "No matches found.";
			}

			gDebugTextView.setText(debugText);
		}
		
	};// End of OnGesturePerformedListener

}
