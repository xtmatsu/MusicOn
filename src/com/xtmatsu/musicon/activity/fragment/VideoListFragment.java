package com.xtmatsu.musicon.activity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ListFragment;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

import com.xtmatsu.musicon.MusicOnConstants;
import com.xtmatsu.musicon.R;
import com.xtmatsu.musicon.activity.adapter.PageAdapter;
import com.xtmatsu.musicon.activity.task.ArtistSearchTask;
import com.xtmatsu.musicon.activity.task.ArtistSearchTaskCallback;
import com.xtmatsu.musicon.activity.task.RankingSearchTask;
import com.xtmatsu.musicon.activity.task.RankingSearchTaskCallback;
import com.xtmatsu.musicon.activity.task.YoutubeSearchTask;
import com.xtmatsu.musicon.activity.task.YoutubeSearchTaskCallback;
import com.xtmatsu.musicon.dto.ItemDto;
import com.xtmatsu.musicon.dto.YoutubeDto;
import com.xtmatsu.musicon.util.DialogUtil;
import com.xtmatsu.musicon.util.GAUtil;

/**
 * A fragment that shows a static list of videos.
 */
public class VideoListFragment extends ListFragment implements
		ArtistSearchTaskCallback, YoutubeSearchTaskCallback, OnScrollListener,
		RankingSearchTaskCallback {

	private List<YoutubeDto> youtubeList;
	private List<String> videoIdList;
	private PageAdapter adapter;
	private View videoBox;
	private String artist;
	private String mode;
	int offset = 0;
	private static DialogUtil waitDialog;
	private boolean isFirst;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// くるくるスタート
		showNowLoading();
		isFirst = true;
		
		GAUtil.screenSend("VideoListFragment");

		youtubeList = new ArrayList<YoutubeDto>();
		videoIdList = new ArrayList<String>();

		adapter = new PageAdapter(getActivity(), R.layout.video_list_item, new ArrayList<YoutubeDto>());

		if (artist != null) {
			ArtistSearchTask task = new ArtistSearchTask(this, artist);
			task.execute(Integer.toString(offset));
		} else {
			RankingSearchTask task = new RankingSearchTask(this, mode);
			task.execute(Integer.toString(offset));
		}
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		videoBox = getActivity().findViewById(R.id.video_box);
		getListView().setOnScrollListener(this);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// String videoId = youtubeList.get(position).videoId;

		VideoFragment videoFragment = (VideoFragment) getFragmentManager()
				.findFragmentById(R.id.video_fragment_container);
		// videoFragment.setVideoId(videoId);
		videoFragment.setVideoIdList(youtubeList, position);
		YoutubeDto youtubeDto = youtubeList.get(position);
		GAUtil.eventSend("VideoClick", youtubeDto.author + "," + youtubeDto.title , youtubeDto.videoId, null);

		// The videoBox is INVISIBLE if no video was previously selected, so we
		// need to show it now.
		if (videoBox.getVisibility() != View.VISIBLE) {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				// Initially translate off the screen so that it can be animated
				// in from below.
				videoBox.setTranslationY(videoBox.getHeight());
			}
			videoBox.setVisibility(View.VISIBLE);
		}

		// If the fragment is off the screen, we animate it in.
		if (videoBox.getTranslationY() > 0) {
			videoBox.animate()
					.translationY(0)
					.setDuration(
							MusicOnConstants.ANIMATION_DURATION_MILLIS);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		adapter.releaseLoaders();
	}

	public void setLabelVisibility(boolean visible) {
		adapter.setLabelVisibility(visible);
	}

	@Override
	public void onSuccess(List<ItemDto> items) {
		if (items.isEmpty() && isFirst) {
			Toast.makeText(getActivity(), "検索結果はありませんでした。", Toast.LENGTH_SHORT).show();
			if (waitDialog != null) {
				waitDialog.dismiss();
				waitDialog = null;
			}
		} else {
			boolean isQuerySearch = artist != null ? true : false ;
			for (int i = 0; i < items.size(); i++) {
				YoutubeSearchTask task = new YoutubeSearchTask(this,
						items.get(i), isQuerySearch);
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
						Integer.toString(i));
			}
		}
		isFirst = false;

	}

	@Override
	public void onYoutubeSearchSuccess(YoutubeDto youtube) {
		if (youtube.author != null) {
			if (waitDialog != null) {
				waitDialog.dismiss();
				waitDialog = null;
			}
			if(!videoIdList.contains(youtube.videoId)){
				((PageAdapter) getListAdapter()).add(youtube);
				youtubeList.add(youtube);
				videoIdList.add(youtube.videoId);
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (totalItemCount == firstVisibleItem + visibleItemCount) {
			offset += MusicOnConstants.PAGE_NUM;
			if (artist != null) {
				ArtistSearchTask task = new ArtistSearchTask(this, artist);
				task.execute(Integer.toString(offset));
			} else {
				RankingSearchTask task = new RankingSearchTask(this, mode);
				task.execute(Integer.toString(offset));
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	private void showNowLoading() {
		// プログレスダイアログの設定
		waitDialog = new DialogUtil(getActivity());

		// プログレスダイアログを表示
		waitDialog.show();
	}

}
