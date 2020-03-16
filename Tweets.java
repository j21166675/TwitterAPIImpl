package com.test.twiter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.test.twitter.utils.CommonHelper;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/*
 * Main Class to process the connection and get the Tweets using Twitter API
 */

public class Tweets {

	String AccessToken;
	String AccessSecret;
	String ConsumerKey;
	String ConsumerSecret;
	CommonHelper commonHelper = new CommonHelper();

	/*
	 * Constructor for Tweets
	 * 
	 * @param AccessToken
	 * 
	 * @param AccessSecret
	 * 
	 * @param ConsumerKey
	 * 
	 * @param ConsumerSecret
	 * 
	 */
	public Tweets(String AccessToken, String AccessSecret, String ConsumerKey,
			String ConsumerSecret) {
		this.AccessToken = AccessToken;
		this.AccessSecret = AccessSecret;
		this.ConsumerKey = ConsumerKey;
		this.ConsumerSecret = ConsumerSecret;

	}

	/*
	 * Private method to combine String
	 * 
	 * @param name
	 * 
	 * @param tweetCount
	 * 
	 * @param userId
	 * 
	 * @return String
	 * 
	 */
	private static String combine(String name, String tweetCount,
			String userId) {
		return "Tweet Count - " + tweetCount + " - From User - " + name
				+ " - ID - " + userId;

	}

	/*
	 * Private method to connect to Twitter Rest API
	 * 
	 * @param queryParam
	 * 
	 * @throws Exception
	 * 
	 * @return HttpResponse
	 * 
	 */
	private HttpResponse getHttpResponse(String queryParam) throws Exception {
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(ConsumerKey,
				ConsumerSecret);

		consumer.setTokenWithSecret(AccessToken, AccessSecret);
		HttpGet request = new HttpGet(
				"https://api.twitter.com/1.1/search/tweets.json?q="
						+ queryParam.trim());
		consumer.sign(request);
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response = client.execute(request);
		return response;

	}

	/*
	 * Private method to get Tweets as HashMap
	 * 
	 * @param jsonArr
	 * 
	 * @param location
	 * 
	 * @throws JSONException
	 * 
	 * @return HashMap
	 * 
	 */
	private static HashMap<String, String> getTweetsMap(JSONArray jsonArr,
			String location) throws JSONException {

		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONObject json = jsonArr.getJSONObject(i);
			if ((json).toString().indexOf(location) != -1) {
				String name = json.getJSONObject("user").get("name").toString();
				String tweetCount = String.valueOf(json.get("retweet_count"));
				String userId = json.getJSONObject("user").get("id_str")
						.toString();
				String combine = combine(name, tweetCount, userId);
				int count = 0;
				if (map.containsKey(combine)) {
					count = combine.charAt(combine.length() - 1) + 1;
				} else {
					combine = combine + String.valueOf(count);
				}
				map.put(combine, " Text : " + (String) json.get("text"));
			}

		}
		return map;

	}

	/*
	 * Private method to handle response from Twitter and convert as Map
	 * 
	 * @param userName
	 * 
	 * @param location
	 * 
	 * @throws Exception
	 * 
	 * @return HashMap
	 * 
	 */
	private Map<String, String> loadTweets(String userName, String location)
			throws Exception {
		HttpResponse response = getHttpResponse(userName);
		if (response == null) {
			throw new Exception(" Response is null from Twitter ");
		}
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new Exception(" Status code other than 200 " + statusCode
					+ ":" + response.getStatusLine().getReasonPhrase());
		}
		String jsonResponseStr = IOUtils
				.toString(response.getEntity().getContent());
		JSONObject json = commonHelper.convertStringToJSON(jsonResponseStr);
		JSONArray jsonArr = commonHelper.convertJSONObjToJSONArray(json,
				"statuses");
		return getTweetsMap(jsonArr, location);

	}

	/*
	 * Main method
	 * 
	 * @param args
	 * 
	 * @param location
	 * 
	 * @throws Exception
	 * 
	 * @return HashMap
	 * 
	 */
	public static void main(String... args) throws Exception {

		// You can pass arguments through command line argument or uncommand the
		// below line and hard code the value.
		/*
		 * args = new String[2]; args[0] = new String("Tom Cruise"); args[1] =
		 * "US";
		 */

		String AccessToken = "2479208708-NE2dTqWYTQCOrVAYRONLhtuKpFZNclkjjK0XrZ4";
		String AccessSecret = "GzbedhQcq2L2UDTDQB3SPUhlhtKYlzYAqAwNBAA63H54i";
		String ConsumerKey = "9A255OGUma75D04JdFuq6GLkT";
		String ConsumerSecret = "4dNScqL0LZcKDvcu2NYiKx7u1u3In3aiQIaiHo4a9TM0EqBnVJ";
		Tweets tweets = new Tweets(AccessToken, AccessSecret, ConsumerKey,
				ConsumerSecret);
		if (args == null || args.length < 2)
			throw new Exception(
					" No Argument to Process || Not all required parameters have been passed ");
		String userName = args[0].trim().replace(" ", "%20");
		String location = args[1].trim();
		System.out.println(args[0] + " - " + args[1]);
		TreeMap<String, String> treeMap = new TreeMap<String, String>(
				Collections.reverseOrder());
		treeMap.putAll(tweets.loadTweets(userName, location));

		if (treeMap.isEmpty()) {
			System.out.println(
					" No Tweets sent by Twiter API for this location or for this User. Please try with other user and location. ");
		} else {

			System.out.println(
					" --------------------------- Result Tweets -----------------------------");
			int count = 1;
			for (Entry<String, String> entry : treeMap.entrySet()) {
				if (count++ == 10)
					break;
				System.out.println(entry.getKey() + entry.getValue());
			}

			System.out.println(
					" -----------------------------------------------------------------------");
		}
	}
}
