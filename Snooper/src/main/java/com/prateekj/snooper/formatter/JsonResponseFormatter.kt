package com.prateekj.snooper.formatter

import com.prateekj.snooper.utils.Logger

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

class JsonResponseFormatter : ResponseFormatter {

  override fun format(response: String): String {
    try {
      val json = JSONTokener(response).nextValue()
      return if (json is JSONObject) {
        JSONObject(response).toString(INDENT_SPACES)
      } else JSONArray(response).toString(INDENT_SPACES)
    } catch (e: JSONException) {
      e.printStackTrace()
      Logger.e(TAG, e.message, e)
    }

    return response
  }

  companion object {
    private val TAG = JsonResponseFormatter::class.java.simpleName
    private const val INDENT_SPACES = 4
  }
}
