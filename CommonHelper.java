package com.test.twitter.utils;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/*
 * Helper Class to do some common functionality
 */

public class CommonHelper {

	/*
	 * Converts String to JSON Object
	 * 
	 * @param str
	 * 
	 * @return JSONObject
	 * 
	 */
	public JSONObject convertStringToJSON(String str) throws JSONException {
		JSONObject jsnobject = new JSONObject(str);
		return jsnobject;

	}

	/*
	 * Converts JSON Object to String
	 * 
	 * @param JSON
	 * 
	 * @return String
	 * 
	 */
	public String convertJSONToString(JSONObject json) {
		return null;

	}

	/*
	 * Converts JSON Object to JSONArray
	 * 
	 * @param Json
	 * 
	 * @param String
	 * 
	 * @return JSONArray
	 * 
	 */
	public JSONArray convertJSONObjToJSONArray(JSONObject json, String head)
			throws JSONException {
		JSONArray jsonArray = json.getJSONArray(head);
		for (int i = 0; i < jsonArray.length(); i++) {
			json = jsonArray.getJSONObject(i);
		}
		return jsonArray;
	}

}
