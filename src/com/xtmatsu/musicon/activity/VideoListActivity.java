/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xtmatsu.musicon.activity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.bugsense.trace.BugSenseHandler;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.xtmatsu.musicon.MusicOnConstants;
import com.xtmatsu.musicon.R;
import com.xtmatsu.musicon.activity.fragment.VideoFragment;
import com.xtmatsu.musicon.activity.fragment.VideoListFragment;
import com.xtmatsu.musicon.util.GAUtil;

/**
 * A sample Activity showing how to manage multiple YouTubeThumbnailViews in an adapter for display
 * in a List. When the list items are clicked, the video is played by using a YouTubePlayerFragment.
 * <p>
 * The demo supports custom fullscreen and transitioning between portrait and landscape without
 * rebuffering.
 */
@TargetApi(13)
public final class VideoListActivity extends Activity implements OnFullscreenListener, OnQueryTextListener {

  /** The padding between the video list and the video in landscape orientation. */
  private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;

  private FragmentTransaction listFragmentTransaction;
  private VideoListFragment listFragment;
  private VideoFragment videoFragment;

  private View videoBox;
  private View closeButton;

  private boolean isFullscreen;
  
  /* Action Bar */
  private SearchView searchView;
  private String[] modeArray = null;
  private int MODE = 0;
  private boolean isNotFirst = false;
  
  /* Drawer */
  private DrawerLayout mDrawerLayout;
  private ListView mDrawerList;
  private ActionBarDrawerToggle mDrawerToggle;
  private CharSequence mDrawerTitle;
  private CharSequence mTitle;
  private String[] mPlanetTitles;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.video_list_demo);
    
    BugSenseHandler.initAndStartSession(VideoListActivity.this, MusicOnConstants.BUGSENSE_KEY);
    GAUtil.getTracker(this); 
    
    mTitle = mDrawerTitle = getTitle();
    mPlanetTitles = getResources().getStringArray(R.array.action_list);
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerList = (ListView) findViewById(R.id.left_drawer);

    // set a custom shadow that overlays the main content when the drawer opens
    mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    // set up the drawer's list view with items and click listener
    mDrawerList.setAdapter(new ArrayAdapter<String>(this,
            R.layout.drawer_list_item, mPlanetTitles));
    mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    // enable ActionBar app icon to behave as action to toggle nav drawer
    getActionBar().setDisplayHomeAsUpEnabled(true);
    getActionBar().setHomeButtonEnabled(true);

    // ActionBarDrawerToggle ties together the the proper interactions
    // between the sliding drawer and the action bar app icon
    mDrawerToggle = new ActionBarDrawerToggle(
            this,                  /* host Activity */
            mDrawerLayout,         /* DrawerLayout object */
            R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
            R.string.drawer_open,  /* "open drawer" description for accessibility */
            R.string.drawer_close  /* "close drawer" description for accessibility */
            ) {
        public void onDrawerClosed(View view) {
            getActionBar().setTitle(mTitle);
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }

        public void onDrawerOpened(View drawerView) {
            getActionBar().setTitle(mDrawerTitle);
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }
    };
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    if (savedInstanceState == null) {
        selectItem(0);
    }
    
    // arraylistの設定(strings.xmlの配列を取得)
    modeArray = getResources().getStringArray(R.array.action_list);

    if(getFragmentManager().findFragmentById(R.id.list_fragment) == null){
    	listFragment = new VideoListFragment();
    	listFragment.setMode(modeArray[MODE]);
    	listFragmentTransaction = getFragmentManager().beginTransaction();
    	listFragmentTransaction.add(R.id.list_fragment, listFragment).commit();
    }
    if(getFragmentManager().findFragmentById(R.id.video_fragment_container) == null){
    	videoFragment = new VideoFragment();
    	FragmentTransaction videoFragmentTransaction = getFragmentManager().beginTransaction();
    	videoFragmentTransaction.add(R.id.video_fragment_container, videoFragment).commit();
    }

    videoBox = findViewById(R.id.video_box);
    closeButton = findViewById(R.id.close_button);

    videoBox.setVisibility(View.INVISIBLE);

  }
  
  @Override
	protected void onStart() {
		super.onStart();
		layout();
	}
  
  @Override
  public void onStop() {
    super.onStop();
    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
  }
  
  /* The click listner for ListView in the navigation drawer */
  private class DrawerItemClickListener implements ListView.OnItemClickListener {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          selectItem(position);
      }
  }
  
  private void selectItem(int position) {
      // update the main content by replacing fragments
	  MODE = position;
	  
	  if(isNotFirst){
	  // 選択したランキング順にする
	  listFragment = new VideoListFragment();
	  listFragmentTransaction = getFragmentManager().beginTransaction();
	  listFragment.setArtist(null);
	  listFragment.setMode(modeArray[MODE]);
	  listFragmentTransaction.replace(R.id.list_fragment, listFragment);
	  listFragmentTransaction.addToBackStack(null);
	  listFragmentTransaction.commit();
	  GAUtil.eventSend("RankingSearch", modeArray[MODE], null, null);
	  }else{
		  isNotFirst = true;
	  }
	  
      // update selected item and title, then close the drawer
      mDrawerList.setItemChecked(position, true);
      setTitle(mPlanetTitles[position]);
      mDrawerLayout.closeDrawer(mDrawerList);
      
  }
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// action barの設定
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_menu, menu);
	 
	    // SearchViewの設定
	    searchView = (SearchView) menu.findItem(R.id.search_view)
	            .getActionView();
	    searchView.setQueryHint(MusicOnConstants.hint);
	    searchView.setOnQueryTextListener(this);
	    	    
      return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onQueryTextChange(String query) {
		return false;
}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// ワードで検索
		listFragment = new VideoListFragment();
		listFragmentTransaction = getFragmentManager().beginTransaction();
		listFragment.setArtist(query);
		listFragmentTransaction.replace(R.id.list_fragment, listFragment);
		listFragmentTransaction.addToBackStack(null);
		listFragmentTransaction.commit();
		
	    // ソフトキーボードを非表示にする
		searchView.clearFocus();
		
		GAUtil.eventSend("QuerySearch", query, "input", null);
		
		return true;
	}
	
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	       // The action bar home/up action should open or close the drawer.
	       // ActionBarDrawerToggle will take care of this.
	      if (mDrawerToggle.onOptionsItemSelected(item)) {
	          return true;
	      }
	          return super.onOptionsItemSelected(item);
	  }
	  
	  @Override
	  public void setTitle(CharSequence title) {
	      mTitle = title;
	      getActionBar().setTitle(mTitle);
	  }
	  
		/* Called whenever we call invalidateOptionsMenu() */
	    @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	        // If the nav drawer is open, hide action items related to the content view
	    	//TODO 検索ボタンを消す処理を書く
