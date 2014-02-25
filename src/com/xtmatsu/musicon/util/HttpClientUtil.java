package com.xtmatsu.musicon.util;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.net.ParseException;
import android.util.Log;

public class HttpClientUtil {
	

	public static String getContent(String url) {
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 7 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);
		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpConnectionParams.setTcpNoDelay(params, true);
		
		HttpUriRequest httpRequest = new HttpGet(url);
		
		HttpResponse httpResponse = null;
		
		try {
            httpResponse = httpClient.execute(httpRequest);
        }
        catch (ClientProtocolException e) {
        	Log.e("HttpClientUtil", "ClientProtocolException occurred. URL:"+url, e);
        }
        catch (IOException e){
        	Log.e("HttpClientUtil", "IOException occurred. URL:"+url, e);
        }
		
		String json = null;
		
		if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            HttpEntity httpEntity = httpResponse.getEntity();
            try {
                json = EntityUtils.toString(httpEntity);
            }
            catch (ParseException e) {
            	Log.e("HttpClientUtil", "ParseException occurred. URL:"+url, e);
            }
            catch (IOException e) {
            	Log.e("HttpClientUtil", "IOException occurred. URL:"+url, e);
            }
            finally {
                try {
                    httpEntity.consumeContent();
                }
                catch (IOException e) {
                	Log.e("HttpClientUtil", "IOException occurred. URL:"+url, e);
                }
            }
        }
		
		httpClient.getConnectionManager().shutdown();
		
		
		
		return json;
	}
	
	

}
