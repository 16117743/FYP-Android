package com.chat.bluetooth.util;

import com.google.gson.Gson;

public final class JSONUtils {
  private static final Gson gson = new Gson();

  public JSONUtils(){}

  public static boolean isJSONValid(String JSON_STRING) {
      try {
          //gson.fromJson(JSON_STRING, Object.class);
          String[][] data = gson.fromJson(JSON_STRING, String[][].class);
          data = null;
          return true;
      } catch(com.google.gson.JsonSyntaxException ex) { 
          return false;
      }
  }
}