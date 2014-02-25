package com.xtmatsu.musicon.activity.task;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.xtmatsu.musicon.MusicOnConstants;
import com.xtmatsu.musicon.dto.ItemDto;
import com.xtmatsu.musicon.util.HttpClientUtil;

public class ArtistSearchTask extends AsyncTask<String, Integer, List<ItemDto>> {
	
	private ArtistSearchTaskCallback callback;
	private String artistName;

	public ArtistSearchTask(ArtistSearchTaskCallback callback, String artistName) {
		
		super();
        this.callback = callback;
        this.artistName = artistName;
		
	}
	
	@Override
    protected void onPostExecute(List<ItemDto> itemList) {
        callback.onSuccess(itemList);
    }

	@Override
	protected List<ItemDto> doInBackground(String... params) {
		List<ItemDto> itemList = new ArrayList<ItemDto>();
		
		String url = makeUrlString(artistName, params);
		Log.d("ArtistSearchTask", "iTunesURL:"+url);
		
		String content = HttpClientUtil.getContent(url);
		if(content!=null){
			try {
				JSONObject rootObject = new JSONObject(content);
				JSONArray itemArray = rootObject.getJSONArray("results");
				int itemCount = itemArray.length();
				ItemDto itemDto;
				for (int i = 0; i < itemCount; i++) {
					itemDto = new ItemDto();
					itemDto.author = itemArray.getJSONObject(i).getString("artistName");
					itemDto.title = itemArray.getJSONObject(i).getString("trackName");
				    itemList.add(itemDto);
				}
			} catch (JSONException e) {
				Log.e("ArtistSearchTask", "Exception getting JSON data", e);
			}
		}
		return itemList;
	}
	
	
	private String makeUrlString(String artistName, String...  params) {
		
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme(MusicOnConstants.scheme);
		uriBuilder.authority(MusicOnConstants.authorityItunes);
		uriBuilder.path(MusicOnConstants.pathItunes);
		uriBuilder.appendQueryParameter("term", artistName);
		uriBuilder.appendQueryParameter("country", MusicOnConstants.country);
		uriBuilder.appendQueryParameter("media", MusicOnConstants.media);
		uriBuilder.appendQueryParameter("entity", MusicOnConstants.entity);
		uriBuilder.appendQueryParameter("attribute", MusicOnConstants.attribute);
		uriBuilder.appendQueryParameter("offset", params[0]);
		uriBuilder.appendQueryParameter("limit", MusicOnConstants.count);
		
		String uri= uriBuilder.toString();

		return uri;
	}

}
