package com.xtmatsu.musicon.activity.task;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.xtmatsu.musicon.MusicOnConstants;
import com.xtmatsu.musicon.dto.ItemDto;
import com.xtmatsu.musicon.dto.YoutubeDto;
import com.xtmatsu.musicon.util.HttpClientUtil;

public class YoutubeSearchTask extends AsyncTask<String, Integer, YoutubeDto>{
	
	private YoutubeSearchTaskCallback callback;
	private ItemDto item;
	private boolean isQuerySearch;
	
	public YoutubeSearchTask(YoutubeSearchTaskCallback callback, ItemDto item, boolean isQuerySearch) {
		super();
        this.callback = callback;
        this.item = item;
        this.isQuerySearch = isQuerySearch;
	}
	
	@Override
    protected void onPostExecute(YoutubeDto youtube) {
        callback.onYoutubeSearchSuccess(youtube);
    }

	@Override
	protected YoutubeDto doInBackground(String... params) {
		YoutubeDto youtubeDto = new YoutubeDto();
		String url;
		String content;
		url = makeUrlString(params, item);
		
		Log.d("YoutubeSearchTask", "YoutubeURL:"+url);
		
		content = HttpClientUtil.getContent(url);
		if(content!=null){
				try {
					JSONObject rootObject = new JSONObject(content);
					JSONObject object = rootObject.getJSONObject("feed").getJSONArray("entry").getJSONObject(0);
					youtubeDto.title = object.getJSONObject("title").getString("$t");
					youtubeDto.author = object.getJSONArray("author").getJSONObject(0).getJSONObject("name").getString("$t");
					youtubeDto.link = object.getJSONArray("link").getJSONObject(0).getString("href");
					Uri uri = Uri.parse(youtubeDto.link);
					youtubeDto.videoId = uri.getQueryParameter("v");
					youtubeDto.position = Integer.parseInt(params[0]);
					youtubeDto.thumbnail = object.getJSONObject("media$group").getJSONArray("media$thumbnail").getJSONObject(0).getString("url");
				} catch (JSONException e) {
					Log.e("YoutubeSearchTask", "Exception getting JSON data", e);
				}
				
		}
		return youtubeDto;
	}

	private String makeUrlString(String[] params, ItemDto dto) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme(MusicOnConstants.scheme);
		uriBuilder.authority(MusicOnConstants.authorityY);
		uriBuilder.path(MusicOnConstants.pathY);
		uriBuilder.appendQueryParameter("vq", dto.author + "+" + dto.title);
		uriBuilder.appendQueryParameter("alt", MusicOnConstants.alt);
		if(isQuerySearch){
			uriBuilder.appendQueryParameter("orderby", MusicOnConstants.orderbyRelevance);
		}else{
			uriBuilder.appendQueryParameter("orderby", MusicOnConstants.orderbyViewCount);
		}
		uriBuilder.appendQueryParameter("relevance_lang_languageCode", MusicOnConstants.relevance_lang_languageCode);
		uriBuilder.appendQueryParameter("max-results", MusicOnConstants.maxResults);
		
		String uri= uriBuilder.toString();

		return uri;
	}


}
