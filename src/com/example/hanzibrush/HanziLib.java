package com.example.hanzibrush;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureStore;
import android.gesture.Prediction;
import android.os.Environment;

/**
 * This class is responsible for keeping all hanzi characters
 * stored, and offering interfaces to necessary subsets of 
 * hanzi depending on the current game's scene, as well as
 * associated functions.
 * @author fran
 *
 */
public class HanziLib {
	
	/**
	 * Globals.
	 */
	private int orientationStyle = GestureStore.ORIENTATION_SENSITIVE;
	private int sequenceType = GestureStore.SEQUENCE_SENSITIVE;
	private int minScore = 10;
	private int currentScene;
	
	/**
	 * Activity resources.
	 */
	private final File gStoreFile = new File(
			Environment.getExternalStorageDirectory(),"gestures");
	
	/**
	 * The set of Hanzi associated with the current
	 * scene in the game.
	 */
	private GestureLibrary currentSet;
	
	/**
	 * The full set of Hanzi available in system 
	 * resources.
	 */
	private GestureLibrary fullSet;
	
	/**
	 * Used for classifying.
	 */
	ArrayList<Prediction> prediction;
	
	/**
	 * Maps game scene number to a list of strings representing
	 * the names of the hanzi available during the scene.
	 */
	@SuppressLint("UseSparseArrays")
	private static HashMap<Integer, String []> hanziSceneMap =
			new HashMap<Integer, String []>(); 
			{
		hanziSceneMap.put(0, new String [] {"moon","big","king"} );
		hanziSceneMap.put(1, new String [] {"person","duck","garden"} );
	}
	
	public HanziLib() {
		fullSet = GestureLibraries.fromFile(gStoreFile);
		fullSet.setOrientationStyle(orientationStyle);
		fullSet.setSequenceType(sequenceType);
		fullSet.load();
		// Use when we import lib into app as resource:
		//fullSet = GestureLibraries.fromRawResource(gStoreResource);
		
		
	}
	
	/**
	 * Returns a GestureLibrary object filled with only
	 * the hanzi required for the given scene number.
	 * @param sceneNum
	 * @return GestureLibrary
	 */
	private GestureLibrary genLibrary(Integer sceneNum) {
		String [] curHanzi = hanziSceneMap.get(sceneNum);
		
		GestureLibrary gLib = GestureLibraries.fromFile(this.gStoreFile);
		
		for (String hanzi : curHanzi) {
			gLib.addGesture(hanzi, fullSet.getGestures(hanzi).get(0));
		}
		
		gLib.setOrientationStyle(orientationStyle);
		gLib.setSequenceType(sequenceType);
		
		// This throws exceptions, but works.
		// May want to try simply not loading.
		//gLib.load();
		
		return gLib;
	}
	
	public int getTotalNumber() {  
		return fullSet.getGestureEntries().size();
	}
	
	public int getCurrentNumber() {
		return currentSet.getGestureEntries().size();
	}
	
	/**
	 * Classify the given gesture. 
	 * @param g
	 * @return True if there is at least one possible match.
	 */
	public boolean classify(Gesture g) {
		prediction = currentSet.recognize(g);
		return (prediction.size() > 0 
				&& prediction.get(0).score > minScore);
	}
	
	public int getNumMatches() {
		return prediction.size();
	}
	
	public Prediction getTopMatch() {
		return prediction.get(0);
	}
	
	public void setCurrentScene(int sceneNum) {
		currentScene = sceneNum;
		currentSet = genLibrary(sceneNum);
	}
	
	public int getCurrentScene() {
		return currentScene;
	}

}
