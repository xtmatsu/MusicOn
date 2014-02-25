package com.xtmatsu.musicon;

public class MusicOnConstants {
	
	/** The duration of the animation sliding up the video in portrait. */
	public static final int ANIMATION_DURATION_MILLIS = 300;
	
	public static final String DEVELOPER_KEY = "AI39si4bIB7iYo7DA7s-kAV6HmUpM05vE_KG16a5Tw8NQWogfbeW8W73OSblS5M_HIimD9fCYJfvSj67NWyPQB_vs5PuDOMLeQ";
	
	/* 開発用*/
//	public static final String BUGSENSE_KEY = "66d4f925";
//	public static final String GA_KEY = "UA-48345839-2";
	
	/* 本番用*/
	public static final String BUGSENSE_KEY = "6fcbb4f1";
	public static final String GA_KEY = "UA-48345839-1";
	
	public static final int PAGE_NUM = 10;

	
	/**
	 * Tapnow用
	 */
	/**注目ランキング検索**/
	public static String scheme = "http";
	public static String authority = "api.tapnow.jp";
	public static String path = "/listif/lists/tapnow/ap/music/ranking/allcate/allsubcate/allperiod/allprice/contents";
	public static String cid = "amid.sample";
	public static String method = "traverse";
	public static String alt = "json";
	public static String count = "10";

	/**ニューリリース**/
	public static String pathNewRelease = "/listif/lists/tapnow/ap/goods/music/onclick/contents";
	
	/**総合ランキング**/
	public static String pathAllRanking = "/listif/lists/tapnow/ap/music/ranking/itunes/japan/contents";
	
	/**邦楽ランキング**/
	public static String pathJpRanking = "/listif/lists/tapnow/ap/music/ranking/itunes/jpop/contents";
	
	/**洋楽ランキング**/
	public static String pathForeignRanking = "/listif/lists/tapnow/ap/music/ranking/itunes/usa/contents";
	
	/**
	 * itunes API
	 */
	public static String authorityItunes = "ax.itunes.apple.com";
	public static String pathItunes = "/WebObjects/MZStoreServices.woa/wa/wsSearch";
	public static String country = "jp";
	public static String media = "music";
	public static String attribute = "artistTerm";
	public static String entity = "song";
	public static String limit = "10";
	
	/**
	 * Youtube用
	 */
	public static String authorityY = "gdata.youtube.com";
	public static String pathY = "/feeds/api/videos";
	public static String orderbyRelevance = "relevance";
	public static String orderbyViewCount = "viewCount";
	public static String relevance_lang_languageCode = "ja";
	public static String maxResults = "1";
	
	public static String format = "json";
	public static String applicationId = "1026387823859293319";

	public static CharSequence hint = "アーティスト名";

}
