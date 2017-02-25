package com.prateekj.snooper.formatter;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonResponseFormatter implements ResponseFormatter{

  private static final String TAG = JsonResponseFormatter.class.getSimpleName();
  private static final int INDENT_SPACES = 2;

  @Override
  public String format(String response) {
    try {
      Object json = new JSONTokener(response).nextValue();
      if (json instanceof JSONObject) {
        return new JSONObject(response).toString(INDENT_SPACES);
      } else if (json instanceof JSONArray) {
        return new JSONArray(response).toString(INDENT_SPACES);
      }
    } catch (JSONException e) {
      e.printStackTrace();
      Log.d(TAG, e.getMessage(), e);
    }
    return "";
  }
}
