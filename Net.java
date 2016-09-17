package com.zap.chatty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import android.util.Log;

//2014-08-15
public class Net
{

	static DefaultHttpClient httpclient = new DefaultHttpClient();
	//Input a URL and get its contents
	//customize for GET
	static String DownloadText(String URL)
	{
		InputStream in = null;
		try {
			in = Net.OpenHttpConnection(URL);
		} catch (IOException e) {
			Log.d("Networking", e.getLocalizedMessage());
			return "";
		}
		return Net.stream2String(in);
	}
	
	//Input a URL and get its contents in InputStream
	private static InputStream OpenHttpConnection(String urlString) throws IOException
	{
		{
			HttpGet request;
			HttpEntity entity;
			HttpResponse response;
			
			request = new HttpGet(urlString);
			response = httpclient.execute(request);
			entity = response.getEntity();
			InputStream in = entity.getContent();
			
			return in;
			}

	}
	
	private static String stream2String(InputStream is)
	{
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line+"\n");
			}
		}
		catch (IOException e) {		e.printStackTrace();	}
		finally {
			if (br != null) {
				try {		br.close();		} 
				catch (IOException e) {		e.printStackTrace();		}
			}
		}
		return sb.toString();
	}
	
	//Input a URL and List<NameValuePair> for post and get the content of response in String
	static String HttpPostURL(String url,List<NameValuePair> nameValuePairs)
	{
		HttpPost httppost = new HttpPost(url);

		try {
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		    HttpResponse response = httpclient.execute(httppost);
		    HttpEntity ent=response.getEntity();
		    InputStream in=ent.getContent();
		    return Net.stream2String(in);

		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}
		return "";
	}

}