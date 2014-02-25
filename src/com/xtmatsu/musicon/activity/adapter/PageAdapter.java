package com.xtmatsu.musicon.activity.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailLoader.ErrorReason;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.xtmatsu.musicon.MusicOnConstants;
import com.xtmatsu.musicon.R;
import com.xtmatsu.musicon.dto.YoutubeDto;

/**
 * Adapter for the video list. Manages a set of YouTubeThumbnailViews, including initializing each
 * of them only once and keeping track of the loader of each one. When the ListFragment gets
 * destroyed it releases all the loaders.
 */
public class PageAdapter extends ArrayAdapter<YoutubeDto> {

  private final List<YoutubeDto> entries;
  private final List<View> entryViews;
  private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
  private final LayoutInflater inflater;
  private final ThumbnailListener thumbnailListener;

  private boolean labelsVisible;

  public PageAdapter(Context context, int resource, List<YoutubeDto> entries) {
	super(context, resource, entries);
    this.entries = entries;

    entryViews = new ArrayList<View>();
    thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
    inflater = LayoutInflater.from(context);
    thumbnailListener = new ThumbnailListener();

    labelsVisible = true;
  }

	public void releaseLoaders() {
    for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
      loader.release();
    }
  }

  public void setLabelVisibility(boolean visible) {
    labelsVisible = visible;
    for (View view : entryViews) {
      view.findViewById(R.id.text).setVisibility(visible ? View.VISIBLE : View.GONE);
    }
  }

  @Override
  public int getCount() {
    return entries.size();
  }

  @Override
  public YoutubeDto getItem(int position) {
    return entries.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
	PostHolder holder;
    View view = convertView;
    YoutubeDto entry = entries.get(position);

    // There are three cases here
    if (view == null) {
      // 1) The view has not yet been created - we need to initialize the YouTubeThumbnailView.
      view = inflater.inflate(R.layout.video_list_item, parent, false);
      holder = new PostHolder();
      holder.thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
      holder.thumbnail.setTag(entry.videoId);
      holder.thumbnail.initialize(MusicOnConstants.DEVELOPER_KEY, thumbnailListener);
      holder.label = ((TextView) view.findViewById(R.id.text));
      view.setTag(holder);
    } else {
    	holder = (PostHolder) view.getTag();
     	  holder.thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
     	  holder.loader = thumbnailViewToLoaderMap.get(holder.thumbnail);
      if (holder.loader == null) {
        // 2) The view is already created, and is currently being initialized. We store the
        //    current videoId in the tag.
    	  holder.thumbnail.setTag(entry.videoId);
      } else {
        // 3) The view is already created and already initialized. Simply set the right videoId
        //    on the loader.
//        thumbnail.setImageResource(R.drawable.loading_thumbnail);
    	  holder.loader.setVideo(entry.videoId);
      }
    }
    holder.label.setText(entry.title);
    holder.label.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
    return view;
  }
  
  private static class PostHolder {

      public TextView label;
      public YouTubeThumbnailView thumbnail;
      public YouTubeThumbnailLoader loader;
  }

  private final class ThumbnailListener implements
      YouTubeThumbnailView.OnInitializedListener,
      YouTubeThumbnailLoader.OnThumbnailLoadedListener {

    @Override
    public void onInitializationSuccess(
        YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
      loader.setOnThumbnailLoadedListener(this);
      thumbnailViewToLoaderMap.put(view, loader);
      view.setImageResource(R.drawable.loading_thumbnail);
      String videoId = (String) view.getTag();
      loader.setVideo(videoId);
    }

    @Override
    public void onInitializationFailure(
        YouTubeThumbnailView view, YouTubeInitializationResult loader) {
      view.setImageResource(R.drawable.no_thumbnail);
    }

    @Override
    public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
    }

    @Override
    public void onThumbnailError(YouTubeThumbnailView view, ErrorReason errorReason) {
      view.setImageResource(R.drawable.no_thumbnail);
    }
  }

}