//	        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//	        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
	        return super.onPrepareOptionsMenu(menu);
	    }
	    @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
	    }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    // Pass any configuration change to the drawer toggls
    mDrawerToggle.onConfigurationChanged(newConfig);
    
    layout();
  }

  @Override
  public void onFullscreen(boolean isFullscreen) {
    this.isFullscreen = isFullscreen;

    layout();
  }

  /**
   * Sets up the layout programatically for the three different states. Portrait, landscape or
   * fullscreen+landscape. This has to be done programmatically because we handle the orientation
   * changes ourselves in order to get fluent fullscreen transitions, so the xml layout resources
   * do not get reloaded.
   */
  private void layout() {
    boolean isPortrait =
        getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

    listFragment.getView().setVisibility(isFullscreen ? View.GONE : View.VISIBLE);
    listFragment.setLabelVisibility(isPortrait);
    closeButton.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

    if (isFullscreen) {
      videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
      setLayoutSize(videoFragment.getView(), MATCH_PARENT, MATCH_PARENT);
      setLayoutSizeAndGravity(videoBox, MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
    } else if (isPortrait) {
      setLayoutSize(listFragment.getView(), MATCH_PARENT, MATCH_PARENT);
      setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
      setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
    } else {
      videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
      int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
      setLayoutSize(listFragment.getView(), screenWidth / 4, MATCH_PARENT);
      int videoWidth = screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
      setLayoutSize(videoFragment.getView(), videoWidth, WRAP_CONTENT);
      setLayoutSizeAndGravity(videoBox, videoWidth, WRAP_CONTENT,
          Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    }
  }

  public void onClickClose(@SuppressWarnings("unused") View view) {
    listFragment.getListView().clearChoices();
    listFragment.getListView().requestLayout();
    videoFragment.pause();
    videoBox.animate()
        .translationYBy(videoBox.getHeight())
        .setDuration(MusicOnConstants.ANIMATION_DURATION_MILLIS)
        .withEndAction(new Runnable() {
          @Override
          public void run() {
            videoBox.setVisibility(View.INVISIBLE);
          }
        });
  }


  // Utility methods for layouting.

  private int dpToPx(int dp) {
    return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
  }

  private static void setLayoutSize(View view, int width, int height) {
    LayoutParams params = view.getLayoutParams();
    params.width = width;
    params.height = height;
    view.setLayoutParams(params);
  }

  private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
    params.width = width;
    params.height = height;
    params.gravity = gravity;
    view.setLayoutParams(params);
  }

}
