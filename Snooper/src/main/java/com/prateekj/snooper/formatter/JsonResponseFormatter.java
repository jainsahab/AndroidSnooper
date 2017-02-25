package com.prateekj.snooper.formatter;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonResponseFormatter implements ResponseFormatter{
  @Override
  public String format(String response) {
    String formattedResponse = "";
    try {
      formattedResponse = new JSONObject(response).toString(2);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return formattedResponse;
  }
}
