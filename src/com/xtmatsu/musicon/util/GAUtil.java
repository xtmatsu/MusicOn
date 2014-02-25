package com.xtmatsu.musicon.util;

import java.util.HashMap;

import android.content.Context;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.xtmatsu.musicon.MusicOnConstants;

public class GAUtil {
	public static Tracker tracker;
	
	public static void getTracker(Context context){
		tracker = GoogleAnalytics.getInstance(context).getTracker(MusicOnConstants.GA_KEY);
	}

	public static void screenSend( String screenName) {
		HashMap<String, String> hitParameters = new HashMap<String, String>();
	    hitParameters.put(Fields.HIT_TYPE, "appview");
	    hitParameters.put(Fields.SCREEN_NAME, screenName);
	    tracker.send(hitParameters);
	}
	
	public static void eventSend(String category, String action, String label, Long value){
		tracker.send(MapBuilder
			      .createEvent(category,     // Event category (required)
			    		  		action,  // Event action (required)
			    		  		label,   // Event label
			    		  		value)            // Event value
			      .build()
			  );
		
	}

}
