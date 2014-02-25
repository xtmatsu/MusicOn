package com.xtmatsu.musicon.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.xtmatsu.musicon.MusicOnConstants;
import com.xtmatsu.musicon.activity.VideoListActivity;
import com.xtmatsu.musicon.dto.YoutubeDto;

public class VideoFragment extends YouTubePlayerFragment
implements OnInitializedListener {

private YouTubePlayer player;
private String videoId;

public static VideoFragment newInstance() {
return new VideoFragment();
}

@Override
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);

initialize(MusicOnConstants.DEVELOPER_KEY, this);
}

@Override
public void onDestroy() {
if (player != null) {
  player.release();
}
super.onDestroy();
}

public void setVideoId(String videoId) {
if (videoId != null && !videoId.equals(this.videoId)) {
  this.videoId = videoId;
  if (player != null) {
    player.loadVideo(videoId);
  }
}
}

public void setVideoIdList(List<YoutubeDto> youtubeList, int position) {
	if (youtubeList != null) {
		List<String> videoList = new ArrayList<String>();
		int count = 0;
		for(YoutubeDto youtube : youtubeList){
			count++;
			if(count > position){
				videoList.add(youtube.videoId);
			}
		}
		if (player != null && !videoList.isEmpty()) {
			player.loadVideos(videoList);
		}
  }
}

public void pause() {
if (player != null) {
  player.pause();
}
}

@Override
public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean restored) {
this.player = player;
player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
player.setOnFullscreenListener((VideoListActivity) getActivity());
if (!restored && videoId != null) {
  player.loadVideo(videoId);
}
}

@Override
public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
this.player = null;
}

}
