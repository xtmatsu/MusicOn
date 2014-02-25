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

public class RankingSearchTask extends AsyncTask<String, Integer, List<ItemDto>> {
	
	private RankingSearchTaskCallback callback;
	private String mode;

	public RankingSearchTask(RankingSearchTaskCallback callback, String mode) {
		
		super();
        this.callback = callback;
        this.mode = mode;
		
	}
	
	@Override
    protected void onPostExecute(List<ItemDto> itemList) {
        callback.onSuccess(itemList);
    }

	@Override
	protected List<ItemDto> doInBackground(String... params) {
		List<ItemDto> itemList = new ArrayList<ItemDto>();
		
		String url = makeUrlString(params);
		Log.d("TapnowURL", url);
		
		String content = HttpClientUtil.getContent(url);
		if(content!=null){
			try {
				JSONObject rootObject = new JSONObject(content);
				JSONArray itemArray = rootObject.getJSONObject("rss").getJSONObject("channel").getJSONArray("item");
				int itemCount = itemArray.length();
				ItemDto itemDto;
				for (int i = 0; i < itemCount; i++) {
					itemDto = new ItemDto();
					if(mode.equals("ニューリリース")){
						itemDto.author = itemArray.getJSONObject(i).getJSONObject("artist").getString("$t");
					}else{
						itemDto.author = itemArray.getJSONObject(i).getJSONObject("author").getString("$t");	
					}
					itemDto.title = itemArray.getJSONObject(i).getJSONObject("title").getString("$t");
				    itemList.add(itemDto);
				}
			} catch (JSONException e) {
				Log.e("RankingSearchTask", "Exception getting JSON data", e);
			}
		}
		return itemList;
	}
	
	
	private String makeUrlString(String...  params) {
		
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme(MusicOnConstants.scheme);
		uriBuilder.authority(MusicOnConstants.authority);
		
		//TODO 書き方きれいに
		if(mode.equals("注目ランキング")){
			uriBuilder.path(MusicOnConstants.path);
		}else if(mode.equals("ニューリリース")){
			uriBuilder.path(MusicOnConstants.pathNewRelease);
		}else if(mode.equals("総合ランキング")){
			uriBuilder.path(MusicOnConstants.pathAllRanking);
		}else if(mode.equals("邦楽ランキング")){
			uriBuilder.path(MusicOnConstants.pathJpRanking);
		}else if(mode.equals("洋楽ランキング")){
			uriBuilder.path(MusicOnConstants.pathForeignRanking);
		}
		uriBuilder.appendQueryParameter("cid", MusicOnConstants.cid);
		uriBuilder.appendQueryParameter("alt", MusicOnConstants.alt);
		uriBuilder.appendQueryParameter("method", MusicOnConstants.method);
		uriBuilder.appendQueryParameter("start", params[0]);
		uriBuilder.appendQueryParameter("count", MusicOnConstants.count);
		
		
		String uri= uriBuilder.toString();

		return uri;
	}

}
